package com.project.model.repository;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.project.model.entity.QuantityCard;

public class CardRepositoryImpl extends RepositoryImpl implements CardQuery {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private CardRepository cardRepository;

	@Override
	public int getNumberOfPages() {
		int cards = (int) cardRepository.count();
		return cards / 100 + (cards % 100 == 0 ? 0 : 1);
	}

	@Override
	public List<QuantityCard> getQuantityCardsByPageOrderByName(int page) {
		String query = "select id, name, image_url, cost, rarity_id from card order by name, id asc limit(100) offset(((:page)-1)*100)";
		return this.getInstancesList(QuantityCard.class,
				entityManager.createNativeQuery(query).setParameter("page", page).getResultList());
	}

	@Override
	public List<QuantityCard> getQuantityCardsByPageOrderByNameWithSelectedRarities(int page, List<String> rarities) {
		StringBuilder builder = new StringBuilder();
		builder.append("select id, name, image_url, cost, rarity_id from card where rarity_id in('");
		for (String rarity : rarities) {
			builder.append(rarity);
			builder.append("\',\'");
		}
		builder.delete(builder.length() - 2, builder.length());
		builder.append(") order by name, id asc limit(100) offset(((:page)-1)*100)");
		return this.getInstancesList(QuantityCard.class,
				entityManager.createNativeQuery(builder.toString()).setParameter("page", page).getResultList());
	}

	@Override
	public int getNumberOfPagesWithSelectedRarities(List<String> rarities) {
		StringBuilder builder = new StringBuilder();
		builder.append("select count(id) from card where rarity_id in('");
		for (String rarity : rarities) {
			builder.append(rarity);
			builder.append("\',\'");
		}
		builder.delete(builder.length() - 2, builder.length());
		builder.append(')');
		int cards = ((BigInteger) entityManager.createNativeQuery(builder.toString()).getSingleResult()).intValue();
		return cards / 100 + (cards % 100 == 0 ? 0 : 1);
	}

}