package controller;

/*
 * ���̵� �� ��й�ȣ ã�⸦ ���� ������ ��Ʈ�ѷ� Ŭ�����̴�.
 * ������δ� �̸����� �Է��Ͽ� ������ �̸����� �����ش�.(���������� �ش� �̸��Ϸ� ��� ���ִ� ȸ���� ã�� ���� �� ��� �Է��� �̸��Ϸ� ���̵� �����ش�.)
 * ��й�ȣ ã���� ��� ���̵���� �Է��Ͽ��� �ϸ� ���� �������� �̸��Ϸ� ���̵�� ��й�ȣ�� �����ش�.
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.control.TextField;
import source.Sessions;

public class SearchController implements Initializable {

	@FXML private TextField mail1_Text;					//���̵� ã�⸦ ���� ���� �ؽ�Ʈ �ʵ�
	@FXML private TextField mail2_Text;					//��й�ȣ ã�⸦ ���� ���� �ؽ�Ʈ �ʵ�
	@FXML private TextField id_Text;					//��й�ȣ ã�⸦ ���� ���̵� �ؽ�Ʈ �ʵ�
	private Sessions sessions;							//���� ����
	
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	//���� ȭ�鿡���� ���� �Ҵ��� ���� �޼ҵ�
	public void setSession(Sessions sessions){
		this.sessions = sessions;
	}
	
	//���̵� ã�� �κп����� ã�� ��ư�� ���� �̺�Ʈ ó�� �޼ҵ�
	public void handle_Search1Btn(){
		
		if(mail1_Text.getText().equals("")){
			sessions.popup("������ �Է��ϼ���.");				//���� �ؽ�Ʈ �κ��� ���� �κ� ���ǹ����� �˾�â�� Ȱ��ȭ	
		}else{
			String message="���̵�ã��///"+mail1_Text.getText();
			sessions.connectSocket();
			sessions.writeSocket(message);
			
			String data = sessions.readSocket(10);
														//���̵� ã�⸦ ���� ������ ������ �ϰ� ������ �������ν� �������� �������� �������θ� �޾ƿ´�.
			if(data.equals("����")){
				sessions.popup("�̸��Ϸ� ���۵Ǿ����ϴ�.");
				sessions.alterStage("�α���");			//ã�� �� �˾�â Ȱ��ȭ�� �α��� �������� ȭ�� ��ȯ
			}else
				sessions.popup("���̵� ã�� ����");			//���� �� �˾�â Ȱ��ȭ
		}
	}
	
	//��й�ȣ ã�� �κп����� ã�� ��ư�� ���� �̺�Ʈ ó�� �޼ҵ�
	public void handle_Search2Btn(){
		
		if((!mail2_Text.getText().equals(""))&&!id_Text.getText().equals("")){
														//���ϰ� ���̵� �ؽ�Ʈ �κ��� ���鿡 ���� ���ǹ�
			String message="��й�ȣã��///"+mail2_Text.getText()+"///"+id_Text.getText();
			sessions.connectSocket();
			sessions.writeSocket(message);

			String data = sessions.readSocket(10);
														//��й�ȣ ã�⸦ ���� ���� ���� �� ���̵�� ������ ������ �������θ� �޾ƿ´�.
			if(data.equals("����")){
				sessions.popup("�̸��Ϸ� ���۵Ǿ����ϴ�.");
				sessions.alterStage("�α���");
			}else
				sessions.popup("���̵� ã�� ����");
		}else
			sessions.popup("������ �Է��ϼ���.");

	}
	
	//�ݱ� ��ư�� ���� �̺�Ʈ ó�� �޼ҵ�
	public void handle_CancelBtn(){
		 sessions.alterStage("�α���");
		 sessions.stopSession();
	}

}
