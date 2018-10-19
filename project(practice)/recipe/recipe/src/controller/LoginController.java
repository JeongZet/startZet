package controller;

/*
 * �α��� ȭ���� �̺�Ʈ ó���� ���� ȭ������
 * �Է� ���� ���̵�� ��й�ȣ
 * �α��� ȭ�鿡�� ���� ������ �������� �� 3����
 * ȸ������ ������, ã�� ������, �α��� �������� ���� ������ ����Ʈ �������� �ִ�.
 * �ݱ� ��ư �� ���α׷� ����.
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

	private Sessions sessions;									//�α��� ������ ����
	@FXML private TextField id_Text;							//���̵� �ؽ�Ʈ �ʵ�
	@FXML private PasswordField pw_Text;						//�н����� �ʵ�
	
	public void initialize(URL location, ResourceBundle resources) {
		
		id_Text.setText("���̵� �Է��ϼ���.");
		pw_Text.setText("��й�ȣ");
		
		id_Text.setOnKeyPressed(event->{
			if(event.getCode().equals(KeyCode.ENTER)){
				handle_LogBtn();
			}
		});
		
		pw_Text.setOnKeyPressed(event->{
			if(event.getCode().equals(KeyCode.ENTER)){
				handle_LogBtn();
			}
		});//���̵� �ؽ�Ʈ �ʵ�� ��й�ȣ �ʵ忡���� ���� Ű ó�� 
		
	}
	
	//���� ȭ�鿡���� ���� �Ҵ��� ���� �޼ҵ�
	public void setSession(Sessions sessions){
		this.sessions=sessions;
	}
	
	//ȸ������ ��ư �̺�Ʈ ó�� �޼ҵ�
	public void handle_RegBtn(){
		sessions.alterStage("ȸ������");
	}
	
	//�α��� ��ư �̺�Ʈ ó�� �޼ҵ�
	public void handle_LogBtn(){
		sessions.connectSocket();						//�α����� ���� ������ ������ ������ ����
		
		if(id_Text.getText().equals("")){
			id_Text.setText("���̵� �Է��ϼ���.");
		}
		if(pw_Text.getText().equals("")){
			pw_Text.setText("��й�ȣ");
		}												//���̵�� �н������� �������� ���� ���� ó��				
		
		String message = "�α���///"+id_Text.getText()+"///"+pw_Text.getText();	
														//�տ� �α��� �ؽ�Ʈ�� �񵿱� ���������� ó�� ������ ���� ��� 

		sessions.writeSocket(message);					//�Է¹��� ���̵�� �н����带 ������ ����
		
		String data = sessions.readSocket(40);			//�����κ��� �α��� ���� ���ο� �ش� ���̵��� ���̵�, ����, ���մ븦 �о��
		String[] datas = data.split("///");				//��� �����͵��� '///'�� ���� 
		
		if(datas[0].equals("����")){
			User user = null;
			if(datas[1].equals("������")){
				user = new User("root","������","������");
			}else{
				user = new User(datas[1], datas[2], datas[3]);
			}
				sessions.setUser(user);
				
				sessions.writeSocket("����Ʈ///1");
				sessions.alterStage("����Ʈ");
			
		}else{
			sessions.popup("���̵� �н����尡 Ʋ���ϴ�.");
		}
														//�α��� ���� ���ο� �����ڿ� �Ϲ������� ����
	}
	
	//���̵� �Ǵ� ��й�ȣ ã�⸦ ���� ã�� ��ư �̺�Ʈ ó�� �޼ҵ�
	public void handle_SearchBtn(){
		sessions.alterStage("ã��");
	}
	
	//�ݱ� ��ư �̺�Ʈ ó�� �޼ҵ�
	public void handle_CancelBtn(){
		Platform.exit();
	}
}
