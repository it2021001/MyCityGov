package gr.mycitygov.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class LoginDto {
    private String identifier; // username OR email
    private String password;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}