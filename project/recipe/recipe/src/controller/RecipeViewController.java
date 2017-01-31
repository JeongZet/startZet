package controller;

/*
 * ������ ����Ʈ���� TableView�� Row�� ���� Ŭ���Ͽ� Ȱ��ȭ�� ��Ʈ�� Ŭ������
 * �����κ��� �����ǿ� ��ϵ� Scene�� ������ �޾ƿ´�.
 * Scene�� �����δ� �׸��� Scene ������ ������ �ִ�.
 * �������� ����� ��ư�� ������
 * �ֿ� ������δ� �����κ��� �޾ƿ� ���۸� �̹����� ��ȯ�Ͽ� ImageView�� ���
 * ���� ��ư�� ���� ��ư���� ������ ���� �� ��ü ���� ��ư���� Scene ����鸸 ���
 * ��õ ��ɰ� ��� ����� �����Ѵ�.
 */

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.util.Callback;
import source.Sessions;

public class RecipeViewController implements Initializable {

	@FXML private ImageView recipe_ImageView;				//Scene�� �׸��� ����ϴ� �̹�����
	@FXML private TextArea recipe_TextArea;					//Scene�� ������ ����ϴ� �ؽ�Ʈ�����
	@FXML private ListView<CommentContent> comment_ListView;//��� ����Ʈ���� ����ϴ� ����Ʈ��
	@FXML private TextArea comment_Text;					//��� �Է��� ���� �ؽ�Ʈ�����
	@FXML private Button prior_Btn;							//������ư
	@FXML private Button next_Btn;							//������ư
	@FXML private Button show_Btn;							//��ü���� or ������� ���� ��ư
	@FXML private Button recommend_Btn;						//��õ��ư
	private Sessions session;								//���� ���� �Ҵ��� ���� ���� ��ü �ʱ�ȭ
	private int scene;										//�ִ� Scene ���� �����ϴ� ����
	private int nowScene;									//���� Scene �������� �����ϴ� ����
	private String allMessage;								//��ü ���� �� Scene�� ������� ��� �����ϴ� ���� 
	private ObservableList<CommentContent> commentList = FXCollections.observableArrayList(); //��� ����Ʈ���� �����ϴ� ��ü ����Ʈ
	
	//���� ���������� ������ �Ҵ��ϱ� ���� �޼ҵ�
	public void setSession(Sessions session){
		this.session=session;
		nowScene=1;								//���� Scene ������ �ʱ�ȭ
		view_Scene();							//Scene�� �̹��� �� ������ �����κ��� �޾ƿ��� �޼ҵ� ȣ��
		
		scene = session.getRecipe().getScene();	//�� ���������� �޾ƿ� �� ������ �� �Ҵ�
		
		view_Comment();							//����� ListView�� ������ִ� �޼ҵ� ȣ��
		
	}
	
	public void initialize(URL arg0, ResourceBundle arg1) {

		show_Btn.setOnAction(event->handle_ShowBtn());		//��ü���� ������ �̺�Ʈ ó��
		recipe_TextArea.setEditable(false);					//�������� ���� �κ� ���� �Ұ� ó��
		
	}
	
	//�����κ��� Scene�� ����� �̹����� �޾ƿ� ���� TextArea�� ImageView�� ������ִ� �޼ҵ�
	public void view_Scene(){
		
		recipe_TextArea.clear();					//���� ������������ ������ �ʱ�ȭ
		
		String message = session.readSocket(500);	//���� 500�� ũ�⸦ �������� �о��(Scene�� ����� Scene�� ��ϵ� �̹��� ���� ũ��)
		
		String[] datas = message.split("///");
		
		if(nowScene==1){
			prior_Btn.setDisable(true);
		}else
			prior_Btn.setDisable(false);
		
		if(nowScene==session.getRecipe().getScene()){
			next_Btn.setDisable(true);
		}else
			next_Btn.setDisable(false);
		
		//���� ��ư�� ���� ��ư�� Ȱ��ȭ�� ��Ȱ��ȭ
		
		recipe_TextArea.appendText(datas[1]);		//�����κ��� �޾ƿ� ������ TextArea�� ���
		
		ByteBuffer imageBuffer = ByteBuffer.allocate(Integer.parseInt(datas[2]));	
		//�̹��� ���۸� �ޱ� ���� �̸� ���� �޾Ƴ��� ������ ũ�⸸ŭ�� ����Ʈ ���� �ʱ�ȭ
		
		session.writeSocket("�̹���///"+Integer.toString(session.getRecipe().getRNo())+"///"+nowScene);
		//�̹����� �����޶�� ������ ��û�ϱ� ���� ���� ������ ��ȣ�� ���� Scene ��ȣ�� ������ ���� 
		
		try{
			Future<Integer> read_Future = session.getSocketChannel().read(imageBuffer);
			read_Future.get();
			imageBuffer.flip();
			//�̹����� ���۷� �޾ƿ��� �޾ƿ��� ���� �Ϸ��� ������ ���ŷ �� imageBuffer�� position �ʱ�ȭ
			
			byte[] imageByte = imageBuffer.array();
			
			InputStream is = new ByteArrayInputStream(imageByte);
			//�̹��� ���۸� ����Ʈ �迭�� ��ȯ �� ��Ʈ������ ��ȯ
			
			Image image = new Image(is);
			
			recipe_ImageView.setImage(image);	//�̹����� ��� ��Ʈ���� Image ��ü�� �ʱ�ȭ �� ImageView�� Setting��.
			
		}catch(Exception e){
			if(session.getSocketChannel().isOpen()) session.stopSession();
		}
		

		if(session.getUser().getID().equals("root")){
			recommend_Btn.setText("ä                    ��");
		}//������ ���� �α��� �� ��õ ��ư�� ä�� ��ư���� �ʱ�ȭ
		
	}
	
