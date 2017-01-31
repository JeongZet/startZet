package controller;

/*
 * 로그인 후에 DB에 등록 되어있는 레시피 리스트들을 TableView에 출력하여 주는 클래스로
 * 등록자ID, 레시피 이름, 요리 재료 등 Recipe 클래스에 SimpleStringProperty 변수들로 저장되어있는 항목들을 출력하여 주며
 * 기능으로는 ComboBox에서 요리 종류를 선택하여 종류에 따른 정렬하여 보여주며
 * 또 검색 기능을 보유하고 있다. 이 페이지 클래스에서 변경 가능한 페이지는 총 5개로
 * 요리 등록 페이지, 선호도 페이지, 레시피 뷰 페이지, 로그인 페이지가 있으며 도전! 요리 버튼을 클릭하면 정식 허용 되어 있지 않은
 * 요리 리스트들의 리스트를 출력하여 주는 해당 페이지의 일부를 수정항 페이지가 있다.
 */

import java.net.URL;
import java.util.*;

import javafx.beans.value.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import source.Recipe;
import source.Sessions;

public class RecipeListController implements Initializable {

	@FXML TableView<Recipe> tableView;					//Recipe 클래스를 출력하여주는 tableView
	@FXML private TextField search_Text;				//찾기 기능을 위한 텍스트 필드
	@FXML private ComboBox<String> kind_ComboBox;		//요리 종류 선택을 위한 ComboBox
	@FXML Button btn_Alter;								//도전 요리 페이지와 정식 요리 페이지 변경을 위한 버튼
	private Sessions sessions;							//전 페이지에서의 세션을 할당 받기 위한 세션
	private ObservableList<Recipe> recipeList = FXCollections.observableArrayList();
	private ObservableList<Recipe> searchList = FXCollections.observableArrayList();
	//두 개의 클래스 리스트를 만든 이유는 처음 받아온 Recipe 클래스들을 찾기나 종류 선택으로 변경되는 것을 막기 위해
	//recipeList에 저장한 값을 searchList에 복사함으로써 searchList 클래스 리스트가 변경되더라도 원본 리스트들은 변경되는 것을
	//막기 위해서이다.
	
	private String[] datas;								//서버로부터 넘어오는 데이터들의 값을 저장할 스트링 배열
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		
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
		//TableView의 TableColumn 들을 선언 및 Recipe 클래스에서 SimpleStringProperty 변수들을 세팅해줌.
		
		tableView.setItems(searchList);
		
		kind_ComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
			
			public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
				Iterator<Recipe> iterator = recipeList.iterator();		//Recipe 클래스 타입의 recipeList 값 반복자 선언 
				searchList.clear();										//전에 할당된 searchList 초기화
				
