package com.certimetergroup.smart_travel.userapi.service;

import com.certimetergroup.smart_travel.userapi.dto.PagedResDTO;
import com.certimetergroup.smart_travel.userapi.dto.UserReqDTO;
import com.certimetergroup.smart_travel.userapi.dto.UserResDTO;
import com.certimetergroup.smart_travel.userapi.enumeration.UserRoleEnum;
import com.certimetergroup.smart_travel.userapi.exception.FailureException;
import com.certimetergroup.smart_travel.userapi.exception.ResponseEnum;
import com.certimetergroup.smart_travel.userapi.filter.UserFilter;
import com.certimetergroup.smart_travel.userapi.mapper.UserMapper;
import com.certimetergroup.smart_travel.userapi.model.User;
import com.certimetergroup.smart_travel.userapi.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final ReactiveMongoTemplate mongoTemplate;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public Mono<PagedResDTO<UserResDTO>> getUsers(int page, int size, String sort, String order,
      UserFilter filters) {
    Sort.Direction direction = order.equalsIgnoreCase("desc") ?
        Sort.Direction.DESC :
        Sort.Direction.ASC;
    Sort sortBy = Sort.by(direction, sort);
    long skip = (long) page * size;

    // Filter query
    Query query = new Query().with(sortBy).skip(skip).limit(size);
    Criteria criteria = new Criteria();

    List<Criteria> criteriaList = new ArrayList<>();

    // User ID filter
    if (filters.getId() != null && !filters.getId().isBlank()) {
      try {
        criteriaList.add(
            Criteria.where("id").is(new ObjectId(filters.getId()))
        );
      } catch (IllegalArgumentException e) {
        // Invalid ObjectId, ignore this filter
      }
    }

    // Name filter
    if (filters.getName() != null && !filters.getName().isBlank()) {
      String regex = "(?i).*" + Pattern.quote(filters.getName()) + ".*";
      criteriaList.add(new Criteria().orOperator(
          Criteria.where("firstname").regex(regex),
          Criteria.where("lastname").regex(regex)
      ));
    }

    // Email filter
    if (filters.getEmail() != null && !filters.getEmail().isBlank()) {
      String regex = "(?i).*" + Pattern.quote(filters.getEmail()) + ".*";
      criteriaList.add(Criteria.where("email").regex(regex));
    }

    // Role filter
    if (filters.getRole() != null) {
      criteriaList.add(Criteria.where("role").is(filters.getRole().toString()));
    }

    if (!criteriaList.isEmpty()) {
      criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
      query.addCriteria(criteria);
    }

    Mono<List<User>> contentMono = mongoTemplate.find(query, User.class).collectList();

    // Count query
    Query countQuery = new Query();
    if (!criteriaList.isEmpty()) {
      countQuery.addCriteria(criteria);
    }
    Mono<Long> totalCountMono = mongoTemplate.count(countQuery, User.class);

    return Mono.zip(contentMono, totalCountMono)
        .map(tuple -> {
          List<User> users = tuple.getT1();
          long totalElements = tuple.getT2();
          int totalPages = (int) Math.ceil((double) totalElements / size);
          int elementsInPage = users.size();

          return PagedResDTO.<UserResDTO>builder()
              .content(users.stream().map(userMapper::toDto).toList())
              .totalElements(totalElements)
              .totalPages(totalPages)
              .currentPage(page)
              .elementsInPage(elementsInPage)
              .build();
        });
  }

  @Override
  public Mono<UserResDTO> getUserById(String id) {
    return userRepository.findById(id)
        .map(userMapper::toDto)
        .switchIfEmpty(
            Mono.error(new FailureException(
                ResponseEnum.USER_NOT_FOUND,
                "User with id " + id + " not found")
            )
        );
  }

  @Override
  public Mono<UserResDTO> getUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(userMapper::toDto)
        .switchIfEmpty(
            Mono.error(new FailureException(
                ResponseEnum.USER_NOT_FOUND,
                "User with email " + email + " not found")
            )
        );
  }

  @Override
  public Mono<List<UserResDTO>> getUsersList(String name, boolean excludeCustomers) {
    Flux<User> usersFlux;

    if (name == null || name.isBlank()) {
      return Mono.just(Collections.emptyList());
    }
    String[] tokens = name.trim().split("\\s+");

    // For firstname or surname
    if (tokens.length == 1) {
      usersFlux = userRepository
          .findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(tokens[0], tokens[0])
          .take(10);
    } else {
      usersFlux = Flux.fromArray(tokens)
          .flatMap(token -> userRepository
              .findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(token, token))
          .distinct() // remove duplicates
          .take(10);
    }

    return usersFlux
        .filter(user -> !excludeCustomers ||
            !user.getRole().equals(UserRoleEnum.CUSTOMER))
        .map(userMapper::toDto)
        .collectList();
  }

  @Override
  public Mono<UserResDTO> createUser(@Valid UserReqDTO userReqDTO) {
    // Hash password
    userReqDTO.setPassword(
        BCrypt.hashpw(userReqDTO.getPassword(), BCrypt.gensalt(12))
    );
    return userRepository.existsByEmail(userReqDTO.getEmail())
        .flatMap(exists -> {
          if (Boolean.TRUE.equals(exists)) {
            return Mono.error(new FailureException(
                ResponseEnum.EMAIL_ALREADY_EXISTS,
                "A user with email " + userReqDTO.getEmail() + " already exists"
            ));
          }
          User userToSave = userMapper.toEntity(userReqDTO);
          return userRepository.save(userToSave)
              .map(userMapper::toDto)
              .switchIfEmpty(
                  Mono.error(new FailureException(
                      ResponseEnum.UNEXPECTED_ERROR,
                      "Error while creating user"
                  ))
              );
        });
  }

  @Override
  public Mono<UserResDTO> updateUser(String id, @Valid UserReqDTO userReqDTO) {
    return getUserEntityById(id)
        .flatMap(existingUser -> {
          User updatedUser = userMapper.toEntity(userReqDTO);
          updatedUser.setId(existingUser.getId());
          return userRepository.save(updatedUser);
        })
        .map(userMapper::toDto);
  }

  @Override
  public Mono<Void> deleteUser(String id) {
    return getUserEntityById(id)
        .flatMap(user ->
            userRepository.deleteById(user.getId())
        );
  }

  private Mono<User> getUserEntityById(String id) {
    return userRepository.findById(id)
        .switchIfEmpty(Mono.error(new FailureException(
            ResponseEnum.USER_NOT_FOUND,
            "User with id " + id + " not found")
        ));
  }
}
