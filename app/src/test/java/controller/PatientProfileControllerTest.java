package controller;

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
        
        // --- Tes Kegagalan untuk Phone ---
        @Test
        @DisplayName("Gagal: Melempar exception jika phone null")
        void testValidateAccountInfo_WhenPhoneIsNull_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateAccountInfo("userValid", "Nama Valid", "valid@email.com", null, "password");
            });
            assertEquals("Nomor telepon tidak boleh kosong.", exception.getMessage());
        }

        @Test
        @DisplayName("Gagal: Melempar exception jika phone kosong")
        void testValidateAccountInfo_WhenPhoneIsBlank_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateAccountInfo("userValid", "Nama Valid", "valid@email.com", "  ", "password");
            });
            assertEquals("Nomor telepon tidak boleh kosong.", exception.getMessage());
        }

        // --- Tes Kegagalan untuk Password ---
        @Test
        @DisplayName("Gagal: Melempar exception jika password null")
        void testValidateAccountInfo_WhenPasswordIsNull_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateAccountInfo("userValid", "Nama Valid", "valid@email.com", "08123", null);
            });
            assertEquals("Password tidak boleh kosong.", exception.getMessage());
        }

        @Test
        @DisplayName("Gagal: Melempar exception jika password kosong")
        void testValidateAccountInfo_WhenPasswordIsBlank_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateAccountInfo("userValid", "Nama Valid", "valid@email.com", "08123", "  ");
            });
            assertEquals("Password tidak boleh kosong.", exception.getMessage());
        }
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

        @Test
        @DisplayName("Gagal: Melempar exception jika golongan darah kosong")
        void testValidateMedicalInfo_WhenGolonganDarahIsBlank_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateMedicalInfo("  ", "Debu", "75", "180");
            });
            assertEquals("Golongan darah harus dipilih.", exception.getMessage());
        }

        // --- Tes Kegagalan untuk Riwayat Alergi ---
        @Test
        @DisplayName("Gagal: Melempar exception jika riwayat alergi null")
        void testValidateMedicalInfo_WhenRiwayatAlergiIsNull_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateMedicalInfo("A", null, "75", "180");
            });
            assertEquals("Riwayat alergi tidak boleh kosong.", exception.getMessage());
        }

        @Test
        @DisplayName("Gagal: Melempar exception jika riwayat alergi kosong")
        void testValidateMedicalInfo_WhenRiwayatAlergiIsBlank_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateMedicalInfo("A", "  ", "75", "180");
            });
            assertEquals("Riwayat alergi tidak boleh kosong.", exception.getMessage());
        }
        
        // --- Tes Kegagalan untuk Berat Badan ---
        @Test
        @DisplayName("Gagal: Melempar exception jika berat badan null")
        void testValidateMedicalInfo_WhenBeratBadanIsNull_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateMedicalInfo("A", "Debu", null, "180");
            });
            assertEquals("Berat badan tidak boleh kosong.", exception.getMessage());
        }

        @Test
        @DisplayName("Gagal: Melempar exception jika berat badan kosong")
        void testValidateMedicalInfo_WhenBeratBadanIsBlank_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateMedicalInfo("A", "Debu", "  ", "180");
            });
            assertEquals("Berat badan tidak boleh kosong.", exception.getMessage());
        }

        // --- Tes Kegagalan untuk Tinggi Badan ---
        @Test
        @DisplayName("Gagal: Melempar exception jika tinggi badan null")
        void testValidateMedicalInfo_WhenTinggiBadanIsNull_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateMedicalInfo("A", "Debu", "75", null);
            });
            assertEquals("Tinggi badan tidak boleh kosong.", exception.getMessage());
        }

        @Test
        @DisplayName("Gagal: Melempar exception jika tinggi badan kosong")
        void testValidateMedicalInfo_WhenTinggiBadanIsBlank_ShouldThrowException() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                profileController.validateMedicalInfo("A", "Debu", "75", "  ");
            });
            assertEquals("Tinggi badan tidak boleh kosong.", exception.getMessage());
        }
    }
}