package com.project.model.service;

public enum SortType {

	NAME("Name", "name"), RARITY("Rarity", "rarity_id"), SET("Set","set_id"), COST("Cost","cost", "Low - High", "High - Low");

	private String name;
	
	private String columnName;
	
	public final OrderType ASC;
	
	public final OrderType DESC;

	private SortType(String name, String columnName) {
		this(name, columnName, "A - Z", "Z - A");
	}
	
	private SortType(String name, String columnName, String asc, String desc) {
		this.name = name;
		this.columnName = columnName;
		ASC = new OrderType("ASC", asc);
		DESC = new OrderType("DESC", desc);
	}
	
	public class OrderType{
	
		private String order;
		
		private String name;

		private OrderType(String order, String name) {
			this.order = order;
			this.name=name;
		}
		
		public String getOrder() {
			return order;
		}
		
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return order;
		}
		
	}
	
	public OrderType getOrderType(String arg) {
		if(arg.equals("ASC"))
			return ASC;
		if(arg.equals("DESC"))
			return DESC;
		return null;
	}
	
	public OrderType[] orderTypeValues() {
		return new OrderType[]{ASC, DESC};
	}
	
	public String getName() {
		return name;
	}

	public String getColumnName() {
		return columnName;
	}
	
}