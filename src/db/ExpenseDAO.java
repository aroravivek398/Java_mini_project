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

    // ===================== ADD EXPENSE =====================
    public boolean addExpense(ExpenseModel expense) throws Exception {
        primaryExecution();
        String query = "INSERT INTO expenses(user_id, date, category, amount, descp) VALUES (?, ?, ?, ?, ?)";
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


    // ===================== GET ALL EXPENSES =====================
    public ArrayList<ExpenseModel> getAllExpense(int userId) throws Exception {
        primaryExecution();
        String query = "SELECT * FROM expenses WHERE user_id = ? ORDER BY date DESC";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
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
            list.add(model);
        }
        ps.close();
        conn.close();
        return list;
    }

    // ===================== GET BY CATEGORY =====================
    public ArrayList<ExpenseModel> getExpenseWithCategory(int userId, String category) throws Exception {
        primaryExecution();
        String query = "SELECT * FROM expenses WHERE user_id = ? AND category = ? ORDER BY date DESC";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ps.setString(2, category);
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
            list.add(model);
        }
        ps.close();
        conn.close();
        return list;
    }

    // ===================== DELETE EXPENSE =====================

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


    // ===================== TOTAL OF ALL TIME =====================
    public double getTotalExpense(int userId) throws Exception {
        primaryExecution();
        String query = "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE user_id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        double total = rs.next() ? rs.getDouble(1) : 0;
        ps.close();
        conn.close();
        return total;
    }

    // ===================== THIS MONTH TOTAL =====================
    public double getThisMonthTotal(int userId) throws Exception {
        primaryExecution();
        String query = "SELECT COALESCE(SUM(amount), 0) FROM expenses " +
                       "WHERE user_id = ? AND MONTH(date) = MONTH(CURDATE()) AND YEAR(date) = YEAR(CURDATE())";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        double total = rs.next() ? rs.getDouble(1) : 0;
        ps.close();
        conn.close();
        return total;
    }

    // ===================== CATEGORY WISE MONTHLY EXPENSE =====================
    public double getMonthlyExpenseByCategory(int userId, String category, int month, int year) throws Exception {
        primaryExecution();
        String query = "SELECT COALESCE(SUM(amount), 0) FROM expenses " +
                       "WHERE user_id = ? AND LOWER(category) = LOWER(?) AND MONTH(date) = ? AND YEAR(date) = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ps.setString(2, category);
        ps.setInt(3, month);
        ps.setInt(4, year);
        ResultSet rs = ps.executeQuery();
        double total = rs.next() ? rs.getDouble(1) : 0;
        ps.close();
        conn.close();
        return total;
    }

    // ===================== SEARCH BY DATE RANGE =====================
    public ArrayList<ExpenseModel> searchByDate(int userId, String fromDate, String toDate) throws Exception {
        primaryExecution();
        String query = "SELECT * FROM expenses WHERE user_id = ? AND date BETWEEN ? AND ? ORDER BY date DESC";
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
            list.add(model);
        }
        ps.close();
        conn.close();
        return list;
    }
}