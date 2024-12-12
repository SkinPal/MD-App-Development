## MD App Development


## Team Member - MD

| Bangkit ID | Name | Learning Path | University |LinkedIn |
| ---      | ---       | ---       | ---       | ---       |
| A444B4KX1918 | Ika Jihan Pratiwi | Mobile Development | 	Universitas Ivet  | [![text](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/ikajihanpratiwi) |
| A312B4KX3218 | Nadzira Karimantika Atsarirahmati |  Mobile Development | Universitas Sebelas Maret | [![text](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/nadzira-karimantika-atsarirahmati-211b62271) |


## Deskripsi Proyek
Proyek ini adalah aplikasi berbasis Kotlin yang dirancang untuk dijalankan di Android Studio. Aplikasi ini menyediakan fitur seperti skin analysis, skin recommendation, and skin tracking dengan memanfaatkan AI.

## Prasyarat
Sebelum memulai, pastikan Anda memiliki:

1. **Android Studio** (versi terbaru) - IDE utama untuk mengembangkan dan menjalankan aplikasi Android.
2. **JDK (Java Development Kit)** - Sebaiknya gunakan versi terbaru.
3. **Gradle** - Terintegrasi dalam Android Studio, tetapi pastikan sinkronisasi berjalan lancar.
4. **Koneksi Internet** - Diperlukan untuk mengunduh dependensi proyek.
5. **Perangkat Android atau Emulator** - Untuk menjalankan aplikasi.

## Cara Menggunakan
Ikuti langkah-langkah berikut untuk menjalankan proyek ini:

### 1. Ekstrak File
- Unduh file zip proyek dan ekstrak ke direktori pilihan Anda.

### 2. Buka Proyek di Android Studio
- Jalankan Android Studio.
- Pilih "Open an Existing Project".
- Arahkan ke folder hasil ekstraksi, lalu pilih folder utama proyek.

### 3. Sinkronisasi Proyek
- Android Studio akan secara otomatis memulai sinkronisasi Gradle.
- Pastikan tidak ada error selama proses ini. Jika terjadi error, periksa koneksi internet atau file `build.gradle`.

### 4. Jalankan Aplikasi
- Hubungkan perangkat Android Anda dengan mode developer (aktifkan "USB Debugging") atau siapkan emulator melalui Android Studio.
- Klik tombol "Run" (ikon segitiga hijau) di toolbar.
- Pilih perangkat target, lalu tunggu hingga aplikasi berjalan.

### 5. Debugging dan Pengujian
- Gunakan fitur "Logcat" di Android Studio untuk memantau log selama aplikasi berjalan.
- Pastikan semua fitur berfungsi seperti yang diharapkan.

## Struktur Folder
Proyek ini memiliki struktur folder berikut:

- `app/`: 
  - Berisi kode sumber aplikasi termasuk file XML (UI) dan file Kotlin (logika aplikasi).
- `gradle/`: 
  - Berisi konfigurasi Gradle untuk pengelolaan dependensi dan build.
- `build.gradle`: 
  - File utama untuk mengelola versi SDK, dependensi, dan konfigurasi proyek.
- `manifest/`:
  - Berisi file `AndroidManifest.xml` yang mengatur izin aplikasi, aktivitas, dan metadata lainnya.

## Catatan Penting
- **Masalah Sinkronisasi:** Jika terjadi error selama sinkronisasi Gradle, pastikan Anda memiliki koneksi internet stabil dan versi Gradle yang kompatibel.
- **Versi Minimum Android:** Perangkat target harus memenuhi persyaratan versi minimum yang ditentukan dalam file `build.gradle`.
- **Debugging:** Jika aplikasi tidak berjalan, gunakan "Logcat" untuk melacak error.


