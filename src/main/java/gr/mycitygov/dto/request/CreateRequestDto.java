package gr.mycitygov.dto.request;

public class CreateRequestDto {

    private Long requestTypeId;
    private String description;

    private String locationText;   // PROBLEM_REPORT

    private String address;        // APPLICATION
    private String purpose;
    private String afm;
    private String amka;
    private String citizenIdNumber;

    public Long getRequestTypeId() { return requestTypeId; }
    public void setRequestTypeId(Long requestTypeId) { this.requestTypeId = requestTypeId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocationText() { return locationText; }
    public void setLocationText(String locationText) { this.locationText = locationText; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getAfm() { return afm; }
    public void setAfm(String afm) { this.afm = afm; }

    public String getAmka() { return amka; }
    public void setAmka(String amka) { this.amka = amka; }

    public String getCitizenIdNumber() { return citizenIdNumber; }
    public void setCitizenIdNumber(String citizenIdNumber) { this.citizenIdNumber = citizenIdNumber; }
}
