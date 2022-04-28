package application;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ControllerMain {
	@FXML
	private TextField typer;
	
	@FXML
	private ScrollPane scroll;
	
	@FXML
	private Pane scrollFrame;
	
	@FXML
	private Button dict;
	
	@FXML
	private Button leave;
	
	@FXML
	private Button send;
	
	@FXML
	private Text roomName;
	
	@FXML
	private Text roomSetting;
	
	@FXML
	private Text roomAdmits;
	
	private ArrayList<Pane> scrollObjects = new ArrayList<Pane>();
	
	private float currentScroll = 77;
	
	// UI 제어
	public void scrollUp(int move) {
		// 스크롤에 있는 모든 오브젝트 move만큼 위로 이동
		Iterator<Pane> scrollIter = scrollObjects.iterator();
		while (scrollIter.hasNext() == true) {
			Pane scrollObject = scrollIter.next();
			scrollObject.setLayoutY(scrollObject.getLayoutY() + move);
			scrollIter.remove();
		}
	}
	
	public String getTextInput() {
		return typer.getText();
	}
	
	public void uiControl(HashMap<String, String> command) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					String act = command.get("act");
					String param = command.get("param");
					String msg = command.get("msg");
					String effect = command.get("effect");
					if (act.equals("room") == true) {
						for (int ind = 0; ind < scrollObjects.size(); ind++) {
							scrollFrame.getChildren().remove(scrollObjects.get(ind));
						}
						scrollObjects = new ArrayList<Pane>();
						
						//scrollUp(60); // 전에 있던 UI 오브젝트들 모두 세로 사이즈만큼 위로
						Pane roomPane = new Pane(); // 새 Pane 생성
						roomPane.setLayoutX(0);
						roomPane.setLayoutY(currentScroll);
						roomPane.setPrefWidth(816);
						roomPane.setPrefHeight(77);
						roomPane.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-width: 0 0 2 0;");
						scrollFrame.getChildren().add(roomPane);
						Text roomName = new Text();
						roomName.setLayoutX(70);
						roomName.setLayoutY(23);
						roomName.setWrappingWidth(223.6513671875);
						roomName.setText(command.get("roomName"));
						roomName.setFont(new Font("Hancom Gothic Regular", 20.0));
						roomPane.getChildren().add(roomName);
						Pane roomProfile = new Pane();
						roomProfile.setLayoutX(14);
						roomProfile.setLayoutY(14);
						roomProfile.setPrefWidth(50);
						roomProfile.setPrefHeight(50);
						roomProfile.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: lightgray; -fx-border-radius: 15;");
						roomPane.getChildren().add(roomProfile);
						Text roomType = new Text();
						roomType.setLayoutX(83);
						roomType.setLayoutY(47);
						roomType.setWrappingWidth(223.6513671875);
						if (command.get("roomType").equals("justchat") == true) {
							roomType.setText("채팅");							
						} else if (command.get("roomType").equals("mafia") == true) {
							roomType.setText("마피아");
						} else if (command.get("roomType").equals("wordbomb") == true) {
							roomType.setText("끝말잇기");
						}
						roomType.setFont(new Font("Hancom Gothic Regular", 18.0));
						roomPane.getChildren().add(roomType);
						Text roomSetting = new Text();
						roomSetting.setLayoutX(84);
						roomSetting.setLayoutY(64);
						roomSetting.setWrappingWidth(223.6513671875);
						roomSetting.setFont(new Font("Hancom Gothic Regular", 14.0));
						roomSetting.setText("설정 없음");
						roomPane.getChildren().add(roomSetting);
						Text roomCapacity = new Text();
						roomCapacity.setLayoutX(707);
						roomCapacity.setLayoutY(22);
						roomCapacity.setWrappingWidth(87.0);
						roomCapacity.setFont(new Font("Hancom Gothic Regular", 18.0));
						roomCapacity.setText(command.get("current") + " / " + command.get("roomCapacity"));
						roomCapacity.setTextAlignment(TextAlignment.CENTER);
						roomPane.getChildren().add(roomCapacity);
						Button roomJoin = new Button();
						roomJoin.setLayoutX(707);
						roomJoin.setLayoutY(29);
						roomJoin.setPrefWidth(87);
						roomJoin.setPrefHeight(40);
						roomJoin.setText("참가");
						roomJoin.setStyle("-fx-background-color: ghostwhite; -fx-background-radius: 15; -fx-border-color: lightgray; -fx-border-radius: 15;");
						roomJoin.setFont(new Font("Hancom Gothic Regular", 20.0));
						
						roomJoin.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								
							}
						});
						roomPane.getChildren().add(roomJoin);
						
						scrollObjects.add(roomPane);
						currentScroll += 77;
					}
				} catch (Exception error) {
					error.printStackTrace();
				}
			}
		});
	}
}