package controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import source.Sessions;

public class RecipeViewController implements Initializable {

	@FXML private ImageView recipe_ImageView;
	@FXML private TextArea recipe_TextArea;
	@FXML private ListView comment_ListView;
	@FXML private TextArea comment_Text;
	@FXML private Button prior_Btn;
	@FXML private Button next_Btn;
	@FXML private Button show_Btn;
	private Sessions session;
	private int scene;
	private int nowScene;
	private String allMessage;
	
	public void setSession(Sessions session){
		this.session=session;
		nowScene=1;
		view_Scene();
		
		scene = session.getRecipe().getScene();
		
		
		
	}
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		show_Btn.setOnAction(event->handle_ShowBtn());
		recipe_TextArea.setEditable(false);
		
	}
	
	public void view_Scene(){
		
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
		
		session.getSocketChannel().read(imageBuffer, imageBuffer , new CompletionHandler<Integer, ByteBuffer>(){

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
		});
		
	}
	
	public void view_Setting(){
		
		
		
	}
	
	public void handle_CommentBtn(){
		
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
		
	}

	public void handle_NextBtn(){
		
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
}
