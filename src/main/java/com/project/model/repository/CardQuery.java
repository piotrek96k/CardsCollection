package com.project.model.repository;

import java.util.List;

import com.project.model.entity.QuantityCard;
import com.project.model.service.SortType;
import com.project.model.service.SortType.OrderType;

public interface CardQuery {

	public int getNumberOfPages();
	
	public List<QuantityCard> getQuantityCardsByPageOrderByValue(int page, SortType sortType, OrderType orderType);
	
	public List<QuantityCard> getQuantityCardsByPageOrderByValueWithSelectedRarities(int page, SortType sortType, OrderType orderType, List<String> rarities);
	
	public List<QuantityCard> getQuantityCardsByPageOrderByValueWithSearch(int page, SortType sortType, OrderType orderType, String search);
	
	public List<QuantityCard> getQuantityCardsByPageOrderByValueWithSelectedRaritiesWithSearch(int page, SortType sortType, OrderType orderType, List<String> rarities, String search);
	
	public int getNumberOfPagesWithSelectedRarities(List<String> rarities);
	
	public int getNumberOfPagesWithSearch(String search);
	
	public int getNumberOfPagesWithSelectedRaritiesWithSearch(List<String> rarities, String search);
	
}
