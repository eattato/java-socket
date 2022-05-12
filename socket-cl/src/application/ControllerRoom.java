package application;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;
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
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
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
	
	@FXML
	private Pane fileFrame;
	
	private ArrayList<Pane> scrollObjects = new ArrayList<Pane>();
	private ArrayList<Button> fileButtons = new ArrayList<Button>();
	
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
				for (int ind = 0; ind < files.size(); ind++) {
					File file = files.get(ind);
					System.out.println("드롭됨: " + file.getPath());
					Image img = new Image(file.getAbsolutePath());
					
					// 이미지인지 확인
					if (img.isError() == false) {
						try {
							// 파일 사이즈에 맞춰 버퍼 준비
							byte[] buff = new byte[(int) file.length()];
							try (FileInputStream fileis = new FileInputStream(file)) {
								fileis.read(buff);
							}
							
							// base64 인코더로 이미지를 스트링으로 인코딩해서 전송할 이미지 목록에 올림
							Encoder encoder = Base64.getEncoder();
							String encodedImage = new String(encoder.encode(buff));
							main.sendingImages.add(encodedImage);
							//System.out.println("이미지 추가: " + encodedImage);
							
							Button fileButton = new Button();
							fileButton.setText(file.getName());
							fileButton.setLayoutX(fileButtons.size() * 105);
							fileButton.setLayoutY(10);
							fileButton.setPrefWidth(100);
							fileButton.setPrefHeight(30);
							fileButton.setStyle("-fx-background-color: ghostwhite; -fx-background-radius: 10;");
							fileButton.setFont(new Font("Hancom Gothic Regular", 12.0));
							fileFrame.getChildren().add(fileButton);
							fileButtons.add(fileButton);
							
							fileButton.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									int imageIndex = main.sendingImages.indexOf(encodedImage);
									if (imageIndex != -1) {
										main.sendingImages.remove(imageIndex);
										for (int ind = imageIndex; ind < fileButtons.size(); ind++) {
											fileButtons.get(ind).setLayoutX((ind - 1) * 105);
										}
									}
									fileFrame.getChildren().remove(fileButton);
								}
							});
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				//event.setDropCompleted(dropSuccess);
				event.consume();
			}
		});
	}
	
	public void clearFileButtons() {
		for (int ind = 0; ind < fileButtons.size(); ind++) {
			fileFrame.getChildren().remove(fileButtons.get(ind));
		}
		fileButtons.clear();
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
						
						roomAdmits.setText(command.get("roomCurrent") + " / " + command.get("roomCapacity"));
						
						currentScroll += 62;
					} else if (act.equals("msg") == true || act.equals("selfmsg") == true) {
						double scrollHeight = 50;
						
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
						boolean hasImage = false;
						double imagePadding = 10;
						double imgScale = 1;
						double imgWidth = 0;
						double imgHeight = 0;
						ImageView imageView = null;
						String encoded = command.get("image");
						if (encoded != null && encoded.equals("") == false) {
							hasImage = true;
							System.out.println("이미지를 받았습니다.");
							try {
								Decoder decoder = Base64.getDecoder();
								String[] encodedImages = encoded.split(" ");
								for (int ind = 0; ind < encodedImages.length; ind++) {
									byte[] decodedImage = decoder.decode(encodedImages[ind]);
									Image image = new Image(new ByteArrayInputStream(decodedImage));
									imageView = new ImageView();
									imageView.setImage(image);
									
									imgWidth = image.getWidth();
									imgHeight = image.getHeight();
									// 가로 최대 300, 세로 최대 100 픽셀
									if (imgWidth > 300 || imgHeight > 100) {
										// 가로가 세로보다 크면 가로 기준으로 비율 조정
										if (imgWidth > imgHeight) {
											imgScale = 300 / imgWidth;
										} else { // 세로가 더 크거나 같으면 세로 기준으로 비율 조정
											imgScale = 100 / imgHeight;
										}
									}
									
									imageView.setFitWidth(imgWidth * imgScale);
									imageView.setFitHeight(imgHeight * imgScale);
								}
							} catch (Exception error) {
								error.printStackTrace();
							}
						}
						
						// 본인
						if (act.equals("selfmsg") == true) {
							chatLabel.setLayoutX(683);
							chatLabel.setStyle("-fx-background-color: yellow; -fx-background-radius: 15;");
							if (hasImage == true) {
								chatLabel.setLayoutX(723 - (imgWidth * imgScale + imagePadding));
								chatLabel.setStyle("-fx-background-color: yellow; -fx-background-radius: 15; -fx-label-padding: " + (imgHeight * imgScale + 10) + " 0 0 0;");
							}
						} else {
							chatLabel.setLayoutX(70);
							chatLabel.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
							if (hasImage == true) {
								chatLabel.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-label-padding: " + (imgHeight * imgScale + 10) + " 0 0 0;");
							}
						}
						
						// 이미지 있을 때 사이즈 조정
						if (hasImage == true) {
							chatLabel.setPrefWidth(imgWidth * imgScale + imagePadding * 2);
							
							// 세로는 위에만 패딩 있으면 되서 *2 안 넣음
							chatLabel.setPrefHeight(imgHeight * imgScale + imagePadding + 42);
						}
						
						// 평범한 말풍선
						if (identifier.equals(latest) == true) {
							chatLabel.setLayoutY(4);
							if (hasImage == false) {
								scrollHeight = 50;
								chatPane.setPrefHeight(50);
							} else {
								// 챗용 공간 42 + 챗 패인 패딩 8 + 이미지 패딩 윗면
								scrollHeight = imgHeight * imgScale + 42 + 8 + imagePadding;
								chatPane.setPrefHeight(scrollHeight);
							}
						} else {
							// 프로필 포함 말풍선
							latest = identifier;
							if (hasImage == false) {
								scrollHeight = 77;
								chatPane.setPrefHeight(77);
							} else {
								scrollHeight = imgHeight * imgScale + 42 + 8 + 27 + imagePadding;
								chatPane.setPrefHeight(scrollHeight);
							}
							chatLabel.setLayoutY(33);
							
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
							
							if (command.get("profile").equals("") == false) {
								try {
									Decoder decoder = Base64.getDecoder();
									byte[] decodedImage = decoder.decode(command.get("profile"));
									Image image = new Image(new ByteArrayInputStream(decodedImage));
									ImageView profileImg = new ImageView();
									profileImg.setPreserveRatio(true);
									profileImg.setPickOnBounds(true);
									profileImg.setFitWidth(48);
									profileImg.setFitWidth(50);
									profileImg.setImage(image);
									profileImg.setStyle("-fx-background-radius: 15;");
									profileFrame.getChildren().add(profileImg);
									
//									Rectangle profileClip = new Rectangle();
//									profileClip.setWidth(48);
//									profileClip.setHeight(50);
//									profileClip.setArcWidth(25);
//									profileClip.setArcHeight(25);
//									profileClip.setVisible(false);
//									profileFrame.getChildren().add(profileClip);
									
									//profileImg.setClip(profileClip);
								} catch (Exception error) {
									error.printStackTrace();
								}
							}
						}
						currentScroll += scrollHeight;
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
						final boolean hasImagef = hasImage;
						final double imageWidthf = imgWidth * imgScale + 10;
						final double imagePaddingf = imagePadding;
						
						double chatLabelOriX = chatLabel.getLayoutX();
						double chatLabelOriY = chatLabel.getLayoutY();
						if (hasImage == true) {
							imageView.setLayoutX(chatLabelOriX + imagePadding);
							imageView.setLayoutY(chatLabelOriY + imagePadding);
							chatPane.getChildren().add(imageView);
						}
						
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
										if (hasImagef == false) {
											chatLabel.setPrefWidth(currentTextWidth * (20 / 10) + 50);
										} else {
											if (currentTextWidth * (20 / 10) + 50 > imageWidthf + imagePaddingf * 2) {
												chatLabel.setPrefWidth(currentTextWidth * (20 / 10) + 50);
											}
										}
										if (act.equals("selfmsg") == true) {
											if (hasImagef == false) {
												chatLabel.setLayoutX(chatLabelOriX - currentTextWidth * (20 / 10));
											} else {
												if (currentTextWidth * (20 / 10) + 50 > imageWidthf + imagePaddingf * 2) {
													chatLabel.setLayoutX(chatLabelOriX - currentTextWidth * (20 / 10));
												}
											}
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