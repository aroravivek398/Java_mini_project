package db;

import java.sql.*;
import java.time.LocalDate;
import model.BudgetModel;
import util.DbUtils;

public class BudgetDAO {
    private String driverName = "com.mysql.cj.jdbc.Driver";
    private Connection conn;
 
    private void primaryExecution() throws Exception {
        Class.forName(driverName);
        conn = DriverManager.getConnection(DbUtils.url, DbUtils.username, DbUtils.password);
    }

    // Budget set karo — agar pehle se hai to update, nahi to insert
    public boolean setBudget(BudgetModel budget) throws Exception {
        primaryExecution();

        // Pehle check karo is month ka budget already hai ya nahi
        String checkQuery = "SELECT id FROM budget WHERE user_id = ? AND month = ? AND year = ?";
        PreparedStatement checkPs = conn.prepareStatement(checkQuery);
        checkPs.setInt(1, budget.getUserId());
        checkPs.setInt(2, budget.getMonth());
        checkPs.setInt(3, budget.getYear());
        ResultSet rs = checkPs.executeQuery();

        boolean result;
        if (rs.next()) {
            //Already hai — update karo
            int existingId = rs.getInt("id");
            String updateQuery = "UPDATE budget SET monthly_budget = ? WHERE id = ?";
            PreparedStatement updatePs = conn.prepareStatement(updateQuery);
            updatePs.setDouble(1, budget.getMonthlyBudget());
            updatePs.setInt(2, existingId);
            result = updatePs.executeUpdate() > 0;
            updatePs.close();
        } else {
            //Nahi hai — insert karo
            String insertQuery = "INSERT INTO budget(user_id, monthly_budget, month, year) VALUES (?, ?, ?, ?)";
            PreparedStatement insertPs = conn.prepareStatement(insertQuery);
            insertPs.setInt(1, budget.getUserId());
            insertPs.setDouble(2, budget.getMonthlyBudget());
            insertPs.setInt(3, budget.getMonth());
            insertPs.setInt(4, budget.getYear());
            result = insertPs.executeUpdate() > 0;
            insertPs.close();
        }

        checkPs.close();
        conn.close();
        return result;
    }

    // Current month ka budget fetch karo
    public BudgetModel getCurrentBudget(int userId) throws Exception {
        primaryExecution();

        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear  = LocalDate.now().getYear();

        String query = "SELECT * FROM budget WHERE user_id = ? AND month = ? AND year = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ps.setInt(2, currentMonth);
        ps.setInt(3, currentYear);

        ResultSet rs = ps.executeQuery();
        BudgetModel budget = null;

        if (rs.next()) {
            budget = new BudgetModel(
                rs.getInt("user_id"),
                rs.getDouble("monthly_budget"),
                rs.getInt("month"),
                rs.getInt("year")
            );
            budget.setId(rs.getInt("id"));
        }

        ps.close();
        conn.close();
        return budget; // null return hoga agar budget set nahi hua
    }

    // Budget Left calculate karo
    public double getBudgetLeft(int userId) throws Exception {
        BudgetModel budget = getCurrentBudget(userId);
        if (budget == null) return 0; // budget set hi nahi hua

        ExpenseDAO expenseDao = new ExpenseDAO();
        double thisMonthExpense = expenseDao.getThisMonthTotal(userId);

        return budget.getMonthlyBudget() - thisMonthExpense;
    }
}