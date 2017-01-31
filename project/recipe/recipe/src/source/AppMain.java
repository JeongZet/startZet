package source;

/*
 * 앱 메인 화면의 시작 부분으로 화면을 띄워주기 위한
 * 클래스이다.
 */

import javafx.application.Application;
import javafx.stage.Stage;

public class AppMain extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Sessions sessions = new Sessions();
		sessions.setStage(primaryStage);
		sessions.alterStage("로그인");
		
	}
	//실행 시 새로운 세션을 만들고 해당 스테이지를 할당
}
