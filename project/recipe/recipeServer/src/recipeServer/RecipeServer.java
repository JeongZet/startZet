package recipeServer;

/*
 * ������ ���α׷��� ���� ������ �ϴ� Ŭ������
 * Ŭ���̾�Ʈ�� ������ ���۸� �񵿱������ �޾� ó���ϸ� ������ ����ڵ��� ó���� �޾� �ǽð����� �ٷ� ó���� �����ϴ�.
 * Ŭ���̾�Ʈ�κ��� �α���, ȸ������, ������ ����Ʈ, ��, ������ ��� ���� ���ڷ� �����Ͽ� ���� ó���� �Ѵ�.
 * ������δ� SMTP��ɰ� JDBC, NIO �񵿱�� ä�α׷� ����� �ִ�.
 * ���� �� �����ǿ� ���� ��� �����ʹ� Oracle DB�� ����Ǿ� ������ Ŭ���̾�Ʈ�� ������ ���ۿ� ���� ���� �ٸ� ������� DB�� ó���ϸ�,
 * ���̵� ã�⳪ ��й�ȣ ã��� SMTP�� �̿��Ͽ� DB�� ����Ǿ� �ִ� Email �ּҸ� ���� �ش� �ּҷ� ���̵�� ��й�ȣ�� �����ִ� ����̴�.
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

	AsynchronousChannelGroup channelGroup;						//socket ������ ���� ä�� �׷�
	AsynchronousServerSocketChannel serverSocketChannel;		//�񵿱�� ��������ä��
	List<Client> connections = new Vector<Client>();			//Ŭ���̾�Ʈ�� ���ӿ� ���� ����Ʈ
	Identification identification = new Identification();		//DB, SMTP�� ���Ǵ� ID,PW �����ص� Ŭ����
	
	//������ �����ϱ� ���� �޼ҵ�� �񵿱�� ä�� �׷��� �ʱ�ȭ �ϰ� �ش� �׷쿡 ��������ä���� ���
	//Ŭ���̾�Ʈ���� ������ ��û�� ��� ��û�� ����Ͽ� Ŭ���̾�Ʈ�� Ŀ�ؼ� ����Ʈ�� �߰��Ѵ�.
	void startServer(){
		try{
			channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
			//ä�� �׷� �ʱ�ȭ (��밡���� ���μ��� ��, ������Ǯ)
			serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
			serverSocketChannel.bind(new InetSocketAddress(5001));
			//������ ���� 5001�� ��Ʈ�� ������ ���ε�
			
			Platform.runLater(()->{
				displayText("[���� ����]");
				btn_StartStop.setText("Stop");
			});
			
			serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>(){
				//Ŭ���̾�Ʈ�κ��� ������������ ���� �㰡�� ���� ��
				public void completed(AsynchronousSocketChannel socketChannel, Void arg1) {
					try{
						String message = "[���� ���� : "+ socketChannel.getRemoteAddress() + " : " + Thread.currentThread().getName() + " ] ";
						
						Platform.runLater(()->{
							displayText(message);
						});
						
					}catch(Exception e){}
					
					Client client = new Client(socketChannel);
					connections.add(client);
					//Ŭ���̾�Ʈ�� ���� ���� Ȯ���� ���� Ŀ�ؼ� ����Ʈ�� �߰�
					
					Platform.runLater(()->
					displayText("[���� ���� : " + connections.size() + "]"));
					
					serverSocketChannel.accept(null,this);
					//�ٸ� Ŭ���̾�Ʈ�� ������ ����ϱ� ���� �ٽ� ȣ��
				}
				public void failed(Throwable arg0, Void arg1) {
					if(serverSocketChannel.isOpen()) stopServer();
				}
				
			});
		}catch(Exception e){}
	}
	
	//������ ���� ���� �޼ҵ�� ä�� �׷��� ������ �ݴ´�.
	void stopServer(){
		try{
			if(channelGroup!=null&&!channelGroup.isShutdown())
				channelGroup.shutdownNow();
			//ä�α׷��� ���������� ��� ���������� �ݴ´�.
			Platform.runLater(()->{
				displayText("[���� ����]");
				btn_StartStop.setText("Start");
			});
		}catch(Exception e){}
	}
	
	//������ ����Ǵ� Ŭ���̾�Ʈ ������ ó���ϱ� ���� Ŭ������ 
	//�� Ŭ�������� �����͸� �ް� ������ ���� ��� �ְ��Ѵ�.
	//Ŭ���̾�Ʈ�� ��û�� ���� 18������ ��û�� ó�����ָ� 
	class Client{
		AsynchronousSocketChannel socketChannel;				//�񵿱� ���� ä��
		String message;											//Ŭ���̾�Ʈ�κ��� �о�� ���۸� �����ϱ� ���� ��Ʈ�� ����
		
		//�����ڷ� Ŭ���̾�Ʈ ��ü ������ ���ÿ� receive() �޼ҵ带 ȣ���Ͽ� ��������� Ŭ���̾�Ʈ�� ������ ���۸� �޾ƿ´�.
		Client(AsynchronousSocketChannel socketChannel){
			this.socketChannel=socketChannel;
			receive();
		}
		
		//Ŭ���̾�Ʈ�� ������ ���۸� �޾ƿ��� �޼ҵ��̸� �� �޼ҵ忡�� Ŭ���̾�Ʈ�� ��� ��û�� ó�����ִ� �������� �޼ҵ��̴�.
		void receive(){
			ByteBuffer read_Buffer = ByteBuffer.allocate(50);	//Ŭ���̾�Ʈ�� ������ ���۸� �ޱ� ���� ����Ʈ ���� �ʱ�ȭ
			socketChannel.read(read_Buffer, read_Buffer, new CompletionHandler<Integer, ByteBuffer>(){
				//Ŭ���̾�Ʈ�� ������ ���۸� ��������� �о���� ���� �񵿱� ���� ä���� read �޼ҵ�
				public void completed(Integer arg0, ByteBuffer attachment) {
					try{
						attachment.flip();							//�޾ƿ� �������� position 0���� ����
						Charset charset = Charset.forName("UTF-8");	//������ ���� �������� ������ ���ڵ� ���� ĳ���� UTF-8 �������� ���� 
						String data = charset.decode(attachment).toString();	//�޾ƿ� ���� ���ڵ�
						String[] datas = data.split("///");						//���ڵ��� �����͸� ��Ʈ�� �迭�� ���� ����
						String message = " - ["+socketChannel.getRemoteAddress()+" : "+Thread.currentThread().getName()+"]" ;
						//�ش� Ŭ���̾�Ʈ ��ü�� ������ Ŭ���̾�Ʈ�� IP �ּҿ� �ش� Ŭ���̾�Ʈ�� ����ϴ� �������� �̸� ����� ���� �޽���
						
						switch(datas[0]){
						//datas[0] ���� � ��û�� �ߴ����� ���� �� �� �ְ� ����Ǿ� ������ switch ���� ���ؼ� �� ��û���� ����
						//�� 18������ ��û�� �����ص�
						case "�α���":
							Platform.runLater(()->displayText("[�α���]"+message));
							loginDB(datas);
							break;
							
						case "�ߺ�Ȯ��":
							Platform.runLater(()->displayText("[�ߺ�Ȯ��]"+message));
							checkDB(datas[1]);
							break;
							
						case "ȸ������":
							Platform.runLater(()->displayText("[ȸ������]"+message));
							regDB(datas);
							break;
							
						case "���̵�ã��":
							Platform.runLater(()->displayText("[���̵�ã��]"+message));
							searchDB(datas);
							break;
							
						case "��й�ȣã��":
							Platform.runLater(()->displayText("[��й�ȣã��]"+message));
							searchDB(datas);
							break;
							
						case "����Ʈ":
							Platform.runLater(()->displayText("[����Ʈ]"+message));
							listDB(datas[1]);
							break;
							
						case "��":
							Platform.runLater(()->displayText("[��]"+message));
							viewDB(datas);
							break;
							
						case "�̹���":
							Platform.runLater(()->displayText("[�̹��� ����]"+message));
							imageDB(datas);
							break;
							
						case "��ü����":
							Platform.runLater(()->displayText("[�̹��� ����]"+message));
							allViewDB(datas[1]);
							break;
							
						case "��õ":
							Platform.runLater(()->displayText("[��õ]"+message));
							recommendDB(datas);
							break;
							
						case "��۵��":
							Platform.runLater(()->displayText("[��۵��]"+message));
							commentDB(datas);
							break;
							
						case "��ۺ���":
							Platform.runLater(()->displayText("[��ۺ���]"+message));
							commentViewDB(datas[1]);
							break;
							
						case "�����ǵ��":
							Platform.runLater(()->displayText("[�����ǵ��]"+message));
							recipeRegDB(datas);
							break;
							
						case "�����":
							Platform.runLater(()->displayText("[�����]"+message));
							sceneRegDB(datas);
							break;
							
						case "��ȣ���޺��ڽ�":
							Platform.runLater(()->displayText("[��ȣ���޺��ڽ�]"+message));
							preferenceComboBoxDB();
							break;
							
						case "��ȣ��":
							Platform.runLater(()->displayText("[��ȣ��]"+message));
							preferenceDB(datas);
							break;
							
						case "ä��":
							Platform.runLater(()->displayText("[ä��]"+message));
							adoptDB(datas);
							break;
							
						case "��������":
							Platform.runLater(()->displayText("[��������]"+message));
							fail();
							break;
						}
					}catch(Exception e){}
					ByteBuffer read_Buffer = ByteBuffer.allocate(1000);
					socketChannel.read(read_Buffer, read_Buffer, this);
					//��������� Ŭ���̾�Ʈ�� ���� ���۸� �о���� ���� read() �޼ҵ� ȣ��
				}
				public void failed(Throwable arg0, ByteBuffer arg1) {
					if(socketChannel.isOpen()) fail();
				}
			});
		}
		
		//�α��� ��û�� ���� ó���� ���� �޼ҵ�� DB���� ����� SELECT���� ����Ͽ� ������� ���� ���θ� Ȯ���ϸ� 
		//�����ڿ� �Ϲ� ������ ���е� �����Ѵٸ� �ش� ����ID, ����, ������ Ŭ���̾�Ʈ�� �����ش�. ���нÿ��� ���� ��� �޽����� Ŭ���̾�Ʈ�� ������.
		//Ŭ���̾�Ʈ�κ��� ���� ���̵�� �н����带 �޾ƿ´�.
		void loginDB(String[] datas){
			Connection conn = null;
			conn = connDB(conn);					//DB ������ ���� �޼ҵ� ȣ��
			try{
				String sql = "SELECT USERID, UNAME, UGENDER, UAGE FROM RECIPE_USER WHERE USERID=? AND UPW=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, datas[1]);
				pstmt.setString(2, datas[2]);
				//DB�κ��� WHERE���� Ŭ���̾�Ʈ�κ��� ���� �����͸� ���ä�
				
				ResultSet rs = pstmt.executeQuery();
				
				if(rs.next()){
					//������� ��ȸ
					if(rs.getString("UNAME").equals("������")) 
						writeSocket("����///������");
					else
						writeSocket("����///"+rs.getString("USERID")+"///"+rs.getString("UGENDER")+"///"+rs.getString("UAGE")+"///");
				}else{
					writeSocket("����");
				}
				pstmt.close();
				closeDB(conn);						//DB ������ �����ϱ� ���� �޼ҵ� ȣ��
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		//ȸ�������� �� �ÿ� ���̵� �ߺ� üũ ��û�� ó���� ���� �޼ҵ�� DB���� ������ ���� SELECT���� ����Ͽ� ������� ������ �� ���� �̶�� �޽����� ������
		//�������� ���� �� �����̶�� �޽����� �����ش�. Ŭ���̾�Ʈ�κ��� ���� ���̵� �޾ƿ´�.
		void checkDB(String data){
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql = "SELECT USERID FROM RECIPE_USER WHERE USERID=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, data);
				
				ResultSet rs = pstmt.executeQuery();
				
				if(rs.next()){
					writeSocket("����");
				}else
					writeSocket("����");
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		//ȸ������ ��û�� ���� ó���� ���� �޼ҵ�� DB���� ����� INSERT ���� ����Ͽ� Ŭ���̾�Ʈ�� ���� ������ ���Խ����ش�.
		//������ ȸ�����Կ� �����ϸ� ����, �����ϸ� ���ж�� �޽����� Ŭ���̾�Ʈ�� �����ش�.
		//Ŭ���̾�Ʈ�κ��� ���� ���̵�, �н�����, �̸�, ��ȭ��ȣ, ���� �ּ�, ����, ������ �޾ƿ´�.
		void regDB(String[] datas){
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql = "INSERT INTO RECIPE_USER(USERID,UPW,UNAME,UTEL,UMAIL,UGENDER,UAGE) VALUES(?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				
				for(int i=1;i<8;i++){
					pstmt.setString(i, datas[i]);
				}
				
				int row = pstmt.executeUpdate();		//�ش� sql���� ������ �� row���� ������Ʈ�� row ���� ���ϵǸ� �ش� sql���� 1���� ������Ʈ �ϹǷ� 1�� ����
				
				if(row==1){
					writeSocket("����");
				}else
					writeSocket("����");
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	
		//���̵� ã��, ��й�ȣ ã�⸦ ��û ó���� ���� �޼ҵ�� DB���� ����� SELECT ���� ����Ͽ� �ش� ������� ��ȸ�Ѵ�.
		//������ �ÿ��� DB�� ����� �ش� EMAIL�� SMTP�� ����Ͽ� DB�� ����� ���̵�� ��й�ȣ�� �����ش�.
		//Ŭ���̾�Ʈ�κ��� �������̵�� ���� �ּ� �� �޾ƿ´�.
		void searchDB(String[] datas){
			Connection conn = null;
			conn = connDB(conn);
			try{
				String sql=null;
				if(datas[0].equals("���̵�ã��")){
					sql = "SELECT USERID FROM RECIPE_USER WHERE UMAIL=?";;
				}else if(datas[0].equals("��й�ȣã��")){
					sql = "SELECT UPW FROM RECIPE_USER WHERE UMAIL = ? AND USERID=? ";
				}
				PreparedStatement pstmt = conn.prepareStatement(sql);
				
				for(int i=1;i<datas.length;i++){
					pstmt.setString(i, datas[i]);
				}
				
				ResultSet rs = pstmt.executeQuery();
				
				if(rs.next()){
					mailSend("������ ���α׷� ã��",rs.getString(1)+" �Դϴ�.",datas[1]);
					//SMTP�� ����� �޼ҵ带 ȣ��
					writeSocket("����");
				}else
					writeSocket("����");
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		//ã�� �޼ҵ带 ���� SMTP�� ����� �� ȣ��Ǵ� �޼ҵ��̴�.(���¼ҽ� �̿�)
		//����, ����, �޴� ��� ���� �ּ�
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
		
		//������ ����Ʈ�� ��û ó���� ���� �޼ҵ�� DB���� ����� SELECT���� ����Ͽ� ��� ������ ����Ʈ���� �޾ƿ´�.
		//�����ǵ��� ������ ��� ��� ������(���ǿ� �����ϴ� (����, ����))�� �޾ƿ� Ŭ���̾�Ʈ�� �����ش�.
		//�����ǰ� �� ���� ���� ���� ���� ��� ������ �����ش�.
		//Ŭ���̾�Ʈ�κ��� ���� ���������� ���� ������������ �����ϴ� üũ�� �޾ƿ´�.
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
					message="����";
				}
				
				
				writeSocket(message);
				pstmt.close();
				closeDB(conn);
				
			}catch(Exception e){}
			
		}
		
		//������ ����Ʈ���� ������ ���� ���� ������ �� ��û ó���� ���� �޼ҵ�� DB���� ����� SELECT���� ����Ͽ� �ش� �������� ������ �޾ƿ´�.
		//�����κ��� ������ ��ȣ�� SCENE ��ȣ�� �޾ƿ� �ش� ���ǿ� �´� �������� �̹����� ����, ��ȣ�� �޾ƿ´�.
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
					
					BLOB sImage =(BLOB)rs.getBlob("SIMAGE");				//�̹����� �޾ƿ��� ���� BLOB ���� �ʱ�ȭ
					int chunkSize = sImage.getChunkSize();					//�̹����� ũ�⸦ ����
					byte[] imageByte = new byte[chunkSize];					//�ش� �̹����� ũ���� ����Ʈ �迭 ����
					imageByte = sImage.getBytes(1, (int)sImage.length());	
					//BLOB�� ����� �̹����� ����Ʈ �迭�� ���� - length�� 1���� �����ϴ� ������ 0���� �� ��� 0���� �����Ⱚ�� ÷���Ǳ� ������.
					
					ByteBuffer imageBuffer = ByteBuffer.wrap(imageByte);
					//Ŭ���̾�Ʈ���� �����ֱ� ���Ͽ� ����Ʈ �迭�� �����Ͽ� ���۷� ����
					
					writeSocket(Integer.toString(rs.getInt("SNO"))+"///"+rs.getString("SCONTENT")+"///"+Integer.toString(imageBuffer.capacity()));
					//������ ��ȣ�� ����, �̹��� ũ�⸦ ������.(�̹��� ũ�⸦ ������ ������ �̹��� ũ�⸦ �̸� �������� Ŭ���̾�Ʈ ������ ���ʿ��� ������ �������� �ʱ� ���ؼ�)
				}
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){e.printStackTrace();}
		}
		
		//������ �信 ���� ��û ó�� �� �̹����� ������ �޼ҵ��̴�.
		//�κ������� viewDB�� �����ϸ� �̹��� ũ�⸦ ������ �ʰ� �̹��� ���� ��ü�� �����ٴ� �Ϳ� ���̰� �ִ�.
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

		//������ �� ���������� ����Ʈ ���� �󼼺��� â���� ��ü���� ��ư�� ���� ��û ó�� �޼ҵ��̸� SELECT ���� ����Ѵ�.
		//Ŭ���̾�Ʈ�κ��� ������ ��ȣ�� �޾ƿ��� �������� Scene ��ȣ ������ �����Ͽ� ������� ������ ������ �����ش�.
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
					//��� ������ ������ message�� ����
				}
				
				writeSocket(message);
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){e.printStackTrace();}
			
		}

		//��õ�� ���� ��û ó���� ���� �޼ҵ��̸� SELECT ���� INSERT���� ����Ѵ�.
		//Ŭ���̾�Ʈ�κ��� ����� ���̵�� ������ ��ȣ, ����, ������ �޾ƿ´�.
		//���� SELECT������ �ش� ������ ��õ�� �ߴ� �� ���θ� �˾ƺ��� �����Ѵٸ� ���ж�� �޽����� ���� Ŭ���̾�Ʈ���� �̹� �����Ѵٴ� �˾�â�� Ȱ��ȭ �ϰ�
		//�������� �ʴ´ٸ� Ŭ���̾�Ʈ�κ��� �޾ƿ� ������ INSERT�Ͽ� �ش�.
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
					message="����";
				}else{
					sql="INSERT INTO RECIPE_RECOMMEND(USERID, RNO, UGENDER, UAGE) VALUES(?, ?, ?, ?)";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datas[1]);
					pstmt.setInt(2, Integer.parseInt(datas[2]));
					pstmt.setString(3, datas[3]);
					pstmt.setString(4, datas[4]);
					
					int upd=pstmt.executeUpdate();
					
					if(upd==1)
						message = "����";
				}
				
				writeSocket(message);
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){e.printStackTrace();}
			
		}

		//��� ���⿡ ���� ��û ó���� ���� �޼ҵ��̸� INSERT���� ����Ѵ�.
		//Ŭ���̾�Ʈ�κ��� ����� ���̵�� ������ ��ȣ, ������ �޾ƿ´�.
		//INSERT�� �����ϸ� ���� �����ϸ� ���ж�� �޽����� Ŭ���̾�Ʈ���� �����ش�.
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
					writeSocket("����");
				}else
					writeSocket("����");
				
				pstmt.close();
				closeDB(conn);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}

		//������ �� �������� Ȱ��ȭ �ÿ� ���ÿ� ��� ����Ʈ���� Ȱ��ȭ�� ���� ��û ó���� ���� �޼ҵ��̴�.
		//Ŭ���̾�Ʈ���� ������ ��ȣ�� �������� �ش� ������ ��ȣ�� �޾� SELECT���� ����Ͽ� ���ǿ� �����ϴ� ��� ��� ����Ʈ����
		//Ŭ���̾�Ʈ���� ������. �����Ѵٸ� ��� ��� ����Ʈ���� ������ �������� �ʴ´ٸ� ���ж�� �޽����� ������.
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
						//��� ��� ����Ʈ���� message�� ����
					}
				}else
					message="����";
					
				writeSocket(message);
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}

		//������ ��� ��û ó���� ���� �޼ҵ�� INSERT���� ����Ѵ�.
		//����ڷκ��� ������ �̸�, ������ ����, ������ ���, ����� ���̵�, ���� ���� �����͵��� �޾ƿ´�.
		//������ �ÿ��� �ش� �������� ��ȣ�� Ŭ���̾�Ʈ���� ������.
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
						//�ش� ������ ��ȣ�� �ڵ��������̱⿡ Ŭ���̾�Ʈ���� ���� PK�� ���� ���� ���ϱ⿡ �������� ������.
					}
				}else
					writeSocket("����");
			}catch(Exception e){e.printStackTrace();}
		}

		//������ Scene ��� ��û ó���� ���� �޼ҵ�� SELECT, INSERT, UPDATE, DELETE ���� ����Ѵ�.
		//����ڷκ��� ������ ��ȣ, Scene ��ȣ, �̹��� ���� ũ��, �̹��� ���۸� �޾ƿ´�.
		//���� ���� Scene�� �����ϴ� ��찡 �ֱ⿡ ���� ���θ� ���� Ȯ�� �� �����Ѵٸ� UPDATE���� ����Ͽ� �����͸� �ٲٸ�
		//�������� ������ INSERT���� ����Ͽ� �����͸� �����Ѵ�. ������ �ÿ��� �����̶�� Ŭ���̾�Ʈ���� ������
		//�̹��� ����� ���� Ŭ���̾�Ʈ�κ��� �̹��� ���۸� �޾ƿ� DB�� �����Ѵ�.
		//���� Scene ��� �� ������ ������� �ʰ� ����� ��� ������ ����Ʈ�� ��ϵ� �����͵� �����ؾ� �ϱ� ������ DELETE ���� ����Ͽ� �����Ѵ�.
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
					//Scene ���� ���� Ȯ���� ���� SELECT ��
					if(rs.next()){
						sql = "UPDATE RECIPE_SCENE SET SCONTENT=?, SIMAGE=empty_blob() WHERE RNO=? AND SNO=?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, datas[3]);
						pstmt.setString(2, datas[1]);
						pstmt.setString(3, datas[2]);
						//Scene�� �����Ͽ� ������ ���� UPDATE ��
					}else{
						sql = "INSERT INTO RECIPE_SCENE(RNO, SNO, SCONTENT,SIMAGE) VALUES(?, ?, ?,empty_blob())";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, datas[1]);
						pstmt.setString(2, datas[2]);
						pstmt.setString(3, datas[3]);
						//Scene�� �������� �ʾ� ������ ���� INSERT ��
					}
					
					pstmt.executeUpdate();
					
					writeSocket("����");
					
					ByteBuffer imageBuffer = ByteBuffer.allocate(Integer.parseInt(datas[4]));
					//Ŭ���̾�Ʈ�κ��� ���� �̹��� ���� ũ�⸸ŭ�� �̹��� ���� �ʱ�ȭ
					
					Future<Integer> read_Future = socketChannel.read(imageBuffer);
					read_Future.get();
					//�̹��� ���۸� Future��ü�� �޾� �̹����� �޾ƿ� �� ���� ���ŷ(ũ�Ⱑ ũ�⶧���� ���ŷ ���)
					
					imageBuffer.flip();										//�̹��������� position 0���� ����
					
					byte[] imageByte = imageBuffer.array();					//�̹��� ������ arrayȭ
					
					InputStream is = new ByteArrayInputStream(imageByte);	//DB�� �����ϱ� ���� ��Ʈ�� �ʱ�ȭ
					
					sql = "SELECT SIMAGE FROM RECIPE_SCENE WHERE RNO=? AND SNO=? FOR UPDATE";
					pstmt=conn.prepareStatement(sql);
					pstmt.setString(1, datas[1]);
					pstmt.setString(2, datas[2]);
					rs = pstmt.executeQuery();
					rs.next();
					BLOB blob = ((OracleResultSet)rs).getBLOB("SIMAGE");
					//SELECT���� ����Ͽ� �̹����� ����� BLOB�� ��ġ ����
					
					long position =1;
					int bytesRead=0;
					
					byte[] byteBuffer = new byte[blob.getChunkSize()];
					
					while((bytesRead = is.read(byteBuffer))!=-1){
						blob.putBytes(position, byteBuffer, bytesRead);
						position += bytesRead;
					}
					//DB�� ��Ʈ���� �̿��� �̹����� BLOB ���·� ����
					
					is.close();
					pstmt.close();
					conn.commit();
					//��� ���� �Ϸ� �ÿ� Ŀ��
					
				}else{
					writeSocket("�ѹ�");
					String sql = "DELETE RECIPE_RECIPE WHERE RNO = ?";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datas[1]);
					pstmt.executeUpdate();
					pstmt.close();
					conn.commit();
					//���߿� ���� ����� ��� �����͸� �����ؾ��Է� CASCADE�� ������ ���� �ֱ⿡ ��ϵ� �ش� �����Ǹ� ���������ν� Scene�� ����
				}
				
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		//��ȣ�� ������ �޺��ڽ� ������ ���� �޼ҵ�� SELECT���� ����Ѵ�.
		//����ڷκ��� ���� �޾ƿ��� �����ʹ� ������ DB�� ����Ǿ� �ִ� ��õ ���̺��� ���ɰ� �������� �����Ͽ� Ŭ���̾�Ʈ�� ������.
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
		
		//��ȣ�� ������ ����� ���� �޼ҵ�� SELECT���� ����Ͽ��� ���ΰ� ���������� ����Ͽ���.
		//����ڷκ��� ���ɰ� ������ �޾ƿ� �ش� ���ǿ� �����ϴ� ����Ʈ���� ����ڿ��� �����ָ� �������� ���� ��� �����̶�� �޽����� ������.
		//�ټ� ���������� �����غ��̴� ���������� ���� �������� �ʴ�.
		void preferenceDB(String[] datas){
			
			Connection conn = null;
			conn = connDB(conn);
			
			try{
				String sql = "SELECT A.USERID, A.RNAME, A.RITEMS, A.RKIND, A.RRECOMMEND, A.RCOMMENT FROM RECIPE_RECIPE A, (SELECT RNO, COUNT(UGENDER) AS COUNT"
						+ " FROM (SELECT * FROM RECIPE_RECOMMEND ";
				PreparedStatement pstmt=null;
				if(datas[1].equals("��ü ����") && datas[2].equals("��ü ����")){
					sql+=") GROUP BY RNO ORDER BY COUNT DESC) B WHERE A.RNO= B.RNO";
					pstmt = conn.prepareStatement(sql);
				}else if(datas[1].equals("��ü ����")){
					sql+="WHERE UGENDER=?) GROUP BY RNO ORDER BY COUNT DESC) B WHERE A.RNO= B.RNO";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datas[2]);
				}else if(datas[2].equals("��ü ����")){
					sql+="WHERE UAGE=?) GROUP BY RNO ORDER BY COUNT DESC) B WHERE A.RNO= B.RNO";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datas[1]);
				}else if(!datas[1].equals("��ü ����")&&!datas[2].equals("��ü ����")){
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
					message="����";
					
				writeSocket(message);
				
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		//������ ����� ä�ÿ� ���� ��û ó���� ���� �޼ҵ��̸� SELECT���� UPDATE���� ����Ѵ�.
		//Ŭ���̾�Ʈ�κ��� ������ ��ȣ�� �޾ƿ� �ش� �������� ���� ���縦 �ϱ� ���� ���ȴ�.
		//�̹� ���� ����� �ִ� �������� ��� ���ж�� �޽����� ������ ������� �ʾ��� ��� �����̶�� �޽����� ������.
		void adoptDB(String[] datas){
			Connection conn =null;
			conn = connDB(conn);
			try{
				String sql = "SELECT RCHECK FROM RECIPE_RECIPE WHERE RNO=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, datas[1]);
				
				ResultSet rs = pstmt.executeQuery();
				//�ش� �����ǰ� �������� ���� �Ǿ����� ������� �ʾҴ��� Ȯ���� ���� SELECT��
				
				if(rs.next()){
					if(rs.getString("RCHECK").equals("1")){
						writeSocket("����");
						//�̹� �������� ��� �Ǿ� �־ ���ж�� �޽����� ����
					}else{
						sql = "UPDATE RECIPE_RECIPE SET RCHECK='1' WHERE RNO=?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, datas[1]);
						
						int upd = pstmt.executeUpdate();
						
						writeSocket("����");
						//�������� ��� ���� �ʾ� �����̶�� �޽����� ����
					}
				}
				pstmt.close();
				closeDB(conn);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		//Ŭ���̾�Ʈ�� ���� ���ᰡ �Ǿ��� ��� ���� ���� �������� ó���ϱ� ���Ͽ� ��� �Ǵ� �޼ҵ��
		//Ŀ�ؼ� ����Ʈ���� �ش� Ŭ���̾�Ʈ�� �����ϰ� ����ä���� �ݴ´�.
		void fail(){
			try{
				String message = "[Ŭ���̾�Ʈ ��� �ȵ� : "+socketChannel.getRemoteAddress() + " : "+ Thread.currentThread().getName() + "]";
				Platform.runLater(()->{
					displayText(message);
				});
				connections.remove(Client.this);
				//�ش� Ŭ���̾�Ʈ�� Ŀ�ؼ� ����Ʈ���� ����
				socketChannel.close();
				//�ش� ����ä���� �ݴ´�.
			}catch(Exception e){}
		}
		
		//DB���� ������ ���� �޼ҵ�� connection ������ �����Ѵ�.
		Connection connDB(Connection conn){
			try{
				Class.forName("oracle.jdbc.OracleDriver");
				conn = DriverManager.getConnection("jdbc:oracle:thin:@"+identification.getdbIP(), identification.getdbId() , identification.getdbPassword());
				
			}catch(Exception e){
				e.printStackTrace();
			}
			return conn;
		}
		
		//�����ִ� DB�� ������ ���� ���� �޼ҵ��̴�.
		void closeDB(Connection conn){
			try{
				if(!conn.isClosed())
					conn.close();
				}catch(Exception e){}
		}
		
		//���� ä���� Ŭ���̾�Ʈ�κ��� �޽����� �޾ƿ��� ���� read()�޼ҵ带 �����ϰ� �����ص� �޼ҵ��̴�.
		//������ ũ�⸦ �����ϴ� size�� �Ű������� �޾ƿ��� Ŭ���̾�Ʈ�κ��� �޾ƿ� ���۸� ���ڵ��� String ���� �����Ѵ�.
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
		
		//���� ä���� Ŭ���̾�Ʈ�� �޽�����  ������ ���� write()�޼ҵ带 �����ϰ� �����ص� �޼ҵ��̴�.
		//���� String ���� �Ű������� �޾� ���ڵ��Ͽ� Ŭ���̾�Ʈ�� �����ش�.
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
		});//StartStop ��ư�� �̺�Ʈ ó��
		pane_Server.setBottom(btn_StartStop);
		
		Scene scene_Server = new Scene(pane_Server);
		primaryStage.setScene(scene_Server);
		primaryStage.setTitle("Recipe Server");
		primaryStage.setOnCloseRequest(event -> stopServer());
		//â�ݱ� ���� �̺�Ʈ ó���� ������ �ݴ´�.
		primaryStage.show();
		
	}
	
	//TextArea�� �޽����� append ��Ű�� ���� �޼ҵ�
	public void displayText(String message){
		area_Chat.appendText(message + "\n");
	}
	
	public static void main(String[] args){
		launch(args);
	}

}
