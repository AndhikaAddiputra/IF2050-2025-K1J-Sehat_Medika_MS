package controller; // Sesuaikan dengan package Anda

import model.entity.Medication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MedicationPharmacistControllerTest {

    @InjectMocks
    private MedicationPharmacistController controller;

    private List<Medication> semuaObat;
    private Medication obatStokRendah;
    private Medication obatHampirKadaluarsa;
    private Medication obatAntibiotik;
    private Medication obatAnalgesik;
    private Medication obatNormal;

    @BeforeEach
    void setUp() {
        
        obatStokRendah = new Medication();
        obatStokRendah.setName("Ibuprofen");
        obatStokRendah.setStockQuantity(10);
        obatStokRendah.setMinStockLevel(20); // Stok lebih rendah dari min
        obatStokRendah.setExpiryDate(LocalDateTime.now().plusYears(1));
        obatStokRendah.setCategory("Analgesic");

        obatHampirKadaluarsa = new Medication();
        obatHampirKadaluarsa.setName("Amoxicillin");
        obatHampirKadaluarsa.setStockQuantity(100);
        obatHampirKadaluarsa.setMinStockLevel(20);
        obatHampirKadaluarsa.setExpiryDate(LocalDateTime.now().plusDays(15)); // Akan kadaluarsa dalam 15 hari
        obatHampirKadaluarsa.setCategory("Antibiotic");

        obatAntibiotik = new Medication();
        obatAntibiotik.setName("Ciprofloxacin");
        obatAntibiotik.setStockQuantity(50);
        obatAntibiotik.setMinStockLevel(10);
        obatAntibiotik.setExpiryDate(LocalDateTime.now().plusYears(1));
        obatAntibiotik.setCategory("Antibiotic");

        obatAnalgesik = new Medication();
        obatAnalgesik.setName("Paracetamol");
        obatAnalgesik.setStockQuantity(200);
        obatAnalgesik.setMinStockLevel(50);
        obatAnalgesik.setExpiryDate(LocalDateTime.now().plusYears(2));
        obatAnalgesik.setCategory("Analgesic");
        
        obatNormal = new Medication();
        obatNormal.setName("Vitamin C");
        obatNormal.setStockQuantity(100);
        obatNormal.setMinStockLevel(10);
        obatNormal.setExpiryDate(LocalDateTime.now().plusYears(1));
        obatNormal.setCategory("Vitamin");

        semuaObat = Arrays.asList(obatStokRendah, obatHampirKadaluarsa, obatAntibiotik, obatAnalgesik, obatNormal);
    }

    @Test
    @DisplayName("Filter 'Semua' harus mengembalikan semua obat")
    void testFilterMedications_WhenFilterIsSemua_ShouldReturnAllMedications() {
        List<Medication> hasil = controller.filterMedicationList(semuaObat, "Semua");
        assertEquals(5, hasil.size());
    }
    
    @Test
    @DisplayName("Filter 'Stok Rendah' harus mengembalikan obat dengan stok <= min stok")
    void testFilterMedications_WhenFilterIsStokRendah_ShouldReturnLowStock() {
        List<Medication> hasil = controller.filterMedicationList(semuaObat, "Stok Rendah");
        assertEquals(1, hasil.size());
        assertTrue(hasil.contains(obatStokRendah));
    }

    @Test
    @DisplayName("Filter 'Hampir Kadaluarsa' harus mengembalikan obat yang akan kadaluarsa dalam 30 hari")
    void testFilterMedications_WhenFilterIsHampirKadaluarsa_ShouldReturnSoonToExpire() {
        List<Medication> hasil = controller.filterMedicationList(semuaObat, "Hampir Kadaluarsa");
        assertEquals(1, hasil.size());
        assertTrue(hasil.contains(obatHampirKadaluarsa));
    }

    @Test
    @DisplayName("Filter 'Kategori Antibiotik' harus mengembalikan semua antibiotik")
    void testFilterMedications_WhenFilterIsKategoriAntibiotik_ShouldReturnAntibiotics() {
        List<Medication> hasil = controller.filterMedicationList(semuaObat, "Kategori Antibiotik");
        assertEquals(2, hasil.size(), "Harus ada 2 antibiotik");
        assertTrue(hasil.contains(obatHampirKadaluarsa));
        assertTrue(hasil.contains(obatAntibiotik));
    }

    @Test
    @DisplayName("Filter 'Kategori Analgesik' harus mengembalikan semua analgesik")
    void testFilterMedications_WhenFilterIsKategoriAnalgesik_ShouldReturnAnalgesics() {
        List<Medication> hasil = controller.filterMedicationList(semuaObat, "Kategori Analgesik");
        assertEquals(2, hasil.size(), "Harus ada 2 analgesik");
        assertTrue(hasil.contains(obatStokRendah));
        assertTrue(hasil.contains(obatAnalgesik));
    }
}