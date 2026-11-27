# UAS Inggris X — Vercel Mobile Web PWA

Aplikasi web mobile (Android & iOS PWA) untuk siswa **Ujian Akhir Semester (UAS) Ganjil Bahasa Inggris Kelas X (50 Soal Pilihan Ganda)** dengan tampilan *Colourful White* yang cerah dan ramah siswa — langsung pakai tanpa perlu masuk akun Google:
- 50 soal dan 4 teks bacaan (Text 1–4) identik dengan PDF resmi.
- Perhitungan skor otomatis (`jumlah jawaban benar × 2`, maksimal 100) serta rekap pilihan jawaban siswa setelah selesai mengerjakan (tanpa menampilkan kunci jawaban maupun pembahasan).
- **Penyimpanan Cache Browser & Mode Offline Penuh** melalui Service Worker (`sw.js`) dan `localStorage` sehingga progres ujian, timer, dan riwayat skor tersimpan otomatis di browser perangkat meski tanpa koneksi internet.

## Cara Deploy ke Vercel

1. Push proyek ini ke GitHub (atau export ZIP dari AI Studio).
2. Buka **Vercel Dashboard → Add New Project**.
3. Pilih repositori Anda, lalu pada **Root Directory**, atur ke:
   ```
   vercel-web
   ```
4. Biarkan **Framework Preset** pada `Other` (atau default) dan klik **Deploy**.
   Atau melalui CLI:
   ```bash
   cd vercel-web
   npx vercel --prod
   ```
