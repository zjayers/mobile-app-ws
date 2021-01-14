package io.ayers.mobileappws.repository;

import io.ayers.mobileappws.domain.AddressEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository
        extends CrudRepository<AddressEntity, Long> {
}
