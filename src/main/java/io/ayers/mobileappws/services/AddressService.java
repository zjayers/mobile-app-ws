package io.ayers.mobileappws.services;

import io.ayers.mobileappws.shared.AddressDto;

import java.util.Collection;

public interface AddressService {
    Collection<AddressDto> getUserAddresses(String userId);
}
