package controller;

/*
 * �α��� �Ŀ� DB�� ��� �Ǿ��ִ� ������ ����Ʈ���� TableView�� ����Ͽ� �ִ� Ŭ������
 * �����ID, ������ �̸�, �丮 ��� �� Recipe Ŭ������ SimpleStringProperty ������� ����Ǿ��ִ� �׸���� ����Ͽ� �ָ�
 * ������δ� ComboBox���� �丮 ������ �����Ͽ� ������ ���� �����Ͽ� �����ָ�
 * �� �˻� ����� �����ϰ� �ִ�. �� ������ Ŭ�������� ���� ������ �������� �� 5����
 * �丮 ��� ������, ��ȣ�� ������, ������ �� ������, �α��� �������� ������ ����! �丮 ��ư�� Ŭ���ϸ� ���� ��� �Ǿ� ���� ����
 * �丮 ����Ʈ���� ����Ʈ�� ����Ͽ� �ִ� �ش� �������� �Ϻθ� ������ �������� �ִ�.
 */

import java.net.URL;
import java.util.*;

import javafx.beans.value.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import source.Recipe;
import source.Sessions;

public class RecipeListController implements Initializable {

	@FXML TableView<Recipe> tableView;					//Recipe Ŭ������ ����Ͽ��ִ� tableView
	@FXML private TextField search_Text;				//ã�� ����� ���� �ؽ�Ʈ �ʵ�
	@FXML private ComboBox<String> kind_ComboBox;		//�丮 ���� ������ ���� ComboBox
	@FXML Button btn_Alter;								//���� �丮 �������� ���� �丮 ������ ������ ���� ��ư
	private Sessions sessions;							//�� ������������ ������ �Ҵ� �ޱ� ���� ����
	private ObservableList<Recipe> recipeList = FXCollections.observableArrayList();
	private ObservableList<Recipe> searchList = FXCollections.observableArrayList();
	//�� ���� Ŭ���� ����Ʈ�� ���� ������ ó�� �޾ƿ� Recipe Ŭ�������� ã�⳪ ���� �������� ����Ǵ� ���� ���� ����
	//recipeList�� ������ ���� searchList�� ���������ν� searchList Ŭ���� ����Ʈ�� ����Ǵ��� ���� ����Ʈ���� ����Ǵ� ����
	//���� ���ؼ��̴�.
	
	private String[] datas;								//�����κ��� �Ѿ���� �����͵��� ���� ������ ��Ʈ�� �迭
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		
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
		//TableView�� TableColumn ���� ���� �� Recipe Ŭ�������� SimpleStringProperty �������� ��������.
		
		tableView.setItems(searchList);
		
		kind_ComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
			
			public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
				Iterator<Recipe> iterator = recipeList.iterator();		//Recipe Ŭ���� Ÿ���� recipeList �� �ݺ��� ���� 
				searchList.clear();										//���� �Ҵ�� searchList �ʱ�ȭ
				
