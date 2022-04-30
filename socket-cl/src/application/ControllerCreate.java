package application;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

public class ControllerCreate {
	@FXML
	private Button create;
	
	@FXML
	private Button cancel;
	
	@FXML
	private ScrollPane scroll;
	
	@FXML
	private TextField name;
	
	@FXML
	private TextField capacity;
	
	@FXML
	private TextField password;
	
	@FXML
	private Button typeChat;
	
	@FXML
	private Button typeWord;
	
	@FXML
	private Button typeMafia;
	
	@FXML
	private Pane chatSetting;
	
	@FXML
	private Pane wordSetting;
	
	@FXML
	private Pane mafiaSetting;
	
	
	@FXML
	private Button chatAnonymous;
	
	@FXML
	private Button chatSlowmode;
	
	@FXML
	private Button chatFilesend;
	
	@FXML
	private Button wordOkword;
	
	@FXML
	private Button wordNokill;
	
	@FXML
	private Button wordTimer;
	
	@FXML
	private Button mafiaCitizen;
	
	@FXML
	private Button mafiaEnemy;
	

	private Main main;
	public void setMain(Main mainset) {
		main = mainset;
	}
	
	public void createButton() {
		create.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				HashMap<String, String> createMap = new HashMap<>();
				createMap.put("act", "create");
				createMap.put("roomType", main.createSetting.get("roomType"));
				createMap.put("roomName", name.getText());
				createMap.put("roomCapacity", capacity.getText());
				if (main.createSetting.get("roomType").equals("justchat") == true) {
					createMap.put("anonymous", main.createSetting.get("anonymous"));
					createMap.put("slowmode", main.createSetting.get("slowmode"));
					createMap.put("fileSend", main.createSetting.get("fileSend"));
				} else if (main.createSetting.get("roomType").equals("wordbomb") == true) {
					createMap.put("neologism", main.createSetting.get("neologism"));
					createMap.put("oneKillWord", main.createSetting.get("oneKillWord"));
					createMap.put("wordTimer", main.createSetting.get("wordTimer"));
				} else if (main.createSetting.get("roomType").equals("mafia") == true) {
					createMap.put("citizenJob", main.createSetting.get("citizenJob"));
					createMap.put("mafiaCount", main.createSetting.get("mafiaCount"));
				}
				main.send(createMap);
			}
		});
	}
	
	public void cancelButton() {
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				main.stage.setScene(main.sceneMain);
				main.stage.show();
			}
		});
	}
	
	// 설정 버튼들
	public void settingButtons() {
		typeChat.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				main.createSetting.replace("roomType", "justchat");
				chatSetting.setVisible(true);
				wordSetting.setVisible(false);
				mafiaSetting.setVisible(false);
				typeChat.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
				typeWord.setStyle("-fx-background-color: lightgray; -fx-background-radius: 15;");
				typeMafia.setStyle("-fx-background-color: lightgray; -fx-background-radius: 15;");
			}
		});
		
		typeWord.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				main.createSetting.replace("roomType", "wordbomb");
				chatSetting.setVisible(false);
				wordSetting.setVisible(true);
				mafiaSetting.setVisible(false);
				typeChat.setStyle("-fx-background-color: lightgray; -fx-background-radius: 15;");
				typeWord.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
				typeMafia.setStyle("-fx-background-color: lightgray; -fx-background-radius: 15;");
			}
		});
		
		typeMafia.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				main.createSetting.replace("roomType", "mafia");
				chatSetting.setVisible(false);
				wordSetting.setVisible(false);
				mafiaSetting.setVisible(true);
				typeChat.setStyle("-fx-background-color: lightgray; -fx-background-radius: 15;");
				typeWord.setStyle("-fx-background-color: lightgray; -fx-background-radius: 15;");
				typeMafia.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
			}
		});
		
		// 익명 ON OFF
		chatAnonymous.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (main.createSetting.get("anonymous").equals("off") == true) {
					main.createSetting.replace("anonymous", "on");
					chatAnonymous.setText("ON");
					chatAnonymous.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
				} else {
					main.createSetting.replace("anonymous", "off");
					chatAnonymous.setText("OFF");
					chatAnonymous.setStyle("-fx-background-color: lightgray; -fx-background-radius: 15;");
				}
			}
		});
		
		// 슬로우모드
		chatSlowmode.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (main.createSetting.get("slowmode").equals("0") == true) {
					main.createSetting.replace("slowmode", "5");
					chatSlowmode.setText("5");
				} else if (main.createSetting.get("slowmode").equals("5") == true) {
					main.createSetting.replace("slowmode", "10");
					chatSlowmode.setText("10");
				} else if (main.createSetting.get("slowmode").equals("10") == true) {
					main.createSetting.replace("slowmode", "15");
					chatSlowmode.setText("15");
				} else if (main.createSetting.get("slowmode").equals("15") == true) {
					main.createSetting.replace("slowmode", "30");
					chatSlowmode.setText("30");
				} else if (main.createSetting.get("slowmode").equals("30") == true) {
					main.createSetting.replace("slowmode", "0");
					chatSlowmode.setText("0");
				}
			}
		});
		
		// 파일 전송 ON OFF
		chatFilesend.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (main.createSetting.get("fileSend").equals("off") == true) {
					main.createSetting.replace("fileSend", "on");
					chatFilesend.setText("ON");
					chatFilesend.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
				} else {
					main.createSetting.replace("fileSend", "off");
					chatFilesend.setText("OFF");
					chatFilesend.setStyle("-fx-background-color: lightgray; -fx-background-radius: 15;");
				}
			}
		});
		
		// 이건킹정 ON OFF
		wordOkword.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (main.createSetting.get("neologism").equals("off") == true) {
					main.createSetting.replace("neologism", "on");
					wordOkword.setText("ON");
					wordOkword.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
				} else {
					main.createSetting.replace("neologism", "off");
					wordOkword.setText("OFF");
					wordOkword.setStyle("-fx-background-color: lightgray; -fx-background-radius: 15;");
				}
			}
		});
		
		// 한방단어 ON OFF
		wordNokill.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (main.createSetting.get("oneKillWord").equals("off") == true) {
					main.createSetting.replace("oneKillWord", "on");
					wordNokill.setText("ON");
					wordNokill.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
				} else {
					main.createSetting.replace("oneKillWord", "off");
					wordNokill.setText("OFF");
					wordNokill.setStyle("-fx-background-color: lightgray; -fx-background-radius: 15;");
				}
			}
		});
		
		// 끝말잇기 타이머
		wordTimer.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (main.createSetting.get("wordTimer").equals("10") == true) {
					main.createSetting.replace("wordTimer", "30");
					wordTimer.setText("30");
				} else if (main.createSetting.get("wordTimer").equals("30") == true) {
					main.createSetting.replace("wordTimer", "60");
					wordTimer.setText("60");
				} else if (main.createSetting.get("wordTimer").equals("60") == true) {
					main.createSetting.replace("wordTimer", "120");
					wordTimer.setText("120");
				} else if (main.createSetting.get("wordTimer").equals("120") == true) {
					main.createSetting.replace("wordTimer", "180");
					wordTimer.setText("180");
				} else if (main.createSetting.get("wordTimer").equals("180") == true) {
					main.createSetting.replace("wordTimer", "10");
					wordTimer.setText("10");
				}
			}
		});
		
		// 마피아 시민 직업 ON OFF
		mafiaCitizen.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (main.createSetting.get("citizenJob").equals("off") == true) {
					main.createSetting.replace("citizenJob", "on");
					mafiaCitizen.setText("ON");
					mafiaCitizen.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
				} else {
					main.createSetting.replace("citizenJob", "off");
					mafiaCitizen.setText("OFF");
					mafiaCitizen.setStyle("-fx-background-color: lightgray; -fx-background-radius: 15;");
				}
			}
		});
		
		// 마피아 인원
		mafiaEnemy.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (main.createSetting.get("mafiaCount").equals("1") == true) {
					main.createSetting.replace("mafiaCount", "2");
					mafiaEnemy.setText("2");
				} else if (main.createSetting.get("mafiaCount").equals("2") == true) {
					main.createSetting.replace("mafiaCount", "3");
					mafiaEnemy.setText("3");
				} else if (main.createSetting.get("mafiaCount").equals("3") == true) {
					main.createSetting.replace("mafiaCount", "1");
					mafiaEnemy.setText("1");
				}
			}
		});
	}

	
	
	public void uiControl(HashMap<String, String> command) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					String act = command.get("act");
					String param = command.get("param");
					String msg = command.get("msg");
					String effect = command.get("effect");
					if (act.equals("joinMessage") == true || act.equals("leaveMessage") == true) {
						
					}
				} catch (Exception error) {
					error.printStackTrace();
				}
			}
		});
	}
}