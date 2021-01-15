package io.ayers.mobileappws.services.implementations;

import io.ayers.mobileappws.models.dtos.UserDto;
import io.ayers.mobileappws.models.entities.UserEntity;
import io.ayers.mobileappws.models.mappings.UserMapper;
import io.ayers.mobileappws.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


class UserServiceImplTest {

    UserEntity userEntity;
    UserDto userDto;
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;
    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userEntity = UserEntity.builder()
                               .id(1L)
                               .userId(UUID.randomUUID().toString())
                               .encryptedPassword("abcd1234")
                               .email("test@test.com")
                               .firstName("Test")
                               .lastName("User")
                               .build();

        userDto = new UserDto();

        BeanUtils.copyProperties(userEntity, userDto);

        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
        when(userMapper.domainEntityToDto(any(UserEntity.class))).thenReturn(userDto);
    }

    @Test
    void getUserDetailsByEmail() {
        UserDto returnedDto = userService.getUserDetailsByEmail("test@test.com");

        assertNotNull(returnedDto);
        assertThat(returnedDto).usingRecursiveComparison().ignoringFields("password").isEqualTo(userEntity);
    }
}