package controller;

/*
 * 레시피 리스트에서 TableView의 Row를 더블 클릭하여 활성화된 컨트롤 클래스로
 * 서버로부터 레시피에 등록된 Scene의 정보를 받아온다.
 * Scene의 정보로는 그림과 Scene 마다의 설명이 있다.
 * 여러가지 기능의 버튼이 있으며
 * 주요 기능으로는 서버로부터 받아온 버퍼를 이미지로 변환하여 ImageView에 출력
 * 다음 버튼과 이전 버튼으로 페이지 변경 및 전체 보기 버튼으로 Scene 설명들만 출력
 * 추천 기능과 댓글 기능이 존재한다.
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

	@FXML private ImageView recipe_ImageView;				//Scene의 그림을 출력하는 이미지뷰
	@FXML private TextArea recipe_TextArea;					//Scene의 설명을 출력하는 텍스트에어리어
	@FXML private ListView<CommentContent> comment_ListView;//댓글 리스트들을 출력하는 리스트뷰
	@FXML private TextArea comment_Text;					//댓글 입력을 위한 텍스트에어리어
	@FXML private Button prior_Btn;							//이전버튼
	@FXML private Button next_Btn;							//다음버튼
	@FXML private Button show_Btn;							//전체보기 or 순서대로 보기 버튼
	@FXML private Button recommend_Btn;						//추천버튼
	private Sessions session;								//이전 세션 할당을 위한 세션 객체 초기화
	private int scene;										//최대 Scene 수를 저장하는 변수
	private int nowScene;									//현재 Scene 페이지를 저장하는 변수
	private String allMessage;								//전체 보기 시 Scene의 설명들을 모두 저장하는 변수 
	private ObservableList<CommentContent> commentList = FXCollections.observableArrayList(); //댓글 리스트들을 저장하는 객체 리스트
	
	//이전 페이지에서 세션을 할당하기 위한 메소드
	public void setSession(Sessions session){
		this.session=session;
		nowScene=1;								//현재 Scene 페이지 초기화
		view_Scene();							//Scene의 이미지 및 설명을 서버로부터 받아오는 메소드 호출
		
		scene = session.getRecipe().getScene();	//전 페이지에서 받아온 총 페이지 수 할당
		
		view_Comment();							//댓글을 ListView에 출력해주는 메소드 호출
		
	}
	
	public void initialize(URL arg0, ResourceBundle arg1) {

		show_Btn.setOnAction(event->handle_ShowBtn());		//전체보기 버든의 이벤트 처리
		recipe_TextArea.setEditable(false);					//레시피의 설명 부분 수정 불가 처리
		
	}
	
	//서버로부터 Scene의 내용과 이미지를 받아와 각각 TextArea와 ImageView에 출력해주는 메소드
	public void view_Scene(){
		
		recipe_TextArea.clear();					//이전 페이지에서의 내용을 초기화
		
		String message = session.readSocket(500);	//버퍼 500의 크기를 서버에서 읽어옴(Scene의 내용과 Scene에 등록된 이미지 버퍼 크기)
		
		String[] datas = message.split("///");
		
		if(nowScene==1){
			prior_Btn.setDisable(true);
		}else
			prior_Btn.setDisable(false);
		
		if(nowScene==session.getRecipe().getScene()){
			next_Btn.setDisable(true);
		}else
			next_Btn.setDisable(false);
		
		//이전 버튼과 다음 버튼의 활성화와 비활성화
		
		recipe_TextArea.appendText(datas[1]);		//서버로부터 받아온 내용을 TextArea에 출력
		
		ByteBuffer imageBuffer = ByteBuffer.allocate(Integer.parseInt(datas[2]));	
		//이미지 버퍼를 받기 위해 미리 전에 받아놓은 버퍼의 크기만큼의 바이트 버퍼 초기화
		
		session.writeSocket("이미지///"+Integer.toString(session.getRecipe().getRNo())+"///"+nowScene);
		//이미지를 보내달라고 서버에 요청하기 위해 현재 레시피 번호와 현재 Scene 번호를 서버로 보냄 
		
		try{
			Future<Integer> read_Future = session.getSocketChannel().read(imageBuffer);
			read_Future.get();
			imageBuffer.flip();
			//이미지를 버퍼로 받아오고 받아오는 것을 완료할 때까지 블로킹 후 imageBuffer의 position 초기화
			
			byte[] imageByte = imageBuffer.array();
			
			InputStream is = new ByteArrayInputStream(imageByte);
			//이미지 버퍼를 바이트 배열로 변환 후 스트림으로 변환
			
			Image image = new Image(is);
			
			recipe_ImageView.setImage(image);	//이미지가 담긴 스트림을 Image 객체로 초기화 후 ImageView에 Setting함.
			
		}catch(Exception e){
			if(session.getSocketChannel().isOpen()) session.stopSession();
		}
		

		if(session.getUser().getID().equals("root")){
			recommend_Btn.setText("채                    택");
		}//관리자 모드로 로그인 시 추천 버튼을 채택 버튼으로 초기화
		
	}
	
	//서버로부터 댓글 데이터를 받아와 ListView에 출력하여 주는 메소드
	public void view_Comment(){
		
		commentList.clear();											//이전 페이지의 댓글리스트 초기화
		
		session.writeSocket("댓글보기///"+session.getRecipe().getRNo());
		
		String data = session.readSocket(3000);
		//현재 레시피 번호를 넘겨줌으로써 서버로부터 댓글 데이터를 받아옴
		
		if(data.equals("실패")){
			commentList.add(new CommentContent("댓글 없음",""));
		}else{
			String[] datas = data.split("///");
			
			for(int i=0;i<datas.length;i+=2){
				commentList.add(new CommentContent(datas[i], datas[i+1]));
			}
		}//댓글의 유무와 댓글들의 정보를 구분하기 위한 조건문
		
		comment_ListView.setItems(commentList);
		comment_ListView.setCellFactory(new CommentCellFactory());
		//commentList를 ListView에 등록, 리스트 뷰 셀들의 형태 변환을 위한 메소드
	}
	
	//댓글 등록 버튼의 이벤트 처리로 댓글 등록 시 바로 댓글 리스트에 등록 된다.
	public void handle_CommentBtn(){
		
		session.writeSocket("댓글등록///"+session.getUser().getID()+"///"+session.getRecipe().getRNo()+"///"+comment_Text.getText());
		//댓글 등록을 위해 등록자 ID와 레시피 번호 그리고 댓글 내용을 서버로 보낸다. 
		
		String message = session.readSocket(10); 	//후에 성공하였을 시 성공했다는 메시지를 받는다.
		
		if(message.equals("성공")){
			session.popup("댓글 등록에 성공하셨습니다.");
			comment_Text.clear();					//작성 후 텍스트 Area를 초기화한다.
			view_Comment();							//view_Comment()메소드를 호출하여 재갱신한다.
		}
		
	}
	
	//추천 버튼의 이벤트 처리로 추천하지 않았을 시에 추천이 가능하며 추천을 이미 했을 경우 이미 추천했다는 팝업창을 출력한다.
	//관리자 모드 실행 시 채택 버튼의 이벤트 처리로 도전 요리의 레시피 경우 정식 요리로 등록할 수 있는 버튼이다.
	public void handle_RecommendBtn(){
		if(recommend_Btn.getText().equals("추                    천")){
			session.writeSocket("추천///"+session.getUser().getID()+"///"+session.getRecipe().getRNo()+"///"+session.getUser().getGender()+"///"+session.getUser().getAge());
			//추천을 위해 등록자 아이디와 레시피 번호 그리고 추천자의 성별과 나이를 서버로 보낸다.
			String message = session.readSocket(10);//후에 성공하였을 시 성공했다는 메시지를 받는다.
			
			if(message.equals("성공")){
				session.popup("추천 하였습니다.");
			}else
				session.popup("이미 추천하였습니다.");
			
		}else if(recommend_Btn.getText().equals("채                    택")){
			session.writeSocket("채택///"+session.getRecipe().getRNo());
			//채택을 위해 해당 레시피의 번호를 서버로 보낸다.
			String message = session.readSocket(10);//후에 성공하였을 시 성공했다는 메시지를 받는다.
		
			if(message.equals("성공")){
				
				session.writeSocket("리스트///1");
				session.alterStage("리스트");
				session.popup("채택 하였습니다.");
				//채택 성공 후 레시피 리스트 화면으로 전환된다.
			}else
				session.popup("이미 채택하였습니다.");
		}

	}

	//이전 버튼의 이벤트 처리 메소드로 현재 Scene을 -1 하고 새롭게 뷰의 정보를 위해 서버로 보내고
	//view_Scene() 메소드를 호출하여 서버에서 오는 메시지 및 이미지를 받아 처리한다.
	public void handle_PriorBtn(){
		nowScene-=1;
		
		session.writeSocket("뷰///"+session.getRecipe().getRNo()+"///"+nowScene);
		
		view_Scene();
		
	}

	//다음 버튼의 이벤트 처리 메소드로 현재 Scene을 +1 하고 이전 버튼과 동일한 기능.
	public void handle_NextBtn(){
		nowScene+=1;
		
		session.writeSocket("뷰///"+session.getRecipe().getRNo()+"///"+nowScene);
		
		view_Scene();
		
	}

	//전체보기 or 상세보기 버튼의 이벤트 처리 메소드로 TextArea 창의 크기 및 위치 변환, ImageView의 Visible 세팅 변환 및
	//버튼의 활성화 비활성화를 하는 메소드다.
	public void handle_ShowBtn(){
		if(show_Btn.getText().equals("전     체     보     기")){
			
			show_Btn.setText("상     세     보     기");
			
			recipe_ImageView.setVisible(false);
			recipe_TextArea.setLayoutY(14.0);
			recipe_TextArea.setPrefHeight(772.0);
			recipe_TextArea.clear();

			session.writeSocket("전체보기///"+session.getRecipe().getRNo());	
			//서버로부터 해당 레시피 번호의 내용을 받기 위해 메시지 보냄.
			
			allMessage = session.readSocket(1000);
			recipe_TextArea.appendText(allMessage);
			//서버로부터 받아온 메시지를 TextArea에 출력
			
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
			//뷰 화면 출력을 위해 해당 레시피 번호와 제일 처음 페이지를 호출을 위해 서버로 해당 메시지를 보낸다.
			
			view_Scene();	//서버로부터 받아오는 Scene의 내용과 이미지 처리를 위해 view_Scene()메소드 호출
			
			
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
			//다음 이전 버튼 세팅
		}
	}

	//닫기 버튼 이벤트 처리 메소드
	public void handle_CancelBtn(){
		session.writeSocket("리스트///1");
		session.alterStage("리스트");
	}
	
	//댓글 클래스로 등록자 ID와 내용으로 구성됨
	class CommentContent{
		private String userID;
		private String cContent;
		
		//생성자
		CommentContent(String userID, String cContent){
			this.userID = userID;
			this.cContent = cContent;
		}
		
		//Getting 메소드
		String getUserID(){return userID;}
		String getCContent(){return cContent;}
		
	}
	
	//CommentCell 클래스로 리스트 뷰의 셀의 수정을 위해 오버 라이딩 한 것. - 오픈소스 참고(구글링)
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
	
	//리스트뷰의 셀에 팩토리로 호출되었을 때 값을 리턴하기 위한 팩토리 클래스 - 오픈소스 참고(구글링)
	class CommentCellFactory implements Callback<ListView<CommentContent>, ListCell<CommentContent>>{
		
		public ListCell<CommentContent> call(ListView<CommentContent> listView){
		
			return new CommentCell();
			
		}
	}
}
