package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import source.Sessions;

public class SearchController implements Initializable {

	@FXML private TextField mail1_Text;
	@FXML private TextField mail2_Text;
	@FXML private TextField id_Text;
	private Sessions sessions;
	
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void setSession(Sessions sessions){
		this.sessions = sessions;
	}
	
	public void handle_Search1Btn(){
		
		if(mail1_Text.getText().equals("")){
			sessions.popup("정보를 입력하세요.");
		}else{
			String message="아이디찾기///"+mail1_Text.getText();
			sessions.connectSocket();
			sessions.writeSocket(message);
			
			String data = sessions.readSocket(10);
			
			if(data.equals("성공")){
				sessions.popup("이메일로 전송되었습니다.");
				sessions.alterStage("로그인");
			}else
				sessions.popup("아이디 찾기 실패");
		}
	}
	
	public void handle_Search2Btn(){
		
		if((!mail2_Text.getText().equals(""))&&!id_Text.getText().equals("")){
			String message="비밀번호찾기///"+mail2_Text.getText()+"///"+id_Text.getText();
			sessions.connectSocket();
			sessions.writeSocket(message);

			String data = sessions.readSocket(10);
			
			if(data.equals("성공")){
				sessions.popup("이메일로 전송되었습니다.");
				sessions.alterStage("로그인");
			}else
				sessions.popup("아이디 찾기 실패");
		}else
			sessions.popup("정보를 입력하세요.");

	}
	
	public void handle_CancelBtn(){
		 sessions.alterStage("로그인");
		 sessions.stopSession();
	}

}
