package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import source.Sessions;

public class SceneRegisterController implements Initializable {

	@FXML private Button register_Btn;
	@FXML private Button next_Btn;
	@FXML private Button prior_Btn;
	@FXML private TextArea recipe_TextArea;
	@FXML private ImageView recipe_ImageView;
	@FXML private TextField filePath_Text;
	@FXML private Label page_Label;
	private Sessions session;
	private int nowScene=1;
	private int scene;
	private ByteBuffer imageBuffer;
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		prior_Btn.setDisable(true);
		register_Btn.setDisable(true);
	}
	
	public void setSession(Sessions session){
		this.session=session;
		scene=session.getRecipe().getScene();
		buttonSetting();
		pageSetting(nowScene,scene);
	}
	
	public void handle_PathFindBtn() throws IOException{
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files","*.png","*.jpg","*.gif"));
		
		File selectedFile = fileChooser.showOpenDialog(session.getStage());
		if(selectedFile!=null){
			filePath_Text.setText(selectedFile.getPath());
			recipe_ImageView.setImage(new Image("File:///"+selectedFile.getPath()));
			imageBuffer= ByteBuffer.wrap(Files.readAllBytes(selectedFile.toPath()));
		}
	}
	
	private void send_SceneData(String state){
		
		try{
		session.writeSocket("장면등록///"+session.getRecipe().getRNo()+"///"+nowScene+"///"+recipe_TextArea.getText()+"///"+imageBuffer.capacity()+"///"+state);
		
		String message = session.readSocket(10);
		
		if(message.equals("성공")){
		
			Future<Integer> write_Future = session.getSocketChannel().write(imageBuffer);
			write_Future.get();
			
		}
		}catch(Exception e){}
	}
	
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
	
	private void pageSetting(int nowScene, int Scene){
		page_Label.setText(nowScene + " / " + Scene);
	}
	
	public void handle_RegisterBtn(){
		send_SceneData("commit");
		session.writeSocket("리스트///1");
		session.alterStage("리스트");
	}
	
	public void handle_NextBtn(){
		send_SceneData("default");
		
		nowScene+=1;
		buttonSetting();
		
	}

	public void handle_PriorBtn(){
		nowScene-=1;
		buttonSetting();
		
	}
	
	public void handle_CancelBtn(){
		send_SceneData("rollback");
		
		session.alterStage("등록");
	}

}
