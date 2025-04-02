package com.loganjia.store.repositories.user;

import com.loganjia.store.entities.user.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface AddressRepository extends JpaRepository<Address, Long> {
}