	//�����κ��� ��� �����͸� �޾ƿ� ListView�� ����Ͽ� �ִ� �޼ҵ�
	public void view_Comment(){
		
		commentList.clear();											//���� �������� ��۸���Ʈ �ʱ�ȭ
		
		session.writeSocket("��ۺ���///"+session.getRecipe().getRNo());
		
		String data = session.readSocket(3000);
		//���� ������ ��ȣ�� �Ѱ������ν� �����κ��� ��� �����͸� �޾ƿ�
		
		if(data.equals("����")){
			commentList.add(new CommentContent("��� ����",""));
		}else{
			String[] datas = data.split("///");
			
			for(int i=0;i<datas.length;i+=2){
				commentList.add(new CommentContent(datas[i], datas[i+1]));
			}
		}//����� ������ ��۵��� ������ �����ϱ� ���� ���ǹ�
		
		comment_ListView.setItems(commentList);
		comment_ListView.setCellFactory(new CommentCellFactory());
		//commentList�� ListView�� ���, ����Ʈ �� ������ ���� ��ȯ�� ���� �޼ҵ�
	}
	
	//��� ��� ��ư�� �̺�Ʈ ó���� ��� ��� �� �ٷ� ��� ����Ʈ�� ��� �ȴ�.
	public void handle_CommentBtn(){
		
		session.writeSocket("��۵��///"+session.getUser().getID()+"///"+session.getRecipe().getRNo()+"///"+comment_Text.getText());
		//��� ����� ���� ����� ID�� ������ ��ȣ �׸��� ��� ������ ������ ������. 
		
		String message = session.readSocket(10); 	//�Ŀ� �����Ͽ��� �� �����ߴٴ� �޽����� �޴´�.
		
		if(message.equals("����")){
			session.popup("��� ��Ͽ� �����ϼ̽��ϴ�.");
			comment_Text.clear();					//�ۼ� �� �ؽ�Ʈ Area�� �ʱ�ȭ�Ѵ�.
			view_Comment();							//view_Comment()�޼ҵ带 ȣ���Ͽ� �簻���Ѵ�.
		}
		
	}
	
	//��õ ��ư�� �̺�Ʈ ó���� ��õ���� �ʾ��� �ÿ� ��õ�� �����ϸ� ��õ�� �̹� ���� ��� �̹� ��õ�ߴٴ� �˾�â�� ����Ѵ�.
	//������ ��� ���� �� ä�� ��ư�� �̺�Ʈ ó���� ���� �丮�� ������ ��� ���� �丮�� ����� �� �ִ� ��ư�̴�.
	public void handle_RecommendBtn(){
		if(recommend_Btn.getText().equals("��                    õ")){
			session.writeSocket("��õ///"+session.getUser().getID()+"///"+session.getRecipe().getRNo()+"///"+session.getUser().getGender()+"///"+session.getUser().getAge());
			//��õ�� ���� ����� ���̵�� ������ ��ȣ �׸��� ��õ���� ������ ���̸� ������ ������.
			String message = session.readSocket(10);//�Ŀ� �����Ͽ��� �� �����ߴٴ� �޽����� �޴´�.
			
			if(message.equals("����")){
				session.popup("��õ �Ͽ����ϴ�.");
			}else
				session.popup("�̹� ��õ�Ͽ����ϴ�.");
			
		}else if(recommend_Btn.getText().equals("ä                    ��")){
			session.writeSocket("ä��///"+session.getRecipe().getRNo());
			//ä���� ���� �ش� �������� ��ȣ�� ������ ������.
			String message = session.readSocket(10);//�Ŀ� �����Ͽ��� �� �����ߴٴ� �޽����� �޴´�.
		
			if(message.equals("����")){
				
				session.writeSocket("����Ʈ///1");
				session.alterStage("����Ʈ");
				session.popup("ä�� �Ͽ����ϴ�.");
				//ä�� ���� �� ������ ����Ʈ ȭ������ ��ȯ�ȴ�.
			}else
				session.popup("�̹� ä���Ͽ����ϴ�.");
		}

	}

