package com.pokemoncards.model.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import com.pokemoncards.annotation.Password;
import com.pokemoncards.annotation.OnRegister;
import com.pokemoncards.annotation.Unique;

@Entity
@IdClass(AccountId.class)
@Validated
public class Account {

	@Id
	@NotBlank(message = "Username can't be blank", groups = OnRegister.class)
	@Size(min = 4, message = "Username too short", groups = OnRegister.class)
	@Unique(message = "Username in use", groups = OnRegister.class)
	private String username;

	@Id
	@Email(message = "Entered e-mail must be correct", groups = OnRegister.class)
	@Unique(message = "E-mail in use", groups = OnRegister.class)
	private String email;

	@NotBlank(message = "First name can't be blank", groups = OnRegister.class)
	private String firstName;

	@NotBlank(message = "Lastname can't be blank", groups = OnRegister.class)
	private String lastName;

	@Password(groups = OnRegister.class)
	@NotBlank(message = "Password can't be blank", groups = OnRegister.class)
	@Size(min = 8, message = "Password too short", groups = OnRegister.class)
	private String password;

	@OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
	private Cash cash;
	
	@OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
	private FreeCard freeCard;

	private boolean enabled;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "account_roles", joinColumns = {
			@JoinColumn(referencedColumnName = "username", name = "username"),
			@JoinColumn(referencedColumnName = "email", name = "email") }, inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roles;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "account_cards", joinColumns = {
			@JoinColumn(referencedColumnName = "username", name = "username"),
			@JoinColumn(referencedColumnName = "email", name = "email") }, inverseJoinColumns = @JoinColumn(name = "card_id"))
	private List<Card> cards;

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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Cash getCash() {
		return cash;
	}

	public void setCash(Cash cash) {
		this.cash = cash;
	}

	public FreeCard getFreeCard() {
		return freeCard;
	}

	public void setFreeCard(FreeCard freeCard) {
		this.freeCard = freeCard;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	@Override
	public String toString() {
		return "Account [username=" + username + ", email=" + email + ", firstName=" + firstName + ", lastName="
				+ lastName + ", password=" + password + ", cash=" + cash + ", enabled=" + enabled + ", cards=" + cards
				+ "]";
	}

}