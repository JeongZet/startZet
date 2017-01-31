package source;

/*
 * �α����ϴ� �������� ������ �����ϱ� ���� ������
 * ������ ȭ�� ������ ������ �ܵ������� ������ �ִ� Ŭ������
 * ������ �������� ������ �� �ִ� Ŭ�����̴�.
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
	
	private Stage primaryStage;							//AppMain���� �Ҵ� ���� ��������
	private FXMLLoader loader;							//������ ȭ�� ������ ���� FXMLLoader ����
	private String filename;							//FXML ���� ������ ���� filename
	private AsynchronousChannelGroup channelGroup;		//�񵿱� ���� ����� ���� �񵿱�ä�α׷�
	private AsynchronousSocketChannel socketChannel;	//�����鿡�� �ϳ��� �Ҵ�Ǵ� �񵿱� ����
	private Charset charset = Charset.forName("UTF-8");	//������ ���� �������� ������ ���ڵ��� ���� ��������(UTF-8 ����)
	private boolean connecting=false;					//������ ���ᳪ �α׾ƿ��ÿ� ������ ���� ������ ����  
	private Recipe recipe;								//������ �����Ǹ� �����Ͽ� �� ȭ�� ���� �� �����ߴ� ������ ����
	private User user;									//������ �α��� ����
	
	//Set �޼ҵ�
	public void setStage(Stage primaryStage){
		this.primaryStage=primaryStage;
		this.primaryStage.setOnCloseRequest(event->stopSession()); //���������� Setting�ϸ鼭 ���α׷� �������ῡ ���� �̺�Ʈó�� 	
	}
	public void setUser(User user){ this.user=user;}
	public void setRecipe(Recipe recipe){ this.recipe =recipe;}
	
	//Get �޼ҵ�
	public Stage getStage(){ return primaryStage;}
	public AsynchronousSocketChannel getSocketChannel(){ return socketChannel;}
	public Recipe getRecipe(){ return recipe;}
	public User getUser(){return user;}
	public FXMLLoader getLoader(){return loader;}
	
	//ȭ�� ��ȯ �޼ҵ� 
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
	
	//�񵿱� ����ä�� ���� �޼ҵ�
	public void connectSocket(){
		try{
			if(connecting==false){
				channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
				socketChannel = AsynchronousSocketChannel.open(channelGroup);
				
				
				Future<Void> conn_Future = socketChannel.connect(new InetSocketAddress("localhost",5001));
				conn_Future.get();					//����� ������ ���ŷ
				connecting=true;
			}
		}catch(Exception e){
			popup("�ش� ������ �� �� �����ϴ�.");
		}
	}

	//�񵿱� ����ä�� read �޼ҵ�
	public String readSocket(int size){
		ByteBuffer read_Buffer = ByteBuffer.allocate(size);
		
		String data = null;
		try{
			Future<Integer> read_Future = socketChannel.read(read_Buffer);
			read_Future.get();								//���۸� ���� ������ ���ŷ
			read_Buffer.flip();								//���۸� �б� ���� ���� position�� 0���� ����
			
			data = charset.decode(read_Buffer).toString();	//���۸� ���ڵ��Ͽ� String���� ��ȯ
		}
		catch(Exception e){
			if(socketChannel.isOpen()) stopSession();
		}
			
		return data;
	}
	
	//�񵿱� ����ä�� write �޼ҵ�
	public void writeSocket(String data){
		
		ByteBuffer write_Buffer = charset.encode(data);
		try{
			Future<Integer> write_Future = socketChannel.write(write_Buffer);
			write_Future.get();								//���۰� ������ ������ ���ŷ
		}
		catch(Exception e){
			if(socketChannel.isOpen()) stopSession();
		}
			
	}
	
	//Session ���� �� ������ ���� �޼ҵ�
	//������ ���ᳪ ��������, �α׾ƿ����� ������ ������������ ������ �����ִ� �޼ҵ�
	public void stopSession(){
		try{
			if(connecting==true){
				socketChannel.write(Charset.forName("UTF-8").encode("��������///"),null,new CompletionHandler<Integer,Void>(){
	
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
	
	//�˾�â �޼ҵ�
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
	
	//ȭ�� ��ȯ �޼ҵ� - ��ó Ȯ��
	private void filename(String title){
		
		switch (title){
		case "�α���" :
			filename="Login";
			break;
		case "ȸ������" :
			filename="Register";
			break;
		case "ã��" :
			filename="Search";
			break;
		case "����Ʈ" :
			filename="RecipeList";
			break;
		case "��" :
			filename="RecipeView";
			break;
		case "���":
			filename="RecipeRegister";
			break;
		case "�����":
			filename="SceneRegister";
			break;
		case "��ȣ��":
			filename="Preference";
			break;
		}
		
	}
	
	//ȭ�� ��ȯ �޼ҵ� - ��Ʈ�ѷ� ����
	private void controller(String title){
		switch (title){
		case "�α���" :
			LoginController log_Con=loader.getController();
			log_Con.setSession(Sessions.this);
			break;
		case "ȸ������" :
			RegisterController reg_Con=loader.getController();
			reg_Con.setSession(Sessions.this);
			break;
		case "ã��" :
			SearchController search_Con=loader.getController();
			search_Con.setSession(Sessions.this);
			break;
		case "����Ʈ" :
			RecipeListController list_Con=loader.getController();
			list_Con.setSession(Sessions.this);
			break;
		case "��" :
			RecipeViewController view_Con = loader.getController();
			view_Con.setSession(Sessions.this);
			break;
		case "���":
			RecipeRegisterController recipeReg_Con = loader.getController();
			recipeReg_Con.setSession(Sessions.this);
			break;
		case "�����":
			SceneRegisterController sceneReg_Con = loader.getController();
			sceneReg_Con.setSession(Sessions.this);
			break;
		case "��ȣ��":
			PreferenceController preference_Con = loader.getController();
			preference_Con.setSession(Sessions.this);
			break;
		}
	}
	
}
