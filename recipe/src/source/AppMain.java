package source;

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
		sessions.alterStage("·Î±×ÀÎ");
		
	}
}
