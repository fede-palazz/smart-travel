package com.certimetergroup.smart_travel.bff_api.mapper;


import com.certimetergroup.smart_travel.bff_api.dto.user.response.UserNoPwdResDTO;
import com.certimetergroup.smart_travel.bff_api.dto.user.response.UserResDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import shared.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "fullname", expression = "java(firstname + \" \" + lastname)")
  UserNoPwdResDTO toNoPwdResDTO(UserResDTO userResDTO);

  @Mapping(target = "fullname", expression = "java(firstname + \" \" + lastname)")
  UserNoPwdResDTO toNoPwdResDTO(User user);


}
