package com.project.model.repository;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

import com.project.model.entity.QuantityCard;
import com.project.model.service.SortType;
import com.project.model.service.SortType.OrderType;

public class CardRepositoryImpl extends RepositoryImpl implements CardQuery {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private CardRepository cardRepository;

	@Override
	public int getNumberOfPages() {
		int cards = (int) cardRepository.count();
		return getNumberOfPagesFromNumberOfCards(cards);
	}

	private int getNumberOfPagesFromNumberOfCards(int cards) {
		return cards / 100 + (cards % 100 == 0 ? 0 : 1);
	}

	@Override
	public List<QuantityCard> getQuantityCardsByPageOrderByValue(int page, SortType sortType, OrderType orderType) {
		StringBuilder builder = aSelectCardsPartQuery();
		appendCardsOrderPartQuery(builder, sortType, orderType, page);
		Query query = entityManager.createNativeQuery(builder.toString());
		return getInstancesList(QuantityCard.class, query.getResultList());
	}

	@Override
	public List<QuantityCard> getQuantityCardsByPageOrderByValueWithSelectedRarities(int page, SortType sortType,
			OrderType orderType, List<String> rarities) {
		StringBuilder builder = aSelectCardsPartQuery();
		appendCardsRarityPartQuery(builder, rarities);
		appendCardsOrderPartQuery(builder, sortType, orderType, page);
		return this.getInstancesList(QuantityCard.class,
				entityManager.createNativeQuery(builder.toString()).getResultList());
	}

	@Override
	public List<QuantityCard> getQuantityCardsByPageOrderByValueWithSearch(int page, SortType sortType,
			OrderType orderType, String search) {
		StringBuilder builder = aSelectCardsPartQuery();
		appendCardsSearchPartQuery(builder, search, false);
		appendCardsOrderPartQuery(builder, sortType, orderType, page);
		return this.getInstancesList(QuantityCard.class,
				entityManager.createNativeQuery(builder.toString()).getResultList());
	}

	@Override
	public List<QuantityCard> getQuantityCardsByPageOrderByValueWithSelectedRaritiesWithSearch(int page,
			SortType sortType, OrderType orderType, List<String> rarities, String search) {
		StringBuilder builder = aSelectCardsPartQuery();
		appendCardsRarityPartQuery(builder, rarities);
		appendCardsSearchPartQuery(builder, search, true);
		appendCardsOrderPartQuery(builder, sortType, orderType, page);
		return this.getInstancesList(QuantityCard.class,
				entityManager.createNativeQuery(builder.toString()).getResultList());
	}

	private StringBuilder aSelectCardsPartQuery() {
		StringBuilder builder = new StringBuilder();
		builder.append("select id, name, image_url, cost, rarity_id from card ");
		return builder;
	}

	private StringBuilder getCountCardsPartQuery() {
		StringBuilder builder = new StringBuilder();
		builder.append("select count(id) from card ");
		return builder;
	}

	private void appendCardsRarityPartQuery(StringBuilder builder, List<String> rarities) {
		builder.append("where rarity_id in('");
		for (String rarity : rarities) {
			builder.append(rarity);
			builder.append("\',\'");
		}
		builder.delete(builder.length() - 2, builder.length());
		builder.append(") ");
	}

	private void appendCardsSearchPartQuery(StringBuilder builder, String search, boolean withRarity) {
		if (withRarity)
			builder.append("and ");
		else
			builder.append("where ");
		builder.append("upper(name) like upper('%");
		builder.append(search);
		builder.append("%') ");
	}

	private void appendCardsOrderPartQuery(StringBuilder builder, SortType sortType, OrderType orderType, int page) {
		builder.append("order by ");
		builder.append(sortType.getColumnName());
		builder.append(' ');
		builder.append(orderType.getOrder());
		builder.append(", name ");
		builder.append(orderType.getOrder());
		builder.append(", id ");
		builder.append(orderType.getOrder());
		builder.append(" limit(100) offset(((");
		builder.append(page);
		builder.append(")-1)*100)");
	}

	@Override
	public int getNumberOfPagesWithSelectedRarities(List<String> rarities) {
		StringBuilder builder = getCountCardsPartQuery();
		appendCardsRarityPartQuery(builder, rarities);
		int cards = ((BigInteger) entityManager.createNativeQuery(builder.toString()).getSingleResult()).intValue();
		return getNumberOfPagesFromNumberOfCards(cards);
	}

	@Override
	public int getNumberOfPagesWithSearch(String search) {
		StringBuilder builder = getCountCardsPartQuery();
		appendCardsSearchPartQuery(builder, search, false);
		int cards = ((BigInteger) entityManager.createNativeQuery(builder.toString()).getSingleResult()).intValue();
		return getNumberOfPagesFromNumberOfCards(cards);
	}

	@Override
	public int getNumberOfPagesWithSelectedRaritiesWithSearch(List<String> rarities, String search) {
		StringBuilder builder = getCountCardsPartQuery();
		appendCardsRarityPartQuery(builder, rarities);
		appendCardsSearchPartQuery(builder, search, true);
		int cards = ((BigInteger) entityManager.createNativeQuery(builder.toString()).getSingleResult()).intValue();
		return getNumberOfPagesFromNumberOfCards(cards);
	}

}