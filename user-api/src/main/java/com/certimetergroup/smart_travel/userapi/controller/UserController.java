package com.certimetergroup.smart_travel.userapi.controller;

import com.certimetergroup.smart_travel.userapi.dto.PagedResDTO;
import com.certimetergroup.smart_travel.userapi.dto.UserReqDTO;
import com.certimetergroup.smart_travel.userapi.dto.UserResDTO;
import com.certimetergroup.smart_travel.userapi.enumeration.UserRoleEnum;
import com.certimetergroup.smart_travel.userapi.exception.FailureException;
import com.certimetergroup.smart_travel.userapi.exception.ResponseEnum;
import com.certimetergroup.smart_travel.userapi.filter.UserFilter;
import com.certimetergroup.smart_travel.userapi.model.User;
import com.certimetergroup.smart_travel.userapi.service.UserService;
import com.certimetergroup.smart_travel.userapi.validator.ControllerValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @Operation(
      summary = "Get all users",
      responses = @ApiResponse(responseCode = "200")
  )
  @GetMapping("")
  public Mono<ResponseEntity<PagedResDTO<UserResDTO>>> getUsers(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(defaultValue = "lastname") String sort,
      @RequestParam(defaultValue = "asc") String order,
      @ModelAttribute UserFilter filters
  ) {
    var allowedSortValues = List.of("email", "role", "firstname", "lastname");
    // Validate query params
    ControllerValidator.validate(size, page, sort, order, allowedSortValues);

    return userService.getUsers(page, size, sort, order, filters).map(ResponseEntity::ok);
  }

  @Operation(
      summary = "Get user by id",
      parameters = @Parameter(
          name = "id",
          description = "ID of the user to fetch",
          required = true,
          in = ParameterIn.PATH
      ),
      responses = {
          @ApiResponse(responseCode = "200"),
          @ApiResponse(responseCode = "400"),
          @ApiResponse(responseCode = "404", content = @Content())
      }
  )
  @GetMapping("id/{id}")
  public Mono<ResponseEntity<UserResDTO>> getUserById(
      @PathVariable String id) {
    // Check that id is valid
    try {
      ObjectId objectId = new ObjectId(id);
    } catch (IllegalArgumentException e) {
      throw new FailureException(ResponseEnum.INVALID_INPUT, "Invalid id format");
    }
    return userService.getUserById(id).map(ResponseEntity::ok);
  }

  @Operation(
      summary = "Get user by email",
      parameters = @Parameter(
          name = "email",
          description = "Email of the user to fetch",
          required = true,
          in = ParameterIn.PATH
      ),
      responses = {
          @ApiResponse(responseCode = "200"),
          @ApiResponse(responseCode = "400"),
          @ApiResponse(responseCode = "404", content = @Content())
      }
  )
  @GetMapping("email/{email}")
  public Mono<ResponseEntity<UserResDTO>> getUserByEmail(
      @PathVariable String email) {
    return userService.getUserByEmail(email).map(ResponseEntity::ok);
  }

  @Operation(
      summary = "Get user list suggestions",
      responses = {
          @ApiResponse(responseCode = "200"),
      }
  )
  @GetMapping("search")
  public Mono<ResponseEntity<List<UserResDTO>>> getUsersList(
      @RequestParam String name,
      @RequestParam(defaultValue = "false") Boolean excludeCustomers) {
    return userService.getUsersList(name, excludeCustomers).map(ResponseEntity::ok);
  }

  @Operation(
      summary = "Create a new user",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = {@Content(
              mediaType = "application/json",
              schema = @Schema(implementation = User.class)
          )}
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = {@Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UserResDTO.class)
              )}
          ),
          @ApiResponse(responseCode = "400")
      }
  )
  @PostMapping("")
  public Mono<ResponseEntity<UserResDTO>> addUser(@RequestBody UserReqDTO userReqDTO) {
    try {
      // Check that UserRole is valid
      UserRoleEnum.valueOf(userReqDTO.getRole());
    } catch (IllegalArgumentException e) {
      throw new FailureException(ResponseEnum.INVALID_INPUT,
          "Invalid role: " + userReqDTO.getRole());
    }
    return this.userService.createUser(userReqDTO)
        .map(ResponseEntity::ok);
  }

  @Operation(
      summary = "Update the information of an existing user",
      parameters = @Parameter(
          name = "id",
          description = "ID of the user to be updated",
          required = true,
          in = ParameterIn.PATH
      ),
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = {@Content(
              mediaType = "application/json",
              schema = @Schema(implementation = User.class)
          )}
      ),
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = {@Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UserResDTO.class)
              )}
          ),
          @ApiResponse(responseCode = "400"),
          @ApiResponse(responseCode = "404", content = @Content())
      }
  )
  @PutMapping("{id}")
  public Mono<ResponseEntity<UserResDTO>> updateUser(
      @PathVariable String id,
      @RequestBody UserReqDTO userReqDTO
  ) {
    // Check that id is valid
    try {
      ObjectId objectId = new ObjectId(id);
    } catch (IllegalArgumentException e) {
      throw new FailureException(
          ResponseEnum.INVALID_INPUT,
          "Invalid id format"
      );
    }
    // Check that UserRole is valid
    try {
      UserRoleEnum.valueOf(userReqDTO.getRole());
    } catch (IllegalArgumentException e) {
      throw new FailureException(ResponseEnum.INVALID_INPUT,
          "Invalid role: " + userReqDTO.getRole());
    }
    return userService.updateUser(id, userReqDTO)
        .map(ResponseEntity::ok);
  }

  @Operation(
      summary = "Delete user by id",
      parameters = @Parameter(
          name = "id",
          description = "ID of the user to be deleted",
          required = true,
          in = ParameterIn.PATH
      ),
      responses = {
          @ApiResponse(responseCode = "204"),
          @ApiResponse(responseCode = "400"),
          @ApiResponse(responseCode = "404", content = @Content())
      }
  )
  @DeleteMapping("{id}")
  public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
    // Check that id is valid
    try {
      ObjectId objectId = new ObjectId(id);
    } catch (IllegalArgumentException e) {
      return Mono.error(new FailureException(ResponseEnum.INVALID_INPUT, "Invalid id format"));
    }
    return userService.deleteUser(id)
        .then(Mono.just(ResponseEntity.noContent().build()));
  }
}
