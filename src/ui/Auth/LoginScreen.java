package ui.Auth;
import model.UserModel;
import db.DbConnection;
public class LoginScreen {
	public static void main(String[] args) {
		UserModel user = new UserModel("Sk","Sk@gmail.com","12345");
		DbConnection db = new DbConnection();
		try {
			db.addUser(user);
		}
		catch (Exception e){
			System.out.print(e.getMessage());
		}
		
	}
}
