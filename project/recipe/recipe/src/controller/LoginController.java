package controller;

/*
 * 로그인 화면의 이벤트 처리를 위한 화면으로
 * 입력 값은 아이디와 비밀번호
 * 로그인 화면에서 변경 가능한 페이지는 총 3개로
 * 회원가입 페이지, 찾기 페이지, 로그인 성공으로 인한 레시피 리스트 페이지가 있다.
 * 닫기 버튼 시 프로그램 종료.
 */

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import source.Sessions;
import source.User;

public class LoginController implements Initializable {

	private Sessions sessions;									//로그인 유저의 세션
	@FXML private TextField id_Text;							//아이디 텍스트 필드
	@FXML private PasswordField pw_Text;						//패스워드 필드
	
	public void initialize(URL location, ResourceBundle resources) {
		
		id_Text.setText("아이디를 입력하세요.");
		pw_Text.setText("비밀번호");
		
		id_Text.setOnKeyPressed(event->{
			if(event.getCode().equals(KeyCode.ENTER)){
				handle_LogBtn();
			}
		});
		
		pw_Text.setOnKeyPressed(event->{
			if(event.getCode().equals(KeyCode.ENTER)){
				handle_LogBtn();
			}
		});//아이디 텍스트 필드와 비밀번호 필드에서의 엔터 키 처리 
		
	}
	
	//이전 화면에서의 세션 할당을 위한 메소드
	public void setSession(Sessions sessions){
		this.sessions=sessions;
	}
	
	//회원가입 버튼 이벤트 처리 메소드
	public void handle_RegBtn(){
		sessions.alterStage("회원가입");
	}
	
	//로그인 버튼 이벤트 처리 메소드
	public void handle_LogBtn(){
		sessions.connectSocket();						//로그인을 위해 세션의 소켓을 서버에 연결
		
		if(id_Text.getText().equals("")){
			id_Text.setText("아이디를 입력하세요.");
		}
		if(pw_Text.getText().equals("")){
			pw_Text.setText("비밀번호");
		}												//아이디와 패스워드의 공백으로 인한 오류 처리				
		
		String message = "로그인///"+id_Text.getText()+"///"+pw_Text.getText();	
														//앞에 로그인 텍스트는 비동기 서버에서의 처리 구분을 위해 사용 

		sessions.writeSocket(message);					//입력받은 아이디와 패스워드를 서버로 보냄
		
		String data = sessions.readSocket(40);			//서버로부터 로그인 성공 여부와 해당 아이디의 아이디, 성별, 나잇대를 읽어옴
		String[] datas = data.split("///");				//모든 데이터들은 '///'로 구분 
		
		if(datas[0].equals("성공")){
			User user = null;
			if(datas[1].equals("관리자")){
				user = new User("root","관리자","관리자");
			}else{
				user = new User(datas[1], datas[2], datas[3]);
			}
				sessions.setUser(user);
				
				sessions.writeSocket("리스트///1");
				sessions.alterStage("리스트");
			
		}else{
			sessions.popup("아이디나 패스워드가 틀립니다.");
		}
														//로그인 성공 여부와 관리자와 일반유저을 구분
	}
	
	//아이디 또는 비밀번호 찾기를 위한 찾기 버튼 이벤트 처리 메소드
	public void handle_SearchBtn(){
		sessions.alterStage("찾기");
	}
	
	//닫기 버튼 이벤트 처리 메소드
	public void handle_CancelBtn(){
		Platform.exit();
	}
}
