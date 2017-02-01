package controller;

/*
 * 레시피 등록 페이지에서 다음 버튼을 눌러 나오는 Scene 등록 페이지 컨트롤러 클래스로
 * 이미지를 찾아보기를 통해 png, jpg, gif 확장자 파일의 그림파일을 선택하여 미리보기를 할 수 있으며
 * 텍스트 에어리어 창에 설명을 적고 등록 하여 Scene을 등록할 수 있는 기능이 있다.
 * 다음 버튼을 눌러 다음 페이지를 작성 할 수 있으며 이전 버튼을 눌러 이전에 작성했던 것을 새로 작성 할 수 있다.
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

	@FXML private Button register_Btn;						//등록 버튼
	@FXML private Button next_Btn;							//다음 버튼
	@FXML private Button prior_Btn;							//이전 버튼
	@FXML private TextArea recipe_TextArea;					//Scene 설명 텍스트 에어리어
	@FXML private ImageView recipe_ImageView;				//Scene 이미지 미리보기 이미지뷰
	@FXML private TextField filePath_Text;					//파일 경로 텍스트 필드
	@FXML private Label page_Label;							//현재 페이지 / 최대 페이지  표시를 위한 라벨
	private Sessions session;								//유저 세션
	private int nowScene=1;									//현재 페이지
	private int scene;										//최대 페이지
	private ByteBuffer imageBuffer;							//파일 경로를 통해 받아온 이미지를 저장할 이미지 버퍼
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		prior_Btn.setDisable(true);
		register_Btn.setDisable(true);
	}
	
	//이전 화면에서의 세션 할당을 위한 메소드
	public void setSession(Sessions session){
		this.session=session;
		scene=session.getRecipe().getScene();	//세션으로부터 레시피의 총 할당된 Scene 수를 받아온다.
		buttonSetting();						//이전, 다음, 등록 버튼의 활성화 및 비활성화를 위한 메소드 호출
		pageSetting(nowScene,scene);			//현재 페이지 및 최대 페이지 라벨의 출력을 위한 메소드 호출
	}
	
	//찾아보기 버튼의 이벤트 처리 메소드로 png, jpg, gif 확장자의 파일들을 선택할 수 있다.
	//선택하게 되면 이미지 뷰에 해당 이미지를 출력하여 줌으로써 미리보기가 가능하다.
	public void handle_PathFindBtn() throws IOException{
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files","*.png","*.jpg","*.gif"));
		//확장자 추가(이미지 파일 png, jpg, gif)
		
		File selectedFile = fileChooser.showOpenDialog(session.getStage());
		//파일경로로부터 파일에 할당
		
		if(selectedFile!=null){	//파일경로로부터 받은 파일이 null이 아니면 텍스트 필드에 경로를 출력하고 이미지 뷰에 해당 이미지를 출력
			filePath_Text.setText(selectedFile.getPath());
			recipe_ImageView.setImage(new Image("File:///"+selectedFile.getPath()));
			imageBuffer= ByteBuffer.wrap(Files.readAllBytes(selectedFile.toPath()));
			//받아온 이미지를 버퍼로 변환(서버로 보내기 위해)
		}
	}
	
	//Scene에 대한 설명과 이미지 등 정보를 서버로 보내기 위한 메소드
	private void send_SceneData(String state){
		
		try{
		session.writeSocket("장면등록///"+session.getRecipe().getRNo()+"///"+nowScene+"///"+recipe_TextArea.getText()+"///"+imageBuffer.capacity()+"///"+state);
		//Scene 등록을 위해 해당 레시피의 번호와 현재 Scene 번호, Scene 설명, 이미지 버퍼의 크기 그리고 Commit 여부를 보낸다.
		//Commit여부를 보내는 이유는 다음 버튼을 누를 시 서버로 데이터를 보내 해당 로우를 Insert 하지만 도중 취소 할 경우를 대비 하기 위함.
		
		String message = session.readSocket(10);		//성공 여부를 확인하기 위해 데이터를 읽어옴
		
		if(message.equals("성공")){
		
			Future<Integer> write_Future = session.getSocketChannel().write(imageBuffer);
			write_Future.get();
			//이미지 버퍼를 따로 보내며 이미지 버퍼가 온전히 다 보내질 때 까지 블로킹 하여 준다.
		}
		}catch(Exception e){}
	}
	
	//화면에 따른 버튼 및 기능 등을 활성화 및 비활성화를 설정해주는 메소드이다.
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
	
	//페이지 라벨 Setting을 위한 메소드로 현재 페이지와 최대 페이지를 라벨에 Setting 해준다.
	private void pageSetting(int nowScene, int Scene){
		page_Label.setText(nowScene + " / " + Scene);
	}
	
	//등록 버튼에 대한 이벤트 처리를 위한 메소드이다.
	public void handle_RegisterBtn(){
		send_SceneData("commit");				//commit의 상태를 보내줌으로써 여태 Scene 정보들을 commit하여 온전한 데이터로 처리
		session.writeSocket("리스트///1");
		session.alterStage("리스트");				//후엔 레시피 리스트 페이지로 화면 전환
	}
	
	//다음 버튼에 대한 이벤트 처리를 위한 메소드이다.
	public void handle_NextBtn(){
		send_SceneData("default");				//commit을 하지 않는 상태로 도중 취소를 할 경우를 대비하여 default 값을 보냄
		
		nowScene+=1;							//현재 페이지 수를 +1 해준다.
		buttonSetting();						//다음 화면 전환에 따른 버튼 활성화 Setting을 한다.
		
	}
	
	//이전 버튼에 대한 이벤트 처리를 위한 메소드이다.
	public void handle_PriorBtn(){
		nowScene-=1;							//현재 페이지 수를 -1 해준다.
		buttonSetting();						
		
	}
	
	//닫기 버튼에 대한 이벤트 처리를 위한 메소드이다.
	public void handle_CancelBtn(){
		send_SceneData("rollback");				//닫기를 할 경우 Scene 등록이 완전히 마쳐지지 않았기 때문에 서버에서 DB로 rollback을 위해
												//rollback이라고 State를 보내준다.
		
		session.alterStage("등록");				//레시피 등록 페이지로 화면 전환
	}

}
