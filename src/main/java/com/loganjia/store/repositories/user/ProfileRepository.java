package com.loganjia.store.repositories.user;

import com.loganjia.store.entities.user.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
