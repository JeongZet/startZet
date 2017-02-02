package recipeServer;

/*
 * DB  접속을 위한 IP, ID, PW, 또 아이디 찾기나 비밀번호 찾기 할 때 사용되는 이메일 전송을 위한 ID, PW를 임의로 저장해둔 클래스
 */

public class Identification {

	private String dbIp="121.181.123.16:1521:orcl";
	private String dbId="jyh";
	private String dbPassword="0911";
	private String mailId="jyh050211@gmail.com";
	private String mailPW="wjd091123";
	
	public String getdbIP(){return dbIp;}
	public String getdbId(){return dbId;}
	public String getdbPassword(){return dbPassword;}
	public String getmailId(){return mailId;}
	public String getmailPW(){return mailPW;}
	
	
}
