package gr.mycitygov.controller;

import gr.mycitygov.dto.auth.AuthResponseDto;
import gr.mycitygov.dto.auth.LoginDto;
import gr.mycitygov.dto.auth.RegisterCitizenDto;
import gr.mycitygov.service.CitizenAuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CitizenAuthService authService;

    public AuthController(CitizenAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponseDto register(@Valid @RequestBody RegisterCitizenDto dto) {
        return authService.registerCitizen(dto);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginDto dto) {
        return authService.login(dto);
    }
}
