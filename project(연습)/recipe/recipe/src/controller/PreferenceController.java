package controller;

/*
 * ��ȣ���� ���� (���ɺ�, ����) ��õ ���� ���� ���� ����Ʈ�� ���̺� �信 ����Ͽ� �ִ� Ŭ�����̴�.
 * ���� �� ���� ������ �޺��ڽ��� ������ �����ϴ�.
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import source.Recipe;
import source.Sessions;

public class PreferenceController implements Initializable {

	@FXML ComboBox<String> age_ComboBox;				//���ɺ� �޺��ڽ�
	@FXML ComboBox<String> gender_ComboBox;				//���� �޺��ڽ�
	@FXML TableView<Recipe> tableView;					//��ȣ�� ������� ������ִ� ������ Ŭ������ ��� ���̺��
	private Sessions session;							//���� ���������� ������ �Ҵ� �ޱ� ���� ����
	private ObservableList<Recipe> recipeList = FXCollections.observableArrayList();
	//��ȣ�� ������� ������ ����
	
	//���� �������κ��� ������ Setting �ϱ� ���� �޼ҵ�
	public void setSession(Sessions session){
		this.session=session;
		
		setComboBox();			//�޺� �ڽ��� �����̳� ������ �����ϱ� ���� �޼ҵ� ȣ��
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		TableColumn tcUserID = tableView.getColumns().get(0);
		TableColumn tcRName = tableView.getColumns().get(1);
		TableColumn tcRItems = tableView.getColumns().get(2);
		TableColumn tcRKind = tableView.getColumns().get(3);
		TableColumn tcRRecommend = tableView.getColumns().get(4);
		TableColumn tcRComment = tableView.getColumns().get(5);

		tcUserID.setCellValueFactory(new PropertyValueFactory("userID"));
		tcRName.setCellValueFactory(new PropertyValueFactory("rName"));
		tcRItems.setCellValueFactory(new PropertyValueFactory("rItems"));
		tcRKind.setCellValueFactory(new PropertyValueFactory("rKind"));
		tcRRecommend.setCellValueFactory(new PropertyValueFactory("rRecommend"));
		tcRComment.setCellValueFactory(new PropertyValueFactory("rComment"));
		//���̺� ���� �÷��鿡 ���� �Ҵ��ϰ� Recipe Ŭ������ SimpleStringProperty ������ ���� �Ҵ�	
		
		tableView.setItems(recipeList);
		
		age_ComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){

			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				recipeList.clear();		//���� ��� ���� ��� �ʱ�ȭ �Ͽ� �ش�.(�ߺ� ��� �� �� �ֱ� ������)
				session.writeSocket("��ȣ��///"+newValue+"///"+gender_ComboBox.getSelectionModel().selectedItemProperty().getValue());
				//�޺��ڽ����� ���õ� �� newValue�� ���� ���� �޺��ڽ����� ���õ� ���� ������ ������.
				String data = session.readSocket(3000);
				//�׿� �´� ������ �����κ��� �о�´�.
				String[] datas = data.split("///");
				if(!datas[0].equals("����")){
					for(int i=0;i<datas.length;i+=6){
						recipeList.add(new Recipe(datas[i], datas[i+1], datas[i+2], datas[i+3], datas[i+4], datas[i+5]));
					}
					tableView.setItems(recipeList);
				}//�о�� ������ ����Ʈ�� �߰��ϸ� �� ����Ʈ�� ���̺� �信 �����Ͽ� ����Ͽ� �ش�.
			}
		});//���ɺ� �޺��ڽ��� ���� ���� ���� �̺�Ʈ ó�� �ϴ� �κ��̴�.
		
		gender_ComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){

			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				recipeList.clear();
				session.writeSocket("��ȣ��///"+age_ComboBox.getSelectionModel().selectedItemProperty().getValue()+"///"+newValue);
				String data = session.readSocket(1000);
				String[] datas = data.split("///");
				if(!datas[0].equals("����")){
					for(int i=0;i<datas.length;i+=6){
						recipeList.add(new Recipe(datas[i], datas[i+1], datas[i+2], datas[i+3], datas[i+4], datas[i+5]));
					}
					tableView.setItems(recipeList);
				}
			}
		});//���� �޺��ڽ��� ���� ���� ���� �̺�Ʈ ó�� �ϴ� �κ��̴�.
		
	}
	
	//�޺� �ڽ��� ������ �Ҵ��Ͽ� �ִ� �޼ҵ�� �����ǵ� ��õ�ߴ� �������� ������ ������ �������� ���� �޾ƿ´�.
	public void setComboBox(){
		
		session.writeSocket("��ȣ���޺��ڽ�");		//�����κ��� ���� ��û�ϱ� ���� �����͸� ������.
		
		String data = session.readSocket(1000);	//�����κ��� �� ������ �޾ƿ´�.(���ɰ� ����)
		
		String[] datas = data.split("///");
		
		ObservableList<String> ageList = FXCollections.observableArrayList();
		ObservableList<String> genderList = FXCollections.observableArrayList();
		//������ ���ɺ��� ������ ����Ʈ�� �����Ѵ�.
		
		for(int i=0;i<datas.length;i+=2){
			ageList.add(datas[i]);
			genderList.add(datas[i+1]);
		}//����Ʈ�鿡 �ش� ������ �����Ѵ�.
		
		ageList.add("��ü ����");
		genderList.add("��ü ����");
		//��ü���⸦ ������ ��츦 ���� ��ü ������ Ű���嵵 ����
		
		age_ComboBox.setItems(ageList);
		gender_ComboBox.setItems(genderList);
		//�� ����Ʈ���� ������ �޺��ڽ��� �����Ѵ�.
		
	}
	
	//���� ��ư�� ���� �̺�Ʈ ó�� �޼ҵ�
	public void handle_PriorBtn(){

		session.writeSocket("����Ʈ///1");
		session.alterStage("����Ʈ");
	}
	
}
