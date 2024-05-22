package io.avania.io.usermanagement.repository;

import com.eclectics.io.usermodule.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Page<Role> findAllBySoftDeleteFalse(Pageable pageable);
    List<Role> findAllBySoftDeleteFalse();
    Optional<Role> findByName(String name);
    Optional<Role> findByIdAndSoftDeleteFalse(long roleId);
    List<Role> findAllByName(String name);
}
