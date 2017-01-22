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
		
			message = "ȸ������///"+								
					id_Text.getText()+"///"+
					pw_Text.getText()+"///"+
					name_Text.getText()+"///"+
					tel_Text.getText()+"///"+
					mail_Text.getText()+"///"+
					gender_Group.getSelectedToggle().getUserData()+"///"+
					age_Box.getValue();
			
			sessions.writeSocket(message);
			
			String data = sessions.readSocket(10);
			
			if(data.equals("����")){
				sessions.popup("ȸ�� ���Կ� �����Ͽ����ϴ�.");
				sessions.alterStage("�α���");
			}else
				sessions.popup("ȸ�� ���Կ� ����");
			
		}else if(checking!=true){
			sessions.popup("���̵� �ߺ� Ȯ���� �Ͻʽÿ�.");
		}else if(!pw_Text.getText().equals(pwcheck_Text.getText())){
			sessions.popup("��й�ȣ�� �ٸ��ϴ�.");
		}else {
			sessions.popup("��� �׸��� �����ϼž� �մϴ�.");
		}

		
		
	}
	
	public void handle_CancelBtn(){
		sessions.alterStage("�α���");
		sessions.stopSession();
	}
	
	public void handle_CheckBtn(){
		
		if(id_Text.getText().equals("")){
			sessions.popup("���̵� �Է��Ͻÿ�.");
		}else{
			
			sessions.connectSocket();
			
			String message = "�ߺ�Ȯ��///"+id_Text.getText();
			
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
			
			if(data.equals("����")){
				checking=true;
				Platform.runLater(()->sessions.popup("����� �� �ִ� ���̵��Դϴ�."));
			}else if(data.equals("����")){
				checking=false;
				Platform.runLater(()->sessions.popup("�����ϴ� ���̵��Դϴ�."));
			}
		}
	}
}
