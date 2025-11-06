package org.delcom.starter.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test class untuk HomeController.
 * <p>
 * Kelas ini memvalidasi kebenaran semua endpoint controller dengan menyediakan
 * berbagai macam input yang valid, tidak valid, dan kasus batas.
 * Tes ini diupdate untuk menangani ResponseEntity dan kode HttpStatus.
 */
class HomeControllerTest {

    private HomeController controller;

    @BeforeEach
    void initialize() {
        controller = new HomeController();
    }

    private String encodeToBase64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    // --- Tes Dasar ---

    @Test
    @DisplayName("Harus mengembalikan pesan pembuka yang tepat")
    void hello_ShouldReturnCorrectWelcomeMessage() {
        String actual = controller.salamPembuka();
        assertEquals("Halo Abdullah, selamat datang di pengembangan aplikasi Spring Boot!", actual);
    }

    @Test
    @DisplayName("Harus mengembalikan salam personal")
    void helloWithName_ShouldReturnCustomGreeting() {
        String actual = controller.ucapSalam("Abdullah");
        assertEquals("Halo, Abdullah!", actual);
    }

    // --- Tes untuk dataNim ---

    @Test
    @DisplayName("dataNim - NIM Valid (11S)")
    void dataNim_ValidCase() {
        String nim = "11S24007";
        String expectedOutput = """
                Informasi NIM 11S24007:\s
                >> Jurusan: S1 Informatika
                >> Tahun Masuk: 2024
                >> Nomor Urut: 1""";

        ResponseEntity<String> response = controller.dataNim(nim);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOutput, response.getBody());
    }

    @Test
    @DisplayName("dataNim - NIM dengan Panjang Salah")
    void dataNim_WrongLength() {
        String nim = "11S24";
        String expectedMessage = "Format NIM tidak valid. Harus terdiri dari 8 digit.";
        ResponseEntity<String> response = controller.dataNim(nim);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
    }

    @Test
    @DisplayName("dataNim - Kode NIM Tidak Dikenal")
    void dataNim_UnknownPrefix() {
        String nim = "99S24001";
        String expectedMessage = "Kode NIM '99S' tidak dikenali.";
        ResponseEntity<String> response = controller.dataNim(nim);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
    }

    @Test
    @DisplayName("dataNim - Error Parsing Input (Memicu Blok Catch)")
    void dataNim_ParsingError() {
        String nim = "11SXX001";
        ResponseEntity<String> response = controller.dataNim(nim);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("For input string: \"XX\""));
    }

    // --- Tes untuk kalkulasiNilai ---

    @Test
    @DisplayName("kalkulasiNilai - Skenario Perhitungan Lengkap (Nilai A)")
    void kalkulasiNilai_CompleteCalculation() {
        String inputData = String.join("\n",
                "10 15 10 15 20 30",
                "PA|100|80", "T|100|90", "K|100|85", "P|100|95", "UTS|100|75", "UAS|100|88", "---"
        );
        String encodedInput = encodeToBase64(inputData);

        String expectedOutput = """
                Hasil Kalkulasi Nilai:
                >> Partisipasi: 80/100 (8.00/10)
                >> Tugas: 90/100 (13.50/15)
                >> Kuis: 85/100 (8.50/10)
                >> Proyek: 95/100 (14.25/15)
                >> UTS: 75/100 (15.00/20)
                >> UAS: 88/100 (26.40/30)

                >> Nilai Akhir: 85.65
                >> Nilai Huruf: A""";

        ResponseEntity<String> response = controller.kalkulasiNilai(encodedInput);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOutput, response.getBody());
    }

    @Test
    @DisplayName("kalkulasiNilai - Skenario Nilai AB")
    void kalkulasiNilai_ABGradeScenario() {
        String inputData = String.join("\n", 
            "10 15 10 15 20 30", 
            "PA|100|75", "T|100|75", "K|100|75", "P|100|75", "UTS|100|75", "UAS|100|75", "---");
        String encodedInput = encodeToBase64(inputData);
        ResponseEntity<String> response = controller.kalkulasiNilai(encodedInput);
        String responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 75.00"));
        assertTrue(responseBody.contains(">> Nilai Huruf: AB"));
    }

    @Test
    @DisplayName("kalkulasiNilai - Skenario Nilai B")
    void kalkulasiNilai_BGradeScenario() {
        String inputData = String.join("\n", 
            "10 15 10 15 20 30", 
            "PA|100|65", "T|100|65", "K|100|65", "P|100|65", "UTS|100|65", "UAS|100|65", "---");
        String encodedInput = encodeToBase64(inputData);
        ResponseEntity<String> response = controller.kalkulasiNilai(encodedInput);
        String responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 65.00"));
        assertTrue(responseBody.contains(">> Nilai Huruf: B"));
    }
    
    @Test
    @DisplayName("kalkulasiNilai - Skenario Nilai BC")
    void kalkulasiNilai_BCGradeScenario() {
        String inputData = String.join("\n", 
            "10 15 10 15 20 30", 
            "PA|100|60", "T|100|60", "K|100|60", "P|100|60", "UTS|100|60", "UAS|100|60", "---");
        String encodedInput = encodeToBase64(inputData);
        ResponseEntity<String> response = controller.kalkulasiNilai(encodedInput);
        String responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 60.00"));
        assertTrue(responseBody.contains(">> Nilai Huruf: BC"));
    }

    @Test
    @DisplayName("kalkulasiNilai - Skenario Nilai C")
    void kalkulasiNilai_CGradeScenario() {
        String inputData = String.join("\n", 
            "10 15 10 15 20 30", 
            "PA|100|50", "T|100|50", "K|100|50", "P|100|50", "UTS|100|50", "UAS|100|50", "---");
        String encodedInput = encodeToBase64(inputData);
        ResponseEntity<String> response = controller.kalkulasiNilai(encodedInput);
        String responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 50.00"));
        assertTrue(responseBody.contains(">> Nilai Huruf: C"));
    }

    @Test
    @DisplayName("kalkulasiNilai - Skenario Nilai D")
    void kalkulasiNilai_DGradeScenario() {
        String inputData = String.join("\n", 
            "10 15 10 15 20 30", 
            "PA|100|40", "T|100|40", "K|100|40", "P|100|40", "UTS|100|40", "UAS|100|40", "---");
        String encodedInput = encodeToBase64(inputData);
        ResponseEntity<String> response = controller.kalkulasiNilai(encodedInput);
        String responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 40.00"));
        assertTrue(responseBody.contains(">> Nilai Huruf: D"));
    }

    @Test
    @DisplayName("kalkulasiNilai - Skenario Input Minimal")
    void kalkulasiNilai_MinimalInput() {
        String inputData = String.join("\n", 
            "10 15 10 15 20 30", 
            "T|100|90", "UTS|100|50", "---");
        String encodedInput = encodeToBase64(inputData);
        ResponseEntity<String> response = controller.kalkulasiNilai(encodedInput);
        String responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(responseBody.contains(">> Partisipasi: 0/100 (0.00/10)"));
        assertTrue(responseBody.contains(">> Kuis: 0/100 (0.00/10)"));
        assertTrue(responseBody.contains(">> Nilai Akhir: 23.50"));
        assertTrue(responseBody.contains(">> Nilai Huruf: E"));
    }

    @Test
    @DisplayName("kalkulasiNilai - Skenario Simbol Tidak Dikenal")
    void kalkulasiNilai_UnknownSymbol() {
        String inputData = String.join("\n", 
            "10 15 10 15 20 30", 
            "PA|100|80", "XYZ|100|100", "---");
        String encodedInput = encodeToBase64(inputData);
        ResponseEntity<String> response = controller.kalkulasiNilai(encodedInput);
        String responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 8.00"));
    }

    @Test
    @DisplayName("kalkulasiNilai - Skenario Input Hanya Bobot")
    void kalkulasiNilai_WeightOnlyInput() {
        String inputData = "10 15 10 15 20 30\n"; 
        String encodedInput = encodeToBase64(inputData);
        
        ResponseEntity<String> response = controller.kalkulasiNilai(encodedInput);
        String responseBody = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(responseBody.contains(">> Nilai Akhir: 0.00"));
        assertTrue(responseBody.contains(">> Nilai Huruf: E"));
    }

    @Test
    @DisplayName("kalkulasiNilai - Input Format Salah (Memicu Blok Catch)")
    void kalkulasiNilai_MalformedInput() {
        String encodedInput = encodeToBase64("halo");
        String expectedErrorMessage = "Struktur data input tidak valid atau tidak lengkap. Pastikan angka dan format sesuai.";
        ResponseEntity<String> response = controller.kalkulasiNilai(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedErrorMessage, response.getBody());
    }

    @Test
    @DisplayName("kalkulasiNilai - Input Base64 Rusak")
    void kalkulasiNilai_CorruptedBase64() {
        String invalidBase64Data = "!!INVALID_BASE64!!";
        String expectedErrorMessage = "Input Base64 tidak valid.";
        ResponseEntity<String> response = controller.kalkulasiNilai(invalidBase64Data);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().startsWith(expectedErrorMessage));
    }

    // --- Tes untuk analisisMatriksL ---

    @Test
    @DisplayName("analisisMatriksL - Matriks 3x3 (Ganjil, Dominan=Tengah)")
    void analisisMatriksL_3x3Matrix() {
        String matrixData = String.join("\n", 
            "3", "1 2 3", "4 5 6", "7 8 9");
        String encodedInput = encodeToBase64(matrixData);
        String expectedOutput = """
                Nilai L: 20
                Nilai L Terbalik: 20
                Nilai Pusat: 5
                Selisih: 0
                Dominan: 5""";
        ResponseEntity<String> response = controller.analisisMatriksL(encodedInput);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOutput, response.getBody());
    }
    
    @Test
    @DisplayName("analisisMatriksL - Matriks 4x4 (Genap, Dominan=L)")
    void analisisMatriksL_4x4Matrix() {
        String matrixData = String.join("\n", 
            "4", "1 2 3 4", "5 6 7 8", "9 10 11 12", "13 14 15 16");
        String encodedInput = encodeToBase64(matrixData);
        String expectedOutput = """
                Nilai L: 57
                Nilai L Terbalik: 45
                Nilai Pusat: 34
                Selisih: 12
                Dominan: 57""";
        ResponseEntity<String> response = controller.analisisMatriksL(encodedInput);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOutput, response.getBody());
    }

    @Test
    @DisplayName("analisisMatriksL - Matriks 1x1 (Kasus Batas)")
    void analisisMatriksL_1x1Matrix() {
        String encodedInput = encodeToBase64("1\n42");
        String expectedOutput = """
                Nilai L: Tidak Ditemukan
                Nilai L Terbalik: Tidak Ditemukan
                Nilai Pusat: 42
                Selisih: Tidak Ada
                Dominan: 42""";
        ResponseEntity<String> response = controller.analisisMatriksL(encodedInput);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOutput, response.getBody());
    }

    @Test
    @DisplayName("analisisMatriksL - Matriks 2x2 (Kasus Batas)")
    void analisisMatriksL_2x2Matrix() {
        String encodedInput = encodeToBase64("2\n1 2\n3 4");
        String expectedOutput = """
                Nilai L: Tidak Ditemukan
                Nilai L Terbalik: Tidak Ditemukan
                Nilai Pusat: 10
                Selisih: Tidak Ada
                Dominan: 10""";
        ResponseEntity<String> response = controller.analisisMatriksL(encodedInput);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOutput, response.getBody());
    }

    @Test
    @DisplayName("analisisMatriksL - Input Matriks Rusak (Memicu Catch)")
    void analisisMatriksL_InvalidMatrixData() {
        String encodedInput = encodeToBase64("abc");
        String expectedErrorMessage = "Format data matriks tidak valid atau tidak lengkap.";
        ResponseEntity<String> response = controller.analisisMatriksL(encodedInput);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedErrorMessage, response.getBody());
    }

    @Test
    @DisplayName("analisisMatriksL - Input Base64 Rusak")
    void analisisMatriksL_CorruptedBase64() {
        String invalidBase64Data = "!!INVALID_BASE64!!";
        String expectedErrorMessage = "Input Base64 tidak valid.";
        ResponseEntity<String> response = controller.analisisMatriksL(invalidBase64Data);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().startsWith(expectedErrorMessage));
    }

    // --- Tes untuk statistikData ---

    @Test
    @DisplayName("statistikData - Skenario Normal")
    void statistikData_NormalScenario() {
        String encodedInput = encodeToBase64("10 5 8 10 9 5 10 8 7");
        String expectedOutput = """
                Maksimum: 10
                Minimum: 5
                Modus: 10 (3x)
                Unik: 9 (1x)
                Jumlah Tertinggi: 10 * 3 = 30
                Jumlah Terendah: 5 * 2 = 10""";
        ResponseEntity<String> response = controller.statistikData(encodedInput);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOutput, response.getBody());
    }

    @Test
    @DisplayName("statistikData - Input Kosong (Kasus Batas)")
    void statistikData_EmptyInputCase() {
        String encodedInput = encodeToBase64("");
        String expectedOutput = "Tidak ada data input";
        ResponseEntity<String> response = controller.statistikData(encodedInput);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOutput, response.getBody());
    }

    @Test
    @DisplayName("statistikData - Tidak Ada Nilai Unik (Kasus Batas)")
    void statistikData_NoUniqueValue() {
        String encodedInput = encodeToBase64("10 20 10 20");
        String expectedOutput = "Tidak ada angka unik tersisa";
        ResponseEntity<String> response = controller.statistikData(encodedInput);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOutput, response.getBody());
    }

    @Test
    @DisplayName("statistikData - Skenario Tie-Breaker Jumlah Tertinggi (Menang)")
    void statistikData_HighestSumTieBreak_Wins() {
        String encodedInput = encodeToBase64("10 20 10 9"); 
        ResponseEntity<String> response = controller.statistikData(encodedInput);
        String responseBody = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(responseBody.contains("Jumlah Tertinggi: 20 * 1 = 20"));
        assertTrue(responseBody.contains("Unik: 9 (1x)"));
    }

    @Test
    @DisplayName("statistikData - Skenario Tie-Breaker Jumlah Tertinggi (Kalah)")
    void statistikData_HighestSumTieBreak_Loses() {
        String encodedInput = encodeToBase64("20 10 10 9"); 
        ResponseEntity<String> response = controller.statistikData(encodedInput);
        String responseBody = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(responseBody.contains("Jumlah Tertinggi: 20 * 1 = 20"));
        assertTrue(responseBody.contains("Unik: 20 (1x)"));
    }

    @Test
    @DisplayName("statistikData - Input Teks (Bukan Numerik)")
    void statistikData_NonNumericInput() {
        String encodedInput = encodeToBase64("abc");
        String expectedOutput = "Tidak ada data input";
        ResponseEntity<String> response = controller.statistikData(encodedInput);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOutput, response.getBody());
    }

    @Test
    @DisplayName("statistikData - Input Base64 Rusak (Memicu Catch)")
    void statistikData_CorruptedBase64() {
        String invalidBase64Data = "!!INVALID_BASE64!!";
        String expectedErrorMessage = "Input Base64 tidak valid.";
        ResponseEntity<String> response = controller.statistikData(invalidBase64Data);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().startsWith(expectedErrorMessage));
    }
}