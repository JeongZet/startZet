package source;

public class User {
	private String userID;
	private String uGender;
	private String uAge;
	
	public User(String userID, String uGender, String uAge){
		this.userID=userID;
		this.uGender=uGender;
		this.uAge=uAge;
	}
	
	public String getID(){return userID;}
	public String getGender(){return uGender;}
	public String getAge(){return uAge;}
	
	
}
