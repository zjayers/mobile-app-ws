package io.ayers.mobileappws.services.implementations;

import io.ayers.mobileappws.constants.SecurityConstants;
import io.ayers.mobileappws.models.dtos.UserDto;
import io.ayers.mobileappws.models.entities.PasswordResetTokenEntity;
import io.ayers.mobileappws.models.entities.UserEntity;
import io.ayers.mobileappws.models.mappings.UserMapper;
import io.ayers.mobileappws.repositories.PasswordResetTokenRepository;
import io.ayers.mobileappws.repositories.UserRepository;
import io.ayers.mobileappws.security.UserPrincipal;
import io.ayers.mobileappws.services.interfaces.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl
        implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailServiceImpl emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public UserDto createUser(UserDto userDto) {

        String userDtoEmail = userDto.getEmail();
        UserEntity existingUserEntity = userRepository.findByEmail(userDtoEmail);

        if (existingUserEntity != null) throw new RuntimeException("User already exists with email: " + userDtoEmail);

        UserEntity userEntity = userMapper.dtoToDomainEntity(userDto);

        String encodedPassword = bCryptPasswordEncoder.encode(userDto.getPassword());

        String userId = UUID.randomUUID().toString();

        userEntity.setUserId(userId);
        userEntity.setEmailVerificationToken(generateVerificationToken(userId));
        userEntity.setEncryptedPassword(encodedPassword);

        UserEntity savedUserEntity = userRepository.save(userEntity);

        UserDto savedUserDto = userMapper.domainEntityToDto(savedUserEntity);

        emailService.sendVerficationEmail(savedUserDto);

        return savedUserDto;
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        return userMapper.domainEntityToDto(userEntity);
    }

    @Override
    public UserDto getUserDetailsByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        return userMapper.domainEntityToDto(userEntity);
    }

    @Override
    public UserDto updateUser(String userId, UserDto userDto) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) throw new UsernameNotFoundException(userId);

        String firstName = userDto.getFirstName();
        String lastName = userDto.getLastName();
        String email = userDto.getEmail();

        if (firstName != null) userEntity.setFirstName(firstName);
        if (lastName != null) userEntity.setLastName(lastName);
        if (email != null) userEntity.setEmail(email);

        userRepository.save(userEntity);

        return userMapper.domainEntityToDto(userEntity);
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) throw new UsernameNotFoundException(userId);

        userRepository.deleteByUserId(userId);
    }

    @Override
    public Collection<UserDto> getUsers(int page, int limit, boolean confirmed) {

        PageRequest pageRequest = PageRequest.of(page, limit);

        Page<UserEntity> userEntities = confirmed
                                        ? userRepository.findAllUsersWithConfirmedEmailAddress(pageRequest)
                                        : userRepository.findAll(pageRequest);

        return userMapper.domainEntityToDto(userEntities.getContent());
    }

    @Override
    public boolean verifyEmailToken(String token) {

        UserEntity userEntity = userRepository.findByEmailVerificationToken(token);
        if (userEntity == null) return false;

        boolean hasTokenExpired = hasTokenExpired(token);
        if (hasTokenExpired) return false;

        userEntity.setEmailVerificationToken(null);
        userEntity.setEmailVerificationStatus(Boolean.TRUE);
        userRepository.save(userEntity);

        return true;

    }

    @Override
    public boolean requestPasswordReset(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) return false;

        String verificationToken = generateVerificationToken(userEntity.getUserId());

        PasswordResetTokenEntity passwordResetTokenEntity =
                PasswordResetTokenEntity.builder()
                                        .token(verificationToken)
                                        .userDetails(userEntity)
                                        .build();

        passwordResetTokenRepository.save(passwordResetTokenEntity);

        emailService.sendPasswordResetRequest(userEntity.getEmail(),
                verificationToken);

        return true;
    }

    @Override
    public boolean resetPassword(String token, String password) {
        if (hasTokenExpired(token)) return false;

        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);
        if (passwordResetTokenEntity == null) return false;

        String encodedPassword = bCryptPasswordEncoder.encode(password);

        UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
        userEntity.setEncryptedPassword(encodedPassword);
        UserEntity savedUserEntity = userRepository.save(userEntity);

        if (!savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) return false;

        passwordResetTokenRepository.delete(passwordResetTokenEntity);

        return true;
    }

    private boolean hasTokenExpired(String token) {
        Claims claims = Jwts.parser()
                            .setSigningKey(SecurityConstants.TOKEN_SECRET)
                            .parseClaimsJws(token).getBody();

        Date tokenExpirationDate = claims.getExpiration();
        Date currentDate = new Date();

        return tokenExpirationDate.before(currentDate);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new UserPrincipal(userEntity);

    }

    private String generateVerificationToken(String userId) {
        return Jwts.builder()
                   .setSubject(userId)
                   .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                   .signWith(SignatureAlgorithm.HS512, SecurityConstants.TOKEN_SECRET)
                   .compact();
    }

}
