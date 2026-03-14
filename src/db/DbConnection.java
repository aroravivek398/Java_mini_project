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
	
	public void addUser(UserModel user) throws Exception{
		primaryExecution();
		String name = user.name;
		String email = user.email;
		String password = user.password;
		String query = "insert into users(name,email,password) values(?,?,?)";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, name);
		ps.setString(2, email);
		ps.setString(3, password);
		
		ps.executeUpdate();
		conn.close();
	}
	
	public UserModel loginUser(String email,String password) throws Exception{
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