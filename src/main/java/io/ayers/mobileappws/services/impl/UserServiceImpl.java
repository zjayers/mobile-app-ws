package io.ayers.mobileappws.services.impl;

import io.ayers.mobileappws.domain.UserEntity;
import io.ayers.mobileappws.mapping.UserMapper;
import io.ayers.mobileappws.repository.UserRepository;
import io.ayers.mobileappws.services.UserService;
import io.ayers.mobileappws.shared.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl
        implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public UserDto createUser(UserDto userDto) {

        String userDtoEmail = userDto.getEmail();
        UserEntity existingUserEntity = userRepository.findByEmail(userDtoEmail);
        if (existingUserEntity != null) throw new RuntimeException("User already exists with email: " + userDtoEmail);

        UserEntity userEntity = userMapper.dtoToDomainEntity(userDto);

        String generatedUserId = UUID.randomUUID().toString();
        String encodedPassword = bCryptPasswordEncoder.encode(userDto.getPassword());

        userEntity.setUserId(generatedUserId);
        userEntity.setEncryptedPassword(encodedPassword);

        UserEntity savedUserEntity = userRepository.save(userEntity);

        return userMapper.domainEntityToDto(savedUserEntity);
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        return userMapper.domainEntityToDto(userEntity);
    }

    @Override
    public UserDto getUserDetailsByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        return userMapper.domainEntityToDto(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new User(
                userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                new ArrayList<>());

    }
}
