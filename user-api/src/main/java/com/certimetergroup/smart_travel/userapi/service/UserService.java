package com.certimetergroup.smart_travel.userapi.service;


import com.certimetergroup.smart_travel.userapi.dto.PagedResDTO;
import com.certimetergroup.smart_travel.userapi.dto.UserReqDTO;
import com.certimetergroup.smart_travel.userapi.dto.UserResDTO;
import com.certimetergroup.smart_travel.userapi.filter.UserFilter;
import jakarta.validation.Valid;
import java.util.List;
import reactor.core.publisher.Mono;

public interface UserService {

  Mono<PagedResDTO<UserResDTO>> getUsers(
      int page,
      int size,
      String sort,
      String order,
      UserFilter filters
  );

  Mono<UserResDTO> getUserById(String id);

  Mono<UserResDTO> getUserByEmail(String email);

  Mono<List<UserResDTO>> getUsersList(String name, boolean excludeCustomers);

  Mono<UserResDTO> createUser(@Valid UserReqDTO userReqDTO);

  Mono<UserResDTO> updateUser(String id, @Valid UserReqDTO userReqDTO);

  Mono<Void> deleteUser(String id);
}
