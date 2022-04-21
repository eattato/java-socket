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
						HashMap<String, String> freight = (HashMap<String, String>) input.readObject();
						
						String act = freight.get("act");
						if (act.equals("join") == true) {
							controller.uiControl(freight);
						}
					}
				} catch (Exception error) {
					System.out.println("�������� ������ ���������ϴ�.");
				}
			}
		};
		thread.start();
	}
	
	// ������ ����
	public void send(HashMap<String, String> freight) {
		Thread thread = new Thread() {
			public void run() {
				try {
					output.writeObject(freight);
					output.flush();
					System.out.println(freight.get("act") + "�� ����");
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
			
			// FXML ����� �׳� �����带 �� ���� ������ FXML�� �����ϴ� runLater ���
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
							command.put("param", "���λ�");
							//controller.uiControl(command);
							
							HashMap<String, String> command2 = new HashMap<String, String>();
							command2.put("act", "send");
							command2.put("param", "���λ�");
							command2.put("msg", "�������� �� �ŵ��̷�!!! ����!!!!!!!!!!!!!!!!!!!!");
							command2.put("effect", "shake 30");
							//controller.uiControl(command2);
							//controller.uiControl(command2);
							
							HashMap<String, String> serverCommand = new HashMap<String, String>();
							serverCommand.put("act", "create");
							serverCommand.put("roomType", "justchat");
							serverCommand.put("roomName", "�����̸� ���� ä��");
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
