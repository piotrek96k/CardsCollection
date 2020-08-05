package com.project.model.repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import com.project.model.entity.AccountId;
import com.project.model.entity.QuantityCard;

@Service
public class AccountRepositoryImpl implements AccountQuery {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public int countDistinctCardsByUsername(String username) {
		String query = "select count(distinct card_id) from account_cards where username=:username";
		return ((BigInteger) entityManager.createNativeQuery(query).setParameter("username", username)
				.getSingleResult()).intValue();
	}

	@Override
	public List<QuantityCard> getAccountCardsListByPage(String username, int page) {
		page -= 1;
		page *= 100;
		StringBuilder builder = new StringBuilder();
		builder.append(
				"select card.id as id, card.name as name, card.cost as cost, card.image_url as imageUrl, card.rarity_id as rarity, count(card.id) as quantity ");
		builder.append("from card inner join account_cards on account_cards.card_id=card.id ");
		builder.append("where username=:username group by card.id ");
		builder.append("order by card.name, card.id asc limit(100) offset(:page)");
		return getInstancesList(QuantityCard.class, entityManager.createNativeQuery(builder.toString())
				.setParameter("username", username).setParameter("page", page).getResultList());
	}

	@Override
	public AccountId getAccountId(String name) {
		String query = "select username, email from account where username=:name or email=:name";
		Object object = entityManager.createNativeQuery(query).setParameter("name", name).getSingleResult();
		return getInstance(AccountId.class, (Object[]) object);
	}

	private <T> List<T> getInstancesList(Class<T> type, List<?> rawList) {
		List<T> list = new ArrayList<T>();
		for (Object object : rawList)
			if (object instanceof Object[])
				list.add(getInstance(type, (Object[]) object));
		return list;
	}

	private <T> T getInstance(Class<T> type, Object[] parameters) {
		Class<?>[] parameterTypes = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++)
			parameterTypes[i] = parameters[i].getClass();
		try {
			return type.getConstructor(parameterTypes).newInstance(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getNumberOfPages(String username) {
		int cards = countDistinctCardsByUsername(username);
		return cards / 100 + (cards % 100 == 0 ? 0 : 1);
	}

}