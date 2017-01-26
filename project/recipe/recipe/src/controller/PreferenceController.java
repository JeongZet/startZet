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
	
	public void setSession(Sessions session){
		this.session=session;
		
		setComboBox();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		TableColumn tcUserID = tableView.getColumns().get(0);
		TableColumn tcRName = tableView.getColumns().get(1);
		TableColumn tcRItem = tableView.getColumns().get(2);
		TableColumn tcRKind = tableView.getColumns().get(3);
		TableColumn tcRRecommend = tableView.getColumns().get(4);
		TableColumn tcRComment = tableView.getColumns().get(5);

		tcUserID.setCellValueFactory(new PropertyValueFactory("작성자"));
		tcRName.setCellValueFactory(new PropertyValueFactory("요리 이름"));
		tcRItem.setCellValueFactory(new PropertyValueFactory("요리 재료"));
		tcRKind.setCellValueFactory(new PropertyValueFactory("종류"));
		tcRRecommend.setCellValueFactory(new PropertyValueFactory("추천"));
		tcRComment.setCellValueFactory(new PropertyValueFactory("댓글"));
		
		age_ComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){

			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				session.writeSocket("선호도///"+newValue);
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
		
		ageList.add("전부보기");
		genderList.add("전부보기");

		age_ComboBox.setItems(ageList);
		gender_ComboBox.setItems(genderList);
		
	}
	
	public void handle_PriorBtn(){

		session.writeSocket("리스트");
		session.alterStage("리스트");
	}
	
}
