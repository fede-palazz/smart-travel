package com.certimetergroup.smart_travel.userapi.mapper;


import com.certimetergroup.smart_travel.userapi.dto.UserReqDTO;
import com.certimetergroup.smart_travel.userapi.dto.UserResDTO;
import com.certimetergroup.smart_travel.userapi.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "fullname", expression = "java(firstname + \" \" + lastname)")
  UserResDTO toDto(User user);

  @Mapping(target = "id", ignore = true)
  User toEntity(UserReqDTO userReqDTO);

}
