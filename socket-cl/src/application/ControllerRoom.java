package application;
import java.io.File;
import java.io.FileInputStream;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ControllerRoom {
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
	
	@FXML
	private Button start;
	
	private ArrayList<Pane> scrollObjects = new ArrayList<Pane>();
	
	private float currentScroll = 0;
	
	private String latest = "System";
	
	private Main main;
	public void setMain(Main mainset) {
		main = mainset;
	}
	
	public double scrollPos = 1.0;
	
	public void control() {
		leave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				HashMap<String, String> leaveMap = new HashMap<>();
				leaveMap.put("act", "leave");
				main.send(leaveMap);
			}
		});
		
		start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				HashMap<String, String> startMap = new HashMap<>();
				startMap.put("act", "start");
				main.send(startMap);
			}
		});
		
		scrollFrame.heightProperty().addListener(observable -> {
			if (scrollPos == 1.0) {
				scroll.setVvalue(1.0);
			}
		});
		
		scrollFrame.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				if (event.getGestureSource() != scrollFrame) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
            	event.consume();
			}
		});
		
		scrollFrame.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				System.out.println("파일 드롭됨");
				//boolean dropSuccess = false;
				Dragboard drag = event.getDragboard();
				List<File> files = drag.getFiles();
				for (int file = 0; file < files.size(); file++) {
					Image img = new Image(files.get(file).getAbsolutePath());
					if (img.isError() == false) {
//						FileInputStream fileInputStreamReader = new FileInputStream(files.get(file));
//			            byte[] bytes = new byte[(int) files.get(file).length()];
//			            fileInputStreamReader.read(bytes);
//			            
//			            String imgStr = new String(Base64.getEncoder().encode(bytes));
//			            
//			            HashMap<String, String> imageMap = new HashMap<>();
//			            imageMap.put("act", "image");
//			            imageMap.put("param", new String(Base64.encodeBase64(bytes), "UTF-8"));
//			            
//			            main.send(imageMap);
			            
//						PixelReader imgReader = img.getPixelReader();
//						WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();
//						
//						int width = (int) img.getWidth();
//						int height = (int) img.getHeight();
//						int pixels[] = new int[width * height];
//						
//						// 왼쪽 위부터 오른쪽 아래까지 픽셀을 읽음
//						imgReader.getPixels(0, 0, width, height, format, pixels, 0, width);
//						String imgStr = "";
//						for (int ind = 0; ind < pixels.length; ind++) {
//							imgStr += Integer.toString(pixels[ind]);
//							if (ind != pixels.length - 1) {
//								imgStr += " ";
//							}
//						}
//						
//						HashMap<String, String> imageMap = new HashMap<>();
//						imageMap.put("act", "image");
//						imageMap.put("param", imgStr);
//						main.send(imageMap);
//						System.out.println(imgStr);
					}
				}
				
				//event.setDropCompleted(dropSuccess);
				event.consume();
			}
		});
	}
	
	//public void dragDropFrame() {
		//scrollFrame.
	//}
	
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
	
	public String getTextInput() {
		return typer.getText();
	}
	
	public void resetTextInput() {
		typer.setText("");
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
					if (act.equals("joinMessage") == true || act.equals("leaveMessage") == true || act.equals("startMessage") == true || act.equals("notice") == true) {
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
						} else if (act.equals("leaveMessage") == true) {
							joinLabel.setText(param + " 님이 퇴장하였습니다.");
						} else  if (act.equals("startMessage") == true) {
							joinLabel.setText("게임이 시작되었습니다!");
						} else  if (act.equals("notice") == true) {
							joinLabel.setText(param);
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
					} else if (act.equals("msg") == true || act.equals("selfmsg") == true) {
						// 보낸 이 identifier
						String identifier = command.get("author");
						
						Pane chatPane = new Pane();
						chatPane.setPrefWidth(816);
						chatPane.setLayoutY(currentScroll);
						Label chatLabel = new Label();
						chatLabel.setAlignment(Pos.CENTER);
						chatLabel.setFont(new Font("Hancom Gothic Regular", 20.0));
						chatLabel.setPrefWidth(50);
						chatLabel.setPrefHeight(42);
						
						// 이미지 있을 때
						if (command.get("image") != null) {
							System.out.println(command.get("image"));
						}
						
						// 본인
						if (act.equals("selfmsg") == true) {
							chatLabel.setLayoutX(684);
							chatLabel.setStyle("-fx-background-color: yellow; -fx-background-radius: 15;");
							
						} else {
							chatLabel.setLayoutX(70);
							chatLabel.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
						}
						
						// 평범한 말풍선
						if (identifier.equals(latest) == true) {
							currentScroll += 50;
							chatLabel.setLayoutY(4);
							chatPane.setPrefHeight(50);
						} else {
							// 프로필 포함 말풍선
							latest = identifier;
							currentScroll += 77;
							chatLabel.setLayoutY(33);
							chatPane.setPrefHeight(77);
							
							Text author = new Text();
							Pane profileFrame = new Pane();
							if (act.equals("selfmsg") == true) {
								author.setTextAlignment(TextAlignment.RIGHT);
								author.setLayoutX(511);
								profileFrame.setLayoutX(743);
							} else {
								author.setLayoutX(70);
								profileFrame.setLayoutX(14);
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
										if (act.equals("selfmsg") == true) {
											chatLabel.setLayoutX(chatLabelOriX - currentTextWidth * (20 / 10));
										}
										
										if (effectTypef.equals("shake") == true) {
											if (indexFinal != msgSplit.length - 1) {
												if (act.equals("selfmsg") == true) {
													chatLabel.setLayoutX(chatLabelOriX - currentTextWidth * (20 / 10) + (new Random().nextDouble(effectStrengthf) - effectStrengthf / 2));
												} else {
													chatLabel.setLayoutX(chatLabelOriX + (new Random().nextDouble(effectStrengthf) - effectStrengthf / 2));
												}
												chatLabel.setLayoutY(chatLabelOriY + (new Random().nextDouble(effectStrengthf) - effectStrengthf / 2));
											} else {
												if (act.equals("selfmsg") == true) {
													chatLabel.setLayoutX(chatLabelOriX - currentTextWidth * (20 / 10));
												} else {
													chatLabel.setLayoutX(chatLabelOriX);
												}
												chatLabel.setLayoutY(chatLabelOriY);
											}
										}
									});
								}
							}
						};
						thread.start();
					} else if (act.equals("join") == true) {
						currentScroll = 0;
						
						// 스크롤 프레임에 있는 엘레먼트 모두 삭제
						for (int ind = 0; ind < scrollObjects.size(); ind++) {
							scrollFrame.getChildren().remove(scrollObjects.get(ind));
						}
						scrollObjects = new ArrayList<Pane>();
						
						roomName.setText(command.get("roomName"));
						roomSetting.setText(command.get("roomSetting"));
						roomAdmits.setText(command.get("roomCurrent") + " / " + command.get("roomCapacity"));
						
						if (command.get("roomType").equals("justchat") == true) {
							roomAdmits.setLayoutX(740);
							start.setVisible(false);                                                                                                                                                                               
						} else {
							roomAdmits.setLayoutX(651);
							start.setVisible(true);
						}
					}
					
					// 스크롤 늘리기
					scrollPos = scroll.getVvalue();
					if (currentScroll > scrollFrame.getPrefHeight()) {
						scrollFrame.setPrefHeight(currentScroll);
					}
				} catch (Exception error) {
					error.printStackTrace();
				}
			}
		});
	}
}