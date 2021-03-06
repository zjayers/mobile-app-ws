package io.ayers.mobileappws.services.interfaces;

import io.ayers.mobileappws.models.dtos.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;

public interface UserService
        extends UserDetailsService {
    UserDto createUser(UserDto userDto);

    UserDto getUserDetailsByEmail(String email);

    UserDto getUserDetailsByUserId(String userId);

    UserDto updateUser(String userId, UserDto userDto);

    void deleteUser(String userId);

    Collection<UserDto> getUsers(int page, int limit, boolean confirmed);

    boolean verifyEmailToken(String token);

    boolean requestPasswordReset(String email);

    boolean resetPassword(String token, String password);
}
