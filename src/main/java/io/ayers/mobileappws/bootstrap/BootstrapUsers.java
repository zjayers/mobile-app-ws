package io.ayers.mobileappws.bootstrap;

import io.ayers.mobileappws.domain.AddressEntity;
import io.ayers.mobileappws.domain.UserEntity;
import io.ayers.mobileappws.repository.AddressRepository;
import io.ayers.mobileappws.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
@Profile("dev")
public class BootstrapUsers
        implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final Random rand = new Random();
    private final List<AddressEntity> ADDRESS_LIST;

    public BootstrapUsers(UserRepository userRepository,
                          AddressRepository addressRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;

        ADDRESS_LIST = new ArrayList<>();
        ADDRESS_LIST.add(AddressEntity.builder()
                                      .city("City 1")
                                      .country("Country 1")
                                      .postalCode("000001")
                                      .streetName("123 Street")
                                      .type("Billing")
                                      .build());

        ADDRESS_LIST.add(AddressEntity.builder()
                                      .city("City 2")
                                      .country("Country 2")
                                      .postalCode("000002")
                                      .streetName("456 Street")
                                      .type("Shipping")
                                      .build());
        ADDRESS_LIST.add(AddressEntity.builder()
                                      .city("City 3")
                                      .country("Country 3")
                                      .postalCode("000003")
                                      .streetName("789 Street")
                                      .type("Billing")
                                      .build());

    }


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("~~~~~ Adding users to database!");
            loadUsersToDatabase();
        } else {
            log.info("~~~~~ Users already exist - no need to populate database!");
        }
    }

    private void loadUsersToDatabase() {

        String encryptedPassword = bCryptPasswordEncoder.encode("test");


        for (int i = 0; i < 500; i++) {

            AddressEntity randomAddress = ADDRESS_LIST.get(rand.nextInt(ADDRESS_LIST.size()));
            UserEntity userEntity = UserEntity.builder()
                                              .userId(UUID.randomUUID().toString())
                                              .encryptedPassword(encryptedPassword)
                                              .email("test" + i + "@test.com")
                                              .firstName("test")
                                              .lastName(String.valueOf(i))
                                              .build();

            userEntity.getAddresses().add(randomAddress);
            randomAddress.getUserDetails().add(userEntity);

            addressRepository.save(randomAddress);
            userRepository.save(userEntity);
        }
    }
}
