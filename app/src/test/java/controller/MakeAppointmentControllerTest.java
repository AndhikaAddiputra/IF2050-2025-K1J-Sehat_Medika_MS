package controller; 

import model.dao.AppointmentDAO;
import model.entity.Appointment;
import model.entity.Doctor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MakeAppointmentControllerTest {

    // Siapkan mock untuk semua dependensi DAO
    @Mock
    private AppointmentDAO appointmentDAO;
    // Kita tidak butuh mock DoctorDAO, PatientDAO, UserDAO untuk tes ini
    // tapi jika dibutuhkan, bisa ditambahkan dengan cara yang sama.

    // Suntikkan mock di atas ke dalam instance controller
    @InjectMocks
    private MakeAppointmentController controller;

    private Doctor validDoctor;
    private LocalDate futureDate;
    private String validTime;
    private String validSpecialization;

    @BeforeEach
    void setUp() {
        // Siapkan data valid yang akan sering digunakan
        validDoctor = new Doctor(); // Cukup objek kosong untuk tes validasi
        futureDate = LocalDate.now().plusDays(1);
        validTime = "10:00";
        validSpecialization = "Cardiology";
    }

    // --- Tes untuk Logika Validasi (validateInput) ---

    @Test
    @DisplayName("Validasi harus berhasil jika semua input valid")
    void testValidateInput_WithValidInputs_ShouldReturnNull() {
        // ACT
        String result = controller.validateInput(validSpecialization, validDoctor, futureDate, validTime);
        // ASSERT
        assertNull(result, "Seharusnya tidak ada pesan error jika semua input valid");
    }

    @Test
    @DisplayName("Validasi harus gagal jika dokter tidak dipilih (null)")
    void testValidateInput_WhenDoctorIsNull_ShouldReturnErrorMessage() {
        String result = controller.validateInput(validSpecialization, null, futureDate, validTime);
        assertEquals("Please select a doctor.", result);
    }

    @Test
    @DisplayName("Validasi harus gagal jika tanggal adalah masa lalu")
    void testValidateInput_WhenDateIsInThePast_ShouldReturnErrorMessage() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        String result = controller.validateInput(validSpecialization, validDoctor, pastDate, validTime);
        assertEquals("Please select a future date.", result);
    }

    // (Anda bisa melanjutkan pola yang sama untuk menguji setiap kondisi di validateInput)

    // --- Tes untuk Logika Nomor Antrean (getNextQueueNumber) ---

    @Test
    @DisplayName("getNextQueueNumber harus mengembalikan 1 jika tidak ada janji temu di tanggal tersebut")
    void testGetNextQueueNumber_WhenNoAppointmentsExist_ShouldReturnOne() throws SQLException {
        // ARRANGE: Atur mock DAO agar mengembalikan list kosong
        when(appointmentDAO.getAppointmentsByDate(any())).thenReturn(Collections.emptyList());

        // ACT: Panggil metode yang diuji (setelah mengubahnya menjadi tidak private)
        int queueNumber = controller.getNextQueueNumber(LocalDate.now());

        // ASSERT
        assertEquals(1, queueNumber);
    }

    @Test
    @DisplayName("getNextQueueNumber harus mengembalikan nomor berikutnya jika sudah ada janji temu")
    void testGetNextQueueNumber_WhenAppointmentsExist_ShouldReturnNextNumber() throws SQLException {
        // ARRANGE: Atur mock DAO agar mengembalikan list berisi 3 appointment
        List<Appointment> existingAppointments = new ArrayList<>();
        existingAppointments.add(new Appointment());
        existingAppointments.add(new Appointment());
        existingAppointments.add(new Appointment());
        when(appointmentDAO.getAppointmentsByDate(any())).thenReturn(existingAppointments);

        // ACT
        int queueNumber = controller.getNextQueueNumber(LocalDate.now());

        // ASSERT
        assertEquals(4, queueNumber, "Nomor antrean seharusnya ukuran list + 1");
    }
}