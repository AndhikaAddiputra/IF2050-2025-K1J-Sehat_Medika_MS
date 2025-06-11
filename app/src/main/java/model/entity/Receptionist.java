package model.entity;

public class Receptionist {
    private String receptionistId;
    private int userId;
    private String department;
    
    public Receptionist(String receptionistId, int userId, String department) {
        this.receptionistId = receptionistId;
        this.userId = userId;
        this.department = department;
    }
    public Receptionist() {}
    public String getReceptionistId() {
        return receptionistId;
    }
    public void setReceptionistId(String receptionistId) {
        this.receptionistId = receptionistId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
}
