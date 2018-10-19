package controller;

/*
 * 선호도에 따라 (연령별, 성별) 추천 수가 많은 것의 리스트를 테이블 뷰에 출력하여 주는 클래스이다.
 * 연령 및 성별 선택은 콤보박스로 선택이 가능하다.
 */

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import source.Recipe;
import source.Sessions;

public class PreferenceController implements Initializable {

	@FXML ComboBox<String> age_ComboBox;				//연령별 콤보박스
	@FXML ComboBox<String> gender_ComboBox;				//성별 콤보박스
	@FXML TableView<Recipe> tableView;					//선호도 결과값을 출력해주는 레시피 클래스를 담는 테이블뷰
	private Sessions session;							//이전 페이지에서 세션을 할당 받기 위한 변수
	private ObservableList<Recipe> recipeList = FXCollections.observableArrayList();
	//선호도 결과값을 저장할 공간
	
	//이전 페이지로부터 세션을 Setting 하기 위한 메소드
	public void setSession(Sessions session){
		this.session=session;
		
		setComboBox();			//콤보 박스의 연령이나 성별을 구성하기 위한 메소드 호출
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		TableColumn tcUserID = tableView.getColumns().get(0);
		TableColumn tcRName = tableView.getColumns().get(1);
		TableColumn tcRItems = tableView.getColumns().get(2);
		TableColumn tcRKind = tableView.getColumns().get(3);
		TableColumn tcRRecommend = tableView.getColumns().get(4);
		TableColumn tcRComment = tableView.getColumns().get(5);

		tcUserID.setCellValueFactory(new PropertyValueFactory("userID"));
		tcRName.setCellValueFactory(new PropertyValueFactory("rName"));
		tcRItems.setCellValueFactory(new PropertyValueFactory("rItems"));
		tcRKind.setCellValueFactory(new PropertyValueFactory("rKind"));
		tcRRecommend.setCellValueFactory(new PropertyValueFactory("rRecommend"));
		tcRComment.setCellValueFactory(new PropertyValueFactory("rComment"));
		//테이블 뷰의 컬럼들에 값을 할당하고 Recipe 클래스이 SimpleStringProperty 값들을 각각 할당	
		
		tableView.setItems(recipeList);
		
		age_ComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){

			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				recipeList.clear();		//이전 결과 값을 모두 초기화 하여 준다.(중복 출력 될 수 있기 때문에)
				session.writeSocket("선호도///"+newValue+"///"+gender_ComboBox.getSelectionModel().selectedItemProperty().getValue());
				//콤보박스에서 선택된 값 newValue와 현재 성별 콤보박스에서 선택된 값을 서버로 보낸다.
				String data = session.readSocket(3000);
				//그에 맞는 값들을 서버로부터 읽어온다.
				String[] datas = data.split("///");
				if(!datas[0].equals("없음")){
					for(int i=0;i<datas.length;i+=6){
						recipeList.add(new Recipe(datas[i], datas[i+1], datas[i+2], datas[i+3], datas[i+4], datas[i+5]));
					}
					tableView.setItems(recipeList);
				}//읽어온 값들을 리스트에 추가하며 그 리스트를 테이블 뷰에 세팅하여 출력하여 준다.
			}
		});//연령별 콤보박스의 값이 선택 시의 이벤트 처리 하는 부분이다.
		
		gender_ComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){

			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				recipeList.clear();
				session.writeSocket("선호도///"+age_ComboBox.getSelectionModel().selectedItemProperty().getValue()+"///"+newValue);
				String data = session.readSocket(1000);
				String[] datas = data.split("///");
				if(!datas[0].equals("없음")){
					for(int i=0;i<datas.length;i+=6){
						recipeList.add(new Recipe(datas[i], datas[i+1], datas[i+2], datas[i+3], datas[i+4], datas[i+5]));
					}
					tableView.setItems(recipeList);
				}
			}
		});//성별 콤보박스의 값이 선택 시의 이벤트 처리 하는 부분이다.
		
	}
	
	//콤보 박스의 값들을 할당하여 주는 메소드로 레시피들 추천했던 유저들의 성별과 연령을 기준으로 값을 받아온다.
	public void setComboBox(){
		
		session.writeSocket("선호도콤보박스");		//서버로부터 값을 요청하기 위해 데이터를 보낸다.
		
		String data = session.readSocket(1000);	//서버로부터 온 값들을 받아온다.(연령과 성별)
		
		String[] datas = data.split("///");
		
		ObservableList<String> ageList = FXCollections.observableArrayList();
		ObservableList<String> genderList = FXCollections.observableArrayList();
		//성별과 연령별로 저장할 리스트를 선언한다.
		
		for(int i=0;i<datas.length;i+=2){
			ageList.add(datas[i]);
			genderList.add(datas[i+1]);
		}//리스트들에 해당 값들을 저장한다.
		
		ageList.add("전체 보기");
		genderList.add("전체 보기");
		//전체보기를 선택할 경우를 위해 전체 보기라는 키워드도 저장
		
		age_ComboBox.setItems(ageList);
		gender_ComboBox.setItems(genderList);
		//두 리스트들을 각각의 콤보박스에 세팅한다.
		
	}
	
	//이전 버튼에 대한 이벤트 처리 메소드
	public void handle_PriorBtn(){

		session.writeSocket("리스트///1");
		session.alterStage("리스트");
	}
	
}
