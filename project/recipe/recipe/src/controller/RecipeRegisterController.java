package controller;

/*
 * 레시피 등록을 위한 컨트롤러 클래스로 
 * 레시피의 이름, 종류, Scene 수, 재료를 기입하여 서버로 보냄으로써 레시피를 등록할 수 있는 기능을 한다.
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.control.*;
import source.Recipe;
import source.Sessions;

public class RecipeRegisterController implements Initializable {

	@FXML private TextField name_Text;				//레시피 이름을 기입하는 텍스트필드
	@FXML private TextField kind_Text;				//요리 종류를 기입하는 텍스트 필드
	@FXML private TextField scene_Text;				//Scene의 수를 기입하는 텍스트 필드
	@FXML private TextArea item_Text;				//요리 재료를 기입하는 텍스트 필드
	private Sessions session;						//이전 페이지에서 Session 값을 할당 받기 위한 값
	
	//이전 화면에서의 세션 할당을 위한 메소드
	public void setSession(Sessions session){
		this.session=session;
	}
	
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	//다음 버튼에 대한 이벤트 처리 메소드로 다음 버튼을 누를 시 각각 Scene에 대한 설명과 이미지 첨부를 할 수 있는 페이지로 넘어감
	public void handle_NextBtn(){
		if(!name_Text.getText().equals("")&&
				!kind_Text.getText().equals("")&&
				!scene_Text.getText().equals("")&&
				!item_Text.getText().equals("")){	//모든 텍스트 필드에 공백을 기입 하지 못하게 하는 조건
			String mode = "0";						//관리자 모드 구분을 위한 변수
			Recipe recipe= new Recipe(session.getUser().getID(), name_Text.getText(), item_Text.getText(), kind_Text.getText(), "0", "0");
			//등록할 시 세션으로부터 현재 로그인한 유저의 아이디를 가져와 작성자 아이디로 기입하고, 각각 텍스트 필드로부터 값을 가져온다. 그리고 추천 수와 댓글 수는 0 이므로 0을 할당하여
			//Recipe 객체 생성
			session.setRecipe(recipe);				//세션에 새로 만든 Recipe 객체 할당
			session.getRecipe().setScene(Integer.parseInt(scene_Text.getText()));	//세션의 레시피 객체에 Scene 수를 할당
			if(session.getUser().getID().equals("root")){
				mode = "1";
			}//관리자 모드일 시 mode 변수에 1을 대입 - 서버에서 이 변수에 따라 관리자인지 일반 유저인지 구별
			session.writeSocket("레시피등록///"+name_Text.getText()+"///"+kind_Text.getText()+"///"+item_Text.getText()+"///"+session.getUser().getID()+"///"+mode);
			//레시피 등록을 위한 메시지를 보낸다. 각 텍스트 필드의 값들과 유저의 ID, 그리고 mode 변수를 보냄.
			
			session.getRecipe().setRNo(Integer.parseInt(session.readSocket(5)));
			//레시피 고유값을 위한 레시피의 번호를 받아와 세션에 레시피 객체에 저장한다.
			
			session.alterStage("장면등록");			//Scene 등록 페이지로 화면 전환
		
		}else
			session.popup("모든 정보를 입력해주세요.");		//4개의 텍스트 필드 중 1개라도 공백일 시에 팝업창 활성화
	}
	
	//취소 버튼에 대한 이벤트 처리 메소드 
	public void handle_CancelBtn(){
		session.writeSocket("리스트///1");
		session.alterStage("리스트");
	}
}
