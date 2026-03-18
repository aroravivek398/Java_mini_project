package model;

public class ExpenseModel{
		int id;
		public int user_id;
		public String date;
		public String category;
		public double amount;
		public String description;
		
		public ExpenseModel(){}
		
		public ExpenseModel(int user_id,String date,String category,double amount,String desc) {
			this.user_id = user_id;
			this.date = date;
			this.category = category;
			this.amount = amount;
			this.description = desc;
		}

		public String getAmount() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getCategory() {
			// TODO Auto-generated method stub
			return null;
		}
	}

