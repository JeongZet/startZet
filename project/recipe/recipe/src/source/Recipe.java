package source;

/*
 * SimpleStringProperty를 활용하여 레시피들의 이름, 재료, 종류 등을 갖고 있는 클래스로
 * TableView 세팅을 위해 SimpleStringProperty 변수를 사용하였다.
 */

import javafx.beans.property.SimpleStringProperty;

public class Recipe {
	private int rNo;						//레시피 번호
	private int scene;						//레시피 총 장면수
	private SimpleStringProperty userID;	//레시피 등록자 아이디
	private SimpleStringProperty rName;		//레시피 이름
	private SimpleStringProperty rItems;	//레시피 재료
	private SimpleStringProperty rKind;		//레시피 종류
	private SimpleStringProperty rRecommend;//레시피 추천 수
	private SimpleStringProperty rComment;	//레시피 댓글 수

	//레시피 클래스 생성자
	public Recipe(String userID, String rName, String rItems, String rKind, String rRecommend, String rComment){
		this.userID = new SimpleStringProperty(userID);
		this.rName = new SimpleStringProperty(rName);
		this.rItems = new SimpleStringProperty(rItems);
		this.rKind = new SimpleStringProperty(rKind);
		this.rRecommend = new SimpleStringProperty(rRecommend);
		this.rComment = new SimpleStringProperty(rComment);
	}
	
	//레시피 클래스 Setting 메소드들
	public void setScene(int scene){this.scene=scene;}
	public void setRNo(int rNo){ this.rNo=rNo;}
	public void setUserID(String userID){ this.userID.set(userID);}
	public void setRName(String rName){ this.rName.set(rName);}
	public void setRItems(String rItems){ this.rItems.set(rItems);}
	public void setRKind(String rKind){ this.rKind.set(rKind);}
	public void setRRecommend(String rRecommend){ this.rRecommend.set(rRecommend);}
	public void setRComment(String rComment){ this.rComment.set(rComment);}
	
	//레시피 클래스 Getting 메소드들
	public int getScene(){ return scene;}
	public int getRNo(){ return rNo;}
	public String getUserID(){ return userID.get();}
	public String getRName(){ return rName.get();}
	public String getRItems(){ return rItems.get();}
	public String getRKind(){ return rKind.get();}
	public String getRRecommend(){ return rRecommend.get();}
	public String getRComment(){ return rComment.get();}
	
}	
	
