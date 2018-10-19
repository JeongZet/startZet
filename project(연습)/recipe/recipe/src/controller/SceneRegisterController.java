package controller;

/*
 * ������ ��� ���������� ���� ��ư�� ���� ������ Scene ��� ������ ��Ʈ�ѷ� Ŭ������
 * �̹����� ã�ƺ��⸦ ���� png, jpg, gif Ȯ���� ������ �׸������� �����Ͽ� �̸����⸦ �� �� ������
 * �ؽ�Ʈ ����� â�� ������ ���� ��� �Ͽ� Scene�� ����� �� �ִ� ����� �ִ�.
 * ���� ��ư�� ���� ���� �������� �ۼ� �� �� ������ ���� ��ư�� ���� ������ �ۼ��ߴ� ���� ���� �ۼ� �� �� �ִ�.
 */

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import source.Sessions;

public class SceneRegisterController implements Initializable {

	@FXML private Button register_Btn;						//��� ��ư
	@FXML private Button next_Btn;							//���� ��ư
	@FXML private Button prior_Btn;							//���� ��ư
	@FXML private TextArea recipe_TextArea;					//Scene ���� �ؽ�Ʈ �����
	@FXML private ImageView recipe_ImageView;				//Scene �̹��� �̸����� �̹�����
	@FXML private TextField filePath_Text;					//���� ��� �ؽ�Ʈ �ʵ�
	@FXML private Label page_Label;							//���� ������ / �ִ� ������  ǥ�ø� ���� ��
	private Sessions session;								//���� ����
	private int nowScene=1;									//���� ������
	private int scene;										//�ִ� ������
	private ByteBuffer imageBuffer;							//���� ��θ� ���� �޾ƿ� �̹����� ������ �̹��� ����
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		prior_Btn.setDisable(true);
		register_Btn.setDisable(true);
	}
	
	//���� ȭ�鿡���� ���� �Ҵ��� ���� �޼ҵ�
	public void setSession(Sessions session){
		this.session=session;
		scene=session.getRecipe().getScene();	//�������κ��� �������� �� �Ҵ�� Scene ���� �޾ƿ´�.
		buttonSetting();						//����, ����, ��� ��ư�� Ȱ��ȭ �� ��Ȱ��ȭ�� ���� �޼ҵ� ȣ��
		pageSetting(nowScene,scene);			//���� ������ �� �ִ� ������ ���� ����� ���� �޼ҵ� ȣ��
	}
	
	//ã�ƺ��� ��ư�� �̺�Ʈ ó�� �޼ҵ�� png, jpg, gif Ȯ������ ���ϵ��� ������ �� �ִ�.
	//�����ϰ� �Ǹ� �̹��� �信 �ش� �̹����� ����Ͽ� �����ν� �̸����Ⱑ �����ϴ�.
	public void handle_PathFindBtn() throws IOException{
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files","*.png","*.jpg","*.gif"));
		//Ȯ���� �߰�(�̹��� ���� png, jpg, gif)
		
		File selectedFile = fileChooser.showOpenDialog(session.getStage());
		//���ϰ�ηκ��� ���Ͽ� �Ҵ�
		
		if(selectedFile!=null){	//���ϰ�ηκ��� ���� ������ null�� �ƴϸ� �ؽ�Ʈ �ʵ忡 ��θ� ����ϰ� �̹��� �信 �ش� �̹����� ���
			filePath_Text.setText(selectedFile.getPath());
			recipe_ImageView.setImage(new Image("File:///"+selectedFile.getPath()));
			imageBuffer= ByteBuffer.wrap(Files.readAllBytes(selectedFile.toPath()));
			//�޾ƿ� �̹����� ���۷� ��ȯ(������ ������ ����)
		}
	}
	
	//Scene�� ���� ����� �̹��� �� ������ ������ ������ ���� �޼ҵ�
	private void send_SceneData(String state){
		
		try{
		session.writeSocket("�����///"+session.getRecipe().getRNo()+"///"+nowScene+"///"+recipe_TextArea.getText()+"///"+imageBuffer.capacity()+"///"+state);
		//Scene ����� ���� �ش� �������� ��ȣ�� ���� Scene ��ȣ, Scene ����, �̹��� ������ ũ�� �׸��� Commit ���θ� ������.
		//Commit���θ� ������ ������ ���� ��ư�� ���� �� ������ �����͸� ���� �ش� �ο츦 Insert ������ ���� ��� �� ��츦 ��� �ϱ� ����.
		
		String message = session.readSocket(10);		//���� ���θ� Ȯ���ϱ� ���� �����͸� �о��
		
		if(message.equals("����")){
		
			Future<Integer> write_Future = session.getSocketChannel().write(imageBuffer);
			write_Future.get();
			//�̹��� ���۸� ���� ������ �̹��� ���۰� ������ �� ������ �� ���� ���ŷ �Ͽ� �ش�.
		}
		}catch(Exception e){}
	}
	
	//ȭ�鿡 ���� ��ư �� ��� ���� Ȱ��ȭ �� ��Ȱ��ȭ�� �������ִ� �޼ҵ��̴�.
	private void buttonSetting(){
		recipe_ImageView.setImage(null);
		filePath_Text.setText("");
		if(nowScene==scene){
			next_Btn.setDisable(true);
			register_Btn.setDisable(false);
		}else{
			next_Btn.setDisable(false);
			register_Btn.setDisable(true);
		}
		
		if(nowScene==1){
			prior_Btn.setDisable(true);
		}else{
			prior_Btn.setDisable(false);
		}
		pageSetting(nowScene,scene);
	}
	
	//������ �� Setting�� ���� �޼ҵ�� ���� �������� �ִ� �������� �󺧿� Setting ���ش�.
	private void pageSetting(int nowScene, int Scene){
		page_Label.setText(nowScene + " / " + Scene);
	}
	
	//��� ��ư�� ���� �̺�Ʈ ó���� ���� �޼ҵ��̴�.
	public void handle_RegisterBtn(){
		send_SceneData("commit");				//commit�� ���¸� ���������ν� ���� Scene �������� commit�Ͽ� ������ �����ͷ� ó��
		session.writeSocket("����Ʈ///1");
		session.alterStage("����Ʈ");				//�Ŀ� ������ ����Ʈ �������� ȭ�� ��ȯ
	}
	
	//���� ��ư�� ���� �̺�Ʈ ó���� ���� �޼ҵ��̴�.
	public void handle_NextBtn(){
		send_SceneData("default");				//commit�� ���� �ʴ� ���·� ���� ��Ҹ� �� ��츦 ����Ͽ� default ���� ����
		
		nowScene+=1;							//���� ������ ���� +1 ���ش�.
		buttonSetting();						//���� ȭ�� ��ȯ�� ���� ��ư Ȱ��ȭ Setting�� �Ѵ�.
		
	}
	
	//���� ��ư�� ���� �̺�Ʈ ó���� ���� �޼ҵ��̴�.
	public void handle_PriorBtn(){
		nowScene-=1;							//���� ������ ���� -1 ���ش�.
		buttonSetting();						
		
	}
	
	//�ݱ� ��ư�� ���� �̺�Ʈ ó���� ���� �޼ҵ��̴�.
	public void handle_CancelBtn(){
		send_SceneData("rollback");				//�ݱ⸦ �� ��� Scene ����� ������ �������� �ʾұ� ������ �������� DB�� rollback�� ����
												//rollback�̶�� State�� �����ش�.
		
		session.alterStage("���");				//������ ��� �������� ȭ�� ��ȯ
	}

}
