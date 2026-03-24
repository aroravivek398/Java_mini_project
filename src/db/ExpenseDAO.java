
package db;
import java.sql.*;
import java.util.ArrayList;

import model.ExpenseModel;
import util.DbUtils;

public class ExpenseDAO {
	private String dirverName = "com.mysql.cj.jdbc.Driver";
	private Connection conn;
	private void primaryExecution() throws Exception{
		Class.forName(dirverName);
		conn =  DriverManager.getConnection(DbUtils.url, DbUtils.username, DbUtils.password);
	}
	
	public boolean addExpense(ExpenseModel expense) throws Exception {
	    primaryExecution();
	    int user_id = expense.user_id;
	    String date = expense.date;
	    String category = expense.category;
	    double amount = expense.amount;
	    String desc = expense.description;

	    String query = "insert into expenses(user_id, date, category, amount, descp) values (?, ?, ?, ?, ?)";
	    PreparedStatement ps = conn.prepareStatement(query);
	    ps.setInt(1,user_id);
	    ps.setString(2, date);
	    ps.setString(3, category);
	    ps.setDouble(4, amount);
	    ps.setString(5, desc);

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
    public boolean updateExpense(int expenseId,ExpenseModel newexpense) throws Exception {
        primaryExecution();
        String query = "update expenses set date = ? , category = ? , amount = ? , descp = ? where id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, newexpense.date);
        ps.setString(2, newexpense.category);
        ps.setDouble(3, newexpense.amount);
        ps.setString(4, newexpense.description);
        ps.setInt(5, expenseId);

        int rows = ps.executeUpdate();
        ps.close();
        conn.close();
        return rows > 0;
    }
    public ArrayList<ExpenseModel> getExpenseWithDate(int userId, String date) throws Exception{
		primaryExecution();
		String query = "select * from expenses where user_id = ? and date = ? order by date desc";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setInt(1,userId);
		ps.setString(2, date);
		
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
    public double getTotalExpenses(int userId) throws Exception {
    	primaryExecution();
    	String query = "select sum(amount) as total from expenses where user_id = ?";
    	PreparedStatement ps = conn.prepareStatement(query);
    	ps.setInt(1,userId);
    	ResultSet rs = ps.executeQuery();
    	double total = 0;
    	if(rs.next()) {
			total = rs.getDouble("total");
		}
		ps.close();
		conn.close();
    	return total;
    }
}

