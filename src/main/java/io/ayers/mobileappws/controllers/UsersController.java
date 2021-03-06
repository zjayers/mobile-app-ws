package io.ayers.mobileappws.controllers;

import io.ayers.mobileappws.models.dtos.AddressDto;
import io.ayers.mobileappws.models.dtos.UserDto;
import io.ayers.mobileappws.models.mappings.UserMapper;
import io.ayers.mobileappws.models.requests.PasswordResetModel;
import io.ayers.mobileappws.models.requests.PasswordResetRequestModel;
import io.ayers.mobileappws.models.requests.UserDetailsRequestModel;
import io.ayers.mobileappws.models.responses.AddressResponseModel;
import io.ayers.mobileappws.models.responses.OperationStatusResponseModel;
import io.ayers.mobileappws.models.responses.UserDetailsResponseModel;
import io.ayers.mobileappws.services.interfaces.AddressService;
import io.ayers.mobileappws.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
            @RequestParam(value = "limit", defaultValue = "25") int limit,
            @RequestParam(value = "confirmed", defaultValue = "false") boolean confirmed) {

        Collection<UserDto> userDtos = userService.getUsers(page, limit, confirmed);
        Collection<UserDetailsResponseModel> userDetailsResponseModels = userMapper.dtoToResponseModel(userDtos);
        return ResponseEntity.status(HttpStatus.OK)
                             .body(userDetailsResponseModels);
    }

    @GetMapping("/{userId}")
    @PreAuthorize(value = "(hasRole('USER') and #userId == principal.userId) or hasRole('ADMIN')")
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

    @GetMapping(path = "/email-verification")
    public ResponseEntity<OperationStatusResponseModel> getEmailVerificationToken(@RequestParam(value = "token") String token) {

        boolean isVerified = userService.verifyEmailToken(token);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(OperationStatusResponseModel.builder()
                                                               .operationName("EMAIL VERIFICATION")
                                                               .operationResult(isVerified
                                                                                ? "SUCCESS"
                                                                                : "FAILURE")
                                                               .build());
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserDetailsResponseModel> createOneUser(@RequestBody UserDetailsRequestModel userDetailsRequestModel) {

        UserDto userDto = userMapper.requestModelToDto(userDetailsRequestModel);
        UserDto savedUserDto = userService.createUser(userDto);
        UserDetailsResponseModel userDetailsResponseModel = userMapper.dtoToResponseModel(savedUserDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(userDetailsResponseModel);
    }

    @PostMapping(path = "/password-reset",
            consumes = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<OperationStatusResponseModel> resetPasswordRequest(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {

        boolean passwordResetEmailSent
                = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        return ResponseEntity.status(HttpStatus.OK)
                             .body(OperationStatusResponseModel.builder()
                                                               .operationName("REQUEST PASSWORD RESET")
                                                               .operationResult(passwordResetEmailSent
                                                                                ? "SUCCESS"
                                                                                : "FAILURE")
                                                               .build());
    }

    @PostMapping(path = "/do-password-reset",
            consumes = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<OperationStatusResponseModel> resetPassword(@RequestBody PasswordResetModel passwordResetModel) {

        boolean passwordResetSuccessful
                = userService.resetPassword(passwordResetModel.getToken(), passwordResetModel.getPassword());

        return ResponseEntity.status(HttpStatus.OK)
                             .body(OperationStatusResponseModel.builder()
                                                               .operationName("PASSWORD RESET")
                                                               .operationResult(passwordResetSuccessful
                                                                                ? "SUCCESS"
                                                                                : "FAILURE")
                                                               .build());
    }


    @PreAuthorize(value = "(hasRole('USER') and #userId == principal.userId) or hasRole('ADMIN')")
    @PutMapping(path = "/{userId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserDetailsResponseModel> updateOneUser(@PathVariable(name = "userId") String userId,
                                                                  @RequestBody UserDetailsRequestModel userDetailsRequestModel) {

        UserDto userDto = userMapper.requestModelToDto(userDetailsRequestModel);
        UserDto updatedUserDto = userService.updateUser(userId, userDto);
        UserDetailsResponseModel userDetailsResponseModel = userMapper.dtoToResponseModel(updatedUserDto);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(userDetailsResponseModel);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize(value = "(hasRole('USER') and #userId == principal.userId) or hasRole('ADMIN')")
    public ResponseEntity<OperationStatusResponseModel> deleteOneUser(@PathVariable(name = "userId") String userId) {

        userService.deleteUser(userId);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(OperationStatusResponseModel.builder()
                                                               .operationName("DELETE")
                                                               .operationResult("SUCCESS")
                                                               .build());
    }

}
