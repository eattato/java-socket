package application;
	
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Main extends Application { 
	Socket socket;
	
	String ip = "127.0.0.1";
	int port = 8888;
	
	
	@FXML
	private TextArea typer;
	
	@FXML
	private ScrollPane scroll;
	
	@FXML
	private Pane scrollFrame;
	
	@FXML
	private Button dict;
	
	@FXML
	private Button leave;
	
	@FXML
	private Text roomName;
	
	@FXML
	private Text roomSetting;
	
	@FXML
	private Text roomAdmits;
	
	
	private ArrayList<Pane> scrollObjects = new ArrayList<Pane>();
	
	public void startClient(String ip, int port) {
		Thread thread = new Thread() {
			public void run() {
				try {
					recv();
				} catch (Exception error) {
					error.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	public void shutdown() {
		
	}
	
	// 서버에서 받음
	public void recv() {
		try {
			while (true) {
				// 서버 recv 코드랑 똑같음. 
				InputStream input = socket.getInputStream();
				ObjectInputStream inputOBJ = new ObjectInputStream(input);
				@SuppressWarnings("unchecked")
				HashMap<String, String> freight = (HashMap<String, String>) inputOBJ.readObject();
				
				if (freight.get("act") == "join") {
					 
				}
			}
		} catch (Exception error) {
			System.out.println("서버와의 접속이 끊어졌습니다.");
		}
	}
	
	// 서버로 전송
	public void send(HashMap<String, String> freight) {
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream output = socket.getOutputStream();
					ObjectOutputStream outputOBJ = new ObjectOutputStream(output);
					
					outputOBJ.writeObject(freight);
					outputOBJ.flush();
				} catch (Exception error) {
					
				}
			}
		};
	}
	
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
	
	public void uiControl(HashMap<String, String> command) {
		String act = command.get("act");
		String param = command.get("param");
		String msg = command.get("msg");
		if (act.equals("joinMessage") == true || act.equals("leaveMessage") == true) {
			scrollUp(60); // 전에 있던 UI 오브젝트들 모두 세로 사이즈만큼 위로
			Pane joinPane = new Pane(); // 새 Pane 생성
			joinPane.setLayoutX(144);
			joinPane.setLayoutY(15);
			joinPane.setPrefWidth(816);
			joinPane.setPrefHeight(60);
			Label joinLabel = new Label();
			if (act.equals("joinMessage") == true) {
				joinLabel.setText(param + " 님이 입장하였습니다.");
			} else {
				joinLabel.setText(param + " 님이 퇴장하였습니다.");
			}
			joinLabel.setAlignment(Pos.CENTER); // 라벨 가운데 정렬
			joinLabel.setLayoutX(144);
			joinLabel.setLayoutY(16);
			joinLabel.setPrefWidth(524);
			joinLabel.setPrefHeight(31);
			joinLabel.setStyle("-fx-background-color: #00000033; -fx-background-radius: 10;");
			joinPane.getChildren().add(joinLabel);
			scrollFrame.getChildren().add(joinPane);
			scrollObjects.add(joinPane);
		} else if (act.equals("send") == true || act.equals("recv") == true) {
			
		}
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("ui.fxml"));
			Scene scene = new Scene(root,800,400);
			
			primaryStage.setScene(scene);
			primaryStage.show();			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
