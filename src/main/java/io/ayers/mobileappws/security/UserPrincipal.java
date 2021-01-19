package io.ayers.mobileappws.security;

import io.ayers.mobileappws.models.entities.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserPrincipal

        implements UserDetails {

    private static final long serialVersionUID = -435710639205414406L;

    UserEntity userEntity;

    public UserPrincipal(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        userEntity.getRoles().forEach(roleEntity -> {

            grantedAuthorities.add(new SimpleGrantedAuthority(roleEntity.getName()));

            roleEntity.getAuthorities().forEach(a -> {
                grantedAuthorities.add(new SimpleGrantedAuthority(a.getName()));
            });

        });

        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return userEntity.getEncryptedPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userEntity.getEmailVerificationStatus();
    }
}
