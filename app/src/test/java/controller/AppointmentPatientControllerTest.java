package controller; // Sesuaikan dengan package Anda

import model.entity.Appointment;
import model.entity.AppointmentStatus;
import javafx.scene.control.ToggleButton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class AppointmentPatientControllerTest {
    // Static block to initialize JavaFX Platform for tests
    static {
        try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // JavaFX already started, ignore
        }
    }

    // Objek Mock untuk dependensi eksternal dan komponen UI
    private ToggleButton aktifToggle;
    private ToggleButton selesaiToggle;

    // @InjectMocks akan membuat instance AppointmentPatientController
    // dan menyuntikkan semua @Mock di atas ke dalamnya.
    private AppointmentPatientController controller;

    // Variabel untuk menyimpan data tes
    private List<Appointment> semuaJanjiTemu;
    private Appointment janjiTemuAktif_Accepted;
    private Appointment janjiTemuAktif_Requested;
    private Appointment janjiTemuSelesai;

    @BeforeEach
    void setUp() {
        // Gunakan ToggleButton asli
        aktifToggle = new ToggleButton();
        selesaiToggle = new ToggleButton();
        controller = new AppointmentPatientController();
        // Inject toggles into controller via reflection (since fields are private)
        try {
            java.lang.reflect.Field aktifField = AppointmentPatientController.class.getDeclaredField("aktifToggle");
            aktifField.setAccessible(true);
            aktifField.set(controller, aktifToggle);
            java.lang.reflect.Field selesaiField = AppointmentPatientController.class.getDeclaredField("selesaiToggle");
            selesaiField.setAccessible(true);
            selesaiField.set(controller, selesaiToggle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // ARRANGE: Siapkan data tes yang relevan
        
        // Janji temu aktif yang akan datang (status ACCEPTED)
        // -> Harus muncul di filter "Aktif"
        janjiTemuAktif_Accepted = new Appointment();
        janjiTemuAktif_Accepted.setAppointmentId(1);
        janjiTemuAktif_Accepted.setAppointmentStatus(AppointmentStatus.ACCEPTED);
        janjiTemuAktif_Accepted.setAppointmentDate(LocalDateTime.now().plusDays(5));

        // Janji temu aktif yang akan datang (status REQUESTED)
        // -> Harus muncul di filter "Aktif"
        janjiTemuAktif_Requested = new Appointment();
        janjiTemuAktif_Requested.setAppointmentId(2);
        janjiTemuAktif_Requested.setAppointmentStatus(AppointmentStatus.REQUESTED);
        janjiTemuAktif_Requested.setAppointmentDate(LocalDateTime.now().plusDays(3));

        // Janji temu selesai di masa lalu (status ACCEPTED)
        // -> Harus muncul di filter "Selesai"
        janjiTemuSelesai = new Appointment();
        janjiTemuSelesai.setAppointmentId(3);
        janjiTemuSelesai.setAppointmentStatus(AppointmentStatus.ACCEPTED);
        janjiTemuSelesai.setAppointmentDate(LocalDateTime.now().minusDays(5));
        
        // Gabungkan semua data tes ke dalam satu list
        semuaJanjiTemu = Arrays.asList(janjiTemuAktif_Accepted, janjiTemuAktif_Requested, janjiTemuSelesai);
    }

    @Test
    @DisplayName("Filter 'Aktif' harus mengembalikan janji temu di masa depan dengan status REQUESTED atau ACCEPTED")
    void testFilterAppointments_WhenAktifIsSelected_ShouldReturnActiveAppointments() {
        // Gunakan ToggleButton asli
        aktifToggle.setSelected(true);
        selesaiToggle.setSelected(false);
        List<Appointment> hasilFilter = controller.filterAppointments(semuaJanjiTemu);

        // ASSERT: Periksa apakah hasilnya sesuai harapan
        assertEquals(2, hasilFilter.size(), "Harus ada 2 janji temu yang aktif");
        assertTrue(hasilFilter.contains(janjiTemuAktif_Accepted), "Harus berisi janji temu ACCEPTED di masa depan");
        assertTrue(hasilFilter.contains(janjiTemuAktif_Requested), "Harus berisi janji temu REQUESTED di masa depan");
    }

    @Test
    @DisplayName("Filter 'Selesai' harus mengembalikan janji temu di masa lalu dengan status ACCEPTED")
    void testFilterAppointments_WhenSelesaiIsSelected_ShouldReturnCompletedAppointments() {
        aktifToggle.setSelected(false);
        selesaiToggle.setSelected(true);
        List<Appointment> hasilFilter = controller.filterAppointments(semuaJanjiTemu);

        // ASSERT: Periksa apakah hasilnya sesuai harapan
        assertEquals(1, hasilFilter.size(), "Hanya boleh ada 1 janji temu yang selesai");
        assertTrue(hasilFilter.contains(janjiTemuSelesai), "Daftar hasil harus berisi janji temu yang sudah lewat");
    }
}