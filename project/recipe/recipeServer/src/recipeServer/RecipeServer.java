package recipeServer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import oracle.jdbc.internal.OracleResultSet;
import oracle.sql.BLOB;

public class RecipeServer extends Application {

	AsynchronousChannelGroup channelGroup;
	AsynchronousServerSocketChannel serverSocketChannel;
	List<Client> connections = new Vector<Client>();
	Identification identification = new Identification();
	
	void startServer(){
		try{
			channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
			serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
			serverSocketChannel.bind(new InetSocketAddress(5001));
			
			Platform.runLater(()->{
				displayText("[서버 시작]");
				btn_StartStop.setText("Stop");
			});
			
			serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>(){

				public void completed(AsynchronousSocketChannel socketChannel, Void arg1) {
					try{
						String message = "[연결 수락 : "+ socketChannel.getRemoteAddress() + " : " + Thread.currentThread().getName() + " ] ";
						
						Platform.runLater(()->{
							displayText(message);
						});
						
					}catch(Exception e){}
					
					Client client = new Client(socketChannel);
					connections.add(client);
					Platform.runLater(()->
					displayText("[연결 갯수 : " + connections.size() + "]"));
					
					serverSocketChannel.accept(null,this);
				}

				public void failed(Throwable arg0, Void arg1) {
					if(serverSocketChannel.isOpen()) stopServer();
				}
				
			});
			
		}catch(Exception e){}
	}
	
	void stopServer(){
		try{
			if(channelGroup!=null&&!channelGroup.isShutdown())
				channelGroup.shutdownNow();
			
			Platform.runLater(()->{
				displayText("[서버 종료]");
				btn_StartStop.setText("Start");
			});
			
		}catch(Exception e){}
	}
	
	class Client{
		AsynchronousSocketChannel socketChannel;
		String message;
		
		Client(AsynchronousSocketChannel socketChannel){
			this.socketChannel=socketChannel;
			receive();
		}
		
		void receive(){
			ByteBuffer read_Buffer = ByteBuffer.allocate(50);
			socketChannel.read(read_Buffer, read_Buffer, new CompletionHandler<Integer, ByteBuffer>(){
				
				public void completed(Integer arg0, ByteBuffer attachment) {
					try{
						attachment.flip();
						Charset charset = Charset.forName("UTF-8");
						String data = charset.decode(attachment).toString();
						String[] datas = data.split("///");

						switch(datas[0]){
						case "로그인":
							Platform.runLater(()->displayText("[로그인]"));
							loginDB(datas);
							break;
						case "회원가입":
							Platform.runLater(()->displayText("[회원가입]"));
							regDB(datas);
							break;
						case "아이디찾기":
							Platform.runLater(()->displayText("[아이디찾기]"));
							searchDB(datas);
							break;
						case "비밀번호찾기":
							Platform.runLater(()->displayText("[비밀번호찾기]"));
							searchDB(datas);
							break;
						case "중복확인":
							Platform.runLater(()->displayText("[중복확인]"));
							checkDB(datas[1]);
							break;
						case "연결종료":
							Platform.runLater(()->displayText("[연결종료]"));
							fail();
							break;
						case "리스트":
							Platform.runLater(()->displayText("[리스트]"));
							listDB();
							break;
						case "뷰":
							Platform.runLater(()->displayText("[뷰]"));
							viewDB(datas);
							break;
						case "이미지":
							Platform.runLater(()->displayText("[이미지 전송]"));
							imageDB(datas);
							break;
						case "전체보기":
							Platform.runLater(()->displayText("[이미지 전송]"));
							allViewDB(datas[1]);
							break;
						case "추천":
							Platform.runLater(()->displayText("[추천]"));
							recommendDB(datas);
							break;
						case "댓글등록":
							Platform.runLater(()->displayText("[댓글등록]"));
							commentDB(datas);
							break;
						case "댓글보기":
							Platform.runLater(()->displayText("[댓글보기]"));
							commentViewDB(datas[1]);
							break;
						case "레시피등록":
							Platform.runLater(()->displayText("[레시피등록]"));
							recipeRegDB(datas);
							break;
						case "장면등록":
							Platform.runLater(()->displayText("[장면등록]"));
							sceneRegDB(datas);
							break;
						}
					}catch(Exception e){}
					ByteBuffer read_Buffer = ByteBuffer.allocate(1000);
					socketChannel.read(read_Buffer, read_Buffer, this);
				}

				public void failed(Throwable arg0, ByteBuffer arg1) {
					if(socketChannel.isOpen()) fail();
				}
				
				
			});
				
		}
		
		void loginDB(String ... datas){
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql = "SELECT USERID, UGENDER, UAGE FROM RECIPE_USER WHERE USERID=? AND UPW=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, datas[1]);
				pstmt.setString(2, datas[2]);
				
				ResultSet rs = pstmt.executeQuery();
				
				if(rs.next()){
					writeSocket("성공///"+rs.getString("USERID")+"///"+rs.getString("UGENDER")+"///"+rs.getString("UAGE"));
				}else{
					writeSocket("실패");
				}
				
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		void listDB(){
			
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql = "SELECT RNO,USERID, RNAME, RITEMS, RKIND, RRECOMMEND, RCOMMENT, RSCENE FROM RECIPE_RECIPE";
				PreparedStatement pstmt = conn.prepareStatement(sql);

				ResultSet rs = pstmt.executeQuery();
				String message= "";
				
				while(rs.next()){
					
					message+=rs.getInt("RNO")+"///"+ rs.getString("RSCENE")+"///"+
							rs.getString("USERID")+"///"+rs.getString("RNAME")+"///"+
							rs.getString("RITEMS")+"///"+rs.getString("RKIND")+"///"+
							rs.getInt("RRECOMMEND")+"///"+rs.getInt("RCOMMENT")+"///";
					
				}
				
				writeSocket(message);
				
				closeDB(conn);
				
			}catch(Exception e){}
			
		}
		
		void viewDB(String ... datas){
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql = "SELECT SIMAGE, SNO, SCONTENT FROM RECIPE_SCENE WHERE RNO=? AND SNO=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(datas[1]));
				pstmt.setInt(2, Integer.parseInt(datas[2]));
				
				ResultSet rs = pstmt.executeQuery();
				String message= "";
				
				if(rs.next()){
					
					BLOB sImage =(BLOB)rs.getBlob("SIMAGE");
					int chunkSize = sImage.getChunkSize();
					byte[] imageByte = new byte[chunkSize];
					imageByte = sImage.getBytes(1, (int)sImage.length());
					ByteBuffer imageBuffer = ByteBuffer.wrap(imageByte);
					
					writeSocket(Integer.toString(rs.getInt("SNO"))+"///"+rs.getString("SCONTENT")+"///"+Integer.toString(imageBuffer.capacity()));
					
				}
				closeDB(conn);
			}catch(Exception e){e.printStackTrace();}
		}
		
		void imageDB(String ... datas){
			
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql = "SELECT SIMAGE FROM RECIPE_SCENE WHERE RNO=? AND SNO=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(datas[1]));
				pstmt.setInt(2, Integer.parseInt(datas[2]));
				
				ResultSet rs = pstmt.executeQuery();
				
				if(rs.next()){
					
					BLOB sImage =(BLOB)rs.getBlob("SIMAGE");
					int chunkSize = sImage.getChunkSize();
					byte[] imageByte = new byte[chunkSize];
					imageByte = sImage.getBytes(1, (int)sImage.length());
					ByteBuffer imageBuffer = ByteBuffer.wrap(imageByte);
					
					socketChannel.write(imageBuffer);
				}
				closeDB(conn);
			}catch(Exception e){e.printStackTrace();}
		}
		
		void allViewDB(String data){
			
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql = "SELECT SCONTENT FROM RECIPE_SCENE WHERE RNO=? ORDER BY SNO";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(data));
				
				ResultSet rs = pstmt.executeQuery();
				String message= "";
				
				while(rs.next()){
					message += rs.getString("SCONTENT") + "\n";
				}
				
				writeSocket(message);
				
				closeDB(conn);
			}catch(Exception e){e.printStackTrace();}
			
		}
		
		void recommendDB(String ... datas){
			
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql = "SELECT USERID FROM RECIPE_RECOMMEND WHERE USERID=? AND RNO=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, datas[1]);
				pstmt.setInt(2, Integer.parseInt(datas[2]));
				
				ResultSet rs = pstmt.executeQuery();
				String message= "";
				
				if(rs.next()){
					message="실패";
				}else{
					sql="INSERT INTO RECIPE_RECOMMEND(USERID, RNO, UGENDER, UAGE) VALUES(?, ?, ?, ?)";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datas[1]);
					pstmt.setInt(2, Integer.parseInt(datas[2]));
					pstmt.setString(3, datas[3]);
					pstmt.setString(4, datas[4]);
					
					int upd=pstmt.executeUpdate();
					
					if(upd==1)
						message = "성공";
				}
				
				writeSocket(message);
				
				closeDB(conn);
			}catch(Exception e){e.printStackTrace();}
			
		}
		
		void commentDB(String ...datas ){
			Connection conn = null;
			conn = connDB(conn);
			
			try{
				
				String sql = "INSERT INTO RECIPE_COMMENT(USERID,RNO,CCONTENT) VALUES(?,?,?)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, datas[1]);
				pstmt.setInt(2, Integer.parseInt(datas[2]));
				pstmt.setString(3, datas[3]);
				
				int upd = pstmt.executeUpdate();
				
				if(upd==1){
					writeSocket("성공");
				}
				
				closeDB(conn);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		void commentViewDB(String data){
		
			Connection conn = null;
			conn = connDB(conn);
			
			try{
				
				String sql = "SELECT USERID, CCONTENT FROM RECIPE_COMMENT WHERE RNO = ?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(data));
				
				ResultSet rs = pstmt.executeQuery();
				
				String message = "";
				
				if(rs.next()){
					message+= rs.getString("USERID")+"///"+rs.getString("CCONTENT")+"///";
					while(rs.next()){
						
						message+= rs.getString("USERID")+"///"+rs.getString("CCONTENT")+"///";
						
					}
				}else
					message="실패";
					
				writeSocket(message);
				
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		void recipeRegDB(String[] datas){
			Connection conn = null;
			conn = connDB(conn);
			
			try{
				String sql = "INSERT INTO RECIPE_RECIPE(RNAME, RKIND, RITEMS, USERID) VALUES(?, ?, ?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"RNO"});
				pstmt.setString(1, datas[1]);
				pstmt.setString(2, datas[2]);
				pstmt.setString(3, datas[3]);
				pstmt.setString(4, datas[4]);
				
				int upd = pstmt.executeUpdate();
				
				if(upd==1){
					ResultSet rs = pstmt.getGeneratedKeys();
					if(rs.next()){
						int aiValue = rs.getInt(1);
						writeSocket(Integer.toString(aiValue));
					}
				}
			}catch(Exception e){e.printStackTrace();}
		}
		
		void sceneRegDB(String[] datas){
			Connection conn = null;
			conn = connDB(conn);
		
			try{
				if(!datas[5].equals("rollback")){
					conn.setAutoCommit(false);
					String sql = "SELECT SNO FROM RECIPE_SCENE WHERE RNO=? AND SNO =? ";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datas[1]);
					pstmt.setString(2, datas[2]);
					
					ResultSet rs = pstmt.executeQuery();
	
					if(rs.next()){
						sql = "UPDATE RECIPE_SCENE SET SCONTENT=?, SIMAGE=empty_blob() WHERE RNO=? AND SNO=?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, datas[3]);
						pstmt.setString(2, datas[1]);
						pstmt.setString(3, datas[2]);
						
					}else{
						sql = "INSERT INTO RECIPE_SCENE(RNO, SNO, SCONTENT,SIMAGE) VALUES(?, ?, ?,empty_blob())";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, datas[1]);
						pstmt.setString(2, datas[2]);
						pstmt.setString(3, datas[3]);
					}
					
					pstmt.executeUpdate();
					
					writeSocket("성공");
					
					ByteBuffer imageBuffer = ByteBuffer.allocate(Integer.parseInt(datas[4]));
	
					Future<Integer> read_Future = socketChannel.read(imageBuffer);
					read_Future.get();
					imageBuffer.flip();
					byte[] imageByte = imageBuffer.array();
					
					InputStream is = new ByteArrayInputStream(imageByte);
					
					sql = "SELECT SIMAGE FROM RECIPE_SCENE WHERE RNO=? AND SNO=? FOR UPDATE";
					pstmt=conn.prepareStatement(sql);
					pstmt.setString(1, datas[1]);
					pstmt.setString(2, datas[2]);
					rs = pstmt.executeQuery();
					rs.next();
					BLOB blob = ((OracleResultSet)rs).getBLOB("SIMAGE");
					
					long position =1;
					int bytesRead=0;
					
					byte[] byteBuffer = new byte[blob.getChunkSize()];
					
					while((bytesRead = is.read(byteBuffer))!=-1){
						blob.putBytes(position, byteBuffer, bytesRead);
						position += bytesRead;
					}
					is.close();
					conn.commit();
			
				}else{
					writeSocket("롤백");
					String sql = "DELETE RECIPE_RECIPE WHERE RNO = ?";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datas[1]);
					pstmt.executeUpdate();
					conn.commit();
				}
			
			}catch(Exception e){
				e.printStackTrace();
			}
				closeDB(conn);
			
		}
		
		void regDB(String ... datas){
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql = "INSERT INTO RECIPE_USER(USERID,UPW,UNAME,UTEL,UMAIL,UGENDER,UAGE) VALUES(?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				
				for(int i=1;i<8;i++){
					pstmt.setString(i, datas[i]);
				}
				
				int row = pstmt.executeUpdate();
				
				if(row==1){
					writeSocket("성공");
				}else
					writeSocket("실패");
				
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		void checkDB(String data){
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql = "SELECT USERID FROM RECIPE_USER WHERE USERID=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, data);
				
				ResultSet rs = pstmt.executeQuery();
				
				if(rs.next()){
					writeSocket("있음");
				}else
					writeSocket("없음");
				
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		void searchDB(String ... datas){
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql=null;
				if(datas[0].equals("아이디찾기")){
					sql = "SELECT USERID FROM RECIPE_USER WHERE UMAIL=?";;
				}else if(datas[0].equals("비밀번호찾기")){
					sql = "SELECT UPW FROM RECIPE_USER WHERE UMAIL = ? AND USERID=? ";
				}
				PreparedStatement pstmt = conn.prepareStatement(sql);
				
				for(int i=1;i<datas.length;i++){
					pstmt.setString(i, datas[i]);
				}
				
				ResultSet rs = pstmt.executeQuery();
				
				if(rs.next()){
					mailSend("레시피 프로그램 찾기",rs.getString(1)+" 입니다.",datas[1]);
					writeSocket("성공");
				}else
					writeSocket("실패");
				
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		void mailSend(String title, String content,String receiver){
			Properties props = new Properties();
	        props.setProperty("mail.transport.protocol", "smtp");
	        props.setProperty("mail.host", "smtp.gmail.com");
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.port", "465");
	        props.put("mail.smtp.socketFactory.port", "465");
	        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	        props.put("mail.smtp.socketFactory.fallback", "false");
	        props.setProperty("mail.smtp.quitwait", "false");
	         
	        Authenticator auth = new Authenticator(){
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(identification.getmailId() , identification.getmailPW());
	            }
	        };
	    
	        Session session = Session.getDefaultInstance(props,auth);
	        
	        try{
		        MimeMessage message = new MimeMessage(session);
		        message.setSender(new InternetAddress(identification.getmailId()));
		        message.setSubject(title);
		 
		        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
		         
		        Multipart mp = new MimeMultipart();
		        MimeBodyPart mbp1 = new MimeBodyPart();
		        mbp1.setText(content);
		        mp.addBodyPart(mbp1);
		        
		        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		        CommandMap.setDefaultCommandMap(mc);

		        message.setContent(mp);
		         
		        Transport.send(message);
		        
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
		}
		
		Connection connDB(Connection conn){
			try{
				Class.forName("oracle.jdbc.OracleDriver");
				conn = DriverManager.getConnection("jdbc:oracle:thin:@"+identification.getdbIP(), identification.getdbId() , identification.getdbPassword());
				
			}catch(Exception e){
				e.printStackTrace();
			}
			return conn;
		}
		
		void closeDB(Connection conn){
			try{
				if(!conn.isClosed())
					conn.close();
				}catch(Exception e){}
		}
			
		String readSocket(int size){
			
			ByteBuffer read_Buffer = ByteBuffer.allocate(size);
			socketChannel.read(read_Buffer, read_Buffer, new CompletionHandler<Integer, ByteBuffer>(){

				public void completed(Integer arg0, ByteBuffer attachment) {
					attachment.flip();
					Charset charset = Charset.forName("UTF-8");
					message = charset.decode(attachment).toString();
				}

				public void failed(Throwable arg0, ByteBuffer arg1) {
					if(socketChannel.isOpen()) fail();
				}
				
			});
			
			return message;
		}
		
		void writeSocket(String message){
			Charset charset = Charset.forName("UTF-8");
			ByteBuffer write_Buffer = charset.encode(message);
			socketChannel.write(write_Buffer, null, new CompletionHandler<Integer, Void>(){

				public void completed(Integer result, Void attachment) {
				}

				public void failed(Throwable exc, Void attachment) {
					if(socketChannel.isOpen()) fail();
				}
				
			});
		}
		
		void fail(){
			try{
				String message = "[클라이언트 통신 안됨 : "+socketChannel.getRemoteAddress() + " : "+ Thread.currentThread().getName() + "]";
				Platform.runLater(()->{
					displayText(message);
				});
				connections.remove(Client.this);
				socketChannel.close();
				
			}catch(Exception e){}
		}
		
	}
	
	Button btn_StartStop;
	TextArea area_Chat;
	
	public void start(Stage primaryStage) throws Exception {
		
		BorderPane pane_Server = new BorderPane();
		pane_Server.setPrefSize(400, 300);
		
		area_Chat = new TextArea();
		area_Chat.setEditable(false);
		BorderPane.setMargin(area_Chat, new Insets(0,0,2,0));
		pane_Server.setCenter(area_Chat);
		
		btn_StartStop = new Button();
		btn_StartStop.setText("Start");
		btn_StartStop.setPrefSize(Double.MAX_VALUE, 30);
		btn_StartStop.setOnAction(event->{
			if(btn_StartStop.getText().equals("Start"))
				startServer();
			else
				stopServer();
		});
		pane_Server.setBottom(btn_StartStop);
		
		Scene scene_Server = new Scene(pane_Server);
		primaryStage.setScene(scene_Server);
		primaryStage.setTitle("Recipe Server");
		primaryStage.show();
		
	}
	
	public void displayText(String message){
		area_Chat.appendText(message + "\n");
	}
	
	public static void main(String[] args){
		launch(args);
	}

}
