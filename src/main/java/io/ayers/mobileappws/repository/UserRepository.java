package io.ayers.mobileappws.repository;

import io.ayers.mobileappws.domain.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
        extends PagingAndSortingRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);

    void deleteByUserId(String userId);
}
