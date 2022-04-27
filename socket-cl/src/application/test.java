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
