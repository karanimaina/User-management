package io.avania.io.usermanagement.repository;

import com.eclectics.io.usermodule.model.ProfileRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRoleRepository extends JpaRepository<ProfileRoles,Long> {
    List<ProfileRoles> findAllByProfileIdAndSoftDeleteFalse(long profileId);

    boolean existsByProfileIdAndRoleIdAndSoftDeleteFalse(long profileId, long roleId);

    Optional<ProfileRoles> findByProfileIdAndRoleIdAndSoftDeleteFalse(long profileId, long roleId);
    boolean existsByRoleIdAndSoftDeleteFalse(long roleId);
    List<ProfileRoles> findAllByProfileIdAndRoleId(long profileId, long roleId);
    ProfileRoles findTopByRoleId(long roleId);
}
