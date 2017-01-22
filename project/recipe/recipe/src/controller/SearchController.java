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
			sessions.popup("������ �Է��ϼ���.");
		}else{
			String message="���̵�ã��///"+mail1_Text.getText();
			sessions.connectSocket();
			sessions.writeSocket(message);
			
			String data = sessions.readSocket(10);
			
			if(data.equals("����")){
				sessions.popup("�̸��Ϸ� ���۵Ǿ����ϴ�.");
				sessions.alterStage("�α���");
			}else
				sessions.popup("���̵� ã�� ����");
		}
	}
	
	public void handle_Search2Btn(){
		
		if((!mail2_Text.getText().equals(""))&&!id_Text.getText().equals("")){
			String message="��й�ȣã��///"+mail2_Text.getText()+"///"+id_Text.getText();
			sessions.connectSocket();
			sessions.writeSocket(message);

			String data = sessions.readSocket(10);
			
			if(data.equals("����")){
				sessions.popup("�̸��Ϸ� ���۵Ǿ����ϴ�.");
				sessions.alterStage("�α���");
			}else
				sessions.popup("���̵� ã�� ����");
		}else
			sessions.popup("������ �Է��ϼ���.");

	}
	
	public void handle_CancelBtn(){
		 sessions.alterStage("�α���");
		 sessions.stopSession();
	}

}
