package controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import source.Sessions;

public class RecipeViewController implements Initializable {

	@FXML private ImageView recipe_ImageView;
	@FXML private TextArea recipe_TextArea;
	@FXML private ListView<CommentContent> comment_ListView;
	@FXML private TextArea comment_Text;
	@FXML private Button prior_Btn;
	@FXML private Button next_Btn;
	@FXML private Button show_Btn;
	private Sessions session;
	private int scene;
	private int nowScene;
	private String allMessage;
	private ObservableList<CommentContent> commentList = FXCollections.observableArrayList();;
	
	public void setSession(Sessions session){
		this.session=session;
		nowScene=1;
		view_Scene();
		
		scene = session.getRecipe().getScene();
		
		view_Comment();
		
	}
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		show_Btn.setOnAction(event->handle_ShowBtn());
		recipe_TextArea.setEditable(false);
		
	}
	
	public void view_Scene(){
		
		recipe_TextArea.clear();
		
		String message = session.readSocket(500);
		
		String[] datas = message.split("///");
		
		if(nowScene==1){
			prior_Btn.setDisable(true);
		}else
			prior_Btn.setDisable(false);
		
		if(nowScene==session.getRecipe().getScene()){
			next_Btn.setDisable(true);
		}else
			next_Btn.setDisable(false);
		
		recipe_TextArea.appendText(datas[1]);
		
		ByteBuffer imageBuffer = ByteBuffer.allocate(Integer.parseInt(datas[2]));
		
		session.writeSocket("이미지///"+Integer.toString(session.getRecipe().getRNo())+"///"+nowScene);
		
		try{
			Future<Integer> read_Future = session.getSocketChannel().read(imageBuffer);
			read_Future.get();
			imageBuffer.flip();
			
			byte[] imageByte = imageBuffer.array();
			
			InputStream is = new ByteArrayInputStream(imageByte);
			
			BufferedInputStream bis = new BufferedInputStream(is);
			
			Image image = new Image(bis);
			recipe_ImageView.setImage(image);
			
		}
		catch(Exception e){
			if(session.getSocketChannel().isOpen()) session.stopSession();
		}
		
		
		/*session.getSocketChannel().read(imageBuffer, imageBuffer , new CompletionHandler<Integer, ByteBuffer>(){

			public void completed(Integer result, ByteBuffer attachment) {
				attachment.flip();
				byte[] imageByte = attachment.array();
				
				InputStream is = new ByteArrayInputStream(imageByte);
				
				BufferedInputStream bis = new BufferedInputStream(is);
				
				Image image = new Image(bis);
				recipe_ImageView.setImage(image);
			}

			public void failed(Throwable exc, ByteBuffer attachment) {
				exc.printStackTrace();
			}
		});*/
		
		//view_Comment();
		
	}
	
	public void view_Comment(){
		
		commentList.clear();
		
		session.writeSocket("댓글보기///"+session.getRecipe().getRNo());
		
		String data = session.readSocket(1000);
		
		if(data.equals("실패")){
			commentList.add(new CommentContent("댓글 없음",""));
		}else{
			String[] datas = data.split("///");
			
			for(int i=0;i<datas.length;i+=2){
				commentList.add(new CommentContent(datas[i], datas[i+1]));
			}
		}
		comment_ListView.setItems(commentList);
		comment_ListView.setCellFactory(new CommentCellFactory());
		
	}
	
	
	public void handle_CommentBtn(){
		
		
		session.writeSocket("댓글등록///"+session.getUser().getID()+"///"+session.getRecipe().getRNo()+"///"+comment_Text.getText());
		
		String message = session.readSocket(10);
		
		if(message.equals("성공")){
			session.popup("댓글 등록에 성공하셨습니다.");
			comment_Text.clear();
			view_Comment();
		}
		
	}
	
	public void handle_RecommendBtn(){
		
		session.writeSocket("추천///"+session.getUser().getID()+"///"+session.getRecipe().getRNo()+"///"+session.getUser().getGender()+"///"+session.getUser().getAge());
		
		String message = session.readSocket(10);
		
		if(message.equals("성공")){
			session.popup("추천 하였습니다.");
		}else
			session.popup("이미 추천하였습니다.");

	}

	public void handle_PriorBtn(){
		nowScene-=1;
		
		session.writeSocket("뷰///"+session.getRecipe().getRNo()+"///"+nowScene);
		
		view_Scene();
		
	}

	public void handle_NextBtn(){
		nowScene+=1;
		
		session.writeSocket("뷰///"+session.getRecipe().getRNo()+"///"+nowScene);
		
		view_Scene();
		
	}

	public void handle_ShowBtn(){
		if(show_Btn.getText().equals("전     체     보     기")){
			
			show_Btn.setText("상     세     보     기");
			
			recipe_ImageView.setVisible(false);
			recipe_TextArea.setLayoutY(14.0);
			recipe_TextArea.setPrefHeight(772.0);
			recipe_TextArea.clear();
			

			session.writeSocket("전체보기///"+session.getRecipe().getRNo());
			allMessage = session.readSocket(1000);
			recipe_TextArea.appendText(allMessage);
			
			prior_Btn.setDisable(true);
			next_Btn.setDisable(true);
			
		}else if(show_Btn.getText().equals("상     세     보     기")){
			show_Btn.setText("전     체     보     기");
			
			recipe_ImageView.setVisible(true);
			recipe_TextArea.setLayoutY(571.0);
			recipe_TextArea.setPrefHeight(225.0);
			recipe_TextArea.clear();
			
			nowScene = 1;
			
			session.writeSocket("뷰///"+session.getRecipe().getRNo()+"///"+"1");
			view_Scene();
			
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
		}
	}

	public void handle_CancelBtn(){
		session.writeSocket("리스트");
		session.alterStage("리스트");
	}
	
	class CommentContent{
		private String userID;
		private String cContent;
		
		CommentContent(String userID, String cContent){
			this.userID = userID;
			this.cContent = cContent;
		}
		
		String getUserID(){return userID;}
		String getCContent(){return cContent;}
		
	}
	
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
	
	class CommentCellFactory implements Callback<ListView<CommentContent>, ListCell<CommentContent>>{
		
		public ListCell<CommentContent> call(ListView<CommentContent> listView){
		
			return new CommentCell();
			
		}
		
	}
}
