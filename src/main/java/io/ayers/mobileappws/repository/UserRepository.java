package io.ayers.mobileappws.repository;

import io.ayers.mobileappws.domain.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
        extends CrudRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
}