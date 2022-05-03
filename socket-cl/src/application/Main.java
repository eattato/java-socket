package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
	public ControllerCreate controllerCreate;
	public Scene sceneMain;
	public Scene sceneRoom;
	public Scene sceneCreate;
	public Stage stage;
	
	public String inputMode = "standard";
	
	public HashMap<String, Scene> scenes = new HashMap<>();
	
	public HashMap<String, String> createSetting = new HashMap<>();
	
	public void startClient(String ip, int port) {
		try {
			System.out.println("���� ���� ���� ��");
			connection = new Socket(ip, port);
			System.out.println("���� ����� ��Ʈ�� �ε� ��");
			// �߿�! inputStream�� ����� outputStream�� ���� ������ ���Ÿ�ٰ� �����Ǳ� ������ input�� �ڿ� �α�
			output = new ObjectOutputStream(connection.getOutputStream());
			input = new ObjectInputStream(connection.getInputStream());
			System.out.println("���� ����� ��Ʈ�� �ε� �Ϸ�");
			recv();
			//System.out.println("Ŭ���̾�Ʈ ���ε� �Ϸ�");
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
	
	// �������� ����
	public void recv() {
		// Ŭ���̾�Ʈ ������ ������ recv ������ۿ� ������ �� ������ Ǯ �� ��
		Thread thread = new Thread() {
			@SuppressWarnings("unchecked")
			public void run() {
				try {
					while (true) {
						// ���� recv �ڵ�� �Ȱ���
						if (inputMode.equals("standard") == true) {
							var freight = (HashMap<String, String>) input.readObject();
							//System.out.println("�����κ��� �Է¹���!");
							
							// �Է� ��� ����
							if (freight.get("act").equals("inputMode") == true) {
								inputMode = freight.get("param");
								System.out.println("�Է� ��� ����: " + inputMode);
							} else {
								if (freight.get("act").equals("join") == true || freight.get("act").equals("leave") == true) {
									if (freight.get("act").equals("join") == true) {
										HashMap<String, String> joinMap = new HashMap<>();
										joinMap.put("act", "join");
										joinMap.put("roomName", freight.get("roomName"));
										joinMap.put("roomSetting", freight.get("roomSetting"));
										joinMap.put("roomCurrent", freight.get("roomCurrent"));
										joinMap.put("roomCapacity", freight.get("roomCapacity"));
										joinMap.put("roomType", freight.get("roomType"));
										controllerRoom.uiControl(joinMap);
										Platform.runLater(new Runnable() {
											@Override
											public void run() {
												stage.setScene(sceneRoom);
												stage.show();
											}
										});
									} else {
										// ������ ������ �ڵ����� ��� Ŭ���̾�Ʈ�� ���ε��ϴ� ���� ���ε� x
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
							// roomSetting ��ü�� ����
							var roomSetting = (ArrayList<HashMap<String, String>>) input.readObject();
							
							// �Է� ��� ���� (�������� �� ������ �� �游 ���� ����� Ű�� inputMode�� �ְ� ���� �ٲ� ���� ������ �ٲ�)
							if (roomSetting.size() >= 1) {
								if (roomSetting.get(0).containsKey("inputMode") && roomSetting.size() == 1 ) {
									inputMode = roomSetting.get(0).get("inputMode");
									System.out.println("�Է� ��� ����: " + inputMode);
								} else {
									HashMap<String, String> reloadMap = new HashMap<>();
									reloadMap.put("act", "reload");
									controllerMain.uiControl(reloadMap);
									
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
					System.out.println("�������� ������ ���������ϴ�.");
				} catch (Exception error) {
					error.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	// ������ ����
	public void send(HashMap<String, String> freight) {
		try {
			//System.out.println(freight.get("act") + "�� ������ ��.. " + LocalTime.now());
			output.writeObject(freight);
			output.flush();
			System.out.println(freight.get("act") + "�� ���� " + LocalTime.now());
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
	
	public void sendImage(Image image) {
		try {
			//System.out.println(freight.get("act") + "�� ������ ��.. " + LocalTime.now());
			output.writeObject(image);
			output.flush();
			System.out.println("�̹����� ���� " + LocalTime.now());
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
		createSetting.put("roomName", "");
		createSetting.put("roomType", "justchat");
		createSetting.put("anonymous", "off");
		createSetting.put("slowmode", "0");
		createSetting.put("fileSend", "off");
		createSetting.put("neologism", "off");
		createSetting.put("oneKillWord", "off");
		createSetting.put("wordTimer", "60");
		createSetting.put("citizenJob", "off");
		createSetting.put("mafiaCount", "1");
		createSetting.put("nickname", "");
		
		try {
			// ���� �� �ε�� ���ÿ� ��Ʈ�ѷ� ��������
			FXMLLoader fxmlMain = new FXMLLoader(getClass().getResource("uiMain.fxml"));
			Parent rootMain = fxmlMain.load();
			//controllerMain = new ControllerMain();
			controllerMain = (ControllerMain) fxmlMain.getController();
			controllerMain.createButton();
			controllerMain.setMain(Main.this);
			//fxmlMain.setController(controllerMain);
			sceneMain = new Scene(rootMain, 823, 534);
			
			FXMLLoader fxmlRoom = new FXMLLoader(getClass().getResource("uiRoom.fxml"));
			Parent rootRoom = fxmlRoom.load();
			controllerRoom = (ControllerRoom) fxmlRoom.getController();
			controllerRoom.setMain(Main.this);
			controllerRoom.control();
			sceneRoom = new Scene(rootRoom, 823, 534);
			
			FXMLLoader fxmlCreate = new FXMLLoader(getClass().getResource("uiCreate.fxml"));
			Parent rootCreate = fxmlCreate.load();
			controllerCreate = (ControllerCreate) fxmlCreate.getController();
			controllerCreate.setMain(Main.this);
			controllerCreate.createButton();
			controllerCreate.cancelButton();
			controllerCreate.settingButtons();
			sceneCreate = new Scene(rootCreate, 823, 534);
			
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
		
		System.out.println("���� ��Ʈ�ѷ� ��� ��..");
		while (controllerMain == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("���� ��Ʈ�ѷ� �ε� �Ϸ�");
		startClient("", 8888);
	}
	
	public static void main(String[] args) throws InterruptedException {
		//startClient("", 8888);
		launch(args);
	}
}
