package io.ayers.mobileappws.services.impl;

import io.ayers.mobileappws.domain.UserEntity;
import io.ayers.mobileappws.mapping.UserMapper;
import io.ayers.mobileappws.repository.UserRepository;
import io.ayers.mobileappws.services.AddressService;
import io.ayers.mobileappws.shared.AddressDto;
import io.ayers.mobileappws.shared.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl
        implements AddressService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Collection<AddressDto> getUserAddresses(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        UserDto userDto = userMapper.domainEntityToDto(userEntity);
        return userDto.getAddresses();
    }
}
