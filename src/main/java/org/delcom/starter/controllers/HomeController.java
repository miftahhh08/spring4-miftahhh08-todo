package org.delcom.starter.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * Controller utama untuk aplikasi Spring Boot.
 * Mengelola endpoint-endpoint dasar dan adaptasi dari studi kasus.
 *
 * <p>Refaktor ini mempertahankan struktur satu file dengan memisahkan
 * logika endpoint (Web Layer) dari logika bisnis (Service Layer)
 * menggunakan metode privat dalam kelas yang sama.</p>
 */
@RestController
public class HomeController {

    // --- Konstanta untuk Kriteria Penilaian ---
    private static final double BATAS_NILAI_A = 79.5;
    private static final double BATAS_NILAI_AB = 72.0;
    private static final double BATAS_NILAI_B = 64.5;
    private static final double BATAS_NILAI_BC = 57.0;
    private static final double BATAS_NILAI_C = 49.5;
    private static final double BATAS_NILAI_D = 34.0;

    /**
     * Mapping konstan untuk data jurusan.
     * Didefinisikan sebagai static final agar immutable.
     */
    private static final Map<String, String> MAP_JURUSAN = Map.ofEntries(
            Map.entry("11S", "S1 Informatika"),
            Map.entry("12S", "S1 Sistem Informasi"),
            Map.entry("14S", "S1 Teknik Elektro"),
            Map.entry("21S", "S1 Manajemen Rekayasa"),
            Map.entry("22S", "S1 Teknik Metalurgi"),
            Map.entry("31S", "S1 Teknik Bioproses"),
            Map.entry("114", "D4 Rekayasa Perangkat Lunak"),
            Map.entry("113", "D3 Teknologi Informasi"),
            Map.entry("133", "D3 Teknologi Komputer")
    );

    // --- Endpoint Dasar (Web Layer) ---

    @GetMapping("/")
    public String salamPembuka() {
        return "Halo Abdullah, selamat datang di pengembangan aplikasi Spring Boot!";
    }

    @GetMapping("/hello/{nama}")
    public String ucapSalam(@PathVariable String nama) {
        return "Halo, " + nama + "!";
    }

    // --- Endpoint Studi Kasus (Web Layer) ---

