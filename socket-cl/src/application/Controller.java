package application;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class Controller {
	@FXML
	private TextArea typer;
	
	@FXML
	private ScrollPane scroll;
	
	@FXML
	private Pane scrollFrame;
	
	@FXML
	private Button dict;
	
	@FXML
	private Button leave;
	
	@FXML
	private Text roomName;
	
	@FXML
	private Text roomSetting;
	
	@FXML
	private Text roomAdmits;
	
	private ArrayList<Pane> scrollObjects = new ArrayList<Pane>();
	
	private float currentScroll = 0;
	
	private String latest = "System";
	
	// UI ����
	public void scrollUp(int move) {
		// ��ũ�ѿ� �ִ� ��� ������Ʈ move��ŭ ���� �̵�
		Iterator<Pane> scrollIter = scrollObjects.iterator();
		while (scrollIter.hasNext() == true) {
			Pane scrollObject = scrollIter.next();
			scrollObject.setLayoutY(scrollObject.getLayoutY() + move);
			scrollIter.remove();
		}
	}
	
	public void uiControl(HashMap<String, String> command) {
		try {
			String act = command.get("act");
			String param = command.get("param");
			//String msg = command.get("msg");
			if (act.equals("joinMessage") == true || act.equals("leaveMessage") == true) {
				currentScroll += 62;
				latest = "System";
				//scrollUp(60); // ���� �ִ� UI ������Ʈ�� ��� ���� �����ŭ ����
				Pane joinPane = new Pane(); // �� Pane ����
				joinPane.setLayoutX(0);
				joinPane.setLayoutY(currentScroll);
				joinPane.setPrefWidth(816);
				joinPane.setPrefHeight(62);
				scrollFrame.getChildren().add(joinPane);
				Label joinLabel = new Label();
				if (act.equals("joinMessage") == true) {
					joinLabel.setText(param + " ���� �����Ͽ����ϴ�.");
				} else {
					joinLabel.setText(param + " ���� �����Ͽ����ϴ�.");
				}
				joinLabel.setAlignment(Pos.CENTER); // �� ��� ����
				joinLabel.setLayoutX(144);
				joinLabel.setLayoutY(16);
				joinLabel.setPrefWidth(524);
				joinLabel.setPrefHeight(31);
				joinLabel.setStyle("-fx-background-color: #00000033; -fx-background-radius: 10;");
				joinPane.getChildren().add(joinLabel);
				scrollObjects.add(joinPane);
			} else if (act.equals("send") == true || act.equals("recv") == true) {
				Pane chatPane = new Pane();
				chatPane.setPrefWidth(816);
				Label chatLabel = new Label();
				chatLabel.setPrefHeight(42);
				
				// ����
				if (param.equals("Eattato") == true) {
					
				} else {
					chatLabel.setLayoutX(70);
				}
				
				// ����� ��ǳ��
				if (param.equals(param) == true) {
					currentScroll += 50;
					chatPane.setPrefHeight(50);
				} else {
					// ������ ���� ��ǳ��
					latest = param;
					currentScroll += 77;
					chatPane.setPrefHeight(77);
				}
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}