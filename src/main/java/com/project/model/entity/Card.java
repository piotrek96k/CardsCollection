package com.project.model.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Card implements Identifiable<String> {

	@Id
	@NotNull
	private String id;

	@NotBlank
	private String name;

	@NotBlank
	private String imageUrl;

	@NotNull
	@ManyToOne
	private Rarity rarity;

	@NotNull
	@ManyToOne
	private Set set;

	private Integer pokedexNumber;

	private String evolvesFrom;

	private Integer hp;

	@ManyToMany
	@JoinTable(name = "card_types", joinColumns = @JoinColumn(referencedColumnName = "id", name = "card_id"), inverseJoinColumns = @JoinColumn(name = "type_id"))
	@OrderBy("id ASC")
	private List<Type> types;
	
	@ManyToOne
	private Type firstType;

	@Transient
	private int quantity;

	@ManyToMany(mappedBy = "cards")
	protected List<Account> accounts;

	public Card() {
	}

	public Card(String id, String name, String imageUrl, Rarity rarity) {
		this(id, name, imageUrl, rarity, 0);
	}

	public Card(Card card, long quantity) {
		this(card.id, card.name, card.imageUrl, card.rarity, quantity);
	}

	public Card(String id, String name, String imageUrl, Rarity rarity, long quantity) {
		this.id = id;
		this.name = name;
		this.imageUrl = imageUrl;
		this.rarity = rarity;
		this.quantity = (int) quantity;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Rarity getRarity() {
		return rarity;
	}

	public void setRarity(Rarity rarity) {
		this.rarity = rarity;
	}

	public Set getSet() {
		return set;
	}

	public void setSet(Set set) {
		this.set = set;
	}

	public Integer getPokedexNumber() {
		return pokedexNumber;
	}

	public void setPokedexNumber(Integer pokedexNumber) {
		this.pokedexNumber = pokedexNumber;
	}

	public String getEvolvesFrom() {
		return evolvesFrom;
	}

	public void setEvolvesFrom(String evolvesFrom) {
		this.evolvesFrom = evolvesFrom;
	}

	public Integer getHp() {
		return hp;
	}

	public void setHp(Integer hp) {
		this.hp = hp;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Type getFirstType() {
		return firstType;
	}

	public void setFirstType(Type firstType) {
		this.firstType = firstType;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public List<Type> getTypes() {
		return types;
	}

	public void setTypes(List<Type> types) {
		this.types = types;
	}

	@Override
	public String toString() {
		return "Card [id=" + id + ", name=" + name + ", imageUrl=" + imageUrl + "]";
	}

}