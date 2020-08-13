package com.project.model.service;

public enum SortType {

	NAME("Name", "name"), RARITY("Rarity", "id"), SET("Set","set"), COST("Cost","cost", "Low - High", "High - Low");

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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			OrderType other = (OrderType) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (order == null) {
				if (other.order != null)
					return false;
			} else if (!order.equals(other.order))
				return false;
			return true;
		}

		private SortType getEnclosingInstance() {
			return SortType.this;
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