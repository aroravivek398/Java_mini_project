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

	    String query = "insert into users(name, email, password) values (?, ?, ?)";
	    PreparedStatement ps = conn.prepareStatement(query);
	    ps.setString(1, name);
	    ps.setString(2, email);
	    ps.setString(3, password);

	    int rowsAffected = ps.executeUpdate();
	    conn.close();

	    return rowsAffected > 0; 
	}
	public boolean emailExists(String email) throws Exception {
		primaryExecution();
		String query = "select * from users where email = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, email);
		
		ResultSet rs = ps.executeQuery();
		if(rs.next()) { 
			conn.close();
			return true; 
		}else {
			conn.close();
			return false;
		} 
	}

	public boolean updatePassword(String email, String password) throws Exception {
		primaryExecution();
		String query = "update users set password = ? where email = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, password);
		ps.setString(2, email);
		
		int rowsAffected = ps.executeUpdate();
		
		if(rowsAffected > 0) { //means password changes
			conn.close();
			return true; 
		}else {
			conn.close();
			return false;
		}
	}
	
	public UserModel loginUser(String email,String password) throws Exception{ // for login button function
		primaryExecution();
		String query = "select * from users where email = ? and password = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, email);
		ps.setString(2, password);
		
		 ResultSet rs = ps.executeQuery();
		 
		 if(rs.next()) {
			 UserModel user = new UserModel(rs.getString("name"),rs.getString("email"),rs.getString("password"));
			 conn.close();
			 return user;
		 }
		 else {
			 conn.close();
			 return null;
		 }
	}
}