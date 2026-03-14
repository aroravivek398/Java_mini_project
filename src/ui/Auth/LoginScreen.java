package ui.Auth;
import model.UserModel;
import db.DbConnection;
public class LoginScreen {
	public static void main(String[] args) {
		UserModel user = new UserModel("Sk","Sk@gmail.com","12345");
		DbConnection db = new DbConnection();
		String email = "Sk@gmail.com";
		String pass = "12345";
		try {
			UserModel user2 = db.loginUser(email, pass);
			System.out.println(user2.email);
		}
		catch (Exception e){
			System.out.print(e.getMessage());
		}
		
	}
}