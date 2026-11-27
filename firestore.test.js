const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const EXAM_ID = "uas_ganjil_2026";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function emptyAnswers() {
  return Array.from({ length: 50 }, () => "");
}

function sampleAnswers() {
  const arr = emptyAnswers();
  arr[0] = "a";
  arr[1] = "c";
  arr[49] = "b";
  return arr;
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read or write exam_progress or exam_attempts", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("exam_progress").get()
  );
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("exam_attempts").get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("exam_progress")
      .doc(EXAM_ID)
      .set({
        userId: ALICE_UID,
        examId: EXAM_ID,
        studentName: "Alice",
        studentNumber: "01",
        answers: emptyAnswers(),
        flaggedQuestions: [],
        currentQuestion: 1,
        remainingSeconds: 5400,
        status: "in_progress",
        createdAt: new Date(Date.now() - 1000),
        updatedAt: new Date(Date.now() - 1000),
      })
  );
});

test("Authenticated owner: valid exam_progress create, partial update, and list succeed", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 10000);
  const docRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("exam_progress")
    .doc(EXAM_ID);

  await assertSucceeds(
    docRef.set({
      userId: ALICE_UID,
      examId: EXAM_ID,
      studentName: "Sinta Dewi",
      studentNumber: "14",
      answers: emptyAnswers(),
      flaggedQuestions: [],
      currentQuestion: 1,
      remainingSeconds: 5400,
      status: "in_progress",
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    docRef.update({
      answers: sampleAnswers(),
      flaggedQuestions: [2, 15],
      currentQuestion: 16,
      remainingSeconds: 4820,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("exam_progress").get()
  );

  // Immutable createdAt cannot be mutated
  await assertFails(
    docRef.update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Shadow update with ghost field must fail
  await assertFails(
    docRef.update({
      isVerified: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject extra @DocumentId 'id' key on create
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("exam_progress")
      .doc("extra_progress")
      .set({
        id: "extra_progress",
        userId: ALICE_UID,
        examId: EXAM_ID,
        studentName: "Sinta Dewi",
        studentNumber: "14",
        answers: emptyAnswers(),
        flaggedQuestions: [],
        currentQuestion: 1,
        remainingSeconds: 5400,
        status: "in_progress",
        createdAt: created,
        updatedAt: created,
      })
  );

  // Reject future timestamp
  await assertFails(
    docRef.update({
      currentQuestion: 20,
      updatedAt: new Date(Date.now() + 60000),
    })
  );
});

test("Authenticated owner: valid exam_attempts create succeeds and update is immutable", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 1000);
  const attemptRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("exam_attempts")
    .doc("attempt_1");

  await assertSucceeds(
    attemptRef.set({
      userId: ALICE_UID,
      examId: EXAM_ID,
      studentName: "Sinta Dewi",
      studentNumber: "14",
      answers: sampleAnswers(),
      correctCount: 45,
      score: 90,
      durationSeconds: 1850,
      completedAt: now,
      createdAt: now,
    })
  );

  // Tampered score != correctCount * 2 must fail
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("exam_attempts")
      .doc("attempt_bad_score")
      .set({
        userId: ALICE_UID,
        examId: EXAM_ID,
        studentName: "Sinta Dewi",
        studentNumber: "14",
        answers: sampleAnswers(),
        correctCount: 20,
        score: 100,
        durationSeconds: 1200,
        completedAt: now,
        createdAt: now,
      })
  );

  // Completed attempts cannot be updated
  await assertFails(attemptRef.update({ score: 100 }));

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("exam_attempts").get()
  );
});

test("Authenticated user: cannot access or modify another user's exam data", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const bobRef = context
      .firestore()
      .collection("users")
      .doc(BOB_UID)
      .collection("exam_progress")
      .doc(EXAM_ID);
    await bobRef.set({
      userId: BOB_UID,
      examId: EXAM_ID,
      studentName: "Bob",
      studentNumber: "02",
      answers: emptyAnswers(),
      flaggedQuestions: [],
      currentQuestion: 5,
      remainingSeconds: 5000,
      status: "in_progress",
      createdAt: new Date(Date.now() - 10000),
      updatedAt: new Date(Date.now() - 10000),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("exam_progress")
      .doc(EXAM_ID)
      .get()
  );
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("exam_progress").get()
  );
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("exam_progress")
      .doc(EXAM_ID)
      .update({ currentQuestion: 10 })
  );
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("exam_progress")
      .doc(EXAM_ID)
      .delete()
  );
});
