package model;

public class BudgetModel {
	private int id;
	public int user_id;
	public int month; // 1-> jaunary like this
	public int year;
	public double budget_amount;
	
	public BudgetModel(int user_id,int month,int year, double amount){
		this.user_id = user_id;
		this.month = month;
		this.year = year;
		this.budget_amount = amount;
	}
	
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
}

