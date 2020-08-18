package com.pokemoncards.model.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.pokemoncards.annotation.Password;
import com.pokemoncards.annotation.Unique;

@Entity
@IdClass(AccountId.class)
public class Account {

	@Id
	@NotBlank(message = "Username can't be blank")
	@Size(min = 4, message = "Username too short")
	@Unique(message = "Username in use")
	public String username;

	@Id
	@Email(message = "Entered e-mail must be correct")
	@Unique(message = "E-mail in use")
	public String email;

	@NotBlank(message = "First name can't be blank")
	public String firstName;

	@NotBlank(message = "Lastname can't be blank")
	public String lastName;

	@Password
	@NotBlank(message = "Password can't be blank")
	@Size(min = 8, message = "Password too short")
	public String password;

	@NotNull
	public int coins;

	public boolean enabled;

	@ManyToMany
	@JoinTable(name = "account_roles", joinColumns = {
			@JoinColumn(referencedColumnName = "username", name = "username"),
			@JoinColumn(referencedColumnName = "email", name = "email") }, inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roles;

	@ManyToMany
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

	public int getCoins() {
		return coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
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
		return "User [username=" + username + ", password=" + password + "]";
	}

}