package io.ayers.mobileappws.models.mappings;

import io.ayers.mobileappws.models.dtos.AddressDto;
import io.ayers.mobileappws.models.dtos.UserDto;
import io.ayers.mobileappws.models.entities.AddressEntity;
import io.ayers.mobileappws.models.entities.UserEntity;
import io.ayers.mobileappws.models.requests.UserDetailsRequestModel;
import io.ayers.mobileappws.models.responses.AddressResponseModel;
import io.ayers.mobileappws.models.responses.UserDetailsResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {


    // Step 1: Request Model To DTO
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "encryptedPassword", ignore = true)
    @Mapping(target = "emailVerificationToken", ignore = true)
    @Mapping(target = "emailVerificationStatus", ignore = true)
    UserDto requestModelToDto(UserDetailsRequestModel userDetailsRequestModel);

    // Step 2: DTO to Domain Entity
    UserEntity dtoToDomainEntity(UserDto userDto);

    // Step 3: Domain Entity to DTO@Mapping(target = "password", ignore = true)
    //    UserDto domainEntityToDto(UserEntity userEntity);
    @Mapping(target = "password", ignore = true)
    @Mapping(source = "addresses", target = "addresses", qualifiedByName = "addressWithoutUsers")
    UserDto domainEntityToDto(UserEntity userEntity);

    @Mapping(source = "addresses", target = "addresses", qualifiedByName = "addressWithoutUsers")
    Collection<UserDto> domainEntityToDto(Collection<UserEntity> userEntity);

    // Step 4: DTO to Response Model
    UserDetailsResponseModel dtoToResponseModel(UserDto userDto);

    Collection<UserDetailsResponseModel> dtoToResponseModel(Collection<UserDto> userDto);

    @Mapping(target = "userDetails", ignore = true)
    AddressDto addressEntityToDto(AddressEntity addressEntity);

    @Mapping(target = "userDetails", ignore = true)
    List<AddressResponseModel> addressDtoToResponse(Collection<AddressDto> addressDto);

    AddressResponseModel addressDtoToResponse(AddressDto addressDto);

    @Named("addressWithoutUsers")
    default List<AddressDto> addressEntityListToDtoList(List<AddressEntity> addressEntities) {
        return addressEntities.stream().map(this::addressEntityToDto).collect(Collectors.toList());
    }
}


