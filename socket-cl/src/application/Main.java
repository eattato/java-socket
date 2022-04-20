package application;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Main extends Application { 
	static Socket connection;
	
	String ip = "127.0.0.1";
	int port = 8888;
	
	public static void startClient(String ip, int port) {
		try {
			connection = new Socket(ip, port);
			recv();
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
	
	public void shutdown() {
		try {
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 서버에서 받음
	public static void recv() {
		// 클라이언트 측에선 어차피 recv 스레드밖에 없으니 걍 스레드 풀 안 씀
		Thread thread = new Thread() {
			public void run() {
				try {
					while (true) {
						// 서버 recv 코드랑 똑같음. 
						InputStream input = connection.getInputStream();
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
		};
		thread.start();
	}
	
	// 서버로 전송
	public void send(HashMap<String, String> freight) {
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream output = connection.getOutputStream();
					ObjectOutputStream outputOBJ = new ObjectOutputStream(output);
					
					outputOBJ.writeObject(freight);
					outputOBJ.flush();
				} catch (Exception error) {
					error.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxml = new FXMLLoader(getClass().getResource("ui.fxml"));
			Parent root = fxml.load();
			Controller controller = (Controller)fxml.getController();
			Scene scene = new Scene(root, 823, 534);
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// FXML 제어에는 그냥 스레드를 못 쓰기 때문에 FXML이 지원하는 runLater 사용
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							HashMap<String, String> command = new HashMap<String, String>();
							command.put("act", "joinMessage");
							command.put("param", "타지리리님 한판판해요");
							controller.uiControl(command);
							
							HashMap<String, String> command2 = new HashMap<String, String>();
							command2.put("act", "send");
							command2.put("param", "케인");
							command2.put("msg", "gway joy go");
							controller.uiControl(command2);
							//controller.uiControl(command2);
						}
					});
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							HashMap<String, String> command = new HashMap<String, String>();
							command.put("act", "joinMessage");
							command.put("param", "과로사");
							controller.uiControl(command);
							
							HashMap<String, String> command2 = new HashMap<String, String>();
							command2.put("act", "send");
							command2.put("param", "과로사");
							command2.put("msg", "오마에와 모 신데이루!!! 나니!!!!!!!!!!!!!!!!!!!!");
							command2.put("effect", "shake 30");
							controller.uiControl(command2);
							//controller.uiControl(command2);
						}
					});
				}
			};
			thread.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		startClient("", 8888);
		launch(args);
	}
}
