package io.ayers.mobileappws.bootstrap;

import io.ayers.mobileappws.domain.UserEntity;
import io.ayers.mobileappws.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BootstrapUsers
        implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0)
            loadUsersToDatabase();
    }

    private void loadUsersToDatabase() {

        String encryptedPassword = bCryptPasswordEncoder.encode("test");

        for (int i = 0; i < 500; i++) {
            userRepository.save(UserEntity.builder()
                                          .userId(UUID.randomUUID().toString())
                                          .encryptedPassword(encryptedPassword)
                                          .email("test" + i + "@test.com")
                                          .firstName("test")
                                          .lastName(String.valueOf(i))
                                          .build());
        }
    }
}
