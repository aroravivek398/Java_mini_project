package model;


public class ExpenseModel {
    private int id;
    public int user_id;
    public String date;
    public String category;
    public double amount;
    public String description;


    public ExpenseModel() {}

    public ExpenseModel(int user_id, String date, String category, double amount, String desc) {
        this.user_id = user_id;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = desc;
    }
   

    // ✅ Sahi getters
    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    
    public int getId() { return id; }
    public void setId(int id) {this.id=id;}
    
    public int getUserId() { return user_id; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
}