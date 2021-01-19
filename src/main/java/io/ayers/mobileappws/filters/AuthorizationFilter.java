package io.ayers.mobileappws.filters;

import io.ayers.mobileappws.constants.SecurityConstants;
import io.ayers.mobileappws.models.entities.UserEntity;
import io.ayers.mobileappws.repositories.UserRepository;
import io.ayers.mobileappws.security.UserPrincipal;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter
        extends BasicAuthenticationFilter {

    private final UserRepository userRepository;

    public AuthorizationFilter(AuthenticationManager authenticationManager,
                               UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String headerToken = request.getHeader(SecurityConstants.HEADER_STRING);

        if (headerToken != null && headerToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {

            UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(headerToken);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        }

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(String headerToken) {
        String parsableToken = headerToken.replace(SecurityConstants.TOKEN_PREFIX, "");

        String userEmail = Jwts.parser()
                               .setSigningKey(SecurityConstants.TOKEN_SECRET)
                               .parseClaimsJws(parsableToken)
                               .getBody()
                               .getSubject();

        if (userEmail == null) return null;

        UserEntity userEntity = userRepository.findByEmail(userEmail);
        UserPrincipal userPrincipal = new UserPrincipal(userEntity);

        return new UsernamePasswordAuthenticationToken(userEmail, null, userPrincipal.getAuthorities());
    }
}
