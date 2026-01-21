package gr.mycitygov.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterCitizenDto {

    @Email
    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 60, message = "password must be 6-60 chars")
    private String password;

    @NotBlank(message = "fullName is required")
    @Size(min = 2, max = 120, message = "fullName must be 2-120 chars")
    private String fullName;

    @NotBlank(message = "afm is required")
    @Pattern(regexp = "\\d{9}", message = "afm must be exactly 9 digits")
    private String afm;

    @NotBlank(message = "amka is required")
    @Pattern(regexp = "\\d{11}", message = "amka must be exactly 11 digits")
    private String amka;

    @NotBlank(message = "citizenIdNumber is required")
    @Pattern(regexp = "^[A-Z]{2}\\d{6}$", message = "citizenIdNumber must be 2 capital letters followed by 6 digits")
    private String citizenIdNumber;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAfm() {
        return afm;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }

    public String getAmka() {
        return amka;
    }

    public void setAmka(String amka) {
        this.amka = amka;
    }

    public String getCitizenIdNumber() {
        return citizenIdNumber;
    }

    public void setCitizenIdNumber(String citizenIdNumber) {
        this.citizenIdNumber = citizenIdNumber;
    }
}
