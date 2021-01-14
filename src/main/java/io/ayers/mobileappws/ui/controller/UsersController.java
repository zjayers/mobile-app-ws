package io.ayers.mobileappws.ui.controller;

import io.ayers.mobileappws.mapping.UserMapper;
import io.ayers.mobileappws.services.AddressService;
import io.ayers.mobileappws.services.UserService;
import io.ayers.mobileappws.shared.AddressDto;
import io.ayers.mobileappws.shared.UserDto;
import io.ayers.mobileappws.ui.model.request.UserDetailsRequestModel;
import io.ayers.mobileappws.ui.model.response.AddressResponseModel;
import io.ayers.mobileappws.ui.model.response.OperationStatusModel;
import io.ayers.mobileappws.ui.model.response.UserDetailsResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(
        path = "/users",
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
public class UsersController {

    private final UserMapper userMapper;
    private final UserService userService;
    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<Collection<UserDetailsResponseModel>> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "25") int limit) {

        Collection<UserDto> userDtos = userService.getUsers(page, limit);
        Collection<UserDetailsResponseModel> userDetailsResponseModels = userMapper.dtoToResponseModel(userDtos);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(userDetailsResponseModels);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsResponseModel> getOne(@PathVariable(name = "userId") String userId) {

        UserDto userDto = userService.getUserDetailsByUserId(userId);
        UserDetailsResponseModel userDetailsResponseModel = userMapper.dtoToResponseModel(userDto);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(userDetailsResponseModel);
    }

    @GetMapping("/{userId}/addresses")
    public ResponseEntity<CollectionModel<EntityModel<AddressResponseModel>>> getUserAddresses(
            @PathVariable(name = "userId") String userId
    ) {

        Collection<AddressDto> addressDtos = addressService.getUserAddresses(userId);
        Collection<AddressResponseModel> addressResponseModels = userMapper.addressDtoToResponse(addressDtos);
        List<EntityModel<AddressResponseModel>> addresses = addressResponseModels.stream().map(addressResponseModel -> {

            Link selfLink = WebMvcLinkBuilder
                    .linkTo(methodOn(UsersController.class).getUserAddress(userId, addressResponseModel.getAddressId()))
                    .withSelfRel();
            return EntityModel.of(addressResponseModel, selfLink);

        }).collect(Collectors.toList());

        //HATEOAS
        Link userLink = WebMvcLinkBuilder
                .linkTo(methodOn(UsersController.class).getOne(userId))
                .withRel("user");

        Link selfLink = WebMvcLinkBuilder
                .linkTo(methodOn(UsersController.class).getUserAddresses(userId))
                .withSelfRel();

        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(addresses, userLink,
                selfLink));
    }

    @GetMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<EntityModel<AddressResponseModel>> getUserAddress(
            @PathVariable(name = "userId") String userId,
            @PathVariable(name = "addressId") String addressId
    ) {

        Collection<AddressDto> addressDtos = addressService.getUserAddresses(userId);
        AddressDto dto = addressDtos.stream()
                                    .filter(addressDto -> addressDto.getAddressId().equals(addressId))
                                    .findFirst()
                                    .orElse(null);

        AddressResponseModel addressResponseModel = userMapper.addressDtoToResponse(dto);

        //HATEOAS
        Link userLink = WebMvcLinkBuilder
                .linkTo(methodOn(UsersController.class).getOne(userId))
                .withRel("user");

        Link addressesLink = WebMvcLinkBuilder
                .linkTo(methodOn(UsersController.class).getUserAddresses(userId))
                .withRel("addresses");

        Link selfLink = WebMvcLinkBuilder
                .linkTo(methodOn(UsersController.class).getUserAddress(userId, addressId))
                .withSelfRel();


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(EntityModel.of(addressResponseModel, List.of(userLink,
                        addressesLink, selfLink)));
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserDetailsResponseModel> createOne(@RequestBody UserDetailsRequestModel userDetailsRequestModel) {

        UserDto userDto = userMapper.requestModelToDto(userDetailsRequestModel);
        UserDto savedUserDto = userService.createUser(userDto);
        UserDetailsResponseModel userDetailsResponseModel = userMapper.dtoToResponseModel(savedUserDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(userDetailsResponseModel);
    }

    @PutMapping(path = "/{userId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserDetailsResponseModel> updateOne(@PathVariable(name = "userId") String userId,
                                                              @RequestBody UserDetailsRequestModel userDetailsRequestModel) {

        UserDto userDto = userMapper.requestModelToDto(userDetailsRequestModel);
        UserDto updatedUserDto = userService.updateUser(userId, userDto);
        UserDetailsResponseModel userDetailsResponseModel = userMapper.dtoToResponseModel(updatedUserDto);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(userDetailsResponseModel);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<OperationStatusModel> deleteOne(@PathVariable(name = "userId") String userId) {

        userService.deleteUser(userId);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(OperationStatusModel.builder()
                                                       .operationName("DELETE")
                                                       .operationResult("SUCCESS")
                                                       .build());
    }

}
