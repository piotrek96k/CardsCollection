package com.project.model.repository;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import com.project.model.entity.AccountId;
import com.project.model.entity.QuantityCard;

@Service
public class AccountRepositoryImpl extends RepositoryImpl implements AccountQuery {

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
				"select id, name, image_url, cost, rarity_id, count(card.id)");
		builder.append("from card inner join account_cards on account_cards.card_id=card.id ");
		builder.append("where username=:username group by card.id ");
		builder.append("order by card.name, card.id asc limit(100) offset(:page)");
		return getInstancesList(QuantityCard.class, entityManager.createNativeQuery(builder.toString())
				.setParameter("username", username).setParameter("page", page).getResultList());
	}

	@Override
	public AccountId getAccountId(String name) {
		String stringQuery = "select username, email from account where username=:name or email=:name";
		Query query = entityManager.createNativeQuery(stringQuery).setParameter("name", name);
		if(query.getResultList().size()==0)
			return null;
		return getInstance(AccountId.class, (Object[]) query.getSingleResult());
	}

	@Override
	public int getNumberOfPages(String username) {
		int cards = countDistinctCardsByUsername(username);
		return cards / 100 + (cards % 100 == 0 ? 0 : 1);
	}

}