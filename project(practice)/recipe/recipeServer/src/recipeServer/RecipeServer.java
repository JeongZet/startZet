package recipeServer;

/*
 * 레시피 프로그램의 서버 역할을 하는 클래스로
 * 클라이언트가 보내는 버퍼를 비동기식으로 받아 처리하며 다중의 사용자들의 처리를 받아 실시간으로 바로 처리가 가능하다.
 * 클라이언트로부터 로그인, 회원가입, 레시피 리스트, 뷰, 레시피 등록 등을 문자로 구분하여 각각 처리를 한다.
 * 기능으로는 SMTP기능과 JDBC, NIO 비동기식 채널그룹 기능이 있다.
 * 유저 및 레시피에 대한 모든 데이터는 Oracle DB에 저장되어 있으며 클라이언트이 보내는 버퍼에 따라 각기 다른 방법으로 DB에 처리하며,
 * 아이디 찾기나 비밀번호 찾기는 SMTP를 이용하여 DB에 저장되어 있는 Email 주소를 통해 해당 주소로 아이디와 비밀번호를 보내주는 기능이다.
 */

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;

import javafx.application.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import oracle.jdbc.internal.OracleResultSet;
import oracle.sql.BLOB;

public class RecipeServer extends Application {

	AsynchronousChannelGroup channelGroup;						//socket 관리를 위한 채널 그룹
	AsynchronousServerSocketChannel serverSocketChannel;		//비동기식 서버소켓채널
	List<Client> connections = new Vector<Client>();			//클라이언트의 접속에 대한 리스트
	Identification identification = new Identification();		//DB, SMTP에 사용되는 ID,PW 저장해둔 클래스
	
