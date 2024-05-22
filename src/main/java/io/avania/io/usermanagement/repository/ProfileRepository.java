package io.avania.io.usermanagement.repository;


import com.eclectics.io.usermodule.model.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile,Long> {
    Optional<Profile> findByIdAndSoftDeleteFalse(long profileId);

    Optional<Profile> findByNameAndSoftDeleteFalse(String profileName);

    Page<Profile> findAllBySoftDeleteFalse(Pageable pageable);


}