				if(newValue!=null&&!datas[0].equals("없음")){			//콤보박스에서의 값 선택된 상태에서의 화면전환시의
					if(newValue.equals("전부보기")){					//에러를 처리하기 위한 조건
						setAll();
					}
					else{
						while(iterator.hasNext()){
							Recipe recipe = iterator.next();
							if(recipe.getRKind().equals(newValue)){
								searchList.add(recipe);					//콤보박스에서 선택된 요리 종류와 동일한 것들을
																		//searchList에 추가
							}
						}
						tableView.setItems(searchList);					//tableView에 searchList를 출력
					}
				}
			}
		});//요리 종류 콤보 박스의 값 변경에 의한 이벤트 처리를 위한 함수이다.
		
		
		search_Text.setOnKeyPressed(event->{
			if(event.getCode().equals(KeyCode.ENTER)){
				handle_SearchBtn();
			}
		});//검색 텍스트 필드에서의 엔터 키에 대한 이벤트 처리
		
		tableView.setRowFactory(tc->{
			TableRow row = new TableRow();
			row.setOnMouseClicked(event->{
				if(event.getClickCount()==2&&(!row.isEmpty())){
					try{
						sessions.setRecipe(tableView.getItems().get(row.getIndex()));
						sessions.writeSocket("뷰///"+Integer.toString(sessions.getRecipe().getRNo())+"///"+"1");
						sessions.alterStage("뷰");
						
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
			});
			return row;
		});	//테이블 뷰의 각 로우에 의한 이벤트 처리로 더블 클릭 시 뷰 페이지로 이동하는 것을 나타냄.
			//해당 로우의 레시피 클래스의 정보들을 세션에 저장하며 그 정보들을 서버로 보내면서 뷰 페이지로 이동.
	}
	
	//이전 화면에서의 세션 할당을 위한 메소드
	//세션 할당과 동시에 서버로 부터 레시피 리스트들을 버퍼로 받아와 
	//각 행들을 구분하여 Kind_ComboBox에 해당 레시피들의 요리 종류를 초기화해주고
	//모든 리스트들을 TableView에 등록을 위해 setAll() 메소드를 실행
	public void setSession(Sessions sessions){
		this.sessions = sessions;

		String data=sessions.readSocket(3000);	
		datas = data.split("///");
		setKind(datas);
		kind_ComboBox.getSelectionModel().selectFirst();
		if(!datas[0].equals("없음")){
			setAll();
		}		
	}
	
	//서버로부터 레시피 리스트들을 모두 버퍼로 받아와 Recipe 클래스 객체를 만들어 클래스 리스트들에 할당하여
	//TableView에 등록하여 줌.
	public void setAll(){
		recipeList.clear();
		for(int i=0;i<datas.length;i=i+8){
			Recipe recipe = new Recipe(datas[i+2],datas[i+3],datas[i+4],datas[i+5],datas[i+6],datas[i+7]);
			recipe.setRNo(Integer.parseInt(datas[i]));
			recipe.setScene(Integer.parseInt(datas[i+1]));
			recipeList.add(recipe);
		}
		searchList.addAll(recipeList);
		tableView.setItems(recipeList);
	}
	
	//서버로부터 받아온 데이터들의 요리 종류들을 ComboBox에 중복 정리하여 등록하기 위핸 메소드.
	public void setKind(String ... datas){
		HashSet<String> hs = new HashSet<>();		//HashSet을 이용하여 중복 정리를 한다.(동일한 정보 중복 처리)
		
		hs.add("전부보기");							
		for(int i=5;i<datas.length;i=i+8){
			hs.add(datas[i]);
		}
		
		ObservableList<String> kindList = FXCollections.observableArrayList(hs);
		
		kind_ComboBox.setItems(kindList);			
		
	}
	
	//선호도 버튼 이벤트 처리 메소드
	public void handle_PreferenceBtn(){
		sessions.alterStage("선호도");
		
	}
	
	//찾기 버튼 이벤트 처리 메소드로 검색 텍스트 필드에서 텍스트를 가져와
	//레시피의 모든 항목에 대해서 비교하여 찾아와 검색한다.
	public void handle_SearchBtn(){
		ObservableList<Recipe> tempList = FXCollections.observableArrayList();
		Iterator<Recipe> iterator = searchList.iterator();
		while(iterator.hasNext()){
			Recipe recipe = iterator.next();
			if(recipe.getRItems().contains(search_Text.getText())||
					recipe.getRKind().contains(search_Text.getText())||
					recipe.getRName().contains(search_Text.getText())||
					recipe.getUserID().contains(search_Text.getText())){
				tempList.add(recipe);
			}
		}
		tableView.setItems(tempList);
	}
	
	//도전 요리 or 정식 요리 버튼의 이벤트 처리
	//정식 취급된 레시피와 새로 유저로부터 등록된 요리 리스트를 구분하여 보여준다.
	public void handle_AlterBtn(){
		recipeList.clear();						//현재 저장된 recipeList 초기화
		tableView.setItems(recipeList);			//이미 출력된 TableView 초기화
		kind_ComboBox.setItems(FXCollections.observableArrayList());

		if(btn_Alter.getText().equals("도 전!   요 리")){

			btn_Alter.setText("정식 요리");
			sessions.writeSocket("리스트///0");	
			
		}else if(btn_Alter.getText().equals("정식 요리")){
			
			btn_Alter.setText("도 전!   요 리");
			sessions.writeSocket("리스트///1");
		}
		//0이 뜻하는 것은 레시피 리스트의 DB에 정식 요리와 도전 요리를 구분해주는 
		//RCHECK 컬럼의 값으로 0은 도전요리, 1은 정식요리를 뜻한다.
		
		setSession(sessions);					//새롭게 모든 리스트를 출력하여 주는 메소드들을 할당한 메소드 실행
	}
	
	
	//요리 등록 버튼의 이벤트 처리 메소드
	public void handle_RegBtn(){
		sessions.alterStage("등록");
	}
	
	//로그아웃 버튼의 이벤트 처리 메소드
	public void handle_LogoutBtn(){
		 sessions.alterStage("로그인");
		 sessions.stopSession();
	}
	
}
