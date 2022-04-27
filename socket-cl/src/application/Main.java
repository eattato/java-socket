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
			System.out.println("���� ���� ���� ��");
			connection = new Socket(ip, port);
			System.out.println("���� ����� ��Ʈ�� �ε� ��");
			// �߿�! inputStream�� ����� outputStream�� ���� ������ ���Ÿ�ٰ� �����Ǳ� ������ input�� �ڿ� �α�
			output = new ObjectOutputStream(connection.getOutputStream());
			input = new ObjectInputStream(connection.getInputStream());
			System.out.println("���� ����� ��Ʈ�� �ε� �Ϸ�");
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
	
	// �������� ����
	public void recv() {
		// Ŭ���̾�Ʈ ������ ������ recv ������ۿ� ������ �� ������ Ǯ �� ��
		Thread thread = new Thread() {
			public void run() {
				try {
					while (true) {
						// ���� recv �ڵ�� �Ȱ���. 
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
			System.out.println(freight);
			System.out.println(output);
			output.writeObject(freight);
			output.flush();
			System.out.println(freight.get("act") + "�� ����");
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
			// ���� �� �ε�� ���ÿ� ��Ʈ�ѷ� ��������
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
