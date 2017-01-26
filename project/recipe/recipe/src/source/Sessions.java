package source;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class Sessions {
	
	private Stage primaryStage;
	private FXMLLoader loader;
	private String filename;
	private AsynchronousChannelGroup channelGroup;
	private AsynchronousSocketChannel socketChannel;
	private Charset charset = Charset.forName("UTF-8");
	public String message;
	private boolean connecting=false;
	private Recipe recipe;
	private User user;
	
	//Set 메소드
	public void setStage(Stage primaryStage){
		
		this.primaryStage=primaryStage;
		this.primaryStage.setOnCloseRequest(event->stopSession());
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
				conn_Future.get();
				connecting=true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	//비동기 소켓채널 read 메소드
	public String readSocket(int size){
		ByteBuffer read_Buffer = ByteBuffer.allocate(size);
		
		String data = null;
		try{
			Future<Integer> read_Future = socketChannel.read(read_Buffer);
			read_Future.get();
			read_Buffer.flip();
			
			data = charset.decode(read_Buffer).toString();
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
			write_Future.get();
		}
		catch(Exception e){
			if(socketChannel.isOpen()) stopSession();
		}
			
	}
	
	//Session 종료 및 비정상 종료 메소드
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
