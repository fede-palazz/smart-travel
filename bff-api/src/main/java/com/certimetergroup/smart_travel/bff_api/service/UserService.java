package com.certimetergroup.smart_travel.bff_api.service;

import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.OkResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response.PagedResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.user.request.UserReqDTO;
import com.certimetergroup.smart_travel.bff_api.dto.user.response.UserNoPwdResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.user.response.UserResDTO;
import com.certimetergroup.smart_travel.bff_api.filter.UserFilter;
import java.util.List;
import reactor.core.publisher.Mono;

public interface UserService {

  Mono<PagedResDTO<UserNoPwdResDTO>> getUsers(Integer page, Integer size, String sort,
      String order, UserFilter filters);

  Mono<UserResDTO> getActualUserById(String id);

  Mono<UserNoPwdResDTO> getUserById(String id);

  Mono<UserResDTO> getActualUserByEmail(String email);

  Mono<UserNoPwdResDTO> getUserByEmail(String email);

  Mono<List<UserNoPwdResDTO>> getUsersList(String name, boolean excludeCustomers);

  Mono<UserNoPwdResDTO> addUser(UserReqDTO userReq);

  Mono<UserNoPwdResDTO> updateUser(String id, UserReqDTO userReq);

  Mono<OkResDTO> deleteUser(String id);

}
