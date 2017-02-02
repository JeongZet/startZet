package controller;

/*
 * ȸ�� ���� ��Ʈ�ѷ� Ŭ������
 * ���̵�, ��й�ȣ, ��й�ȣ Ȯ��, �̸�, ��ȣ, ����, ����, ������ �Է��� �� ������
 * ������δ� ��й�ȣ Ȯ�� �ϴ� ��� �� ���̵� �ߺ� üũ �� �� �ִ� ���, ȸ�� ���� ����� �ִ�.
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.*;
import javafx.scene.control.*;
import source.Sessions;

public class RegisterController implements Initializable {
	
	private Sessions sessions;									//�α��� ������ ����
	@FXML private TextField id_Text;							//���̵� �ؽ�Ʈ �ʵ�
	@FXML private PasswordField pw_Text;						//�н����� �ʵ�
	@FXML private PasswordField pwcheck_Text;					//�н����� Ȯ�� �ʵ�	
	@FXML private TextField name_Text;							//�̸� �ؽ�Ʈ �ʵ�
	@FXML private TextField tel_Text;							//��ȭ��ȣ �ؽ�Ʈ �ʵ�
	@FXML private TextField mail_Text;							//���� �ؽ�Ʈ �ʵ�
	@FXML private ComboBox<String> age_Box;						//���� �޺� �ڽ�(10���� ���)
	@FXML private ToggleGroup gender_Group;						//���� ���� ��ư�� ��� �׷�
	private boolean checking=false;								//�ߺ� üũ Ȯ�� ����
	
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	//���� ȭ�鿡���� ���� �Ҵ��� ���� �޼ҵ�
	public void setSession(Sessions sessions){
		this.sessions = sessions;
	}
	
	//��� ��ư�� �̺�Ʈ ó���� ���� �޼ҵ�� ������ ���̵�, ��й�ȣ, �̸� ���� ������ ���� ���θ� �о�´�.
	public void handle_RegBtn(){
		
		if((!id_Text.getText().equals(""))&&(!pw_Text.getText().equals(""))&&(!tel_Text.getText().equals(""))&&
				(!mail_Text.getText().equals(""))&&(gender_Group.getSelectedToggle().getUserData()!=null)&&
				(age_Box.getValue()!=null)&&checking==true&&(pw_Text.getText().equals(pwcheck_Text.getText()))){
		//��� �׸� ���� ���� ������ ���� ���ǹ�
			String message = "ȸ������///"+								
					id_Text.getText()+"///"+
					pw_Text.getText()+"///"+
					name_Text.getText()+"///"+
					tel_Text.getText()+"///"+
					mail_Text.getText()+"///"+
					gender_Group.getSelectedToggle().getUserData()+"///"+
					age_Box.getValue();
			
			sessions.writeSocket(message);			//������ ȸ�� ���Կ� �ʿ��� �������� ������ ������.
			
			String data = sessions.readSocket(10);	//�����κ��� ���� ���θ� �޾ƿ´�.
			
			if(data.equals("����")){
				sessions.popup("ȸ�� ���Կ� �����Ͽ����ϴ�.");
				sessions.alterStage("�α���");
			}else
				sessions.popup("ȸ�� ���Կ� ����");
			
		}else if(checking!=true){					//���̵� �ߺ� üũ ���θ� ���� ���ǹ�
			sessions.popup("���̵� �ߺ� Ȯ���� �Ͻʽÿ�.");
		}else if(!pw_Text.getText().equals(pwcheck_Text.getText())){	//��й�ȣ�� ��й�ȣ Ȯ���� ���� �ٸ� ���
			sessions.popup("��й�ȣ�� �ٸ��ϴ�.");
		}else {										//��� �׸��� ���� ���� �ʾ��� ���
			sessions.popup("��� �׸��� �����ϼž� �մϴ�.");
		}
	}
	
	//��� ��ư�� ���� �̺�Ʈ ó�� �޼ҵ�
	public void handle_CancelBtn(){
		sessions.alterStage("�α���");
		sessions.stopSession();
	}
	
	//�ߺ� üũ ��ư�� ���� �̺�Ʈ ó�� �޼ҵ�� ������ ���̵� ������ ������.
	//�׸��� �����κ��� �ߺ��� ���θ� Ȯ���ϴ� �޽����� �޾ƿ´�.
	public void handle_CheckBtn(){
		
		if(id_Text.getText().equals("")){
			sessions.popup("���̵� �Է��Ͻÿ�.");		//���̵� �ؽ�Ʈ �ʵ尡 ������ ��� �˾�â Ȱ��ȭ
		}else{
			
			sessions.connectSocket();				//������ ���۸� ������ ���� ���� ����
			
			String message = "�ߺ�Ȯ��///"+id_Text.getText();
			
			sessions.writeSocket(message);		
			
			String data = sessions.readSocket(10);	//������ ���̵� ������ �ߺ� ������ ���۸� �޾ƿ´�.
			
			if(data.equals("����")){
				checking=true;
				Platform.runLater(()->sessions.popup("����� �� �ִ� ���̵��Դϴ�."));
				id_Text.setEditable(false);
			}else if(data.equals("����")){
				checking=false;
				Platform.runLater(()->sessions.popup("�����ϴ� ���̵��Դϴ�."));
			}//���̵� �ߺ��� ���� ���ο� ���� �˾�â Ȱ��ȭ�� checking ���� �ʱ�ȭ
		}
	}
}
