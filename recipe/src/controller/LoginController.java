package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import source.Sessions;
import source.User;

public class LoginController implements Initializable {

	private Sessions sessions;
	@FXML private TextField id_Text;
	@FXML private PasswordField pw_Text;
	
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
		});
		
	}
	
	public void setSession(Sessions sessions){
		this.sessions=sessions;
	}
	
	public void handle_RegBtn(){
		sessions.alterStage("회원가입");
	}
	
	public void handle_LogBtn(){
		sessions.connectSocket();
		
		if(id_Text.getText().equals("")){
			id_Text.setText("아이디를 입력하세요.");
		}
		if(pw_Text.getText().equals("")){
			pw_Text.setText("비밀번호");
		}
		
		String message = "로그인///"+id_Text.getText()+"///"+pw_Text.getText();
		
		sessions.writeSocket(message);
		
		String data = sessions.readSocket(40);
		String[] datas = data.split("///");
		
		if(datas[0].equals("성공")){
			User user = new User(datas[1], datas[2], datas[3]);
			sessions.setUser(user);
			
			sessions.writeSocket("리스트");
			sessions.alterStage("리스트");
		}else{
			sessions.popup("아이디나 패스워드가 틀립니다.");
		}
		
	}
	
	public void handle_SearchBtn(){
		sessions.alterStage("찾기");
	}
	
	public void handle_CancelBtn(){
		Platform.exit();
	}
}
