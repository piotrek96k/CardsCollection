package com.project.model.repository;

import java.util.List;

import com.project.model.entity.QuantityCard;

public interface CardQuery {

	public int getNumberOfPages();
	
	public List<QuantityCard> getQuantityCardsByPageOrderByName(int page);
	
	public List<QuantityCard> getQuantityCardsByPageOrderByNameWithSelectedRarities(int page, List<String> rarities);
	
	public int getNumberOfPagesWithSelectedRarities(List<String> rarities);
	
}
