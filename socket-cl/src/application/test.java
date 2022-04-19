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
			Controller controller = (Controller)fxml.getController();
			Scene scene = new Scene(root,800,400);
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// FXML ����� �׳� �����带 �� ���� ������ FXML�� �����ϴ� runLater ���
			Thread thread = new Thread() {
				@Override
				public void run() {
					System.out.println("���̰�2");
					try {
						Thread.sleep(5000);
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
							System.out.println("���̰�4");
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
