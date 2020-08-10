package com.project.model.component;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.project.model.service.SortType;

@Component
@Scope(value = "session")
public class SessionData {

	private SortType sortType;
	
	private SortType.OrderType orderType;
	
	{
		sortType=SortType.NAME;
		orderType=SortType.NAME.ASC;
		
	}
	
	public SortType getSortType() {
		return sortType;
	}

	public void setSortType(SortType sortType) {
		this.sortType = sortType;
		orderType = sortType.ASC;
	}

	public SortType.OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(SortType.OrderType orderType) {
		this.orderType = orderType;
	}
	
}
