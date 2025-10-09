package com.certimetergroup.smart_travel.userapi.repository;

import com.certimetergroup.smart_travel.userapi.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

  Mono<User> findByEmail(String email);

  Mono<Boolean> existsByEmail(String email);

  Flux<User> findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(String firstName,
      String lastName);

}
