package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
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
	private int nowScene=1;;
	private int scene;
	private ByteBuffer imageBuffer;
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		prior_Btn.setDisable(true);
		register_Btn.setDisable(true);
	}
	
	public void setSession(Sessions session){
		this.session=session;
		scene=session.getRecipe().getScene();
		
		if(nowScene==scene){
			next_Btn.setDisable(true);
			register_Btn.setDisable(true);
		}
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
	
	public void send_SceneData(){
		
		try{
		session.writeSocket("¾Àµî·Ï///"+session.getRecipe().getRNo()+"///"+recipe_TextArea.getText());
		
		Future<Integer> write_Future = session.getSocketChannel().write(imageBuffer);
		write_Future.get();
		}catch(Exception e){}
	}
	
	public void handle_RegisterBtn(){
		
	}
	
	public void handle_NextBtn(){
		
	}

	public void handle_PriorBtn(){
		
	}
	
	public void handle_CancelBtn(){
		session.alterStage("µî·Ï");
	}

}
