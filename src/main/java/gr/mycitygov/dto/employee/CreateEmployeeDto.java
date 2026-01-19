package gr.mycitygov.dto.employee;

public class CreateEmployeeDto {
    private Long departmentId;
    private String fullName;
    private String password; // <= 10 digits (όπως θες)

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
