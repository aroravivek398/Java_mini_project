package model;

public class BudgetModel {
    private int id;
    private int user_id;
    private double monthly_budget;
    private int month;
    private int year;

    // Default constructor
    public BudgetModel() {}

    // Parameterized constructor
    public BudgetModel(int user_id, double monthly_budget, int month, int year) {
        this.user_id = user_id;
        this.monthly_budget = monthly_budget;
        this.month = month;
        this.year = year;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return user_id; }
    public double getMonthlyBudget() { return monthly_budget; }
    public int getMonth() { return month; }
    public int getYear() { return year; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int user_id) { this.user_id = user_id; }
    public void setMonthlyBudget(double monthly_budget) { this.monthly_budget = monthly_budget; }
    public void setMonth(int month) { this.month = month; }
    public void setYear(int year) { this.year = year; }
}