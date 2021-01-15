package io.ayers.mobileappws.services.implementations;

import io.ayers.mobileappws.constants.SecurityConstants;
import io.ayers.mobileappws.models.dtos.UserDto;
import io.ayers.mobileappws.models.entities.UserEntity;
import io.ayers.mobileappws.models.mappings.UserMapper;
import io.ayers.mobileappws.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class UserServiceImplTest {

    final String ENCRYPTED_PASSWORD = "encrypted_password";
    UserEntity userEntity;
    UserDto userDto;
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;
    @Mock
    EmailServiceImpl emailService;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userEntity = UserEntity.builder()
                               .id(1L)
                               .userId(UUID.randomUUID().toString())
                               .encryptedPassword(ENCRYPTED_PASSWORD)
                               .email("test@test.com")
                               .firstName("Test")
                               .lastName("User")
                               .build();

        userDto = new UserDto();

        BeanUtils.copyProperties(userEntity, userDto);

        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(ENCRYPTED_PASSWORD);

        when(userMapper.domainEntityToDto(any(UserEntity.class))).thenReturn(userDto);
        when(userMapper.dtoToDomainEntity(any(UserDto.class))).thenReturn(userEntity);

        doNothing().when(emailService).sendVerficationEmail(any(UserDto.class));

    }

    @Test
    void getUserDetailsByEmail() {
        UserDto returnedDto = userService.getUserDetailsByEmail(userEntity.getEmail());

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userMapper, times(1)).domainEntityToDto(any(UserEntity.class));

        assertNotNull(returnedDto);
        assertThat(returnedDto).usingRecursiveComparison().ignoringFields("password").isEqualTo(userEntity);
    }

    @Test
    void getUserDetailsByEmail_throwUsernameNotFoundException() {

        when(userRepository.findByEmail(anyString())).thenReturn(null);

        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userService.getUserDetailsByEmail(userEntity.getEmail()));

        verify(userRepository, times(1)).findByEmail(anyString());
        verifyNoInteractions(userMapper);
    }

    @Test
    void createOneUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        UserDto baseUserDto = new UserDto();
        baseUserDto.setFirstName("Frank");
        baseUserDto.setLastName("Kafka");
        baseUserDto.setPassword("PASSWORD");
        baseUserDto.setEmail("test@test.com");
        baseUserDto.setAddresses(new ArrayList<>());

        assertNull(baseUserDto.getUserId());
        assertNull(baseUserDto.getEncryptedPassword());
        assertNull(baseUserDto.getEmailVerificationToken());

        assertFalse(baseUserDto.getEmailVerificationStatus());

        UserDto returnedDto = userService.createUser(baseUserDto);

        assertNotNull(returnedDto);

        String userId = returnedDto.getUserId();
        assertDoesNotThrow(() -> UUID.fromString(userId));

        assertEquals(returnedDto.getEncryptedPassword(), ENCRYPTED_PASSWORD);
        assertNotNull(returnedDto.getEmailVerificationToken());
        assertFalse(returnedDto.getEmailVerificationStatus());

        assertDoesNotThrow(() -> {
            Claims claims = Jwts.parser()
                                .setSigningKey(SecurityConstants.TOKEN_SECRET)
                                .parseClaimsJws(returnedDto.getEmailVerificationToken()).getBody();

            Date tokenExpirationDate = claims.getExpiration();
            Date currentDate = new Date();

            assertTrue(tokenExpirationDate.after(currentDate));
        });

        verify(userRepository, times(1)).findByEmail(baseUserDto.getEmail());
        verify(userMapper, times(1)).dtoToDomainEntity(baseUserDto);
        verify(bCryptPasswordEncoder, times(1)).encode(baseUserDto.getPassword());
        verify(userRepository, times(1)).save(userEntity);
        verify(userMapper, times(1)).domainEntityToDto(userEntity);
        verify(emailService, times(1)).sendVerficationEmail(this.userDto);

    }

    @Test
    void createOneUser_throwRuntimeException() {
        assertThrows(RuntimeException.class, () ->
                userService.createUser(userDto));

        verifyNoInteractions(userMapper, bCryptPasswordEncoder, emailService);
    }
}