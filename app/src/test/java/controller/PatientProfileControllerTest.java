package controller; // Sesuaikan dengan nama package Anda

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProfileControllerTest {

    private PatientProfileController profileController;

    @BeforeEach
    void setUp() {
        // Membuat instance baru sebelum setiap tes dijalankan
        profileController = new PatientProfileController();
    }

    // Menggunakan @Nested untuk mengelompokkan tes agar lebih rapi
    @Nested
    @DisplayName("Tes untuk Validasi Info Akun")
    class AccountInfoValidationTests {

        // =============================================
        // === 1. SKENARIO SUKSES (HAPPY PATH) ===
        // =============================================
        @Test
        @DisplayName("Sukses: Tidak melempar exception jika semua data valid")
        void testValidateAccountInfo_WithValidData_ShouldNotThrowException() {
            assertDoesNotThrow(() -> {
                profileController.validateAccountInfo("userValid", "Nama Lengkap Valid", "valid@email.com", "08123456789", "passwordValid");
            });
        }

        // =============================================
        // === 2. SEMUA SKENARIO GAGAL (UNHAPPY PATHS) ===
        // =============================================

        // --- Tes Kegagalan untuk Username ---
        @Test
        @DisplayName("Gagal: Melempar exception jika username null")
        void testValidateAccountInfo_WhenUsernameIsNull_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateAccountInfo(null, "Nama Valid", "valid@email.com", "08123", "password");
            });
            assertEquals("Username tidak boleh kosong.", exception.getMessage());
        }

        @Test
        @DisplayName("Gagal: Melempar exception jika username hanya spasi")
        void testValidateAccountInfo_WhenUsernameIsBlank_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateAccountInfo("   ", "Nama Valid", "valid@email.com", "08123", "password");
            });
            assertEquals("Username tidak boleh kosong.", exception.getMessage());
        }

        // --- Tes Kegagalan untuk Nama Lengkap ---
        @Test
        @DisplayName("Gagal: Melempar exception jika nama lengkap null")
        void testValidateAccountInfo_WhenFullnameIsNull_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateAccountInfo("userValid", null, "valid@email.com", "08123", "password");
            });
            assertEquals("Nama lengkap tidak boleh kosong.", exception.getMessage());
        }

        @Test
        @DisplayName("Gagal: Melempar exception jika nama lengkap kosong")
        void testValidateAccountInfo_WhenFullnameIsBlank_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateAccountInfo("userValid", "", "valid@email.com", "08123", "password");
            });
            assertEquals("Nama lengkap tidak boleh kosong.", exception.getMessage());
        }

        // --- Tes Kegagalan untuk Email ---
        @Test
        @DisplayName("Gagal: Melempar exception jika email null")
        void testValidateAccountInfo_WhenEmailIsNull_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateAccountInfo("userValid", "Nama Valid", null, "08123", "password");
            });
            assertEquals("Email tidak boleh kosong.", exception.getMessage());
        }

        @Test
        @DisplayName("Gagal: Melempar exception jika email kosong")
        void testValidateAccountInfo_WhenEmailIsBlank_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateAccountInfo("userValid", "Nama Valid", "", "08123", "password");
            });
            assertEquals("Email tidak boleh kosong.", exception.getMessage());
        }
        
        // --- (Lanjutkan pola yang sama untuk 'phone' dan 'password') ---

    }

    @Nested
    @DisplayName("Tes untuk Validasi Info Medis")
    class MedicalInfoValidationTests {

        // =============================================
        // === 1. SKENARIO SUKSES (HAPPY PATH) ===
        // =============================================
        @Test
        @DisplayName("Sukses: Tidak melempar exception jika semua data medis valid")
        void testValidateMedicalInfo_WithValidData_ShouldNotThrowException() {
            assertDoesNotThrow(() -> {
                profileController.validateMedicalInfo("O", "Debu", "75", "180");
            });
        }

        // =============================================
        // === 2. SEMUA SKENARIO GAGAL (UNHAPPY PATHS) ===
        // =============================================

        // --- Tes Kegagalan untuk Golongan Darah ---
        @Test
        @DisplayName("Gagal: Melempar exception jika golongan darah null")
        void testValidateMedicalInfo_WhenGolonganDarahIsNull_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateMedicalInfo(null, "Debu", "75", "180");
            });
            assertEquals("Golongan darah harus dipilih.", exception.getMessage());
        }

        // --- Tes Kegagalan untuk Riwayat Alergi ---
        @Test
        @DisplayName("Gagal: Melempar exception jika riwayat alergi kosong")
        void testValidateMedicalInfo_WhenRiwayatAlergiIsBlank_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateMedicalInfo("A", "  ", "75", "180");
            });
            assertEquals("Riwayat alergi tidak boleh kosong.", exception.getMessage());
        }
        
        // --- (Lanjutkan pola yang sama untuk 'beratBadan' dan 'tinggiBadan') ---

    }
}