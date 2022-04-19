package application;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

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
			String msg = command.get("msg");
			if (act.equals("joinMessage") == true || act.equals("leaveMessage") == true) {
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
				joinLabel.setFont(new Font("Hancom Gothic Regular", 15.0));
				joinPane.getChildren().add(joinLabel);
				scrollObjects.add(joinPane);
				
				currentScroll += 62;
			} else if (act.equals("send") == true || act.equals("recv") == true) {
				Pane chatPane = new Pane();
				chatPane.setPrefWidth(816);
				chatPane.setLayoutY(currentScroll);
				Label chatLabel = new Label();
				chatLabel.setAlignment(Pos.CENTER);
				chatLabel.setFont(new Font("Hancom Gothic Regular", 20.0));
				chatLabel.setPrefWidth(50);
				chatLabel.setPrefHeight(42);
				
				// ����
				boolean isSelf = false;
				if (param.equals("Eattato") == true) {
					isSelf = true;
					chatLabel.setLayoutX(684);
					chatLabel.setStyle("-fx-background-color: yellow; -fx-background-radius: 15;");
					
				} else {
					System.out.println("���� �޼��� �ƴ�");
					isSelf = false;
					chatLabel.setLayoutX(70);
					chatLabel.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
				}
				
				// ����� ��ǳ��
				if (param.equals(latest) == true) {
					System.out.println("ù ���ھƴ�");
					currentScroll += 50;
					chatLabel.setLayoutY(4);
					chatPane.setPrefHeight(50);
				} else {
					// ������ ���� ��ǳ��
					System.out.println("ù ������");
					latest = param;
					currentScroll += 77;
					chatLabel.setLayoutY(33);
					chatPane.setPrefHeight(77);
					
					Text author = new Text();
					Pane profileFrame = new Pane();
					if (isSelf == false) {
						author.setLayoutX(70);
						profileFrame.setLayoutX(14);
					} else {
						author.setTextAlignment(TextAlignment.RIGHT);
						author.setLayoutX(511);
						profileFrame.setLayoutX(743);
					}
					author.setLayoutY(23);
					author.setText(param);
					author.setStrokeType(StrokeType.OUTSIDE);
					author.setWrappingWidth(223);
					author.setFont(new Font("Hancom Gothic Regular", 20.0));
					chatPane.getChildren().add(author);
					
					profileFrame.setLayoutY(14);
					profileFrame.setPrefWidth(48);
					profileFrame.setPrefHeight(50);
					profileFrame.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
					chatPane.getChildren().add(profileFrame);
				}
				//chatLabel.setText(msg);
				
				chatPane.getChildren().add(chatLabel);
				scrollFrame.getChildren().add(chatPane);
				scrollObjects.add(chatPane);
				
				String[] msgSplit = msg.split("");
				Thread thread = new Thread() {
					@Override
					public void run() {
						for (int ind = 0; ind < msgSplit.length; ind++ ) {
							final int indexFinal = ind;
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Platform.runLater(() -> {
								chatLabel.setText(chatLabel.getText() + msgSplit[indexFinal]);
								double currentTextWidth = new Text(chatLabel.getText()).getLayoutBounds().getWidth();
								chatLabel.setPrefWidth(currentTextWidth * (15 / 12 * 1.5) + 50);
							});
						}
					}
				};
				thread.start();
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}