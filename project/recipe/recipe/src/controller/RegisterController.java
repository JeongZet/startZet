package controller;

/*
 * 회원 가입 컨트롤러 클래스로
 * 아이디, 비밀번호, 비밀번호 확인, 이름, 번호, 메일, 나이, 성별을 입력할 수 있으며
 * 기능으로는 비밀번호 확인 하는 기능 과 아이디 중복 체크 할 수 있는 기능, 회원 가입 기능이 있다.
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.*;
import javafx.scene.control.*;
import source.Sessions;

public class RegisterController implements Initializable {
	
	private Sessions sessions;									//로그인 유저의 세션
	@FXML private TextField id_Text;							//아이디 텍스트 필드
	@FXML private PasswordField pw_Text;						//패스워드 필드
	@FXML private PasswordField pwcheck_Text;					//패스워드 확인 필드	
	@FXML private TextField name_Text;							//이름 텍스트 필드
	@FXML private TextField tel_Text;							//전화번호 텍스트 필드
	@FXML private TextField mail_Text;							//메일 텍스트 필드
	@FXML private ComboBox<String> age_Box;						//연령 콤보 박스(10대의 배수)
	@FXML private ToggleGroup gender_Group;						//성별 라디오 버튼의 토글 그룹
	private boolean checking=false;								//중복 체크 확인 변수
	
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	//이전 화면에서의 세션 할당을 위한 메소드
	public void setSession(Sessions sessions){
		this.sessions = sessions;
	}
	
	//등록 버튼의 이벤트 처리를 위한 메소드로 서버로 아이디, 비밀번호, 이름 등을 보내고 성공 여부를 읽어온다.
	public void handle_RegBtn(){
		
		if((!id_Text.getText().equals(""))&&(!pw_Text.getText().equals(""))&&(!tel_Text.getText().equals(""))&&
				(!mail_Text.getText().equals(""))&&(gender_Group.getSelectedToggle().getUserData()!=null)&&
				(age_Box.getValue()!=null)&&checking==true&&(pw_Text.getText().equals(pwcheck_Text.getText()))){
		//모든 항목에 대한 공백 방지를 위한 조건문
			String message = "회원가입///"+								
					id_Text.getText()+"///"+
					pw_Text.getText()+"///"+
					name_Text.getText()+"///"+
					tel_Text.getText()+"///"+
					mail_Text.getText()+"///"+
					gender_Group.getSelectedToggle().getUserData()+"///"+
					age_Box.getValue();
			
			sessions.writeSocket(message);			//서버로 회원 가입에 필요한 정보들을 서버로 보낸다.
			
			String data = sessions.readSocket(10);	//서버로부터 성공 여부를 받아온다.
			
			if(data.equals("성공")){
				sessions.popup("회원 가입에 성공하였습니다.");
				sessions.alterStage("로그인");
			}else
				sessions.popup("회원 가입에 실패");
			
		}else if(checking!=true){					//아이디 중복 체크 여부를 위한 조건문
			sessions.popup("아이디 중복 확인을 하십시오.");
		}else if(!pw_Text.getText().equals(pwcheck_Text.getText())){	//비밀번호와 비밀번호 확인의 값이 다를 경우
			sessions.popup("비밀번호가 다릅니다.");
		}else {										//모든 항목을 기입 하지 않았을 경우
			sessions.popup("모든 항목을 기입하셔야 합니다.");
		}
	}
	
	//취소 버튼에 대한 이벤트 처리 메소드
	public void handle_CancelBtn(){
		sessions.alterStage("로그인");
		sessions.stopSession();
	}
	
	//중복 체크 버튼에 대한 이벤트 처리 메소드로 기입한 아이디를 서버로 보낸다.
	//그리고 서버로부터 중복의 여부를 확인하는 메시지를 받아온다.
	public void handle_CheckBtn(){
		
		if(id_Text.getText().equals("")){
			sessions.popup("아이디를 입력하시오.");		//아이디 텍스트 필드가 공백일 경우 팝업창 활성화
		}else{
			
			sessions.connectSocket();				//서버로 버퍼를 보내기 위해 소켓 연결
			
			String message = "중복확인///"+id_Text.getText();
			
			sessions.writeSocket(message);		
			
			String data = sessions.readSocket(10);	//기입한 아이디를 보내고 중복 여부의 버퍼를 받아온다.
			
			if(data.equals("없음")){
				checking=true;
				Platform.runLater(()->sessions.popup("사용할 수 있는 아이디입니다."));
				id_Text.setEditable(false);
			}else if(data.equals("있음")){
				checking=false;
				Platform.runLater(()->sessions.popup("존재하는 아이디입니다."));
			}//아이디 중복에 대한 여부에 따른 팝업창 활성화와 checking 변수 초기화
		}
	}
}
