package io.ayers.mobileappws.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ayers.mobileappws.config.constants.SecurityConstants;
import io.ayers.mobileappws.services.UserService;
import io.ayers.mobileappws.shared.UserDto;
import io.ayers.mobileappws.ui.model.request.UserLoginRequestModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@RequiredArgsConstructor
public class AuthenticationFilter
        extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        UserLoginRequestModel userLoginRequestModel;

        try {

            userLoginRequestModel = new ObjectMapper().readValue(request.getInputStream(),
                    UserLoginRequestModel.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequestModel.getEmail(),
                        userLoginRequestModel.getPassword(),
                        new ArrayList<>()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {

        String email = ((User) authResult.getPrincipal()).getUsername();

        String token = Jwts.builder()
                           .setSubject(email)
                           .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                           .signWith(
                                   SignatureAlgorithm.HS512, SecurityConstants.TOKEN_SECRET)
                           .compact();

        UserDto userDto = userService.getUserDetailsByEmail(email);

        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        response.addHeader("User", userDto.getUserId());

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
