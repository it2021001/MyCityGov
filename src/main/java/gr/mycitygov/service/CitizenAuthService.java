package gr.mycitygov.service;

import gr.mycitygov.dto.auth.AuthResponseDto;
import gr.mycitygov.dto.auth.LoginDto;
import gr.mycitygov.dto.auth.RegisterCitizenDto;
import gr.mycitygov.enums.UserRole;
import gr.mycitygov.model.CitizenProfile;
import gr.mycitygov.model.User;
import gr.mycitygov.repository.CitizenProfileRepository;
import gr.mycitygov.repository.UserRepository;
import gr.mycitygov.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CitizenAuthService {

    private final UserRepository userRepository;
    private final CitizenProfileRepository citizenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public CitizenAuthService(UserRepository userRepository,
                              CitizenProfileRepository citizenRepo,
                              PasswordEncoder passwordEncoder,
                              JwtService jwtService) {
        this.userRepository = userRepository;
        this.citizenRepo = citizenRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponseDto registerCitizen(RegisterCitizenDto dto) {
        if (citizenRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // 1) Create user with temporary username
        User u = new User();
        u.setUsername("tmp-" + UUID.randomUUID());
        u.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        u.setRole(UserRole.CITIZEN);
        u.setEnabled(true);
        u.setCreatedAt(LocalDateTime.now());

        u = userRepository.save(u);

        // 2) Final username c{id}
        String username = "c" + u.getId();
        u.setUsername(username);
        u = userRepository.save(u);

        // 3) Create profile
        CitizenProfile p = new CitizenProfile();
        p.setUser(u);
        p.setEmail(dto.getEmail());
        p.setCitizenUsername(username);
        p.setFullName(dto.getFullName());
        p.setAfm(dto.getAfm());
        p.setAmka(dto.getAmka());
        p.setCitizenIdNumber(dto.getCitizenIdNumber());
        citizenRepo.save(p);

        AuthResponseDto res = new AuthResponseDto();
        res.setUsername(username);
        res.setRole(u.getRole().name());
        res.setToken(null); // στο register δεν δίνουμε token (ή δίνουμε, αν θες)
        return res;
    }

    public AuthResponseDto login(LoginDto dto) {
        String id = dto.getIdentifier();

        User u;
        if (id.contains("@")) {
            CitizenProfile cp = citizenRepo.findByEmail(id)
                    .orElseThrow(() -> new RuntimeException("No citizen with this email"));
            u = cp.getUser();
        } else {
            u = userRepository.findByUsername(id)
                    .orElseThrow(() -> new RuntimeException("No user with this username"));
        }

        if (!Boolean.TRUE.equals(u.getEnabled())) {
            throw new RuntimeException("User disabled");
        }

        if (!passwordEncoder.matches(dto.getPassword(), u.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(u.getUsername(), u.getId(), u.getRole().name());

        AuthResponseDto res = new AuthResponseDto();
        res.setUsername(u.getUsername());
        res.setRole(u.getRole().name());
        res.setToken(token);
        return res;
    }
}
