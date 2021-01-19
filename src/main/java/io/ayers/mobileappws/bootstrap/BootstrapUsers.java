package io.ayers.mobileappws.bootstrap;

import io.ayers.mobileappws.models.entities.AddressEntity;
import io.ayers.mobileappws.models.entities.AuthorityEntity;
import io.ayers.mobileappws.models.entities.RoleEntity;
import io.ayers.mobileappws.models.entities.UserEntity;
import io.ayers.mobileappws.repositories.AddressRepository;
import io.ayers.mobileappws.repositories.AuthorityRepository;
import io.ayers.mobileappws.repositories.RoleRepository;
import io.ayers.mobileappws.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Component
@Profile("dev")
public class BootstrapUsers
        implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final Random rand = new Random();
    private List<AddressEntity> ADDRESS_LIST;
    private List<RoleEntity> ROLE_LIST;


    public BootstrapUsers(UserRepository userRepository,
                          AddressRepository addressRepository,
                          RoleRepository roleRepository,
                          AuthorityRepository authorityRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.roleRepository = roleRepository;
        this.authorityRepository = authorityRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;

    }


    private void initAddressList() {
        AddressEntity address1 = AddressEntity.builder()
                                              .city("City 1")
                                              .country("Country 1")
                                              .postalCode("000001")
                                              .streetName("123 Street")
                                              .type("Billing")
                                              .build();

        AddressEntity address2 = AddressEntity.builder()
                                              .city("City 2")
                                              .country("Country 2")
                                              .postalCode("000002")
                                              .streetName("456 Street")
                                              .type("Shipping")
                                              .build();

        AddressEntity address3 = AddressEntity.builder()
                                              .city("City 3")
                                              .country("Country 3")
                                              .postalCode("000003")
                                              .streetName("789 Street")
                                              .type("Billing")
                                              .build();

        ADDRESS_LIST = Arrays.asList(address1, address2, address3);
    }

    private void initRoleList() {

        AuthorityEntity READ_AUTHORITY = AuthorityEntity.builder().name("READ_AUTHORITY").build();
        AuthorityEntity WRITE_AUTHORITY = AuthorityEntity.builder().name("WRITE_AUTHORITY").build();
        AuthorityEntity DELETE_AUTHORITY = AuthorityEntity.builder().name("DELETE_AUTHORITY").build();

        RoleEntity adminRole = RoleEntity.builder().name("ROLE_ADMIN").build();
        RoleEntity userRole = RoleEntity.builder().name("ROLE_USER").build();

        adminRole.getAuthorities().addAll(Set.of(READ_AUTHORITY, WRITE_AUTHORITY, DELETE_AUTHORITY));
        userRole.getAuthorities().addAll(Set.of(READ_AUTHORITY));

        ROLE_LIST = Arrays.asList(adminRole, userRole);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("~~~~~ Adding users to database!");
            initAddressList();
            initRoleList();
            loadUsersToDatabase();
        } else {
            log.info("~~~~~ Users already exist - no need to populate database!");
        }
    }

    private void loadUsersToDatabase() {

        String encryptedPassword = bCryptPasswordEncoder.encode("test");

        for (int i = 0; i < 500; i++) {

            AddressEntity randomAddress = ADDRESS_LIST.get(rand.nextInt(ADDRESS_LIST.size()));
            RoleEntity randomRole = ROLE_LIST.get(rand.nextInt(ROLE_LIST.size()));

            UserEntity userEntity = UserEntity.builder()
                                              .userId(UUID.randomUUID().toString())
                                              .encryptedPassword(encryptedPassword)
                                              .email("test" + i + "@test.com")
                                              .firstName("test")
                                              .addresses(Collections.singletonList(randomAddress))
                                              .roles(Set.of(randomRole))
                                              .lastName(String.valueOf(i))
                                              .build();

            userRepository.save(userEntity);
        }
    }
}
