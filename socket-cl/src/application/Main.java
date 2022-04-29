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
import java.time.LocalTime;
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
	
	public String inputMode = "standard";
	
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
			//System.out.println("클라이언트 리로드 완료");
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
			@SuppressWarnings("unchecked")
			public void run() {
				try {
					while (true) {
						// 서버 recv 코드랑 똑같음
						if (inputMode.equals("standard") == true) {
							var freight = (HashMap<String, String>) input.readObject();
							//System.out.println("서버로부터 입력받음!");
							
							// 입력 모드 변경
							if (freight.get("act").equals("inputMode") == true) {
								inputMode = freight.get("param");
								System.out.println("입력 모드 변경: " + inputMode);
							} else {
								if (freight.get("act").equals("join") == true || freight.get("act").equals("leave") == true) {
									if (freight.get("act").equals("join") == true) {
										HashMap<String, String> joinMap = new HashMap<>();
										joinMap.put("act", "join");
										joinMap.put("roomName", freight.get("roomName"));
										joinMap.put("roomSetting", freight.get("roomSetting"));
										joinMap.put("roomCurrent", freight.get("roomCurrent"));
										joinMap.put("roomCapacity", freight.get("roomCapacity"));
										controllerRoom.uiControl(joinMap);
										Platform.runLater(new Runnable() {
											@Override
											public void run() {
												stage.setScene(sceneRoom);
												stage.show();
											}
										});
									} else {
										// 나가면 서버가 자동으로 모든 클라이언트에 리로드하니 굳이 리로드 x
										Platform.runLater(new Runnable() {
											@Override
											public void run() {
												stage.setScene(sceneMain);
												stage.show();
											}
										});
									}
								} else {
									controllerRoom.uiControl(freight);
								}
							}
						} else if (inputMode.equals("rooms") == true) {
							// roomSetting 객체를 받음
							var roomSetting = (ArrayList<HashMap<String, String>>) input.readObject();
							
							// 입력 모드 변경 (서버에서 룸 세팅을 한 방만 새로 만들고 키는 inputMode만 넣고 값은 바꿀 모드로 넣으면 바뀜)
							if (roomSetting.size() >= 1) {
								if (roomSetting.get(0).containsKey("inputMode") && roomSetting.size() == 1 ) {
									inputMode = roomSetting.get(0).get("inputMode");
									System.out.println("입력 모드 변경: " + inputMode);
								} else {
									for (int ind = 0; ind < roomSetting.size(); ind++) {
										HashMap<String, String> roomMap = roomSetting.get(ind);
										roomMap.put("act", "room");
										controllerMain.uiControl(roomMap);
									}
									
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
			//System.out.println(freight.get("act") + "를 보내는 중.. " + LocalTime.now());
			output.writeObject(freight);
			output.flush();
			System.out.println(freight.get("act") + "를 보냄 " + LocalTime.now());
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
		try {
			// 메인 씬 로드와 동시에 컨트롤러 가져오기
			FXMLLoader fxmlMain = new FXMLLoader(getClass().getResource("uiMain.fxml"));
			Parent rootMain = fxmlMain.load();
			//controllerMain = new ControllerMain();
			controllerMain = (ControllerMain) fxmlMain.getController();
			controllerMain.setMain(Main.this);
			//fxmlMain.setController(controllerMain);
			sceneMain = new Scene(rootMain, 823, 534);
			
			FXMLLoader fxmlRoom = new FXMLLoader(getClass().getResource("uiRoom.fxml"));
			Parent rootRoom = fxmlRoom.load();
			controllerRoom = (ControllerRoom) fxmlRoom.getController();
			controllerRoom.setMain(Main.this);
			controllerRoom.leaveButton();
			sceneRoom = new Scene(rootRoom, 823, 534);
			
			sceneRoom.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ENTER) {
					var msgSend = new HashMap<String, String>();
					msgSend.put("act", "msg");
					msgSend.put("param", controllerRoom.getTextInput());
					send(msgSend);
					controllerRoom.resetTextInput();
				}
			});
			
			primaryStage.setScene(sceneMain);
			primaryStage.show();
			stage = primaryStage;
			
			// room create position
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("메인 컨트롤러 대기 중..");
		while (controllerMain == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("메인 컨트롤러 로드 완료");
		startClient("", 8888);
	}
	
	public static void main(String[] args) throws InterruptedException {
		//startClient("", 8888);
		launch(args);
	}
}
