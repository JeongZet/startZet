package source;

/*
 * 로그인하는 유저들의 세션을 유지하기 위한 값으로
 * 유저의 화면 구성과 소켓을 단독적으로 가지고 있는 클래스로
 * 개별의 유저들을 관리할 수 있는 클래스이다.
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.concurrent.*;

import controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.stage.*;

public class Sessions {
	
	private Stage primaryStage;							//AppMain에서 할당 받은 스테이지
	private FXMLLoader loader;							//간단한 화면 구성을 위해 FXMLLoader 변수
	private String filename;							//FXML 파일 관리를 위한 filename
	private AsynchronousChannelGroup channelGroup;		//비동기 소켓 사용을 위한 비동기채널그룹
	private AsynchronousSocketChannel socketChannel;	//유저들에게 하나씩 할당되는 비동기 소켓
	private Charset charset = Charset.forName("UTF-8");	//소켓을 통해 보내지는 데이터 인코딩을 위한 문자집합(UTF-8 형식)
	private boolean connecting=false;					//비정상 종료나 로그아웃시에 소켓이 연결 여부의 변수  
	private Recipe recipe;								//유저가 레시피를 선택하여 뷰 화면 돌입 시 선택했던 레시피 정보
	private User user;									//유저의 로그인 정보
	
	//Set 메소드
	public void setStage(Stage primaryStage){
		this.primaryStage=primaryStage;
		this.primaryStage.setOnCloseRequest(event->stopSession()); //스테이지를 Setting하면서 프로그램 강제종료에 대한 이벤트처리 	
	}
	public void setUser(User user){ this.user=user;}
	public void setRecipe(Recipe recipe){ this.recipe =recipe;}
	
	//Get 메소드
	public Stage getStage(){ return primaryStage;}
	public AsynchronousSocketChannel getSocketChannel(){ return socketChannel;}
	public Recipe getRecipe(){ return recipe;}
	public User getUser(){return user;}
	public FXMLLoader getLoader(){return loader;}
	
	//화면 전환 메소드 
	public void alterStage(String title){
		
		filename(title);
		
		try{
			
			loader = new FXMLLoader(getClass().getResource("../fxml/"+filename+"FXML.fxml"));
			Parent reg_Pane = loader.load();
			controller(title);
			
			primaryStage.setY(0);
			Scene reg_Scene = new Scene(reg_Pane);
			primaryStage.setScene(reg_Scene);
			primaryStage.setTitle(title);
			primaryStage.show();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	//비동기 소켓채널 연결 메소드
	public void connectSocket(){
		try{
			if(connecting==false){
				channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
				socketChannel = AsynchronousSocketChannel.open(channelGroup);
				
				
				Future<Void> conn_Future = socketChannel.connect(new InetSocketAddress("localhost",5001));
				conn_Future.get();					//연결될 때까지 블로킹
				connecting=true;
			}
		}catch(Exception e){
			popup("해당 연결을 할 수 없습니다.");
		}
	}

	//비동기 소켓채널 read 메소드
	public String readSocket(int size){
		ByteBuffer read_Buffer = ByteBuffer.allocate(size);
		
		String data = null;
		try{
			Future<Integer> read_Future = socketChannel.read(read_Buffer);
			read_Future.get();								//버퍼를 읽을 때까지 블로킹
			read_Buffer.flip();								//버퍼를 읽기 위해 현재 position을 0으로 변경
			
			data = charset.decode(read_Buffer).toString();	//버퍼를 디코딩하여 String으로 변환
		}
		catch(Exception e){
			if(socketChannel.isOpen()) stopSession();
		}
			
		return data;
	}
	
	//비동기 소켓채널 write 메소드
	public void writeSocket(String data){
		
		ByteBuffer write_Buffer = charset.encode(data);
		try{
			Future<Integer> write_Future = socketChannel.write(write_Buffer);
			write_Future.get();								//버퍼가 보내질 때까지 블로킹
		}
		catch(Exception e){
			if(socketChannel.isOpen()) stopSession();
		}
			
	}
	
	//Session 종료 및 비정상 종료 메소드
	//비정상 종료나 강제종료, 로그아웃으로 유저의 접속종료함을 서버로 보내주는 메소드
	public void stopSession(){
		try{
			if(connecting==true){
				socketChannel.write(Charset.forName("UTF-8").encode("연결종료///"),null,new CompletionHandler<Integer,Void>(){
	
					public void completed(Integer result, Void attachment) {
						connecting=false;
					}
	
					public void failed(Throwable exc, Void attachment) {
					}
					
				});
				
				if(!channelGroup.isShutdown()&&channelGroup!=null){
					channelGroup.shutdownNow();
				}
			}
		}catch(Exception e){e.printStackTrace();}
	}
	
	//팝업창 메소드
	public void popup(String message){
		try{
			Popup popup = new Popup();
			Parent popup_Pane = FXMLLoader.load(getClass().getResource("../fxml/PopupFXML.fxml"));
			popup.getContent().add(popup_Pane);
			
			Label message_Label= (Label)popup_Pane.lookup("#message_Label");
			message_Label.setText(message);
			
			popup.setAutoHide(true);
			popup.show(primaryStage);
		}catch(Exception e){}
	}
	
	//화면 전환 메소드 - 출처 확인
	private void filename(String title){
		
		switch (title){
		case "로그인" :
			filename="Login";
			break;
		case "회원가입" :
			filename="Register";
			break;
		case "찾기" :
			filename="Search";
			break;
		case "리스트" :
			filename="RecipeList";
			break;
		case "뷰" :
			filename="RecipeView";
			break;
		case "등록":
			filename="RecipeRegister";
			break;
		case "장면등록":
			filename="SceneRegister";
			break;
		case "선호도":
			filename="Preference";
			break;
		}
		
	}
	
	//화면 전환 메소드 - 컨트롤러 설정
	private void controller(String title){
		switch (title){
		case "로그인" :
			LoginController log_Con=loader.getController();
			log_Con.setSession(Sessions.this);
			break;
		case "회원가입" :
			RegisterController reg_Con=loader.getController();
			reg_Con.setSession(Sessions.this);
			break;
		case "찾기" :
			SearchController search_Con=loader.getController();
			search_Con.setSession(Sessions.this);
			break;
		case "리스트" :
			RecipeListController list_Con=loader.getController();
			list_Con.setSession(Sessions.this);
			break;
		case "뷰" :
			RecipeViewController view_Con = loader.getController();
			view_Con.setSession(Sessions.this);
			break;
		case "등록":
			RecipeRegisterController recipeReg_Con = loader.getController();
			recipeReg_Con.setSession(Sessions.this);
			break;
		case "장면등록":
			SceneRegisterController sceneReg_Con = loader.getController();
			sceneReg_Con.setSession(Sessions.this);
			break;
		case "선호도":
			PreferenceController preference_Con = loader.getController();
			preference_Con.setSession(Sessions.this);
			break;
		}
	}
	
}
