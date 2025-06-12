package controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.dao.MedicationDAO;
import model.entity.Medication;
import model.entity.User;
import view.LoginView;

public class MedicationPharmacistController {

    // Sidebar buttons (as defined in your FXML)
    @FXML private Button dashboardSidebarButton;
    @FXML private Button resepObatSidebarButton;
    @FXML private Button keluarSidebarButton;
    @FXML private Button persediaanSidebarButton;
    @FXML private Button notifikasiSidebarButton;
    @FXML private Button addMedicationButton;

    // Table and columns (using fx:id names from your FXML)
    @FXML private TableView<MedicationTableData> dataAppointmentTable;
    @FXML private TableColumn<MedicationTableData, String> colGenerik;
    @FXML private TableColumn<MedicationTableData, String> colName;
    @FXML private TableColumn<MedicationTableData, String> colCategory;
    @FXML private TableColumn<MedicationTableData, String> colStok;
    @FXML private TableColumn<MedicationTableData, String> colUnit;
    @FXML private TableColumn<MedicationTableData, String> colAction;

    @FXML private ComboBox<String> filterToggle;

    private MedicationDAO medicationDAO = new MedicationDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilterComboBox();
    }

    public void setUser(User user) {
        this.currentUser = user;
        loadMedications();
    }

    private void setupTableColumns() {
        colGenerik.setCellValueFactory(new PropertyValueFactory<>("genericName"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));

        colAction.setCellFactory(col -> new TableCell<MedicationTableData, String>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(e -> {
                    MedicationTableData data = getTableView().getItems().get(getIndex());
                    handleEditMedication(data.getMedication());
                });
                deleteButton.setOnAction(e -> {
                    MedicationTableData data = getTableView().getItems().get(getIndex());
                    handleDeleteMedication(data.getMedication());
                });
                // Set button styling (optional)
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, editButton, deleteButton));
                }
            }
        });
    }

    private void setupFilterComboBox() {
        filterToggle.setItems(FXCollections.observableArrayList(
            "Semua", "Stok Rendah", "Hampir Kadaluarsa", "Kategori Antibiotik", "Kategori Analgesik"
        ));
        filterToggle.setValue("Semua");
    }

    private void loadMedications() {
        try {
            List<Medication> medications = medicationDAO.getAllMedications();
            ObservableList<MedicationTableData> tableData = FXCollections.observableArrayList();
            for (Medication med : medications) {
                MedicationTableData data = new MedicationTableData();
                data.setGenericName(med.getGenericName());
                data.setName(med.getName());
                data.setCategory(med.getCategory());
                data.setStockQuantity(String.valueOf(med.getStockQuantity()));
                data.setUnit(med.getUnit());
                data.setMedication(med);
                tableData.add(data);
            }
            dataAppointmentTable.setItems(tableData);
        } catch (Exception e) {
            showError("Error loading medications: " + e.getMessage());
        }
    }

    @FXML
    private void handleFilter(ActionEvent event) {
        String selectedFilter = filterToggle.getValue();
        try {
            List<Medication> medications = medicationDAO.getAllMedications();
            ObservableList<MedicationTableData> filteredData = FXCollections.observableArrayList();
            for (Medication med : medications) {
                boolean include = false;
                switch (selectedFilter) {
                    case "Semua":
                        include = true;
                        break;
                    case "Stok Rendah":
                        include = med.getStockQuantity() <= med.getMinStockLevel();
                        break;
                    case "Hampir Kadaluarsa":
                        include = med.getExpiryDate().isBefore(LocalDateTime.now().plusDays(30));
                        break;
                    case "Kategori Antibiotik":
                        include = "Antibiotic".equalsIgnoreCase(med.getCategory());
                        break;
                    case "Kategori Analgesik":
                        include = "Analgesic".equalsIgnoreCase(med.getCategory()) ||
                                  "Opioid Analgesic".equalsIgnoreCase(med.getCategory());
                        break;
                }
                if (include) {
                    MedicationTableData data = new MedicationTableData();
                    data.setGenericName(med.getGenericName());
                    data.setName(med.getName());
                    data.setCategory(med.getCategory());
                    data.setStockQuantity(String.valueOf(med.getStockQuantity()));
                    data.setUnit(med.getUnit());
                    data.setMedication(med);
                    filteredData.add(data);
                }
            }
            dataAppointmentTable.setItems(filteredData);
        } catch (Exception e) {
            showError("Error filtering medications: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddMedicationClick(ActionEvent event) {
        showMedicationDialog(null);
    }

    private void handleEditMedication(Medication medication) {
        showMedicationDialog(medication);
    }

    private void handleDeleteMedication(Medication medication) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.setContentText("Apakah Anda yakin ingin menghapus obat: " + medication.getName() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = medicationDAO.deleteMedication(medication.getMedicationId());
                if (success) {
                    showInfo("Sukses", "Obat berhasil dihapus!");
                    loadMedications();
                } else {
                    showError("Gagal menghapus obat!");
                }
            } catch (Exception e) {
                showError("Error deleting medication: " + e.getMessage());
            }
        }
    }

    private void showMedicationDialog(Medication medication) {
        Dialog<Medication> dialog = new Dialog<>();
        dialog.setTitle(medication == null ? "Tambah Obat Baru" : "Edit Obat");
        dialog.setHeaderText("Masukkan informasi obat:");

        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        TextField genericNameField = new TextField();
        TextField categoryField = new TextField();
        TextField unitField = new TextField();
        TextField stockField = new TextField();
        TextField minStockField = new TextField();
        TextField sideEffectsField = new TextField();
        TextField contraindicationField = new TextField();

        if (medication != null) {
            nameField.setText(medication.getName());
            genericNameField.setText(medication.getGenericName());
            categoryField.setText(medication.getCategory());
            unitField.setText(medication.getUnit());
            stockField.setText(String.valueOf(medication.getStockQuantity()));
            minStockField.setText(String.valueOf(medication.getMinStockLevel()));
            sideEffectsField.setText(medication.getSideEffects());
            contraindicationField.setText(medication.getContraindication());
        }

        grid.add(new Label("Nama Obat:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Nama Generik:"), 0, 1);
        grid.add(genericNameField, 1, 1);
        grid.add(new Label("Kategori:"), 0, 2);
        grid.add(categoryField, 1, 2);
        grid.add(new Label("Unit:"), 0, 3);
        grid.add(unitField, 1, 3);
        grid.add(new Label("Stok:"), 0, 4);
        grid.add(stockField, 1, 4);
        grid.add(new Label("Min Stok:"), 0, 5);
        grid.add(minStockField, 1, 5);
        grid.add(new Label("Efek Samping:"), 0, 6);
        grid.add(sideEffectsField, 1, 6);
        grid.add(new Label("Kontraindikasi:"), 0, 7);
        grid.add(contraindicationField, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Medication med = medication != null ? medication : new Medication();
                    med.setName(nameField.getText());
                    med.setGenericName(genericNameField.getText());
                    med.setCategory(categoryField.getText());
                    med.setUnit(unitField.getText());
                    med.setStockQuantity(Integer.parseInt(stockField.getText()));
                    med.setMinStockLevel(Integer.parseInt(minStockField.getText()));
                    med.setSideEffects(sideEffectsField.getText());
                    med.setContraindication(contraindicationField.getText());
                    if (medication == null) {
                        med.setExpiryDate(LocalDateTime.now().plusYears(2));
                    }
                    return med;
                } catch (NumberFormatException e) {
                    showError("Stok dan Min Stok harus berupa angka!");
                    return null;
                }
            }
            return null;
        });

        Optional<Medication> result = dialog.showAndWait();
        result.ifPresent(med -> {
            try {
                boolean success;
                if (medication == null) {
                    success = medicationDAO.insertMedication(med);
                    if (success) {
                        showInfo("Sukses", "Obat baru berhasil ditambahkan!");
                    }
                } else {
                    success = medicationDAO.updateMedication(med);
                    if (success) {
                        showInfo("Sukses", "Obat berhasil diperbarui!");
                    }
                }
                if (success) {
                    loadMedications();
                } else {
                    showError("Gagal menyimpan data obat!");
                }
            } catch (Exception e) {
                showError("Error saving medication: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleDashboardClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PharmacistDashboard.fxml"));
            Parent root = loader.load();

            PharmacistDashboardController controller = loader.getController();
            controller.setUser(currentUser);

            Stage currentStage = (Stage) dashboardSidebarButton.getScene().getWindow();
            currentStage.close();
            Stage newStage = new Stage();
            newStage.setTitle("Dashboard Apoteker - Klinik Sehat Medika");
            newStage.setScene(new Scene(root, 1200, 800));
            newStage.show();
        } catch (Exception e) {
            showError("Error returning to dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void handleProfilClick(ActionEvent event){
        showInfo("Info", "Fitur Profil akan segera diimplementasikan.");
    }

    @FXML
    private void handleResepObatClick(ActionEvent event) {
        showInfo("Info", "Fitur Resep Obat akan segera diimplementasikan.");
    }

    @FXML
    private void handlePersediaanObatClick(ActionEvent event) {
        loadMedications();
    }

    @FXML
    private void handleNotifikasiClick(ActionEvent event) {
        showInfo("Info", "Fitur Notifikasi akan segera diimplementasikan.");
    }

    @FXML
    private void handleKeluarClick(ActionEvent event) {
        try {
            Stage currentStage = (Stage) keluarSidebarButton.getScene().getWindow();
            currentStage.close();
            LoginView loginView = new LoginView();
            Stage loginStage = new Stage();
            loginView.start(loginStage);
        } catch (Exception e) {
            showError("Error switching to login view: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Buat metode ini tidak private (package-private) agar bisa diakses dari kelas tes
List<Medication> filterMedicationList(List<Medication> allMedications, String selectedFilter) {
    // Jika filter null atau "Semua", kembalikan semua data
    if (selectedFilter == null || "Semua".equals(selectedFilter)) {
        return allMedications;
    }

    // Gunakan stream untuk memfilter data, ini lebih bersih
    return allMedications.stream()
        .filter(med -> {
            switch (selectedFilter) {
                case "Stok Rendah":
                    return med.getStockQuantity() <= med.getMinStockLevel();
                case "Hampir Kadaluarsa":
                    return med.getExpiryDate().isBefore(LocalDateTime.now().plusDays(30));
                case "Kategori Antibiotik":
                    return "Antibiotic".equalsIgnoreCase(med.getCategory());
                case "Kategori Analgesik":
                    return "Analgesic".equalsIgnoreCase(med.getCategory()) ||
                           "Opioid Analgesic".equalsIgnoreCase(med.getCategory());
                default:
                    return false; // Untuk filter yang tidak dikenal
            }
        })
        .collect(java.util.stream.Collectors.toList());
}

    public static class MedicationTableData {
        private SimpleStringProperty genericName = new SimpleStringProperty();
        private SimpleStringProperty name = new SimpleStringProperty();
        private SimpleStringProperty category = new SimpleStringProperty();
        private SimpleStringProperty stockQuantity = new SimpleStringProperty();
        private SimpleStringProperty unit = new SimpleStringProperty();
        private Medication medication;

        public String getGenericName() {
            return genericName.get();
        }
        public void setGenericName(String value) {
            genericName.set(value);
        }
        public SimpleStringProperty genericNameProperty() {
            return genericName;
        }

        public String getName() {
            return name.get();
        }
        public void setName(String value) {
            name.set(value);
        }
        public SimpleStringProperty nameProperty() {
            return name;
        }

        public String getCategory() {
            return category.get();
        }
        public void setCategory(String value) {
            category.set(value);
        }
        public SimpleStringProperty categoryProperty() {
            return category;
        }

        public String getStockQuantity() {
            return stockQuantity.get();
        }
        public void setStockQuantity(String value) {
            stockQuantity.set(value);
        }
        public SimpleStringProperty stockQuantityProperty() {
            return stockQuantity;
        }

        public String getUnit() {
            return unit.get();
        }
        public void setUnit(String value) {
            unit.set(value);
        }
        public SimpleStringProperty unitProperty() {
            return unit;
        }

        public Medication getMedication() {
            return medication;
        }
        public void setMedication(Medication medication) {
            this.medication = medication;
        }

        public String getAction() {
            return "";
        }
    }
}