package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import java.net.*;
import java.io.*;
import java.util.HashMap;


public class Main extends Application { 
	Socket socket;
	
	String ip = "127.0.0.1";
	int port = 8888;
	
	
	
	public void startClient(String ip, int port) {
		Thread thread = new Thread() {
			public void run() {
				try {
					recv();
				} catch (Exception error) {
					
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
				
				if (freight.get("act") == "") {
					 
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
