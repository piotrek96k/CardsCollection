package com.project.model.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@IdClass(AccountId.class)
public class Account {

	@Id
	@NotBlank
	public String username;

	@Id
	@Email
	public String email;

	@NotBlank
	public String firstName;

	@NotBlank
	public String lastName;

	@NotBlank
	public String password;

	public boolean Enabled = true;

	@ManyToMany
	@JoinTable(name = "account_roles", joinColumns = {
			@JoinColumn(referencedColumnName = "username", name = "username"),
			@JoinColumn(referencedColumnName = "email", name = "email") }, inverseJoinColumns = @JoinColumn(name = "role"))
	private List<Role> roles;

	@ManyToMany
	@JoinTable(name = "account_cards", joinColumns = {
			@JoinColumn(referencedColumnName = "username", name = "username"),
			@JoinColumn(referencedColumnName = "email", name = "email") }, inverseJoinColumns = @JoinColumn(name = "card"))
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
		return Enabled;
	}

	public void setEnabled(boolean enabled) {
		Enabled = enabled;
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