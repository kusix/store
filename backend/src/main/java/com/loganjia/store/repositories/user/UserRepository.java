package com.loganjia.store.repositories.user;

import com.loganjia.store.entities.user.Tag;
import com.loganjia.store.entities.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends CrudRepository<User, Long> {


    @EntityGraph(attributePaths = {"tags","addresses","wishlist"})
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = "addresses")
    List<User> findAll();

}