	//���� ��ư�� �̺�Ʈ ó�� �޼ҵ�� ���� Scene�� -1 �ϰ� ���Ӱ� ���� ������ ���� ������ ������
	//view_Scene() �޼ҵ带 ȣ���Ͽ� �������� ���� �޽��� �� �̹����� �޾� ó���Ѵ�.
	public void handle_PriorBtn(){
		nowScene-=1;
		
		session.writeSocket("��///"+session.getRecipe().getRNo()+"///"+nowScene);
		
		view_Scene();
		
	}

	//���� ��ư�� �̺�Ʈ ó�� �޼ҵ�� ���� Scene�� +1 �ϰ� ���� ��ư�� ������ ���.
	public void handle_NextBtn(){
		nowScene+=1;
		
		session.writeSocket("��///"+session.getRecipe().getRNo()+"///"+nowScene);
		
		view_Scene();
		
	}

	//��ü���� or �󼼺��� ��ư�� �̺�Ʈ ó�� �޼ҵ�� TextArea â�� ũ�� �� ��ġ ��ȯ, ImageView�� Visible ���� ��ȯ ��
	//��ư�� Ȱ��ȭ ��Ȱ��ȭ�� �ϴ� �޼ҵ��.
	public void handle_ShowBtn(){
		if(show_Btn.getText().equals("��     ü     ��     ��")){
			
			show_Btn.setText("��     ��     ��     ��");
			
			recipe_ImageView.setVisible(false);
			recipe_TextArea.setLayoutY(14.0);
			recipe_TextArea.setPrefHeight(772.0);
			recipe_TextArea.clear();

			session.writeSocket("��ü����///"+session.getRecipe().getRNo());	
			//�����κ��� �ش� ������ ��ȣ�� ������ �ޱ� ���� �޽��� ����.
			
			allMessage = session.readSocket(1000);
			recipe_TextArea.appendText(allMessage);
			//�����κ��� �޾ƿ� �޽����� TextArea�� ���
			
			prior_Btn.setDisable(true);
			next_Btn.setDisable(true);
			
		}else if(show_Btn.getText().equals("��     ��     ��     ��")){
			show_Btn.setText("��     ü     ��     ��");
			
			recipe_ImageView.setVisible(true);
			recipe_TextArea.setLayoutY(571.0);
			recipe_TextArea.setPrefHeight(225.0);
			recipe_TextArea.clear();
			
			nowScene = 1;
			
			session.writeSocket("��///"+session.getRecipe().getRNo()+"///"+"1");
			//�� ȭ�� ����� ���� �ش� ������ ��ȣ�� ���� ó�� �������� ȣ���� ���� ������ �ش� �޽����� ������.
			
			view_Scene();	//�����κ��� �޾ƿ��� Scene�� ����� �̹��� ó���� ���� view_Scene()�޼ҵ� ȣ��
			
			
			if(nowScene==1){
				prior_Btn.setDisable(true);
			}else{
				prior_Btn.setDisable(false);
			}
			if(nowScene==scene){
				next_Btn.setDisable(true);
			}else{
				next_Btn.setDisable(false);
			}
			//���� ���� ��ư ����
		}
	}

	//�ݱ� ��ư �̺�Ʈ ó�� �޼ҵ�
	public void handle_CancelBtn(){
		session.writeSocket("����Ʈ///1");
		session.alterStage("����Ʈ");
	}
	
	//��� Ŭ������ ����� ID�� �������� ������
	class CommentContent{
		private String userID;
		private String cContent;
		
		//������
		CommentContent(String userID, String cContent){
			this.userID = userID;
			this.cContent = cContent;
		}
		
		//Getting �޼ҵ�
		String getUserID(){return userID;}
		String getCContent(){return cContent;}
		
	}
	
	//CommentCell Ŭ������ ����Ʈ ���� ���� ������ ���� ���� ���̵� �� ��. - ���¼ҽ� ����(���۸�)
	class CommentCell extends ListCell<CommentContent>{
		
		public void updateItem(CommentContent item, boolean empty){
			
			super.updateItem(item, empty);
			
			String message=null;
			
			if(item==null || empty){
			}else
			{
				message = "   ID : " + item.getUserID()+"\n\n"+
						  item.getCContent();
			}
			
			this.setText(message);
			setGraphic(null);
		}
	}
	
	//����Ʈ���� ���� ���丮�� ȣ��Ǿ��� �� ���� �����ϱ� ���� ���丮 Ŭ���� - ���¼ҽ� ����(���۸�)
	class CommentCellFactory implements Callback<ListView<CommentContent>, ListCell<CommentContent>>{
		
		public ListCell<CommentContent> call(ListView<CommentContent> listView){
		
			return new CommentCell();
			
		}
	}
}
