package com.pokemoncards.model.entity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pokemoncards.model.service.AccountService;

@Entity
@IdClass(AccountId.class)
public class Cash{

	public static final int COINS_PER_DAY;

	static {
		COINS_PER_DAY = 100;
	}

	@Id
	@NotBlank
	private String username;

	@Id
	@Email
	private String email;

	@NotNull
	private Integer coins;

	@NotNull
	@Column(name = "next_coins_collecting", columnDefinition = "TIMESTAMP")
	private LocalDateTime nextCoinsCollecting;

	@NotNull
	@Column(name = "days_in_row")
	private Integer daysInRow;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
	@JoinColumn(referencedColumnName = "username", name = "username")
	@JoinColumn(referencedColumnName = "email", name = "email")
	private Account account;

	public String convertToJson() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("coins", AccountService.formatInteger(coins));
		node.put("nextCoinsCollecting", getNextTimeCollectingInMilis());
		node.put("nextCoins", AccountService.formatInteger(COINS_PER_DAY*daysInRow));
		return node.toString();
	}

	public long getNextTimeCollectingInMilis() {
		if (nextCoinsCollecting == null)
			return 0;
		return ZonedDateTime.of(nextCoinsCollecting, ZoneOffset.UTC).toInstant().toEpochMilli();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getCoins() {
		return coins;
	}

	public void setCoins(Integer coins) {
		this.coins = coins;
	}

	public Account getAccount() {
		return account;
	}

	public LocalDateTime getNextCoinsCollecting() {
		return nextCoinsCollecting;
	}

	public void setNextCoinsCollecting(LocalDateTime nextCoinsCollecting) {
		this.nextCoinsCollecting = nextCoinsCollecting;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Integer getDaysInRow() {
		return daysInRow;
	}

	public void setDaysInRow(Integer daysInRow) {
		this.daysInRow = daysInRow;
	}

	@Override
	public String toString() {
		return "Cash [coins=" + coins + ", nextCoinsCollecting=" + nextCoinsCollecting + ", daysInRow=" + daysInRow
				+ "]";
	}

}