package application;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.net.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Main extends Application { 
	static Socket connection;
	
	String ip = "127.0.0.1";
	int port = 8888;
	
	public ObjectInputStream input;
	public ObjectOutputStream output;
	public Controller controller;
	
	public void startClient(String ip, int port) {
		try {
			System.out.println("서버 소켓 연결 중");
			connection = new Socket(ip, port);
			System.out.println("서버 입출력 스트림 로딩 중");
			// 중요! inputStream은 연결된 outputStream이 나올 때까지 대기타다가 생성되기 때문에 input을 뒤에 두기
			output = new ObjectOutputStream(connection.getOutputStream());
			input = new ObjectInputStream(connection.getInputStream());
			System.out.println("서버 입출력 스트림 로딩 완료");
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
	public void recv() {
		// 클라이언트 측에선 어차피 recv 스레드밖에 없으니 걍 스레드 풀 안 씀
		Thread thread = new Thread() {
			public void run() {
				try {
					while (true) {
						// 서버 recv 코드랑 똑같음. 
						@SuppressWarnings("unchecked")
						HashMap<String, String> freight = (HashMap<String, String>) input.readObject();
						
						String act = freight.get("act");
						if (act.equals("join") == true) {
							controller.uiControl(freight);
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
					output.writeObject(freight);
					output.flush();
					System.out.println(freight.get("act") + "를 보냄");
				} catch (Exception error) {
					error.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	public void start(Stage primaryStage) {
		startClient("", 8888);
		
		try {
			FXMLLoader fxml = new FXMLLoader(getClass().getResource("ui.fxml"));
			Parent root = fxml.load();
			controller = (Controller)fxml.getController();
			Scene scene = new Scene(root, 823, 534);
			
			scene.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ENTER) {
					HashMap<String, String> msgSend = new HashMap<String, String>();
					msgSend.put("act", "msg");
					msgSend.put("msg", controller.getTextInput());
					send(msgSend);
				}
			});
			
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
							//controller.uiControl(command);
							
							HashMap<String, String> command2 = new HashMap<String, String>();
							command2.put("act", "send");
							command2.put("param", "과로사");
							command2.put("msg", "오마에와 모 신데이루!!! 나니!!!!!!!!!!!!!!!!!!!!");
							command2.put("effect", "shake 30");
							//controller.uiControl(command2);
							//controller.uiControl(command2);
							
							HashMap<String, String> serverCommand = new HashMap<String, String>();
							serverCommand.put("act", "create");
							serverCommand.put("roomType", "justchat");
							serverCommand.put("roomName", "뭉탱이를 위한 채팅");
							send(serverCommand);
							
							HashMap<String, String> joinCommand = new HashMap<String, String>();
							joinCommand.put("act", "join");
							joinCommand.put("param", "0");
							send(joinCommand);
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
		//startClient("", 8888);
		launch(args);
	}
}
