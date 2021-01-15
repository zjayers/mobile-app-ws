package io.ayers.mobileappws.repositories;

import io.ayers.mobileappws.models.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
        extends PagingAndSortingRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);

    void deleteByUserId(String userId);

    UserEntity findByEmailVerificationToken(String token);

    @Query(value = "SELECT * FROM users WHERE users.email_verification_status IS TRUE",
            countQuery = "SELECT COUNT(*) FROM users WHERE users.email_verification_status IS TRUE",
            nativeQuery = true)
    Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);

    //    @Query(value = "SELECT * FROM users WHERE users.first_name = :firstName AND users.last_name = :lastName", nativeQuery = true)
    @Query(value = "select user from UserEntity user where user.firstName = :firstName and  user.lastName = :lastName")
    UserEntity findByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);
}
