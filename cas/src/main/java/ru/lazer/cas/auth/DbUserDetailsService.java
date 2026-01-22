package ru.lazer.cas.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.lazer.cas.db.UserAccountRepository;

import java.util.List;

@Service
public class DbUserDetailsService implements UserDetailsService {
    private final UserAccountRepository users;
    public DbUserDetailsService(UserAccountRepository users) { this.users = users; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var u = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        java.util.List<? extends GrantedAuthority> auth = u.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                .toList();

        return User.withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .authorities(auth)
                .disabled(!u.isEnabled())
                .build();
    }
}
