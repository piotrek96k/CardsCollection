package com.project.model.repository;

import java.util.List;

import com.project.model.entity.Card;
import com.project.model.entity.Rarity;
import com.project.model.service.SortType;
import com.project.model.service.SortType.OrderType;

public interface CardQuery extends Query{

	public int getNumberOfPages();
	
	public List<Card> getCardsByPageOrderByValue(int page, SortType sortType, OrderType orderType);
	
	public List<Card> getCardsByPageOrderByValueWithSelectedRarities(int page, SortType sortType, OrderType orderType, List<Rarity> rarities);
	
	public List<Card> getCardsByPageOrderByValueWithSearch(int page, SortType sortType, OrderType orderType, String search);
	
	public List<Card> getCardsByPageOrderByValueWithSelectedRaritiesWithSearch(int page, SortType sortType, OrderType orderType, List<Rarity> rarities, String search);
	
	public int getNumberOfPagesWithSelectedRarities(List<Rarity> rarities);
	
	public int getNumberOfPagesWithSearch(String search);
	
	public int getNumberOfPagesWithSelectedRaritiesWithSearch(List<Rarity> rarities, String search);
	
}
