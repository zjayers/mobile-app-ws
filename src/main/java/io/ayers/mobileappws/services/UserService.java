package io.ayers.mobileappws.services;

import io.ayers.mobileappws.shared.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService
        extends UserDetailsService {
    UserDto createUser(UserDto userDto);
}
