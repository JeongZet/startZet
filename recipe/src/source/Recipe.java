package source;

import javafx.beans.property.SimpleStringProperty;

public class Recipe {
	private int rNo;
	private int scene;
	private SimpleStringProperty userID;
	private SimpleStringProperty rName;
	private SimpleStringProperty rItems;
	private SimpleStringProperty rKind;
	private SimpleStringProperty rRecommend;
	private SimpleStringProperty rComment;

	public Recipe(String userID, String rName, String rItems, String rKind, String rRecommend, String rComment){
		this.userID = new SimpleStringProperty(userID);
		this.rName = new SimpleStringProperty(rName);
		this.rItems = new SimpleStringProperty(rItems);
		this.rKind = new SimpleStringProperty(rKind);
		this.rRecommend = new SimpleStringProperty(rRecommend);
		this.rComment = new SimpleStringProperty(rComment);
	}
	
	public void setScene(int scene){this.scene=scene;}
	public void setRNo(int rNo){ this.rNo=rNo;}
	public void setUserID(String userID){ this.userID.set(userID);}
	public void setRName(String rName){ this.rName.set(rName);}
	public void setRItems(String rItems){ this.rItems.set(rItems);}
	public void setRKind(String rKind){ this.rKind.set(rKind);}
	public void setRRecommend(String rRecommend){ this.rRecommend.set(rRecommend);}
	public void setRComment(String rComment){ this.rComment.set(rComment);}
	
	public int getScene(){ return scene;}
	public int getRNo(){ return rNo;}
	public String getUserID(){ return userID.get();}
	public String getRName(){ return rName.get();}
	public String getRItems(){ return rItems.get();}
	public String getRKind(){ return rKind.get();}
	public String getRRecommend(){ return rRecommend.get();}
	public String getRComment(){ return rComment.get();}
	
}	
	
