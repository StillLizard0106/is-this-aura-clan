package com.is_this_aura_clan.CanteenQ.account;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

	Optional<UserAccount> findByFirebaseUid(String firebaseUid);

	Optional<UserAccount> findByEmail(String email);

	boolean existsByStudentId(String studentId);
}
