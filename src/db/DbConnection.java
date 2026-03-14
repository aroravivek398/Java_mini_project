package db;
import java.sql.*;
import util.DbUtils; 
import model.UserModel;
public class DbConnection{
	private String dirverName = "com.mysql.cj.jdbc.Driver";
	private Connection conn;
	
	private void primaryExecution() throws Exception{
		Class.forName(dirverName);
		conn =  DriverManager.getConnection(DbUtils.url, DbUtils.username, DbUtils.password);
	}
	
	public boolean addUser(UserModel user) throws Exception {
	    primaryExecution();
	    String name     = user.name;
	    String email    = user.email;
	    String password = user.password;

	    String query = "INSERT INTO users(name, email, password) VALUES (?, ?, ?)";
	    PreparedStatement ps = conn.prepareStatement(query);
	    ps.setString(1, name);
	    ps.setString(2, email);
	    ps.setString(3, password);

	    int rowsAffected = ps.executeUpdate();
	    conn.close();

	    return rowsAffected > 0; 
	}

	public boolean checkUser(UserModel user) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean emailExists(String email) throws Exception {

	    return false;
	}

	public void updatePassword(String email, String password) {
		// TODO Auto-generated method stub
		
	}
	
	
}