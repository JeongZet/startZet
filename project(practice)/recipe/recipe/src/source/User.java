package source;

/*
 * 로그인한 유저의 정보를 가지고 있는 클래스로
 * 유저의 아이디, 성별, 나이 정보를 가지고 있다.
 * 아이디, 성별, 나이의 정보는 댓글과 추천을 등록할 때 사용되어진다. 
 */

public class User {
	private String userID;			//유저 ID
	private String uGender;			//유저 성별
	private String uAge;			//유저 나이
	
	
	//유저 클래스 생성자
	public User(String userID, String uGender, String uAge){
		this.userID=userID;
		this.uGender=uGender;
		this.uAge=uAge;
	}
	
	
	//유저 클래스 Getting 메소드
	public String getID(){return userID;}
	public String getGender(){return uGender;}
	public String getAge(){return uAge;}
	
	
}
