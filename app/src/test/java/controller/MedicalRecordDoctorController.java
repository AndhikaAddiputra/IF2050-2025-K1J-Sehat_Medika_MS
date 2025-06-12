package controller; 

import model.dao.MedicalRecordDAO;
import model.dao.PatientDAO;
import model.entity.MedicalRecord;
import model.entity.Doctor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordDoctorControllerTest {

    // Siapkan mock untuk semua dependensi DAO yang digunakan
    @Mock
    private MedicalRecordDAO medicalRecordDAO;
    @Mock
    private PatientDAO patientDAO;
    // DoctorDAO tidak perlu di-mock jika tidak ada metodenya yang dipanggil langsung dalam logika tes

    // Suntikkan semua mock di atas ke dalam instance controller
    @InjectMocks
    private MedicalRecordDoctorController controller;

    private Doctor currentDoctor;

    @BeforeEach
    void setUp() {
        // Siapkan data dasar untuk setiap tes
        currentDoctor = new Doctor();
        currentDoctor.setDoctorId("D001");
        
        // Atur state internal controller seolah-olah dokter sudah login
        // Ini membutuhkan akses ke field, jadi kita bisa gunakan setter atau membuatnya package-private
        // Untuk contoh ini, kita asumsikan bisa mengaturnya.
        controller.currentDoctor = currentDoctor;
    }

    // --- Tes untuk Logika Validasi ---

    @Test
    @DisplayName("Validasi form tambah harus berhasil jika semua data diisi")
    void testValidateFormForAdd_WithValidData_ShouldReturnNull() {
        // ACT
        String result = controller.validateFormForAdd("Flu", "Pusing, Batuk", "Istirahat");
        // ASSERT
        assertNull(result, "Seharusnya tidak ada pesan error jika semua data valid");
    }

    @Test
    @DisplayName("Validasi form tambah harus gagal jika diagnosis kosong")
    void testValidateFormForAdd_WhenDiagnosisIsBlank_ShouldReturnErrorMessage() {
        // ACT
        String result = controller.validateFormForAdd("  ", "Pusing, Batuk", "Istirahat");
        // ASSERT
        assertEquals("Diagnosis tidak boleh kosong!", result);
    }
    
    // --- Tes untuk Logika Update ---

    @Test
    @DisplayName("Update rekam medis harus memanggil DAO dengan data yang benar")
    void testHandleUpdateRecord_WithValidData_ShouldCallUpdateDao() throws SQLException {
        // ARRANGE
        // 1. Siapkan data rekam medis yang akan 'dipilih'
        MedicalRecord selectedRecord = new MedicalRecord();
        selectedRecord.setRecordID(101);
        selectedRecord.setDoctorId(currentDoctor.getDoctorId());
        selectedRecord.setPatientId("P001");
        selectedRecord.setDiagnosis("Demam Awal");
        
        // Atur state internal controller seolah-olah record ini sudah dipilih
        controller.selectedRecord = selectedRecord;

        // 2. Siapkan data baru dari form
        String newDiagnosis = "Demam Lanjutan";
        String newSymptoms = "Suhu tinggi";
        String newTreatment = "Obat penurun panas";
        String newNotes = "Perlu observasi";

        // 3. Siapkan ArgumentCaptor untuk menangkap objek yang dikirim ke DAO
        ArgumentCaptor<MedicalRecord> recordCaptor = ArgumentCaptor.forClass(MedicalRecord.class);
        
        // ACT
        // Panggil logika inti dari handleUpdateRecord. Kita buat metode baru yang bisa diuji.
        // Asumsikan kita refactor handleUpdateRecord menjadi seperti ini:
        controller.performUpdate(newDiagnosis, newSymptoms, newTreatment, newNotes);
        
        // ASSERT
        // 1. Verifikasi bahwa metode updateMedicalRecord di DAO dipanggil tepat 1 kali
        verify(medicalRecordDAO, times(1)).updateMedicalRecord(recordCaptor.capture());

        // 2. Tangkap objek yang dikirim dan periksa isinya
        MedicalRecord capturedRecord = recordCaptor.getValue();
        assertEquals(101, capturedRecord.getRecordID()); // Pastikan ID tetap sama
        assertEquals("Demam Lanjutan", capturedRecord.getDiagnosis());
        assertEquals("Suhu tinggi", capturedRecord.getSymptoms());
        assertEquals("Obat penurun panas", capturedRecord.getTreatment());
        assertEquals("Perlu observasi", capturedRecord.getNotes());
    }
}
