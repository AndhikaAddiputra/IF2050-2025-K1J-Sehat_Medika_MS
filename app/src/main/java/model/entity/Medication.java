package model.entity;

import java.time.LocalDateTime;

public class Medication {
    private int medicationId;
    private String name;
    private String genericName;
    private String category;
    private String unit;
    private int stockQuantity;
    private int minStockLevel;
    private LocalDateTime expiryDate;
    private String sideEffects;
    private String contraindication;

    public Medication() {}

    public Medication(int medicationId, String name, String genericName, String category, String unit, int stockQuantity, 
                    int minStockLevel, LocalDateTime expiryDate, String sideEffects, String contraindication) {
        this.medicationId = medicationId;
        this.name = name;
        this.genericName = genericName;
        this.category = category;
        this.unit = unit;
        this.stockQuantity = stockQuantity;
        this.minStockLevel = minStockLevel;
        this.expiryDate = expiryDate;
        this.sideEffects = sideEffects;
        this.contraindication = contraindication;
    }

    public int getMedicationId() { return medicationId; }
    public String getName() { return name; }
    public String getGenericName() { return genericName; }
    public String getCategory() { return category; }
    public String getUnit() { return unit; }
    public int getStockQuantity() { return stockQuantity; }
    public int getMinStockLevel() { return minStockLevel; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public String getSideEffects() { return sideEffects; }
    public String getContraindication() { return contraindication; }

    public void setMedicationId(int medicationId) { this.medicationId = medicationId; }
    public void setName(String name) { this.name = name; }
    public void setGenericName(String genericName) { this.genericName = genericName; }
    public void setCategory(String category) { this.category = category; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public void setSideEffects(String sideEffects) { this.sideEffects = sideEffects; }
    public void setContraindication(String contraindication) { this.contraindication = contraindication; }

    @Override
    public String toString() {
        return "Medication{" +
               "medicationId=" + medicationId +
               ", name='" + name + '\'' +
               ", genericName='" + genericName + '\'' +
               ", category='" + category + '\'' +
               ", unit='" + unit + '\'' +
               ", stockQuantity=" + stockQuantity +
               ", minStockLevel=" + minStockLevel +
               ", expiryDate=" + expiryDate +
               ", sideEffects='" + sideEffects + '\'' +
               ", contraindication='" + contraindication + '\'' +
               '}';
    }
}

