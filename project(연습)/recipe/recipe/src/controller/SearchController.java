package controller;

/*
 * 아이디 및 비밀번호 찾기를 위한 페이지 컨트롤러 클래스이다.
 * 기능으로는 이메일을 입력하여 서버로 이메일을 보내준다.(서버에서는 해당 이메일로 등록 되있는 회원을 찾아 존재 할 경우 입력한 이메일로 아이디를 보내준다.)
 * 비밀번호 찾기의 경우 아이디까지 입력하여야 하며 같은 형식으로 이메일로 아이디와 비밀번호를 보내준다.
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.control.TextField;
import source.Sessions;

public class SearchController implements Initializable {

	@FXML private TextField mail1_Text;					//아이디 찾기를 위한 메일 텍스트 필드
	@FXML private TextField mail2_Text;					//비밀번호 찾기를 위한 메일 텍스트 필드
	@FXML private TextField id_Text;					//비밀번호 찾기를 위한 아이디 텍스트 필드
	private Sessions sessions;							//유저 세션
	
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	//이전 화면에서의 세션 할당을 위한 메소드
	public void setSession(Sessions sessions){
		this.sessions = sessions;
	}
	
	//아이디 찾기 부분에서의 찾기 버튼에 대한 이벤트 처리 메소드
	public void handle_Search1Btn(){
		
		if(mail1_Text.getText().equals("")){
			sessions.popup("정보를 입력하세요.");				//메일 텍스트 부분의 공백 부분 조건문으로 팝업창을 활성화	
		}else{
			String message="아이디찾기///"+mail1_Text.getText();
			sessions.connectSocket();
			sessions.writeSocket(message);
			
			String data = sessions.readSocket(10);
														//아이디 찾기를 위해 서버와 연결을 하고 메일을 보냄으로써 존재하지 않은지의 성공여부를 받아온다.
			if(data.equals("성공")){
				sessions.popup("이메일로 전송되었습니다.");
				sessions.alterStage("로그인");			//찾을 시 팝업창 활성화와 로그인 페이지로 화면 전환
			}else
				sessions.popup("아이디 찾기 실패");			//실패 시 팝업창 활성화
		}
	}
	
	//비밀번호 찾기 부분에서의 찾기 버튼에 대한 이벤트 처리 메소드
	public void handle_Search2Btn(){
		
		if((!mail2_Text.getText().equals(""))&&!id_Text.getText().equals("")){
														//메일과 아이디 텍스트 부분의 공백에 대한 조건문
			String message="비밀번호찾기///"+mail2_Text.getText()+"///"+id_Text.getText();
			sessions.connectSocket();
			sessions.writeSocket(message);

			String data = sessions.readSocket(10);
														//비밀번호 찾기를 위해 서버 연결 후 아이디와 메일을 보내고 성공여부를 받아온다.
			if(data.equals("성공")){
				sessions.popup("이메일로 전송되었습니다.");
				sessions.alterStage("로그인");
			}else
				sessions.popup("아이디 찾기 실패");
		}else
			sessions.popup("정보를 입력하세요.");

	}
	
	//닫기 버튼에 대한 이벤트 처리 메소드
	public void handle_CancelBtn(){
		 sessions.alterStage("로그인");
		 sessions.stopSession();
	}

}
