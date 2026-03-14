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
	
	
}