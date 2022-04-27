package application;
	
import javafx.application.Application;
import javafx.application.Platform;
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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class test extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxml = new FXMLLoader(getClass().getResource("ui.fxml"));
			Parent root = fxml.load();
			ControllerRoom controller = (ControllerRoom)fxml.getController();
			Scene scene = new Scene(root,823,534);
			
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
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							HashMap<String, String> command = new HashMap<String, String>();
							command.put("act", "joinMessage");
							command.put("param", "Ÿ�������� �������ؿ�");
							controller.uiControl(command);
							
							HashMap<String, String> command2 = new HashMap<String, String>();
							command2.put("act", "send");
							command2.put("param", "����");
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
							command.put("param", "���λ�");
							controller.uiControl(command);
							
							HashMap<String, String> command2 = new HashMap<String, String>();
							command2.put("act", "send");
							command2.put("param", "���λ�");
							command2.put("msg", "�������� �� �ŵ��̷�!!! ����!!!!!!!!!!!!!!!!!!!!");
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
		System.out.println("ez");
		launch(args);
	}
}
