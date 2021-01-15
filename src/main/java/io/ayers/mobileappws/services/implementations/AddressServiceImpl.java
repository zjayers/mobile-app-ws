package io.ayers.mobileappws.services.implementations;

import io.ayers.mobileappws.models.dtos.AddressDto;
import io.ayers.mobileappws.models.dtos.UserDto;
import io.ayers.mobileappws.models.entities.UserEntity;
import io.ayers.mobileappws.models.mappings.UserMapper;
import io.ayers.mobileappws.repositories.UserRepository;
import io.ayers.mobileappws.services.interfaces.AddressService;
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
