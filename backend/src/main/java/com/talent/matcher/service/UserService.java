package com.talent.matcher.service;

import com.talent.matcher.model.User;
import com.talent.matcher.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initDefaultUser() {
        userRepository.findByUsername("admin").orElseGet(() ->
                userRepository.save(User.builder()
                        .username("admin")
                        .passwordHash(passwordEncoder.encode("admin"))
                        .requirePasswordChange(true)
                        .build()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities("USER")
                .build();
    }

    public void changePassword(String username, String newPassword) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setRequirePasswordChange(false);
            userRepository.save(user);
        });
    }

    public boolean requirePasswordChange(String username) {
        return userRepository.findByUsername(username).map(User::isRequirePasswordChange).orElse(false);
    }
}
