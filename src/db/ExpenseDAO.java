package db;

import java.sql.*;
import java.util.ArrayList;
import model.ExpenseModel;
import util.DbUtils;

public class ExpenseDAO {
    private String driverName = "com.mysql.cj.jdbc.Driver";
    private Connection conn;

    private void primaryExecution() throws Exception {
        Class.forName(driverName);
        conn = DriverManager.getConnection(DbUtils.url, DbUtils.username, DbUtils.password);
    }
    
    public boolean addExpense(ExpenseModel expense) throws Exception {
        primaryExecution();
        String query = "insert into expenses(user_id, date, category, amount, descp) values (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, expense.user_id);
        ps.setString(2, expense.date);
        ps.setString(3, expense.category);
        ps.setDouble(4, expense.amount);
        ps.setString(5, expense.description);
        int rowsAffected = ps.executeUpdate();
        ps.close();
        conn.close();
        return rowsAffected > 0;
    }
    
	
	public ArrayList<ExpenseModel> getAllExpense(int userId) throws Exception{
		primaryExecution();
		String query = "select * from expenses where user_id = ? order by date desc";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setInt(1,userId);
		
		ResultSet rs = ps.executeQuery();
		ArrayList<ExpenseModel> list = new ArrayList<ExpenseModel>();
		
		while(rs.next()) {
			ExpenseModel model = new ExpenseModel(rs.getInt("user_id"),rs.getString("date"),rs.getString("category"),rs.getDouble("amount"),rs.getString("descp"));
			model.setId(rs.getInt("id"));
			list.add(model);
		}
		ps.close();
		conn.close();
		return list;
	}
	
	public ArrayList<ExpenseModel> getExpenseWithCategory(int userId, String category) throws Exception{
		primaryExecution();
		String query = "select * from expenses where user_id = ? and category = ? order by date desc";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setInt(1,userId);
		ps.setString(2, category);
		
		ResultSet rs = ps.executeQuery();
		ArrayList<ExpenseModel> list = new ArrayList<ExpenseModel>();
		while(rs.next()) {
			ExpenseModel model = new ExpenseModel(rs.getInt("user_id"),rs.getString("date"),rs.getString("category"),rs.getDouble("amount"),rs.getString("descp"));
			model.setId(rs.getInt("id"));
			list.add(model);
		}
		ps.close();
		conn.close();
		return list;
	}

    public boolean deleteExpense(int expenseId) throws Exception {
        primaryExecution();
        String query = "DELETE FROM expenses WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, expenseId);
        int rows = ps.executeUpdate();
        ps.close();
        conn.close();
        return rows > 0;
    }

    public double getTotalExpense(int userId) throws Exception {
        primaryExecution();
        String query = "select sum(amount) as total from expenses where user_id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        double total = rs.next() ? rs.getDouble("total") : 0;
        ps.close();
        conn.close();
        return total;
    }

    public double getMonthlyTotal(int user_id, int month, int year) throws Exception {
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
		
        ps.close();
        conn.close();
        return totalSpent;
    }

    public double getMonthlyExpenseByCategory(int user_id, String category, int month, int year) throws Exception {
        primaryExecution();
        String query = "select coalesce(sum(amount), 0) as total from expenses where user_id = ? and lower(category) = lower(?) and month(date) = ? and year(date) = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, user_id);
        ps.setString(2, category);
        ps.setInt(3, month);
        ps.setInt(4, year);
        
        ResultSet rs = ps.executeQuery();
        double total = 0;
        if(rs.next()) {
            total = rs.getDouble("total");
        }
        
        ps.close();
        conn.close();
        return total;
    }

    public ArrayList<ExpenseModel> searchByDate(int userId, String fromDate, String toDate) throws Exception {
        primaryExecution();
        String query = "select * from expenses where user_id = ? and date between ? and ? order by date desc";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ps.setString(2, fromDate);
        ps.setString(3, toDate);
        ResultSet rs = ps.executeQuery();

        ArrayList<ExpenseModel> list = new ArrayList<>();
        while (rs.next()) {
            ExpenseModel model = new ExpenseModel(
            	
                rs.getInt("user_id"),
                rs.getString("date"),
                rs.getString("category"),
                rs.getDouble("amount"),
                rs.getString("descp")
            );
            model.setId(rs.getInt("id"));
            list.add(model);
        }
        ps.close();
        conn.close();
        return list;
    }

	public boolean updateExpense(int expenseId, String newCategory, double newAmount, String newDateDB,
			String descText) {
		// TODO Auto-generated method stub
		return false;
	}
    
    
    
    
}