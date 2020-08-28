package com.pokemoncards.model.entity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import javax.json.Json;
import javax.json.JsonObject;
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

@Entity
@IdClass(AccountId.class)
public class FreeCard {

	@Id
	@NotBlank
	private String username;

	@Id
	@Email
	private String email;
	
	@NotNull
	@Column(name = "next_free_card", columnDefinition = "TIMESTAMP")
	private LocalDateTime nextFreeCard;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
	@JoinColumn(referencedColumnName = "username", name = "username")
	@JoinColumn(referencedColumnName = "email", name = "email")
	private Account account;
	
	public String convertToJson() {
		JsonObject json = Json.createObjectBuilder().add("nextFreeCard", getNextFreeCardInMilis()).build();
		return json.toString();
	}
	
	public long getNextFreeCardInMilis() {
		if(nextFreeCard==null)
			return 0;
		return ZonedDateTime.of(nextFreeCard, ZoneOffset.UTC).toInstant().toEpochMilli();
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

	public LocalDateTime getNextFreeCard() {
		return nextFreeCard;
	}

	public void setNextFreeCard(LocalDateTime nextFreeCard) {
		this.nextFreeCard = nextFreeCard;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
}