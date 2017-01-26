package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import source.Recipe;
import source.Sessions;

public class PreferenceController implements Initializable {

	@FXML ComboBox<String> age_ComboBox;
	@FXML ComboBox<String> gender_ComboBox;
	@FXML TableView<Recipe> tableView;
	private Sessions session;
	private ObservableList<Recipe> recipeList = FXCollections.observableArrayList();
	
	public void setSession(Sessions session){
		this.session=session;
		
		setComboBox();
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
			
		tableView.setItems(recipeList);
		
		age_ComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){

			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				recipeList.clear();
				session.writeSocket("선호도///"+newValue+"///"+gender_ComboBox.getSelectionModel().selectedItemProperty().getValue());

				String data = session.readSocket(1000);
				String[] datas = data.split("///");
				if(!datas[0].equals("없음")){
					for(int i=0;i<datas.length;i+=6){
						recipeList.add(new Recipe(datas[i], datas[i+1], datas[i+2], datas[i+3], datas[i+4], datas[i+5]));
					}
					tableView.setItems(recipeList);
				}
			}
		});
		
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
			
		});
		
	}
	
	public void setComboBox(){
		
		session.writeSocket("선호도콤보박스");
		
		String data = session.readSocket(1000);
		
		String[] datas = data.split("///");
		
		ObservableList<String> ageList = FXCollections.observableArrayList();
		ObservableList<String> genderList = FXCollections.observableArrayList();
		
		for(int i=0;i<datas.length;i+=2){
			ageList.add(datas[i]);
			genderList.add(datas[i+1]);
		}
		
		ageList.add("전체 보기");
		genderList.add("전체 보기");

		age_ComboBox.setItems(ageList);
		gender_ComboBox.setItems(genderList);
		
	}
	
	public void handle_PriorBtn(){

		session.writeSocket("리스트///1");
		session.alterStage("리스트");
	}
	
}