				if(newValue!=null&&!datas[0].equals("����")){			//�޺��ڽ������� �� ���õ� ���¿����� ȭ����ȯ����
					if(newValue.equals("���κ���")){					//������ ó���ϱ� ���� ����
						setAll();
					}
					else{
						while(iterator.hasNext()){
							Recipe recipe = iterator.next();
							if(recipe.getRKind().equals(newValue)){
								searchList.add(recipe);					//�޺��ڽ����� ���õ� �丮 ������ ������ �͵���
																		//searchList�� �߰�
							}
						}
						tableView.setItems(searchList);					//tableView�� searchList�� ���
					}
				}
			}
		});//�丮 ���� �޺� �ڽ��� �� ���濡 ���� �̺�Ʈ ó���� ���� �Լ��̴�.
		
		
		search_Text.setOnKeyPressed(event->{
			if(event.getCode().equals(KeyCode.ENTER)){
				handle_SearchBtn();
			}
		});//�˻� �ؽ�Ʈ �ʵ忡���� ���� Ű�� ���� �̺�Ʈ ó��
		
		tableView.setRowFactory(tc->{
			TableRow row = new TableRow();
			row.setOnMouseClicked(event->{
				if(event.getClickCount()==2&&(!row.isEmpty())){
					try{
						sessions.setRecipe(tableView.getItems().get(row.getIndex()));
						sessions.writeSocket("��///"+Integer.toString(sessions.getRecipe().getRNo())+"///"+"1");
						sessions.alterStage("��");
						
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
			});
			return row;
		});	//���̺� ���� �� �ο쿡 ���� �̺�Ʈ ó���� ���� Ŭ�� �� �� �������� �̵��ϴ� ���� ��Ÿ��.
			//�ش� �ο��� ������ Ŭ������ �������� ���ǿ� �����ϸ� �� �������� ������ �����鼭 �� �������� �̵�.
	}
	
	//���� ȭ�鿡���� ���� �Ҵ��� ���� �޼ҵ�
	//���� �Ҵ�� ���ÿ� ������ ���� ������ ����Ʈ���� ���۷� �޾ƿ� 
	//�� ����� �����Ͽ� Kind_ComboBox�� �ش� �����ǵ��� �丮 ������ �ʱ�ȭ���ְ�
	//��� ����Ʈ���� TableView�� ����� ���� setAll() �޼ҵ带 ����
	public void setSession(Sessions sessions){
		this.sessions = sessions;

		String data=sessions.readSocket(3000);	
		datas = data.split("///");
		setKind(datas);
		kind_ComboBox.getSelectionModel().selectFirst();
		if(!datas[0].equals("����")){
			setAll();
		}		
	}
	
	//�����κ��� ������ ����Ʈ���� ��� ���۷� �޾ƿ� Recipe Ŭ���� ��ü�� ����� Ŭ���� ����Ʈ�鿡 �Ҵ��Ͽ�
	//TableView�� ����Ͽ� ��.
	public void setAll(){
		recipeList.clear();
		for(int i=0;i<datas.length;i=i+8){
			Recipe recipe = new Recipe(datas[i+2],datas[i+3],datas[i+4],datas[i+5],datas[i+6],datas[i+7]);
			recipe.setRNo(Integer.parseInt(datas[i]));
			recipe.setScene(Integer.parseInt(datas[i+1]));
			recipeList.add(recipe);
		}
		searchList.addAll(recipeList);
		tableView.setItems(recipeList);
	}
	
	//�����κ��� �޾ƿ� �����͵��� �丮 �������� ComboBox�� �ߺ� �����Ͽ� ����ϱ� ���� �޼ҵ�.
	public void setKind(String ... datas){
		HashSet<String> hs = new HashSet<>();		//HashSet�� �̿��Ͽ� �ߺ� ������ �Ѵ�.(������ ���� �ߺ� ó��)
		
		hs.add("���κ���");							
		for(int i=5;i<datas.length;i=i+8){
			hs.add(datas[i]);
		}
		
		ObservableList<String> kindList = FXCollections.observableArrayList(hs);
		
		kind_ComboBox.setItems(kindList);			
		
	}
	
	//��ȣ�� ��ư �̺�Ʈ ó�� �޼ҵ�
	public void handle_PreferenceBtn(){
		sessions.alterStage("��ȣ��");
		
	}
	
	//ã�� ��ư �̺�Ʈ ó�� �޼ҵ�� �˻� �ؽ�Ʈ �ʵ忡�� �ؽ�Ʈ�� ������
	//�������� ��� �׸� ���ؼ� ���Ͽ� ã�ƿ� �˻��Ѵ�.
	public void handle_SearchBtn(){
		ObservableList<Recipe> tempList = FXCollections.observableArrayList();
		Iterator<Recipe> iterator = searchList.iterator();
		while(iterator.hasNext()){
			Recipe recipe = iterator.next();
			if(recipe.getRItems().contains(search_Text.getText())||
					recipe.getRKind().contains(search_Text.getText())||
					recipe.getRName().contains(search_Text.getText())||
					recipe.getUserID().contains(search_Text.getText())){
				tempList.add(recipe);
			}
		}
		tableView.setItems(tempList);
	}
	
	//���� �丮 or ���� �丮 ��ư�� �̺�Ʈ ó��
	//���� ��޵� �����ǿ� ���� �����κ��� ��ϵ� �丮 ����Ʈ�� �����Ͽ� �����ش�.
	public void handle_AlterBtn(){
		recipeList.clear();						//���� ����� recipeList �ʱ�ȭ
		tableView.setItems(recipeList);			//�̹� ��µ� TableView �ʱ�ȭ
		kind_ComboBox.setItems(FXCollections.observableArrayList());

		if(btn_Alter.getText().equals("�� ��!   �� ��")){

			btn_Alter.setText("���� �丮");
			sessions.writeSocket("����Ʈ///0");	
			
		}else if(btn_Alter.getText().equals("���� �丮")){
			
			btn_Alter.setText("�� ��!   �� ��");
			sessions.writeSocket("����Ʈ///1");
		}
		//0�� ���ϴ� ���� ������ ����Ʈ�� DB�� ���� �丮�� ���� �丮�� �������ִ� 
		//RCHECK �÷��� ������ 0�� �����丮, 1�� ���Ŀ丮�� ���Ѵ�.
		
		setSession(sessions);					//���Ӱ� ��� ����Ʈ�� ����Ͽ� �ִ� �޼ҵ���� �Ҵ��� �޼ҵ� ����
	}
	
	
	//�丮 ��� ��ư�� �̺�Ʈ ó�� �޼ҵ�
	public void handle_RegBtn(){
		sessions.alterStage("���");
	}
	
	//�α׾ƿ� ��ư�� �̺�Ʈ ó�� �޼ҵ�
	public void handle_LogoutBtn(){
		 sessions.alterStage("�α���");
		 sessions.stopSession();
	}
	
}
