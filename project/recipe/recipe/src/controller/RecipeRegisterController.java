package controller;

/*
 * ������ ����� ���� ��Ʈ�ѷ� Ŭ������ 
 * �������� �̸�, ����, Scene ��, ��Ḧ �����Ͽ� ������ �������ν� �����Ǹ� ����� �� �ִ� ����� �Ѵ�.
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.control.*;
import source.Recipe;
import source.Sessions;

public class RecipeRegisterController implements Initializable {

	@FXML private TextField name_Text;				//������ �̸��� �����ϴ� �ؽ�Ʈ�ʵ�
	@FXML private TextField kind_Text;				//�丮 ������ �����ϴ� �ؽ�Ʈ �ʵ�
	@FXML private TextField scene_Text;				//Scene�� ���� �����ϴ� �ؽ�Ʈ �ʵ�
	@FXML private TextArea item_Text;				//�丮 ��Ḧ �����ϴ� �ؽ�Ʈ �ʵ�
	private Sessions session;						//���� ���������� Session ���� �Ҵ� �ޱ� ���� ��
	
	//���� ȭ�鿡���� ���� �Ҵ��� ���� �޼ҵ�
	public void setSession(Sessions session){
		this.session=session;
	}
	
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	//���� ��ư�� ���� �̺�Ʈ ó�� �޼ҵ�� ���� ��ư�� ���� �� ���� Scene�� ���� ����� �̹��� ÷�θ� �� �� �ִ� �������� �Ѿ
	public void handle_NextBtn(){
		if(!name_Text.getText().equals("")&&
				!kind_Text.getText().equals("")&&
				!scene_Text.getText().equals("")&&
				!item_Text.getText().equals("")){	//��� �ؽ�Ʈ �ʵ忡 ������ ���� ���� ���ϰ� �ϴ� ����
			String mode = "0";						//������ ��� ������ ���� ����
			Recipe recipe= new Recipe(session.getUser().getID(), name_Text.getText(), item_Text.getText(), kind_Text.getText(), "0", "0");
			//����� �� �������κ��� ���� �α����� ������ ���̵� ������ �ۼ��� ���̵�� �����ϰ�, ���� �ؽ�Ʈ �ʵ�κ��� ���� �����´�. �׸��� ��õ ���� ��� ���� 0 �̹Ƿ� 0�� �Ҵ��Ͽ�
			//Recipe ��ü ����
			session.setRecipe(recipe);				//���ǿ� ���� ���� Recipe ��ü �Ҵ�
			session.getRecipe().setScene(Integer.parseInt(scene_Text.getText()));	//������ ������ ��ü�� Scene ���� �Ҵ�
			if(session.getUser().getID().equals("root")){
				mode = "1";
			}//������ ����� �� mode ������ 1�� ���� - �������� �� ������ ���� ���������� �Ϲ� �������� ����
			session.writeSocket("�����ǵ��///"+name_Text.getText()+"///"+kind_Text.getText()+"///"+item_Text.getText()+"///"+session.getUser().getID()+"///"+mode);
			//������ ����� ���� �޽����� ������. �� �ؽ�Ʈ �ʵ��� ����� ������ ID, �׸��� mode ������ ����.
			
			session.getRecipe().setRNo(Integer.parseInt(session.readSocket(5)));
			//������ �������� ���� �������� ��ȣ�� �޾ƿ� ���ǿ� ������ ��ü�� �����Ѵ�.
			
			session.alterStage("�����");			//Scene ��� �������� ȭ�� ��ȯ
		
		}else
			session.popup("��� ������ �Է����ּ���.");		//4���� �ؽ�Ʈ �ʵ� �� 1���� ������ �ÿ� �˾�â Ȱ��ȭ
	}
	
	//��� ��ư�� ���� �̺�Ʈ ó�� �޼ҵ� 
	public void handle_CancelBtn(){
		session.writeSocket("����Ʈ///1");
		session.alterStage("����Ʈ");
	}
}
