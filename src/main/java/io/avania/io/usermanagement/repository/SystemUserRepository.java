package io.avania.io.usermanagement.repository;

import com.eclectics.io.usermodule.model.SystemUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author David C Makuba
 * @created 25/01/2023
 **/
public interface SystemUserRepository extends JpaRepository<SystemUser,Long> {
    Optional<SystemUser> findTopByEmail(String email);
    Optional<SystemUser> findTopById(long userId);

    Page<SystemUser> findAllByBlockedFalse(Pageable pageable);

    Page<SystemUser> findAllByBlockedTrue(Pageable pageable);
    Page<SystemUser> findAllByProfileIdAndSoftDeleteFalseAndBlockedFalse(Pageable pageable, long profileId);

    boolean existsByProfileId(long profileId);

    Optional<SystemUser> findTopByItemId(long userId);
}
