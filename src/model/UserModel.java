package model;

public class UserModel {
	private int user_id;
	public String name;
	public String email;
	public String password;
	
	public UserModel(String name, String email, String password){
		this.name= name;
		this.email = email;
		this.password = password;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}
	public void setId(int id) {
		this.user_id = id;
	}
	public int getId() {
		return this.user_id;
	}
}
