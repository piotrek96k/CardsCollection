package com.pokemoncards.model.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.pokemoncards.exception.AccountAlreadyActivatedException;
import com.pokemoncards.exception.NotFoundException;
import com.pokemoncards.model.entity.Account;
import com.pokemoncards.model.entity.Cash;
import com.pokemoncards.model.entity.FreeCard;
import com.pokemoncards.model.entity.Role;
import com.pokemoncards.model.entity.RoleEnum;
import com.pokemoncards.model.entity.Verification;
import com.pokemoncards.model.repository.account.AccountRepository;
import com.pokemoncards.model.repository.account.CashRepository;
import com.pokemoncards.model.repository.account.FreeCardRepository;
import com.pokemoncards.model.repository.account.RoleRepository;
import com.pokemoncards.model.repository.account.VerificationRepository;

@Service
public class RegisterService {

	private static BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private CashRepository cashRepository;

	@Autowired
	private FreeCardRepository freeCardRepository;

	@Autowired
	private VerificationRepository verificationRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private Environment environment;

	public void addAccount(Account account) {
		Role role = roleRepository.getOne(RoleEnum.ROLE_USER.toString());
		List<Role> roles = new ArrayList<Role>();
		roles.add(role);
		account.setRoles(roles);
		account.setPassword(getPasswordEncoder().encode(account.getPassword()));
		account.setEnabled(false);
		accountRepository.save(account);
		setVerification(account);
		setCash(account);
		setFreeCard(account);
	}

	private void setVerification(Account account) {
		Verification verification = new Verification();
		verification.setAccount(account);
		verification.setUsername(account.getUsername());
		verification.setEmail(account.getEmail());
		verification.generateToken();
		verificationRepository.save(verification);
		sendVerificationEmail(verification.getToken(), account.getEmail());
	}

	private void sendVerificationEmail(String token, String email) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("Activate your account by clicking link below\n\n").append("https://")
					.append(InetAddress.getLocalHost().getHostAddress()).append(":")
					.append(environment.getProperty("server.port")).append("/register/").append(token);
			emailService.sendEmail(email, "Pokemon Cards Confirmation Link", builder.toString());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private void setCash(Account account) {
		Cash cash = new Cash();
		cash.setCoins(3_000);
		cash.setAccount(account);
		cash.setUsername(account.getUsername());
		cash.setEmail(account.getEmail());
		cash.setNextCoinsCollecting(LocalDateTime.now(ZoneOffset.UTC));
		cash.setDaysInRow(1);
		cashRepository.save(cash);
	}

	private void setFreeCard(Account account) {
		FreeCard freeCard = new FreeCard();
		freeCard.setAccount(account);
		freeCard.setUsername(account.getUsername());
		freeCard.setEmail(account.getEmail());
		freeCard.setNextFreeCard(LocalDateTime.now(ZoneOffset.UTC));
		freeCardRepository.save(freeCard);
	}

	public void activateAccount(String token) {
		Optional<Verification> verification = verificationRepository.findByToken(token);
		if (verification.isEmpty())
			throw new NotFoundException();
		if (verification.get().getAccount().isEnabled())
			throw new AccountAlreadyActivatedException();
		accountRepository.activateAccount(verification.get().getUsername());
	}

	public void generateNewActivationToken(String username) {
		Optional<Verification> verification = verificationRepository.findByUsername(username);
		if (verification.isEmpty())
			throw new NotFoundException();
		if (verification.get().getAccount().isEnabled())
			throw new AccountAlreadyActivatedException();
		verification.get().generateToken();
		verificationRepository.updateVerification(username, verification.get().getToken());
		sendVerificationEmail(verification.get().getToken(), verification.get().getEmail());
	}

	private static BCryptPasswordEncoder getPasswordEncoder() {
		if (passwordEncoder == null)
			passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder;
	}

}