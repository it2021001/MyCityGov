package gr.mycitygov.security;

import gr.mycitygov.model.User;
import gr.mycitygov.repository.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Το Spring Security καλεί αυτό στη λογική authentication.
     * Εμείς ψάχνουμε στη DB με βάση username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!Boolean.TRUE.equals(u.getEnabled())) {
            throw new DisabledException("User is disabled");
        }

        // Spring θέλει ROLE_ prefix στα authorities
        String roleName = "ROLE_" + u.getRole().name();

        return new org.springframework.security.core.userdetails.User(
                u.getUsername(),
                u.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(roleName))
        );
    }
}
