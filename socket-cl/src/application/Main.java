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
	public ControllerMain controllerMain;
	public ControllerRoom controllerRoom;
	public Scene sceneMain;
	public Scene sceneRoom;
	public Stage stage;
	
	public HashMap<String, Scene> scenes = new HashMap<>();
	
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
						var freight = (HashMap<String, String>) input.readObject();
						String act = freight.get("act");
						String[] clientActs = {"join", "leave"};
						String[] roomActs = {"joinMessage", "leaveMessage", "msg", "selfmsg"};
						String[] mainActs = {"reload"};
						int ind = 0;
						
						if (ind != -1) {
							for (ind = 0; ind < clientActs.length; ind++) {
								if (clientActs[ind].equals(act) == true) {
									if (clientActs[ind].equals("join") == true) {
										stage.setScene(sceneMain);
										stage.show();
									} else if (clientActs[ind].equals("leave") == true) {
										stage.setScene(sceneRoom);
										stage.show();
									}
									ind = -1;
									break;
								}
							}
						}
						
						if (ind != -1) {
							for (ind = 0; ind < roomActs.length; ind++) {
								if (roomActs[ind].equals(act) == true) {
									controllerRoom.uiControl(freight);
									ind = -1;
									break;
								}
							}
						}
						
						if (ind != -1) {
							for (ind = 0; ind < mainActs.length; ind++) {
								if (mainActs[ind].equals(act) == true) {
									controllerMain.uiControl(freight);
									ind = -1;
									break;
								}
							}
						}
					}
				} catch (SocketException error) {
					System.out.println("서버와의 접속이 끊어졌습니다.");
				} catch (Exception error) {
					error.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	// 서버로 전송
	public void send(HashMap<String, String> freight) {
		try {
			System.out.println(freight);
			System.out.println(output);
			output.writeObject(freight);
			output.flush();
			System.out.println(freight.get("act") + "를 보냄");
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
	
	public FXMLLoader loadScene(String resource) {
		FXMLLoader newLoader = null;
		FXMLLoader fxml = new FXMLLoader(getClass().getResource(resource));
		newLoader = fxml;
		return newLoader;
	}
	public void start(Stage primaryStage) {
		startClient("", 8888);
		
		try {
			// 메인 씬 로드와 동시에 컨트롤러 가져오기
			FXMLLoader fxmlMain = new FXMLLoader(getClass().getResource("uiMain.fxml"));
			Parent rootMain = fxmlMain.load();
			controllerMain = (ControllerMain) fxmlMain.getController();
			sceneMain = new Scene(rootMain, 823, 534);
			
			FXMLLoader fxmlRoom = new FXMLLoader(getClass().getResource("uiRoom.fxml"));
			Parent rootRoom = fxmlRoom.load();
			controllerRoom = (ControllerRoom) fxmlRoom.getController();
			sceneRoom = new Scene(rootRoom, 823, 534);
			
			sceneRoom.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ENTER) {
					var msgSend = new HashMap<String, String>();
					msgSend.put("act", "msg");
					msgSend.put("param", controllerRoom.getTextInput());
					send(msgSend);
				}
			});
			
			primaryStage.setScene(sceneMain);
			primaryStage.show();
			stage = primaryStage;
			
			// room create position
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		//startClient("", 8888);
		launch(args);
	}
}
