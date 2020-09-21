package com.pokemoncards.model.repository.account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.pokemoncards.model.entity.AccountId;
import com.pokemoncards.model.entity.Verification;

public interface VerificationRepository extends JpaRepository<Verification, AccountId> {

	@Query(value = "select verification from Verification verification where verification.token=:token")
	public Optional<Verification> findByToken(@Param(value = "token") String token);

	@Query(value = "select verification from Verification verification where verification.username=:username")
	public Optional<Verification> findByUsername(@Param(value = "username") String username);

	@Transactional
	@Modifying
	@Query(value = "update Verification verification set verification.token=:token where verification.username=:username")
	public void updateVerification(@Param(value = "username") String username, @Param(value = "token") String newToken);

}