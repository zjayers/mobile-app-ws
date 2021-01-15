package io.ayers.mobileappws.filters;

import io.ayers.mobileappws.constants.SecurityConstants;
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
import java.util.ArrayList;

public class AuthorizationFilter
        extends BasicAuthenticationFilter {

    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
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

        return userEmail != null
               ? new UsernamePasswordAuthenticationToken(userEmail, null, new ArrayList<>())
               : null;
    }
}