	//서버를 시작하기 위한 메소드로 비동기식 채널 그룹을 초기화 하고 해당 그룹에 서버소켓채널을 등록
	//클라이언트에서 연결을 요청할 경우 요청을 허용하여 클라이언트를 커넥션 리스트에 추가한다.
	void startServer(){
		try{
			channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
			//채널 그룹 초기화 (사용가능한 프로세서 수, 쓰레드풀)
			serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
			serverSocketChannel.bind(new InetSocketAddress(5001));
			//서버를 열고 5001번 포트로 서버를 바인딩
			
			Platform.runLater(()->{
				displayText("[서버 시작]");
				btn_StartStop.setText("Stop");
			});
			
			serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>(){
				//클라이언트로부터 서버소켓으로 연결 허가를 받을 때
				public void completed(AsynchronousSocketChannel socketChannel, Void arg1) {
					try{
						String message = "[연결 수락 : "+ socketChannel.getRemoteAddress() + " : " + Thread.currentThread().getName() + " ] ";
						
						Platform.runLater(()->{
							displayText(message);
						});
						
					}catch(Exception e){}
					
					Client client = new Client(socketChannel);
					connections.add(client);
					//클라이언트의 연결 여부 확인을 위해 커넥션 리스트에 추가
					
					Platform.runLater(()->
					displayText("[연결 갯수 : " + connections.size() + "]"));
					
					serverSocketChannel.accept(null,this);
					//다른 클라이언트의 연결을 대기하기 위해 다시 호출
				}
				public void failed(Throwable arg0, Void arg1) {
					if(serverSocketChannel.isOpen()) stopServer();
				}
				
			});
		}catch(Exception e){}
	}
	
	//서버를 닫을 때의 메소드로 채널 그룹을 강제로 닫는다.
	void stopServer(){
		try{
			if(channelGroup!=null&&!channelGroup.isShutdown())
				channelGroup.shutdownNow();
			//채널그룹이 열려져있을 경우 강제적으로 닫는다.
			Platform.runLater(()->{
				displayText("[서버 종료]");
				btn_StartStop.setText("Start");
			});
		}catch(Exception e){}
	}
	
	//서버로 연결되는 클라이언트 각각을 처리하기 위한 클래스로 
	//이 클래스에서 데이터를 받고 보내는 것을 모두 주관한다.
	//클라이언트의 요청에 따라 18종류의 요청을 처리해주며 
	class Client{
		AsynchronousSocketChannel socketChannel;				//비동기 소켓 채널
		String message;											//클라이언트로부터 읽어온 버퍼를 저장하기 위한 스트링 변수
		
		//생성자로 클라이언트 객체 생성과 동시에 receive() 메소드를 호출하여 계속적으로 클라이언트가 보내는 버퍼를 받아온다.
		Client(AsynchronousSocketChannel socketChannel){
			this.socketChannel=socketChannel;
			receive();
		}
		
		//클라이언트가 보내는 버퍼를 받아오는 메소드이며 이 메소드에서 클라이언트의 모든 요청을 처리해주는 실질적인 메소드이다.
		void receive(){
			ByteBuffer read_Buffer = ByteBuffer.allocate(50);	//클라이언트가 보내는 버퍼를 받기 위해 바이트 버퍼 초기화
			socketChannel.read(read_Buffer, read_Buffer, new CompletionHandler<Integer, ByteBuffer>(){
				//클라이언트가 보내는 버퍼를 계속적으로 읽어오기 위한 비동기 소켓 채널의 read 메소드
				public void completed(Integer arg0, ByteBuffer attachment) {
					try{
						attachment.flip();							//받아온 데이터의 position 0으로 변경
						Charset charset = Charset.forName("UTF-8");	//소켓을 통해 보내지는 데이터 디코딩 위해 캐릭셋 UTF-8 형식으로 선언 
						String data = charset.decode(attachment).toString();	//받아온 버퍼 디코딩
						String[] datas = data.split("///");						//디코딩한 데이터를 스트링 배열로 분할 저장
						String message = " - ["+socketChannel.getRemoteAddress()+" : "+Thread.currentThread().getName()+"]" ;
						//해당 클라이언트 객체로 접속한 클라이언트의 IP 주소와 해당 클라이언트를 담당하는 쓰레드의 이름 출력을 위한 메시지
						
						switch(datas[0]){
						//datas[0] 에는 어떤 요청을 했는지를 구별 할 수 있게 저장되어 있으며 switch 문을 통해서 그 요청들을 구분
						//총 18종류의 요청을 구분해둠
						case "로그인":
							Platform.runLater(()->displayText("[로그인]"+message));
							loginDB(datas);
							break;
							
						case "중복확인":
							Platform.runLater(()->displayText("[중복확인]"+message));
							checkDB(datas[1]);
							break;
							
						case "회원가입":
							Platform.runLater(()->displayText("[회원가입]"+message));
							regDB(datas);
							break;
							
						case "아이디찾기":
							Platform.runLater(()->displayText("[아이디찾기]"+message));
							searchDB(datas);
							break;
							
						case "비밀번호찾기":
							Platform.runLater(()->displayText("[비밀번호찾기]"+message));
							searchDB(datas);
							break;
							
						case "리스트":
							Platform.runLater(()->displayText("[리스트]"+message));
							listDB(datas[1]);
							break;
							
						case "뷰":
							Platform.runLater(()->displayText("[뷰]"+message));
							viewDB(datas);
							break;
							
						case "이미지":
							Platform.runLater(()->displayText("[이미지 전송]"+message));
							imageDB(datas);
							break;
							
						case "전체보기":
							Platform.runLater(()->displayText("[이미지 전송]"+message));
							allViewDB(datas[1]);
							break;
							
						case "추천":
							Platform.runLater(()->displayText("[추천]"+message));
							recommendDB(datas);
							break;
							
						case "댓글등록":
							Platform.runLater(()->displayText("[댓글등록]"+message));
							commentDB(datas);
							break;
							
						case "댓글보기":
							Platform.runLater(()->displayText("[댓글보기]"+message));
							commentViewDB(datas[1]);
							break;
							
						case "레시피등록":
							Platform.runLater(()->displayText("[레시피등록]"+message));
							recipeRegDB(datas);
							break;
							
						case "장면등록":
							Platform.runLater(()->displayText("[장면등록]"+message));
							sceneRegDB(datas);
							break;
							
						case "선호도콤보박스":
							Platform.runLater(()->displayText("[선호도콤보박스]"+message));
							preferenceComboBoxDB();
							break;
							
						case "선호도":
							Platform.runLater(()->displayText("[선호도]"+message));
							preferenceDB(datas);
							break;
							
						case "채택":
							Platform.runLater(()->displayText("[채택]"+message));
							adoptDB(datas);
							break;
							
						case "연결종료":
							Platform.runLater(()->displayText("[연결종료]"+message));
							fail();
							break;
						}
					}catch(Exception e){}
					ByteBuffer read_Buffer = ByteBuffer.allocate(1000);
					socketChannel.read(read_Buffer, read_Buffer, this);
					//계속적으로 클라이언트가 보낸 버퍼를 읽어오기 위해 read() 메소드 호출
				}
				public void failed(Throwable arg0, ByteBuffer arg1) {
					if(socketChannel.isOpen()) fail();
				}
			});
		}
		
		//로그인 요청에 대해 처리를 위한 메소드로 DB와의 연결로 SELECT문을 사용하여 사용자의 존재 여부를 확인하며 
		//관리자와 일반 유저의 구분도 존재한다면 해당 유저ID, 성별, 연령을 클라이언트로 보내준다. 실패시에는 실패 라는 메시지를 클라이언트로 보낸다.
		//클라이언트로부터 유저 아이디와 패스워드를 받아온다.
		void loginDB(String[] datas){
			Connection conn = null;
			conn = connDB(conn);					//DB 연결을 위한 메소드 호출
			try{
				String sql = "SELECT USERID, UNAME, UGENDER, UAGE FROM RECIPE_USER WHERE USERID=? AND UPW=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, datas[1]);
				pstmt.setString(2, datas[2]);
				//DB로부터 WHERE절에 클라이언트로부터 받은 데이터를 세팅ㄴ
				
				ResultSet rs = pstmt.executeQuery();
				
				if(rs.next()){
					//결과값을 조회
					if(rs.getString("UNAME").equals("관리자")) 
						writeSocket("성공///관리자");
					else
						writeSocket("성공///"+rs.getString("USERID")+"///"+rs.getString("UGENDER")+"///"+rs.getString("UAGE")+"///");
				}else{
					writeSocket("실패");
				}
				pstmt.close();
				closeDB(conn);						//DB 연결을 종료하기 위한 메소드 호출
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		//회원가입을 할 시에 아이디 중복 체크 요청에 처리를 위한 메소드로 DB와의 연결을 통해 SELECT문을 사용하여 결과값이 존재할 시 있음 이라는 메시지를 보내고
		//존재하지 않을 시 없음이라는 메시지를 보내준다. 클라이언트로부터 유저 아이디를 받아온다.
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
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		//회원가입 요청에 대해 처리를 위한 메소드로 DB와의 연결로 INSERT 문을 사용하여 클라이언트가 보낸 정보로 가입시켜준다.
		//무사히 회원가입에 성공하면 성공, 실패하면 실패라는 메시지를 클라이언트로 보내준다.
		//클라이언트로부터 유저 아이디, 패스워드, 이름, 전화번호, 메일 주소, 성별, 연령을 받아온다.
		void regDB(String[] datas){
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql = "INSERT INTO RECIPE_USER(USERID,UPW,UNAME,UTEL,UMAIL,UGENDER,UAGE) VALUES(?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				
				for(int i=1;i<8;i++){
					pstmt.setString(i, datas[i]);
				}
				
				int row = pstmt.executeUpdate();		//해당 sql문이 성공할 시 row에는 업데이트한 row 수가 리턴되며 해당 sql문은 1개만 업데이트 하므로 1이 리턴
				
				if(row==1){
					writeSocket("성공");
				}else
					writeSocket("실패");
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	
		//아이디 찾기, 비밀번호 찾기를 요청 처리를 위한 메소드로 DB와의 연결로 SELECT 문을 사용하여 해당 결과값을 조회한다.
		//존재할 시에는 DB에 저장된 해당 EMAIL로 SMTP를 사용하여 DB에 저장된 아이디와 비밀번호를 보내준다.
		//클라이언트로부터 유저아이디와 메일 주소 를 받아온다.
		void searchDB(String[] datas){
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
					//SMTP를 사용한 메소드를 호출
					writeSocket("성공");
				}else
					writeSocket("실패");
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		//찾기 메소드를 통해 SMTP를 사용할 때 호출되는 메소드이다.(오픈소스 이용)
		//제목, 내용, 받는 사람 메일 주소
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
		
		//레시피 리스트를 요청 처리를 위한 메소드로 DB와의 연결로 SELECT문을 사용하여 모든 레시피 리스트들을 받아온다.
		//레시피들이 존재할 경우 모든 레시피(조건에 부합하는 (정식, 도전))를 받아와 클라이언트로 보내준다.
		//레시피가 한 개도 존재 하지 않을 경우 없음을 보내준다.
		//클라이언트로부터 정식 레시피인지 도전 레시피인지를 구분하는 체크를 받아온다.
		void listDB(String data){
			
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql = "SELECT RNO,USERID, RNAME, RITEMS, RKIND, RRECOMMEND, RCOMMENT, RSCENE FROM RECIPE_RECIPE WHERE RCHECK=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data);
				
				ResultSet rs = pstmt.executeQuery();
				String message= "";
				
				if(rs.next()){
					message+=rs.getInt("RNO")+"///"+ rs.getString("RSCENE")+"///"+
							rs.getString("USERID")+"///"+rs.getString("RNAME")+"///"+
							rs.getString("RITEMS")+"///"+rs.getString("RKIND")+"///"+
							rs.getInt("RRECOMMEND")+"///"+rs.getInt("RCOMMENT")+"///";
					while(rs.next()){
						
						message+=rs.getInt("RNO")+"///"+ rs.getString("RSCENE")+"///"+
								rs.getString("USERID")+"///"+rs.getString("RNAME")+"///"+
								rs.getString("RITEMS")+"///"+rs.getString("RKIND")+"///"+
								rs.getInt("RRECOMMEND")+"///"+rs.getInt("RCOMMENT")+"///";
						
					}
				}else{
					message="없음";
				}
				
				
				writeSocket(message);
				pstmt.close();
				closeDB(conn);
				
			}catch(Exception e){}
			
		}
		
		//레시피 리스트에서 레시피 선택 시의 레시피 뷰 요청 처리를 위한 메소드로 DB와의 연결로 SELECT문을 사용하여 해당 레시피의 정보를 받아온다.
		//유저로부터 레시피 번호와 SCENE 번호를 받아와 해당 조건에 맞는 레시피의 이미지와 내용, 번호를 받아온다.
		void viewDB(String[] datas){
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql = "SELECT SIMAGE, SNO, SCONTENT FROM RECIPE_SCENE WHERE RNO=? AND SNO=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(datas[1]));
				pstmt.setInt(2, Integer.parseInt(datas[2]));
				
				ResultSet rs = pstmt.executeQuery();
				
				if(rs.next()){
					
					BLOB sImage =(BLOB)rs.getBlob("SIMAGE");				//이미지를 받아오기 위해 BLOB 변수 초기화
					int chunkSize = sImage.getChunkSize();					//이미지의 크기를 저장
					byte[] imageByte = new byte[chunkSize];					//해당 이미지의 크기의 바이트 배열 선언
					imageByte = sImage.getBytes(1, (int)sImage.length());	
					//BLOB에 저장된 이미지를 바이트 배열에 저장 - length를 1부터 시작하는 이유는 0부터 할 경우 0에서 쓰레기값이 첨가되기 때문에.
					
					ByteBuffer imageBuffer = ByteBuffer.wrap(imageByte);
					//클라이언트에게 보내주기 위하여 바이트 배열을 래핑하여 버퍼로 저장
					
					writeSocket(Integer.toString(rs.getInt("SNO"))+"///"+rs.getString("SCONTENT")+"///"+Integer.toString(imageBuffer.capacity()));
					//레시피 번호와 내용, 이미지 크기를 보낸다.(이미지 크기를 보내는 이유는 이미지 크기를 미리 보내놓아 클라이언트 측에서 불필요한 공간을 낭비하지 않기 위해서)
				}
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){e.printStackTrace();}
		}
		
		//레시피 뷰에 대한 요청 처리 중 이미지를 보내는 메소드이다.
		//부분적으로 viewDB와 동일하며 이미지 크기를 보내지 않고 이미지 버퍼 자체를 보낸다는 것에 차이가 있다.
		void imageDB(String[] datas){
			
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
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){e.printStackTrace();}
		}

		//레시피 뷰 페이지에서 디폴트 값인 상세보기 창에서 전체보기 버튼에 대한 요청 처리 메소드이며 SELECT 문을 사용한다.
		//클라이언트로부터 레시피 번호를 받아오며 레시피의 Scene 번호 순으로 정렬하여 순서대로 레시피 내용을 보내준다.
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
					//모든 레시피 내용을 message에 저장
				}
				
				writeSocket(message);
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){e.printStackTrace();}
			
		}

		//추천에 대한 요청 처리를 위한 메소드이며 SELECT 문과 INSERT문을 사용한다.
		//클라이언트로부터 등록자 아이디와 레시피 번호, 성별, 연령을 받아온다.
		//먼저 SELECT문으로 해당 유저가 추천을 했는 지 여부를 알아보고 존재한다면 실패라는 메시지를 보내 클라이언트에서 이미 존재한다는 팝업창을 활성화 하고
		//존재하지 않는다면 클라이언트로부터 받아온 정보로 INSERT하여 준다.
		void recommendDB(String[] datas){
			
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
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){e.printStackTrace();}
			
		}

		//댓글 쓰기에 대한 요청 처리를 위한 메소드이며 INSERT문을 사용한다.
		//클라이언트로부터 등록자 아이디와 레시피 번호, 내용을 받아온다.
		//INSERT에 성공하면 성공 실패하면 실패라는 메시지를 클라이언트에게 보내준다.
		void commentDB(String[] datas){
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
				}else
					writeSocket("실패");
				
				pstmt.close();
				closeDB(conn);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}

		//레시피 뷰 페이지가 활성화 시에 동시에 댓글 리스트들을 활성화에 대한 요청 처리를 위한 메소드이다.
		//클라이언트에선 레시피 번호를 보내오며 해당 레시피 번호를 받아 SELECT문을 사용하여 조건에 부합하는 모든 댓글 리스트들을
		//클라이언트에게 보낸다. 존재한다면 모든 댓글 리스트들을 보내고 존재하지 않는다면 실패라는 메시지를 보낸다.
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
						//모든 댓글 리스트들을 message에 저장
					}
				}else
					message="실패";
					
				writeSocket(message);
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}

		//레시피 등록 요청 처리를 위한 메소드로 INSERT문을 사용한다.
		//사용자로부터 레시피 이름, 레시피 종류, 레시피 재료, 등록자 아이디, 등재 여부 데이터들을 받아온다.
		//성공할 시에는 해당 레시피의 번호를 클라이언트에게 보낸다.
		void recipeRegDB(String[] datas){
			Connection conn = null;
			conn = connDB(conn);
			
			try{
				String sql = "INSERT INTO RECIPE_RECIPE(RNAME, RKIND, RITEMS, USERID, RCHECK) VALUES(?, ?, ?, ?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"RNO"});
				pstmt.setString(1, datas[1]);
				pstmt.setString(2, datas[2]);
				pstmt.setString(3, datas[3]);
				pstmt.setString(4, datas[4]);
				pstmt.setString(5, datas[5]);
				
				int upd = pstmt.executeUpdate();
				
				if(upd==1){
					ResultSet rs = pstmt.getGeneratedKeys();
					if(rs.next()){
						int aiValue = rs.getInt(1);
						writeSocket(Integer.toString(aiValue));
						//해당 레시피 번호가 자동증가값이기에 클라이언트에서 고유 PK를 갖고 있지 못하기에 서버에서 보내줌.
					}
				}else
					writeSocket("실패");
			}catch(Exception e){e.printStackTrace();}
		}

		//레시피 Scene 등록 요청 처리를 위한 메소드로 SELECT, INSERT, UPDATE, DELETE 문을 사용한다.
		//사용자로부터 레시피 번호, Scene 번호, 이미지 버퍼 크기, 이미지 버퍼를 받아온다.
		//제일 먼저 Scene을 수정하는 경우가 있기에 존재 여부를 먼저 확인 후 존재한다면 UPDATE문을 사용하여 데이터를 바꾸며
		//존재하지 않으면 INSERT문을 사용하여 데이터를 삽입한다. 성공할 시에는 성공이라고 클라이언트에게 보내며
		//이미지 등록을 위해 클라이언트로부터 이미지 버퍼를 받아와 DB에 삽입한다.
		//만약 Scene 등록 중 끝까지 등록하지 않고 취소할 경우 레시피 리스트로 등록된 데이터도 삭제해야 하기 때문에 DELETE 문을 사용하여 삭제한다.
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
					//Scene 존재 여부 확인을 위한 SELECT 문
					if(rs.next()){
						sql = "UPDATE RECIPE_SCENE SET SCONTENT=?, SIMAGE=empty_blob() WHERE RNO=? AND SNO=?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, datas[3]);
						pstmt.setString(2, datas[1]);
						pstmt.setString(3, datas[2]);
						//Scene이 존재하여 수정을 위한 UPDATE 문
					}else{
						sql = "INSERT INTO RECIPE_SCENE(RNO, SNO, SCONTENT,SIMAGE) VALUES(?, ?, ?,empty_blob())";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, datas[1]);
						pstmt.setString(2, datas[2]);
						pstmt.setString(3, datas[3]);
						//Scene이 존재하지 않아 삽입을 위한 INSERT 문
					}
					
					pstmt.executeUpdate();
					
					writeSocket("성공");
					
					ByteBuffer imageBuffer = ByteBuffer.allocate(Integer.parseInt(datas[4]));
					//클라이언트로부터 받은 이미지 버퍼 크기만큼의 이미지 버퍼 초기화
					
					Future<Integer> read_Future = socketChannel.read(imageBuffer);
					read_Future.get();
					//이미지 버퍼를 Future객체로 받아 이미지를 받아올 때 까지 블로킹(크기가 크기때문에 블로킹 사용)
					
					imageBuffer.flip();										//이미지버퍼의 position 0으로 변경
					
					byte[] imageByte = imageBuffer.array();					//이미지 버퍼의 array화
					
					InputStream is = new ByteArrayInputStream(imageByte);	//DB에 저장하기 위한 스트림 초기화
					
					sql = "SELECT SIMAGE FROM RECIPE_SCENE WHERE RNO=? AND SNO=? FOR UPDATE";
					pstmt=conn.prepareStatement(sql);
					pstmt.setString(1, datas[1]);
					pstmt.setString(2, datas[2]);
					rs = pstmt.executeQuery();
					rs.next();
					BLOB blob = ((OracleResultSet)rs).getBLOB("SIMAGE");
					//SELECT문을 사용하여 이미지를 등록할 BLOB의 위치 갱신
					
					long position =1;
					int bytesRead=0;
					
					byte[] byteBuffer = new byte[blob.getChunkSize()];
					
					while((bytesRead = is.read(byteBuffer))!=-1){
						blob.putBytes(position, byteBuffer, bytesRead);
						position += bytesRead;
					}
					//DB로 스트림을 이용해 이미지를 BLOB 형태로 갱신
					
					is.close();
					pstmt.close();
					conn.commit();
					//모든 과정 완료 시에 커밋
					
				}else{
					writeSocket("롤백");
					String sql = "DELETE RECIPE_RECIPE WHERE RNO = ?";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datas[1]);
					pstmt.executeUpdate();
					pstmt.close();
					conn.commit();
					//도중에 닫을 경우의 모든 데이터를 제거해야함로 CASCADE의 설정을 갖고 있기에 등록된 해당 레시피를 삭제함으로써 Scene도 삭제
				}
				
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		//선호도 페이지 콤보박스 세팅을 위한 메소드로 SELECT문을 사용한다.
		//사용자로부터 따로 받아오는 데이터는 없으며 DB에 저장되어 있는 추천 테이블에서 연령과 성별들을 구별하여 클라이언트에 보낸다.
		void preferenceComboBoxDB(){
			
			Connection conn =null;
			conn=connDB(conn);
			try{
				
				String sql = "SELECT DISTINCT UAGE, UGENDER FROM RECIPE_RECOMMEND";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();
				
				String datas = "";
				
				while(rs.next()){
					datas+=rs.getString("UAGE")+"///"+rs.getString("UGENDER")+"///";
				}
				
				writeSocket(datas);
				
				pstmt.close();
				closeDB(conn);
				
			}catch(Exception e){}
			
		}
		
		//선호도 페이지 출력을 위한 메소드로 SELECT문을 사용하였고 조인과 서브쿼리를 사용하였다.
		//사용자로부터 연령과 성별을 받아와 해당 조건에 부합하는 리스트들을 사용자에게 보내주며 존재하지 않을 경우 없음이라는 메시지를 보낸다.
		//다소 쿼리문들이 복잡해보이는 느낌이지만 전혀 복잡하지 않다.
		void preferenceDB(String[] datas){
			
			Connection conn = null;
			conn = connDB(conn);
			
			try{
				String sql = "SELECT A.USERID, A.RNAME, A.RITEMS, A.RKIND, A.RRECOMMEND, A.RCOMMENT FROM RECIPE_RECIPE A, (SELECT RNO, COUNT(UGENDER) AS COUNT"
						+ " FROM (SELECT * FROM RECIPE_RECOMMEND ";
				PreparedStatement pstmt=null;
				if(datas[1].equals("전체 보기") && datas[2].equals("전체 보기")){
					sql+=") GROUP BY RNO ORDER BY COUNT DESC) B WHERE A.RNO= B.RNO";
					pstmt = conn.prepareStatement(sql);
				}else if(datas[1].equals("전체 보기")){
					sql+="WHERE UGENDER=?) GROUP BY RNO ORDER BY COUNT DESC) B WHERE A.RNO= B.RNO";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datas[2]);
				}else if(datas[2].equals("전체 보기")){
					sql+="WHERE UAGE=?) GROUP BY RNO ORDER BY COUNT DESC) B WHERE A.RNO= B.RNO";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datas[1]);
				}else if(!datas[1].equals("전체 보기")&&!datas[2].equals("전체 보기")){
					sql+="WHERE UAGE=? AND UGENDER=?) GROUP BY RNO ORDER BY COUNT DESC) B WHERE A.RNO= B.RNO";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datas[1]);
					pstmt.setString(2, datas[2]);
				}
				
				ResultSet rs = pstmt.executeQuery();
				
				String message = "";
				
				if(rs.next()){
					message+= rs.getString("USERID")+"///"+rs.getString("RNAME")+"///"+rs.getString("RITEMS")+"///"+
				rs.getString("RKIND")+"///"+rs.getInt("RRECOMMEND")+"///"+rs.getInt("RCOMMENT")+"///";
					while(rs.next()){
						message+= rs.getString("USERID")+"///"+rs.getString("RNAME")+"///"+rs.getString("RITEMS")+"///"+
					rs.getString("RKIND")+"///"+rs.getInt("RRECOMMEND")+"///"+rs.getInt("RCOMMENT")+"///";
					}
				}else
					message="없음";
					
				writeSocket(message);
				
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		//관리자 모드의 채택에 대한 요청 처리를 위한 메소드이며 SELECT문과 UPDATE문을 사용한다.
		//클라이언트로부터 레시피 번호를 받아와 해당 레시피의 정식 등재를 하기 위해 사용된다.
		//이미 정식 등재되 있는 레시피의 경우 실패라고 메시지를 보내며 등재되지 않았을 경우 성공이라는 메시지를 보낸다.
		void adoptDB(String[] datas){
			Connection conn =null;
			conn = connDB(conn);
			try{
				String sql = "SELECT RCHECK FROM RECIPE_RECIPE WHERE RNO=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, datas[1]);
				
				ResultSet rs = pstmt.executeQuery();
				//해당 레시피가 정식으로 등재 되었는지 등재되지 않았는지 확인을 위한 SELECT문
				
				if(rs.next()){
					if(rs.getString("RCHECK").equals("1")){
						writeSocket("실패");
						//이미 정식으로 등록 되어 있어서 실패라는 메시지를 보냄
					}else{
						sql = "UPDATE RECIPE_RECIPE SET RCHECK='1' WHERE RNO=?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, datas[1]);
						
						int upd = pstmt.executeUpdate();
						
						writeSocket("성공");
						//정식으로 등록 되지 않아 성공이라는 메시지를 보냄
					}
				}
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		//클라이언트가 연결 종료가 되었을 경우 연결 종료 서버에서 처리하기 위하여 사용 되는 메소드로
		//커넥션 리스트에서 해당 클라이언트를 제거하고 소켓채널을 닫는다.
		void fail(){
			try{
				String message = "[클라이언트 통신 안됨 : "+socketChannel.getRemoteAddress() + " : "+ Thread.currentThread().getName() + "]";
				Platform.runLater(()->{
					displayText(message);
				});
				connections.remove(Client.this);
				//해당 클라이언트를 커넥션 리스트에서 제거
				socketChannel.close();
				//해당 소켓채널을 닫는다.
			}catch(Exception e){}
		}
		
		//DB와의 연결을 위한 메소드로 connection 변수를 리턴한다.
		Connection connDB(Connection conn){
			try{
				Class.forName("oracle.jdbc.OracleDriver");
				conn = DriverManager.getConnection("jdbc:oracle:thin:@"+identification.getdbIP(), identification.getdbId() , identification.getdbPassword());
				
			}catch(Exception e){
				e.printStackTrace();
			}
			return conn;
		}
		
		//열려있는 DB의 연결을 끊기 위한 메소드이다.
		void closeDB(Connection conn){
			try{
				if(!conn.isClosed())
					conn.close();
				}catch(Exception e){}
		}
		
		//소켓 채널의 클라이언트로부터 메시지를 받아오기 위한 read()메소드를 간편하게 정리해둔 메소드이다.
		//버퍼의 크기를 설정하는 size를 매개변수로 받아오고 클라이언트로부터 받아온 버퍼를 디코딩한 String 값을 리턴한다.
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
		
		//소켓 채널의 클라이언트로 메시지를  보내기 위한 write()메소드를 간편하게 정리해둔 메소드이다.
		//보낼 String 값을 매개변수로 받아 인코딩하여 클라이언트로 보내준다.
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
		
	}
	
	private Button btn_StartStop;
	private TextArea area_Chat;
	
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
		});//StartStop 버튼의 이벤트 처리
		pane_Server.setBottom(btn_StartStop);
		
		Scene scene_Server = new Scene(pane_Server);
		primaryStage.setScene(scene_Server);
		primaryStage.setTitle("Recipe Server");
		primaryStage.setOnCloseRequest(event -> stopServer());
		//창닫기 시의 이벤트 처리로 서버를 닫는다.
		primaryStage.show();
		
	}
	
	//TextArea에 메시지를 append 시키기 위한 메소드
	public void displayText(String message){
		area_Chat.appendText(message + "\n");
	}
	
	public static void main(String[] args){
		launch(args);
	}

}
