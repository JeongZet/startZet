package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import source.Sessions;

public class RegisterController implements Initializable {
	
	private Sessions sessions;
	@FXML private TextField id_Text;
	@FXML private PasswordField pw_Text;
	@FXML private TextField pwcheck_Text;
	@FXML private TextField name_Text;
	@FXML private TextField tel_Text;
	@FXML private TextField mail_Text;
	@FXML private ComboBox<String> age_Box;
	@FXML private ToggleGroup gender_Group;
	private boolean checking=false;
	private String message;
	
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void setSession(Sessions sessions){
		this.sessions = sessions;
	}
	
	public void handle_RegBtn(){
		
		if((!id_Text.getText().equals(""))&&(!pw_Text.getText().equals(""))&&(!tel_Text.getText().equals(""))&&
				(!mail_Text.getText().equals(""))&&(gender_Group.getSelectedToggle().getUserData()!=null)&&
				(age_Box.getValue()!=null)&&checking==true&&(pw_Text.getText().equals(pwcheck_Text.getText()))){
		
			message = "회원가입///"+								
					id_Text.getText()+"///"+
					pw_Text.getText()+"///"+
					name_Text.getText()+"///"+
					tel_Text.getText()+"///"+
					mail_Text.getText()+"///"+
					gender_Group.getSelectedToggle().getUserData()+"///"+
					age_Box.getValue();
			
			sessions.writeSocket(message);
			
			String data = sessions.readSocket(10);
			
			if(data.equals("성공")){
				sessions.popup("회원 가입에 성공하였습니다.");
				sessions.alterStage("로그인");
			}else
				sessions.popup("회원 가입에 실패");
			
		}else if(checking!=true){
			sessions.popup("아이디 중복 확인을 하십시오.");
		}else if(!pw_Text.getText().equals(pwcheck_Text.getText())){
			sessions.popup("비밀번호가 다릅니다.");
		}else {
			sessions.popup("모든 항목을 기입하셔야 합니다.");
		}

		
		
	}
	
	public void handle_CancelBtn(){
		sessions.alterStage("로그인");
		sessions.stopSession();
	}
	
	public void handle_CheckBtn(){
		
		if(id_Text.getText().equals("")){
			sessions.popup("아이디를 입력하시오.");
		}else{
			
			sessions.connectSocket();
			
			String message = "중복확인///"+id_Text.getText();
			
			sessions.writeSocket(message);
			
			/*
			session.getSocketChannel().read(read_Buffer, read_Buffer, new CompletionHandler<Integer, ByteBuffer>(){
	
				public void completed(Integer result, ByteBuffer attachment) {
					attachment.flip();
					String message = Charset.forName("UTF-8").decode(attachment).toString();
					System.out.println(message);
					
				}
	
				public void failed(Throwable exc, ByteBuffer attachment) {
					if(session.getSocketChannel().isOpen()) session.stopSession();
				}
			});
			*/
			String data = sessions.readSocket(10);
			
			if(data.equals("없음")){
				checking=true;
				Platform.runLater(()->sessions.popup("사용할 수 있는 아이디입니다."));
			}else if(data.equals("있음")){
				checking=false;
				Platform.runLater(()->sessions.popup("존재하는 아이디입니다."));
			}
		}
	}
}
