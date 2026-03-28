package db;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import model.BudgetModel;
import util.DbUtils;
public class BudgetDAO {
	
	public static void main(String[] args) throws Exception {
		BudgetDAO bg = new BudgetDAO();

		// Test 1 - getBudget
		System.out.println(bg.getBudget(1).get(0).budget_amount);

		// Test 2 - setBudget
		BudgetModel model = new BudgetModel(1, 4, 2026, 50000);
		System.out.println(bg.setBudget(1, model));

		// Test 3 - isBudgetExceeded
		System.out.println(bg.isBudgetExceeded(1, 3, 2026));

		// Test 4 - getRemainingBudget
		System.out.println(bg.getRemainingBudget(1, 3, 2026));
	}
	
	private String dirverName = "com.mysql.cj.jdbc.Driver";
	private Connection conn;
	private LocalDate today = LocalDate.now();
	private int month;
	private int year;
	
	private void primaryExecution() throws Exception{
		Class.forName(dirverName);
		conn =  DriverManager.getConnection(DbUtils.url, DbUtils.username, DbUtils.password);
		this.month = this.getMonth();
		this.year = this.getYear();
	}
	
	private int getMonth() {
		return today.getMonthValue();
	}
	
	private int getYear() {
		return today.getYear();
	}
	
	public boolean isBudgetSet(int user_id,int month, int year) throws Exception {
		primaryExecution();
		String query = "select budget_amount from budgets where user_id = ? and month = ? and year = ?";
	    PreparedStatement ps = conn.prepareStatement(query);
	    ps.setInt(1, user_id);
	    ps.setInt(2, month);
	    ps.setInt(3, year);
	    
	    ResultSet rs = ps.executeQuery();
	    
		if(rs.next()) {
			return true;
		}else {
			return false;
		}
	}
	
	//getRemainingBudget
	public ArrayList<BudgetModel> getBudget(int user_id) throws Exception {
		primaryExecution();
		String query = "select * from budgets where user_id = ?";
	    PreparedStatement ps = conn.prepareStatement(query);
	    ps.setInt(1, user_id);
	    ResultSet rs = ps.executeQuery();
	    var list =new ArrayList<BudgetModel>();
	    while(rs.next()) {
	    	BudgetModel m = new BudgetModel(rs.getInt("user_id"),rs.getInt("month"),rs.getInt("year"),rs.getDouble("budget_amount"));
	    	m.setId(rs.getInt("id"));
	    	list.add(m);
	    }
	    ps.close();
	    conn.close();
	    return list;
	}
	
	public boolean setBudget(int user_id, BudgetModel model) throws Exception{
		if(isBudgetSet(user_id,model.month, model.year)) {
			return false;
		}
		 primaryExecution(); 
		String query = "insert into budgets (user_id, month, year, budget_amount) values (?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setInt(1,model.user_id);
		ps.setInt(2, model.month);
		ps.setInt(3, model.year);
		ps.setDouble(4, model.budget_amount);
		
		int rowsAffected = ps.executeUpdate();
	    ps.close();
	    conn.close();
		
		return rowsAffected > 0;
		
	}
	
	public boolean isBudgetExceeded(int user_id,int month,int year) throws Exception {
		primaryExecution();
		String query = "select sum(amount) as total_spent from expenses where user_id = ? and month(date) = ? and year(date) = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setInt(1, user_id);
		ps.setInt(2, month);
		ps.setInt(3, year);
		
		ResultSet rs = ps.executeQuery();
		double totalSpent = 0;
		if(rs.next()) {
		    totalSpent = rs.getDouble("total_spent");
		}
		
		query = "select budget_amount from budgets where user_id = ? and month = ? and year = ?";
		ps = conn.prepareStatement(query);
		ps.setInt(1, user_id);
		ps.setInt(2, month);
		ps.setInt(3, year);
		rs = ps.executeQuery();
		
		double budgetAmount = 0;
		if(rs.next()) {
			budgetAmount = rs.getDouble("budget_amount");
		}
		ps.close();
		conn.close();
		
		return totalSpent > budgetAmount;
	}
	public double getRemainingBudget(int user_id,int month,int year) throws Exception {
		
		if(isBudgetExceeded(user_id,month,year)) {
			return 0;
		}
		primaryExecution();
		String query = "select sum(amount) as total_spent from expenses where user_id = ? and month(date) = ? and year(date) = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setInt(1, user_id);
		ps.setInt(2, month);
		ps.setInt(3, year);
		
		ResultSet rs = ps.executeQuery();
		double totalSpent = 0;
		if(rs.next()) {
		     totalSpent = rs.getDouble("total_spent");
		}
		
		query = "select budget_amount from budgets where user_id = ? and month = ? and year = ?";
		ps = conn.prepareStatement(query);
		ps.setInt(1, user_id);
		ps.setInt(2, month);
		ps.setInt(3, year);
		rs = ps.executeQuery();
		
		double budgetAmount = 0;
		if(rs.next()) {
			budgetAmount = rs.getDouble("budget_amount");
		}
		
		ps.close();
		conn.close();
		return budgetAmount - totalSpent;
	}
}
