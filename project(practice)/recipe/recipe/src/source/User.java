package source;

/*
 * �α����� ������ ������ ������ �ִ� Ŭ������
 * ������ ���̵�, ����, ���� ������ ������ �ִ�.
 * ���̵�, ����, ������ ������ ��۰� ��õ�� ����� �� ���Ǿ�����. 
 */

public class User {
	private String userID;			//���� ID
	private String uGender;			//���� ����
	private String uAge;			//���� ����
	
	
	//���� Ŭ���� ������
	public User(String userID, String uGender, String uAge){
		this.userID=userID;
		this.uGender=uGender;
		this.uAge=uAge;
	}
	
	
	//���� Ŭ���� Getting �޼ҵ�
	public String getID(){return userID;}
	public String getGender(){return uGender;}
	public String getAge(){return uAge;}
	
	
}
