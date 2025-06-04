package model.entity;

public class Pharmacist {
    private String pharmacistId;
    private int userId;
    private String licenseNumber;
    
    public Pharmacist(){}
    public Pharmacist(String pharmacistId, int userId, String licenseNumber){
        this.pharmacistId = pharmacistId;
        this.userId = userId;
        this.licenseNumber = licenseNumber;
    }

    public String getPharmacistId(){ return pharmacistId; }
    public int getUserId(){ return userId; }
    public String getLicenseNumber(){ return licenseNumber; }

    public void setPharmacistId(String pharmacistId) { this.pharmacistId = pharmacistId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    @Override
    public String toString() {
        return "Pharmacist{" +
            "pharmacistId='" + pharmacistId + '\'' +
            ", userId=" + userId +
            ", licenseNumber='" + licenseNumber + '\'' +
            '}';
    }
}
