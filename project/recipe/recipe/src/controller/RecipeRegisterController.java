package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import source.Recipe;
import source.Sessions;

public class RecipeRegisterController implements Initializable {

	@FXML private TextField name_Text;
	@FXML private TextField kind_Text;
	@FXML private TextField scene_Text;
	@FXML private TextArea item_Text;
	private Sessions session;
	
	public void setSession(Sessions session){
		this.session=session;
	}
	
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	public void handle_NextBtn(){
		if(!name_Text.getText().equals("")&&
				!kind_Text.getText().equals("")&&
				!scene_Text.getText().equals("")&&
				!item_Text.getText().equals("")){
			String mode = "0";
			Recipe recipe= new Recipe(session.getUser().getID(), name_Text.getText(), item_Text.getText(), kind_Text.getText(), "0", "0");
			session.setRecipe(recipe);
			session.getRecipe().setScene(Integer.parseInt(scene_Text.getText()));
			if(session.getUser().getID().equals("root")){
				mode = "1";
			}
			session.writeSocket("레시피등록///"+name_Text.getText()+"///"+kind_Text.getText()+"///"+item_Text.getText()+"///"+session.getUser().getID()+"///"+mode);
			
			session.getRecipe().setRNo(Integer.parseInt(session.readSocket(5)));
			
			session.alterStage("장면등록");
		
		}else
			session.popup("모든 정보를 입력해주세요.");
	}
	
	public void handle_CancelBtn(){
		session.writeSocket("리스트///1");
		session.alterStage("리스트");
	}
}
