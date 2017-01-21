package controller;

import java.awt.RenderingHints.Key;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import source.Recipe;
import source.Sessions;

public class RecipeListController implements Initializable {

	@FXML TableView<Recipe> tableView;
	@FXML private TextField search_Text;
	@FXML private ComboBox<String> kind_ComboBox;
	private Sessions sessions;
	private ObservableList<Recipe> recipeList = FXCollections.observableArrayList();
	private ObservableList<Recipe> searchList = FXCollections.observableArrayList();
	
	private String[] datas;
	
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
		
		tableView.setItems(searchList);
		
		kind_ComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
			
			public void changed(ObservableValue<? extends String> arg0, String oldValue, String newValue) {
				Iterator<Recipe> iterator = recipeList.iterator();
				searchList.clear();
				if(newValue.equals("전부보기"))
					setAll();
				else{
					while(iterator.hasNext()){
						Recipe recipe = iterator.next();
						if(recipe.getRKind().equals(newValue)){
							searchList.add(recipe);
						}
					}
					tableView.setItems(searchList);
				}
			}
		});
		
		search_Text.setOnKeyPressed(event->{
			if(event.getCode().equals(KeyCode.ENTER)){
				handle_SearchBtn();
			}
		});
		
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
		});
	}
	
	public void setSession(Sessions sessions){
		this.sessions = sessions;
		String data=sessions.readSocket(3000);
	
		datas = data.split("///");
		setKind(datas);
		setAll();
		
	}
	
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
	
	public void setKind(String ... datas){
		HashSet<String> hs = new HashSet<>();
		for(int i=5;i<datas.length;i=i+8){
			hs.add(datas[i]);
		}
		hs.add("전부보기");
		
		ObservableList<String> kindList = FXCollections.observableArrayList(hs);
		
		kind_ComboBox.setItems(kindList);
		
	}
	
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
	
	public void handle_RegBtn(){
		sessions.alterStage("등록");
	}
	
	public void handle_LogoutBtn(){
		 sessions.alterStage("로그인");
		 sessions.stopSession();
	}
	
}