    /**
     * Endpoint untuk adaptasi StudiKasus1 (Data NIM).
     * Menerima NIM dan memprosesnya melalui metode processDataNim.
     * Mengembalikan 200 OK jika sukses, 400 Bad Request jika format salah.
     */
    @GetMapping("/dataNim/{nim}")
    public ResponseEntity<String> dataNim(@PathVariable String nim) {
        try {
            String hasil = processDataNim(nim);
            return ResponseEntity.ok(hasil);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint untuk adaptasi StudiKasus2 (Kalkulasi Nilai).
     * Menerima input nilai (Base64) dan memprosesnya via processKalkulasiNilai.
     * Mengembalikan 200 OK jika berhasil, 400 Bad Request jika input invalid.
     */
    @GetMapping("/kalkulasiNilai")
    public ResponseEntity<String> kalkulasiNilai(@RequestParam String dataBase64) {
        try {
            String inputTerdocode = decodeDataBase64(dataBase64);
            String hasil = processKalkulasiNilai(inputTerdocode);
            return ResponseEntity.ok(hasil);
        } catch (NoSuchElementException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            return new ResponseEntity<>("Struktur data input tidak valid atau tidak lengkap. Pastikan angka dan format sesuai.", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint untuk adaptasi StudiKasus3 (Analisis Matriks L).
     * Menerima input matriks (Base64) dan memprosesnya via processAnalisisMatriks.
     * Mengembalikan 200 OK jika berhasil, 400 Bad Request jika input invalid.
     */
    @GetMapping("/analisisMatriksL")
    public ResponseEntity<String> analisisMatriksL(@RequestParam String dataBase64) {
        try {
            String inputTerdocode = decodeDataBase64(dataBase64);
            String hasil = processAnalisisMatriks(inputTerdocode);
            return ResponseEntity.ok(hasil);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Format data matriks tidak valid atau tidak lengkap.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint untuk adaptasi StudiKasus4 (Statistik Data).
     * Menerima input list angka (Base64) dan memprosesnya via processStatistikData.
     * Mengembalikan 200 OK jika berhasil, 400 Bad Request jika input invalid.
     */
    @GetMapping("/statistikData")
    public ResponseEntity<String> statistikData(@RequestParam String dataBase64) {
        try {
            String inputTerdocode = decodeDataBase64(dataBase64);
            String hasil = processStatistikData(inputTerdocode);
            return ResponseEntity.ok(hasil);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Input Base64 tidak valid.", HttpStatus.BAD_REQUEST);
        }
    }

    // --- Metode Bantu & Utility (Private) ---

    /**
     * Metode bantu untuk decode string Base64.
     * Menghindari duplikasi kode di berbagai endpoint.
     *
     * @param dataBase64 String input yang di-encode Base64.
     * @return String yang sudah di-decode.
     * @throws IllegalArgumentException jika input Base64 tidak valid.
     */
    private String decodeDataBase64(String dataBase64) {
        try {
            byte[] bytesTerdocode = Base64.getDecoder().decode(dataBase64);
            return new String(bytesTerdocode, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("Input Base64 tidak valid: " + e.getMessage());
        }
    }

    /**
     * Metode bantu untuk konversi skor numerik ke nilai huruf.
     *
     * @param skor Nilai akhir (double).
     * @return String yang merepresentasikan Nilai (A, AB, B, ... E).
     */
    private String konversiKeNilaiHuruf(double skor) {
        if (skor >= BATAS_NILAI_A) return "A";
        else if (skor >= BATAS_NILAI_AB) return "AB";
        else if (skor >= BATAS_NILAI_B) return "B";
        else if (skor >= BATAS_NILAI_BC) return "BC";
        else if (skor >= BATAS_NILAI_C) return "C";
        else if (skor >= BATAS_NILAI_D) return "D";
        else return "E";
    }

    // --- Business Logic Methods (Private "Service Layer") ---

    /**
     * Logika inti untuk Studi Kasus 1: Data NIM.
     *
     * @param nim NIM yang akan diproses.
     * @return String hasil format data.
     * @throws IllegalArgumentException jika format NIM tidak valid.
     */
    private String processDataNim(String nim) {
        StringBuilder builder = new StringBuilder();

        if (nim.length() != 8) {
            throw new IllegalArgumentException("Format NIM tidak valid. Harus terdiri dari 8 digit.");
        }

        String kode = nim.substring(0, 3);
        String tahunStr = nim.substring(3, 5);
        String nomorStr = nim.substring(5);
        String namaJurusan = MAP_JURUSAN.get(kode);

        if (namaJurusan != null) {
            int tahunMasuk = 2000 + Integer.parseInt(tahunStr);
            builder.append("Informasi NIM ").append(nim).append(": \n");
            builder.append(">> Jurusan: ").append(namaJurusan).append("\n");
            builder.append(">> Tahun Masuk: ").append(tahunMasuk).append("\n");
            builder.append(">> Nomor Urut: ").append(Integer.parseInt(nomorStr));
        } else {
            throw new IllegalArgumentException("Kode NIM '" + kode + "' tidak dikenali.");
        }
        return builder.toString();
    }

    /**
     * Logika inti untuk Studi Kasus 2: Kalkulasi Nilai.
     *
     * @param input String dekode yang berisi data nilai.
     * @return String hasil format kalkulasi nilai.
     */
    private String processKalkulasiNilai(String input) {
        StringBuilder builder = new StringBuilder();
        try (Scanner scanner = new Scanner(input)) {
            scanner.useLocale(Locale.US);

            int bobotPA = scanner.nextInt();
            int bobotTugas = scanner.nextInt();
            int bobotKuis = scanner.nextInt();
            int bobotProyek = scanner.nextInt();
            int bobotUTS = scanner.nextInt();
            int bobotUAS = scanner.nextInt();
            scanner.nextLine();

            int totalPA = 0, maksPA = 0;
            int totalTugas = 0, maksTugas = 0;
            int totalKuis = 0, maksKuis = 0;
            int totalProyek = 0, maksProyek = 0;
            int totalUTS = 0, maksUTS = 0;
            int totalUAS = 0, maksUAS = 0;

            while (scanner.hasNextLine()) {
                String baris = scanner.nextLine().trim();
                if (baris.equals("---")) break;

                String[] bagian = baris.split("\\|");
                String kode = bagian[0];
                int nilaiMaks = Integer.parseInt(bagian[1]);
                int nilai = Integer.parseInt(bagian[2]);

                switch (kode) {
                    case "PA": maksPA += nilaiMaks; totalPA += nilai; break;
                    case "T": maksTugas += nilaiMaks; totalTugas += nilai; break;
                    case "K": maksKuis += nilaiMaks; totalKuis += nilai; break;
                    case "P": maksProyek += nilaiMaks; totalProyek += nilai; break;
                    case "UTS": maksUTS += nilaiMaks; totalUTS += nilai; break;
                    case "UAS": maksUAS += nilaiMaks; totalUAS += nilai; break;
                    default: break;
                }
            }

            double rataPA = (maksPA == 0) ? 0 : (totalPA * 100.0 / maksPA);
            double rataTugas = (maksTugas == 0) ? 0 : (totalTugas * 100.0 / maksTugas);
            double rataKuis = (maksKuis == 0) ? 0 : (totalKuis * 100.0 / maksKuis);
            double rataProyek = (maksProyek == 0) ? 0 : (totalProyek * 100.0 / maksProyek);
            double rataUTS = (maksUTS == 0) ? 0 : (totalUTS * 100.0 / maksUTS);
            double rataUAS = (maksUAS == 0) ? 0 : (totalUAS * 100.0 / maksUAS);

            int bulatPA = (int) Math.round(rataPA);
            int bulatTugas = (int) Math.round(rataTugas);
            int bulatKuis = (int) Math.round(rataKuis);
            int bulatProyek = (int) Math.round(rataProyek);
            int bulatUTS = (int) Math.round(rataUTS);
            int bulatUAS = (int) Math.round(rataUAS);

            double terbobotPA = (bulatPA / 100.0) * bobotPA;
            double terbobotTugas = (bulatTugas / 100.0) * bobotTugas;
            double terbobotKuis = (bulatKuis / 100.0) * bobotKuis;
            double terbobotProyek = (bulatProyek / 100.0) * bobotProyek;
            double terbobotUTS = (bulatUTS / 100.0) * bobotUTS;
            double terbobotUAS = (bulatUAS / 100.0) * bobotUAS;

            double nilaiAkhir = terbobotPA + terbobotTugas + terbobotKuis + 
                              terbobotProyek + terbobotUTS + terbobotUAS;

            builder.append("Hasil Kalkulasi Nilai:\n");
            builder.append(String.format(Locale.US, ">> Partisipasi: %d/100 (%.2f/%d)\n", bulatPA, terbobotPA, bobotPA));
            builder.append(String.format(Locale.US, ">> Tugas: %d/100 (%.2f/%d)\n", bulatTugas, terbobotTugas, bobotTugas));
            builder.append(String.format(Locale.US, ">> Kuis: %d/100 (%.2f/%d)\n", bulatKuis, terbobotKuis, bobotKuis));
            builder.append(String.format(Locale.US, ">> Proyek: %d/100 (%.2f/%d)\n", bulatProyek, terbobotProyek, bobotProyek));
            builder.append(String.format(Locale.US, ">> UTS: %d/100 (%.2f/%d)\n", bulatUTS, terbobotUTS, bobotUTS));
            builder.append(String.format(Locale.US, ">> UAS: %d/100 (%.2f/%d)\n", bulatUAS, terbobotUAS, bobotUAS));
            builder.append("\n");
            builder.append(String.format(Locale.US, ">> Nilai Akhir: %.2f\n", nilaiAkhir));
            builder.append(String.format(Locale.US, ">> Nilai Huruf: %s\n", konversiKeNilaiHuruf(nilaiAkhir)));
        }
        return builder.toString().trim();
    }

    /**
     * Logika inti untuk Studi Kasus 3: Analisis Matriks L.
     *
     * @param input String dekode yang berisi data matriks.
     * @return String hasil format analisis matriks.
     */
    private String processAnalisisMatriks(String input) {
        StringBuilder builder = new StringBuilder();
        try (Scanner scanner = new Scanner(input)) {
            int ukuranMatriks = scanner.nextInt();
            int[][] matriks = new int[ukuranMatriks][ukuranMatriks];
            for (int i = 0; i < ukuranMatriks; i++) {
                for (int j = 0; j < ukuranMatriks; j++) {
                    matriks[i][j] = scanner.nextInt();
                }
            }

            if (ukuranMatriks == 1) {
                int nilaiTengah = matriks[0][0];
                builder.append("Nilai L: Tidak Ditemukan\n");
                builder.append("Nilai L Terbalik: Tidak Ditemukan\n");
                builder.append("Nilai Pusat: ").append(nilaiTengah).append("\n");
                builder.append("Selisih: Tidak Ada\n");
                builder.append("Dominan: ").append(nilaiTengah);
                return builder.toString();
            }

            if (ukuranMatriks == 2) {
                int jumlah = 0;
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        jumlah += matriks[i][j];
                    }
                }
                builder.append("Nilai L: Tidak Ditemukan\n");
                builder.append("Nilai L Terbalik: Tidak Ditemukan\n");
                builder.append("Nilai Pusat: ").append(jumlah).append("\n");
                builder.append("Selisih: Tidak Ada\n");
                builder.append("Dominan: ").append(jumlah);
                return builder.toString();
            }

            int nilaiL = 0;
            for (int i = 0; i < ukuranMatriks; i++) {
                nilaiL += matriks[i][0];
            }
            for (int j = 1; j < ukuranMatriks - 1; j++) {
                nilaiL += matriks[ukuranMatriks - 1][j];
            }

            int nilaiLBalik = 0;
            for (int i = 0; i < ukuranMatriks; i++) {
                nilaiLBalik += matriks[i][ukuranMatriks - 1];
            }
            for (int j = 1; j < ukuranMatriks - 1; j++) {
                nilaiLBalik += matriks[0][j];
            }

            int nilaiPusat;
            if (ukuranMatriks % 2 == 1) {
                nilaiPusat = matriks[ukuranMatriks / 2][ukuranMatriks / 2];
            } else {
                int tengah1 = ukuranMatriks / 2 - 1;
                int tengah2 = ukuranMatriks / 2;
                nilaiPusat = matriks[tengah1][tengah1] + matriks[tengah1][tengah2] + 
                            matriks[tengah2][tengah1] + matriks[tengah2][tengah2];
            }

            int selisih = Math.abs(nilaiL - nilaiLBalik);
            int dominan = (selisih == 0) ? nilaiPusat : Math.max(nilaiL, nilaiLBalik);

            builder.append("Nilai L: ").append(nilaiL).append("\n");
            builder.append("Nilai L Terbalik: ").append(nilaiLBalik).append("\n");
            builder.append("Nilai Pusat: ").append(nilaiPusat).append("\n");
            builder.append("Selisih: ").append(selisih).append("\n");
            builder.append("Dominan: ").append(dominan);
        }
        return builder.toString().trim();
    }

    /**
     * Logika inti untuk Studi Kasus 4: Statistik Data.
     *
     * @param input String dekode yang berisi data angka.
     * @return String hasil format statistik.
     */
    private String processStatistikData(String input) {
        StringBuilder builder = new StringBuilder();
        try (Scanner scanner = new Scanner(input)) {
            List<Integer> angkaList = new ArrayList<>();
            while (scanner.hasNextInt()) {
                angkaList.add(scanner.nextInt());
            }

            if (angkaList.isEmpty()) {
                builder.append("Tidak ada data input");
                return builder.toString();
            }

            Map<Integer, Integer> frekuensi = new LinkedHashMap<>();
            int maksimum = Integer.MIN_VALUE, minimum = Integer.MAX_VALUE;
            int modus = 0, frekuensiModus = 0;

            for (int angka : angkaList) {
                frekuensi.put(angka, frekuensi.getOrDefault(angka, 0) + 1);
                int frekuensiSekarang = frekuensi.get(angka);
                if (frekuensiSekarang > frekuensiModus) {
                    frekuensiModus = frekuensiSekarang;
                    modus = angka;
                }
                if (angka > maksimum) maksimum = angka;
                if (angka < minimum) minimum = angka;
            }

            Set<Integer> tereliminasi = new TreeSet<>();
            int unik = -1;
            int index = 0;
            while (index < angkaList.size()) {
                int current = angkaList.get(index);
                if (tereliminasi.contains(current)) {
                    index++;
                    continue;
                }
                int nextIndex = index + 1;
                while (nextIndex < angkaList.size() && angkaList.get(nextIndex) != current) {
                    nextIndex++;
                }
                if (nextIndex < angkaList.size()) {
                    for (int k = index + 1; k < nextIndex; k++) {
                        tereliminasi.add(angkaList.get(k));
                    }
                    tereliminasi.add(current);
                    index = nextIndex + 1;
                } else {
                    unik = current;
                    break;
                }
            }

            if (unik == -1) {
                builder.append("Tidak ada angka unik tersisa");
                return builder.toString();
            }

            int nilaiJT = -1, countJT = -1;
            long produkJT = Long.MIN_VALUE;
            for (Map.Entry<Integer, Integer> entry : frekuensi.entrySet()) {
                int nilai = entry.getKey(), count = entry.getValue();
                long produk = (long) nilai * count;
                if (produk > produkJT || (produk == produkJT && nilai > nilaiJT)) {
                    produkJT = produk;
                    nilaiJT = nilai;
                    countJT = count;
                }
            }

            int nilaiJR = minimum;
            int countJR = frekuensi.get(minimum);
            long produkJR = (long) nilaiJR * countJR;

            builder.append("Maksimum: ").append(maksimum).append("\n");
            builder.append("Minimum: ").append(minimum).append("\n");
            builder.append("Modus: ").append(modus).append(" (").append(frekuensiModus).append("x)\n");
            builder.append("Unik: ").append(unik).append(" (").append(frekuensi.get(unik)).append("x)\n");
            builder.append("Jumlah Tertinggi: ").append(nilaiJT).append(" * ").append(countJT).append(" = ").append(produkJT).append("\n");
            builder.append("Jumlah Terendah: ").append(nilaiJR).append(" * ").append(countJR).append(" = ").append(produkJR);
        }
        return builder.toString().trim();
    }
}