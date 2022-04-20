package application;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Controller {
	@FXML
	private TextField typer;
	
	@FXML
	private ScrollPane scroll;
	
	@FXML
	private Pane scrollFrame;
	
	@FXML
	private Button dict;
	
	@FXML
	private Button leave;
	
	@FXML
	private Button send;
	
	@FXML
	private Text roomName;
	
	@FXML
	private Text roomSetting;
	
	@FXML
	private Text roomAdmits;
	
	private ArrayList<Pane> scrollObjects = new ArrayList<Pane>();
	
	private float currentScroll = 0;
	
	private String latest = "System";
	
	// UI 제어
	public void scrollUp(int move) {
		// 스크롤에 있는 모든 오브젝트 move만큼 위로 이동
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
			String effect = command.get("effect");
			if (act.equals("joinMessage") == true || act.equals("leaveMessage") == true) {
				latest = "System";
				//scrollUp(60); // 전에 있던 UI 오브젝트들 모두 세로 사이즈만큼 위로
				Pane joinPane = new Pane(); // 새 Pane 생성
				joinPane.setLayoutX(0);
				joinPane.setLayoutY(currentScroll);
				joinPane.setPrefWidth(816);
				joinPane.setPrefHeight(62);
				scrollFrame.getChildren().add(joinPane);
				Label joinLabel = new Label();
				if (act.equals("joinMessage") == true) {
					joinLabel.setText(param + " 님이 입장하였습니다.");
				} else {
					joinLabel.setText(param + " 님이 퇴장하였습니다.");
				}
				joinLabel.setAlignment(Pos.CENTER); // 라벨 가운데 정렬
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
				
				// 본인
				boolean isSelf = false;
				if (param.equals("Eattato") == true) {
					isSelf = true;
					chatLabel.setLayoutX(684);
					chatLabel.setStyle("-fx-background-color: yellow; -fx-background-radius: 15;");
					
				} else {
					isSelf = false;
					chatLabel.setLayoutX(70);
					chatLabel.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
				}
				
				// 평범한 말풍선
				if (param.equals(latest) == true) {
					currentScroll += 50;
					chatLabel.setLayoutY(4);
					chatPane.setPrefHeight(50);
				} else {
					// 프로필 포함 말풍선
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
				
				String effectType = "";
				double effectStrength = 0;
				if (effect != null) {
					if (effect.split(" ").length == 2) {
						effectType = effect.split(" ")[0];
						effectStrength = Double.parseDouble(effect.split(" ")[1]);
					}
				}
				final String effectTypef = effectType;
				final double effectStrengthf = effectStrength;
				
				double chatLabelOriX = chatLabel.getLayoutX();
				double chatLabelOriY = chatLabel.getLayoutY();
				
				String[] msgSplit = msg.split("");
				Thread thread = new Thread() {
					@Override
					public void run() {
						for (int ind = 0; ind < msgSplit.length; ind++ ) {
							final int indexFinal = ind;
							try {
								Thread.sleep(500 / msgSplit.length);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Platform.runLater(() -> {
								String currentText = chatLabel.getText() + msgSplit[indexFinal];
								double currentTextWidth = new Text(currentText).getLayoutBounds().getWidth();
								chatLabel.setText(currentText);
								chatLabel.setPrefWidth(currentTextWidth * (20 / 10) + 50);
								
								if (effectTypef.equals("shake") == true) {
									if (indexFinal != msgSplit.length - 1) {
										chatLabel.setLayoutX(chatLabelOriX + (new Random().nextDouble(effectStrengthf) - effectStrengthf / 2));
										chatLabel.setLayoutY(chatLabelOriY + (new Random().nextDouble(effectStrengthf) - effectStrengthf / 2));
									} else {
										chatLabel.setLayoutX(chatLabelOriX);
										chatLabel.setLayoutY(chatLabelOriY);
									}
								}
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