import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

// 서버 켜기/끄기 제어 및 클라이언트 접속 관리를 위한 클래스
public class Server {
	// 스레드 풀 생성
	public static ExecutorService threadPool;
	
	// 클라이언트 리스트 생성
	public static ArrayList<Client> clients = new ArrayList<Client>();
	public static ArrayList<HashMap<String, String>> clientData = new ArrayList<>();
	public static ArrayList<ArrayList<Client>> rooms = new ArrayList<>();
	public static ArrayList<HashMap<String, String>> roomSetting = new ArrayList<>();
	
	// 서버 소켓 생성
	static ServerSocket server;
	
	public static void main(String[] args) throws IOException {
		InetAddress[] dns = null;
		try {
			dns = InetAddress.getAllByName("jjabtu.herokuapp.com");
		} catch (UnknownHostException error) {
			System.out.println("도메인을 찾을 수 없습니다");
		}
		
		//if (dns == null || dns.length == 0) {
			serverStart("", 8888);
		//} else {
			//serverStart(dns[0].getHostAddress(), Integer.parseInt(System.getenv("PORT")));
		//}
	}
	
	public static void serverStart(String ip, int port) throws SocketException {
		try {
			if (ip.equals("")) {
				// 로컬 호스트 서버 소켓 객체
				server = new ServerSocket(8888);
				System.out.println("서버가 localhost:" + 8888 + "에서 열렸습니다.");
			} else {
				// 서버 소켓 객체 생성
				server = new ServerSocket();
				// 서버 소켓에 서버 컴의 IP, 포트 할당
				server.bind(new InetSocketAddress(ip, port));
				System.out.println("서버가 " + ip + ":" + port + "에서 열렸습니다.");
			}
			
			
			ArrayList<Client> room = new ArrayList<>();
			//room.add(client);
			rooms.add(room);
			HashMap<String, String> setting = new HashMap<>();
			setting.put("roomName", "이지맨");
			setting.put("roomType", "justchat");
			setting.put("roomCurrent", "0");
			setting.put("roomCapacity", "3");
			setting.put("roomInd", "0");
			setting.put("roomOwner", "system");
			setting.put("roomPassword", "");
			setting.put("anonymous", "off");
			setting.put("fileSend", "on");
			roomSetting.add(setting);
			
			
			Runnable thread = new Runnable() {
				@Override
				public void run() {
					BufferedReader cmdInput = new BufferedReader(new InputStreamReader(System.in));
					try {
						while (true) {
							String cmd = cmdInput.readLine();
							if (cmd.equals("shutdown") == true) {
								System.out.println("명령으로 인해 서버를 종료합니다.");
								shutdown();
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
			};
			threadPool = Executors.newCachedThreadPool();
			threadPool.submit(thread);
			
			try {
				while (server.isClosed() == false) {
					if (server.isClosed() == false) {
						// 클라이언트 연결 요청 수락
						Socket connection = server.accept();
						Client client = new Client(connection);
						
						//System.out.println("접속: " + (InetSocketAddress)connection.getRemoteSocketAddress());
					}
				}
			} catch (Exception error) {
				if (server.isClosed() == false) {
					System.out.println("소켓 종료로 서버를 종료합니다.");
					error.printStackTrace();
					shutdown();
				}
				System.out.println("서버 소켓 상태: " + server.isClosed());
				//error.printStackTrace();
			}
		} catch (Exception error) {
			error.printStackTrace();
			// 서버에 오류 났는데 열려있으면 닫기
			if (server.isClosed() == false) {
				System.out.println("서버 오류로 서버를 종료합니다.");
				shutdown();
			}
		}
	}
	
	public static void shutdown() {
		try {
			System.out.println("서버가 종료되었습니다.");
			Iterator<Client> iter = clients.iterator();
			while (iter.hasNext()) {
				Client client = iter.next();
				System.out.println("접속 해제: " + (InetSocketAddress)client.connection.getRemoteSocketAddress());
				
				// 클라이언트 소켓 연결 종료
				client.connection.close();
				iter.remove();
			}
			
			if (server != null && server.isClosed() == false) {
				// 서버 소켓 종료
				server.close();
			}
			
			if (threadPool != null && threadPool.isShutdown() == false) {
				// 스레드 풀 종료
				threadPool.shutdown();
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public static void announcement(HashMap<String, String> order) {
		for (int ind = 0; ind < clients.size(); ind++) {
			clients.get(ind).send(order);
		}
	}
	
	public static void sendRoomDatas() {
		ArrayList<HashMap<String, String>> roomsCopy = new ArrayList<>();
		
		for (int ind = 0; ind < roomSetting.size(); ind++) {
			HashMap<String, String> originalSetting = roomSetting.get(ind);
			HashMap<String, String> settingCopy = new HashMap<>();
			settingCopy.put("current", Integer.toString(rooms.get(ind).size()));
			settingCopy.put("roomInd", Integer.toString(ind));
			
			settingCopy.put("roomInd", Integer.toString(ind));
			originalSetting.forEach((key, val) -> {
				settingCopy.put(key, val);
			});
			
			// 비번 안 걸린 방이면 비번 false로 바꿔서 클라이언트 줌
			if (originalSetting.get("roomPassword").equals("") == true) {
				settingCopy.replace("roomPassword", "false");
			} else {
				// 비번 걸려 있는 방이면 비번 true로 바꿔서 클라이언트 줌
				settingCopy.replace("roomPassword", "true");
			}
			
			roomsCopy.add(settingCopy);
		}
		
		for (int ind = 0; ind < clients.size(); ind++) {
			HashMap<String, String> inputModeRooms = new HashMap<>();
			inputModeRooms.put("act", "inputMode");
			inputModeRooms.put("param", "rooms");
			ArrayList<HashMap<String, String>> inputModeStandard = new ArrayList<>();
			HashMap<String, String> inputModeStandardSetting = new HashMap<>();
			inputModeStandardSetting.put("inputMode", "standard");
			inputModeStandard.add(inputModeStandardSetting);
			
			Client client = clients.get(ind);
			client.send(inputModeRooms); // 클라이언트 인풋 모드 방 설정 전송 모드로 변경
			client.sendRoom(roomsCopy); // 클라이언트로 방 설정들 보냄
			client.sendRoom(inputModeStandard); // 클라이언트 인풋 모드 원상복구
		}
	}
	
	public static void clientProcess(Client client, HashMap<String, String> order) {
		int clientInd = clients.indexOf(client);
		HashMap<String, String> clientInfo = clientData.get(clientInd);
		if (order.get("act") != null) {
			System.out.println("요청: " + clientInfo + " - " + order.get("act"));
			if (order.get("act").equals("create")) {
				// 방 생성
				String roomType = order.get("roomType");
				String roomName = order.get("roomName");
				String roomCapacity = order.get("roomCapacity");
				String roomPassword = order.get("roomPassword");
				int capacity = 0;
				try {
					capacity = Integer.parseInt(roomCapacity);					
				} catch (Exception error) {
					
				}
				
				String[] availableRoomType = {"justchat", "mafia", "wordbomb"};
				
				// 이미 참가되어있는지 확인
				int joined = -1;
				for (int room = 0; room < rooms.size(); room++) {
					for (int cl = 0; cl < rooms.get(room).size(); cl++) {
						if (rooms.get(room).get(cl) == client) {
							joined = room;
						}
					}
				}
				
				if (joined == -1) {
					if (roomType != null && roomName != null && capacity >= 1 && roomPassword != null) {
						boolean roomTypeFound = false;
						for (int ind = 0; ind < availableRoomType.length; ind++) {
							if (availableRoomType[ind].equals(roomType) == true) {
								roomTypeFound = true;
								break;
							}
						}
						
						// 방 타입이 사용가능하면
						if (roomTypeFound == true) {
							ArrayList<Client> room = new ArrayList<>();
							rooms.add(room);
							
							HashMap<String, String> setting = new HashMap<>();
							setting.put("roomName", roomName);
							setting.put("roomType", roomType);
							setting.put("roomCurrent", "0");
							setting.put("roomCapacity", roomCapacity);
							setting.put("roomOwner", clientData.get(clientInd).get("identifier"));
							setting.put("roomPassword", roomPassword);
							
							ArrayList<String> modeSettings = new ArrayList<>();
							if (roomType.equals("justchat") == true) {
								modeSettings.add("anonymous");
								modeSettings.add("slowmode");
								modeSettings.add("fileSend");
							} else if (roomType.equals("wordbomb") == true) {
								modeSettings.add("oneKillWord");
								modeSettings.add("neologism");
								modeSettings.add("wordTimer");
							} else if (roomType.equals("mafia") == true) {
								modeSettings.add("citizenJob");
								modeSettings.add("mafiaCount");
							}
							for (int ind = 0; ind < modeSettings.size(); ind++) {
								if (order.get(modeSettings.get(ind)) != null) {
									setting.put(modeSettings.get(ind), order.get(modeSettings.get(ind)));									
								}
							}
							
							roomSetting.add(setting);
							System.out.println("방 생성: " + clientInfo);
							
							// 만든 방에 클라이언트 참가시킴
							HashMap<String, String> joinMap = new HashMap<>();
							joinMap.put("act", "join");
							joinMap.put("param", Integer.toString(rooms.size() - 1));
							joinMap.put("roomPassword", roomPassword);
							if (order.get("nickname") != null) {
								joinMap.put("nickname", order.get("nickname"));								
							}
							
							clientProcess(client, joinMap);
						} else {
							System.out.println(clientInfo.get("identifier") + ": 방 생성에 실패하였습니다 - 잘못된 방 타입");
						}
					} else {
						System.out.println(clientInfo.get("identifier") + ": 방 생성에 실패하였습니다 - 방 이름이나 방 타입이 정해지지 않았습니다");
					}
				} else {
					System.out.println(clientInfo.get("identifier") + ": 방 생성에 실패하였습니다 - 이미 참가되어 있습니다");
				}
			} else if (order.get("act").equals("join")) {
				// 방 입장
				
				// 참가하는 방 인덱스 확인
				int joiningRoom = -1;
				if (order.get("param") != null) {
					try {
						joiningRoom = Integer.parseInt(order.get("param"));
					} catch (NumberFormatException error) {
						joiningRoom = -1;
						System.out.println(clientInfo.get("identifier") + ": 방 입장에 실패하였습니다 - 잘못된 방 인덱스");
					}
				}
				
				// 이미 참가되어있는지 확인
				int joined = -1;
				Loop1 :
				for (int room = 0; room < rooms.size(); room++) {
					for (int cl = 0; cl < rooms.get(room).size(); cl++) {
						//System.out.println("room checking " + room + " - " + cl);
						if (rooms.get(room).get(cl) == client) {
							joined = room;
							break Loop1;
						}
					}
				}
				
				// 방에 참가가 안 되어있어야함
				if (joined == -1 && joiningRoom != -1 && joiningRoom < rooms.size()) {
					ArrayList<Client> room = rooms.get(joiningRoom);
					boolean pwDone = false;
					if (roomSetting.get(joiningRoom).get("roomPassword").equals("") == true) {
						pwDone = true;
					} else if (roomSetting.get(joiningRoom).get("roomPassword").equals(order.get("roomPassword")) == true) {
						pwDone = true;
					}
					
					if (pwDone == true) {
						int capacity = Integer.parseInt(roomSetting.get(joiningRoom).get("roomCapacity"));
						if (capacity > room.size()) {
							// 클라이언트 소켓을 해당 방에 추가
							rooms.get(joiningRoom).add(client);
							roomSetting.get(joiningRoom).replace("roomCurrent", Integer.toString(room.size()));
							System.out.println("참가: " + clientInfo + " - " + joiningRoom + ": " + roomSetting.get(joiningRoom).get("roomName"));
							HashMap<String, String> joinMap = new HashMap<>();
							joinMap.put("act", "join");
							joinMap.put("roomName", roomSetting.get(joiningRoom).get("roomName"));
							if (roomSetting.get(joiningRoom).get("roomPassword").equals("") == true) {
								joinMap.put("roomSetting", "설정 없음");
							} else {
								joinMap.put("roomSetting", "암호 존재");
							}
							joinMap.put("roomCurrent", roomSetting.get(joiningRoom).get("roomCurrent"));
							joinMap.put("roomCapacity", roomSetting.get(joiningRoom).get("roomCapacity"));
							if (clientInfo.get("identifier").equals(roomSetting.get(joiningRoom).get("roomOwner")) == true) {
								joinMap.put("roomType", roomSetting.get(joiningRoom).get("roomType"));
							} else {
								joinMap.put("roomType", "justchat");
							}
							client.send(joinMap);
							sendRoomDatas();
							
							// 입력한 닉네임 사용
							if (order.get("nickname") != null) {
								clientInfo.replace("username", order.get("nickname"));
							}
							
							HashMap<String, String> joinMsg = new HashMap<>();
							joinMsg.put("act", "joinMessage");
							joinMsg.put("param", clientInfo.get("username"));
							joinMsg.put("roomCurrent", roomSetting.get(joiningRoom).get("roomCurrent"));
							joinMsg.put("roomCapacity", roomSetting.get(joiningRoom).get("roomCapacity"));
							for (int cl = 0; cl < room.size(); cl++) {
								room.get(cl).send(joinMsg);
							}
						} else {
							System.out.println(clientInfo.get("identifier") + ": 방이 꽉 찼습니다");
						}
					} else {
						System.out.println(clientInfo.get("identifier") + ": 비밀번호가 일치하지 않습니다");
					}
				} else {
					System.out.println(clientInfo.get("identifier") + ": 방 입장에 실패하였습니다 - 이미 참가되어있거나 방을 찾을 수 없습니다");
					System.out.println(joined + " " + joiningRoom + " " + order.get("param"));
				}
			} else if (order.get("act").equals("leave")) {
				// 방 퇴장
				
				// 방에 참가되어 있는지 확인
				int joined = -1;
				Loop1 :
				for (int room = 0; room < rooms.size(); room++) {
					for (int cl = 0; cl < rooms.get(room).size(); cl++) {
						if (rooms.get(room).get(cl) == client) {
							joined = room;
							break Loop1;
						}
					}
				}
				
				// 참가되어 있지 않으면
				if (joined == -1) {
					System.out.println(clientInfo.get("identifier") + ": 방 퇴장에 실패하였습니다 - 참가되어 있지 않습니다");
				} else {
					var room = rooms.get(joined);
					room.remove(room.indexOf(client));
					System.out.println(clientInfo.get("identifier") + ": 퇴장 - " + roomSetting.get(joined).get("roomName"));
					
					// 인원 없는 방이면
					if (room.size() <= 0) {
						// 서버에서 만든 방 아니면
						if (roomSetting.get(joined).get("roomOwner").equals("system") == false) {
							rooms.remove(joined); // 방 삭제
							roomSetting.remove(joined); // 방 삭제
						}
					} else if (roomSetting.get(joined).get("roomOwner").equals(clientInfo.get("identifier"))) {
						// 만약 방장으로 나간거라면
						Client altClient = room.get(0); // 다음으로 들어온 사람으로 방장 넘김
						roomSetting.get(joined).replace("roomOwner", clientData.get(clients.indexOf(altClient)).get("identifier"));
						HashMap<String, String> ownerMsg = new HashMap<>();
						ownerMsg.put("act", "notice");
						ownerMsg.put("param", clientData.get(clients.indexOf(altClient)).get("username") + " 님이 방장이 되었습니다.");
					}
					HashMap<String, String> leaveMap = new HashMap<>();
					leaveMap.put("act", "leave");
					client.send(leaveMap);
					sendRoomDatas();
					
					for (int cl = 0; cl < room.size(); cl++) {
						HashMap<String, String> leaveMsg = new HashMap<>();
						leaveMsg.put("act", "leaveMessage");
						leaveMsg.put("param", clientInfo.get("username"));
						
						if (room.get(cl) != client) {
							// 보낸 본인이 아니라면
							room.get(cl).send(leaveMsg);
						}
					}
				}
			} else if (order.get("act").equals("type")) {
				// 타이핑
			} else if (order.get("act").equals("msg")) {
				// 메세지 송출
				String msg = order.get("param");
				if (msg != null) {
					// 방에 참가가 되어있는지 확인
					int joined = -1;
					Loop1 :
					for (int room = 0; room < rooms.size(); room++) {
						for (int cl = 0; cl < rooms.get(room).size(); cl++) {
							if (rooms.get(room).get(cl) == client) {
								joined = room;
								break Loop1;
							}
						}
					}
					
					// 참가가 되어있다면
					if (joined != -1) {
						ArrayList<Client> room = rooms.get(joined);
						for (int cl = 0; cl < room.size(); cl++) {
							HashMap<String, String> sendMsg = new HashMap<>();
							if (roomSetting.get(joined).get("anonymous").equals("on") == true) {
								sendMsg.put("param", "익명");
								sendMsg.put("profile", clientInfo.get("profile"));
							} else {
								sendMsg.put("param", clientInfo.get("username"));
								sendMsg.put("profile", clientInfo.get("profile"));
							}
							sendMsg.put("author", clientInfo.get("identifier"));
							sendMsg.put("msg", msg);
							
							// 이미지 포함이면 이미지 받은 거 그대로 다른 클라이언트에 줌
							if (roomSetting.get(joined).get("fileSend").equals("on") == true && order.get("image") != null) {
								System.out.println("이미지를 전송합니다");
								sendMsg.put("image", order.get("image"));
							}
							
							if (room.get(cl) != client) {
								// 보낸 본인이 아니라면
								sendMsg.put("act", "msg");
							} else {
								sendMsg.put("act", "selfmsg");
							}
							room.get(cl).send(sendMsg);
						}
						System.out.println("메세지 전송: " + clientInfo.get("identifier"));
					} else {
						System.out.println(clientInfo.get("identifier") + ": 메세지 전송에 실패하였습니다 - 참가되어 있지 않습니다");
					}
				} else {
					System.out.println(clientInfo.get("identifier") + ": 메세지 전송에 실패하였습니다 - 보낼 메세지가 없습니다");
				}
			} else if (order.get("act").equals("reload")) {
				ArrayList<HashMap<String, String>> roomsCopy = new ArrayList<>();
				
				for (int ind = 0; ind < roomSetting.size(); ind++) {
					HashMap<String, String> originalSetting = roomSetting.get(ind);
					HashMap<String, String> settingCopy = new HashMap<>();
					settingCopy.put("current", Integer.toString(rooms.get(ind).size()));
					
					settingCopy.put("roomInd", Integer.toString(ind));
					originalSetting.forEach((key, val) -> {
						settingCopy.put(key, val);
					});
					
					// 비번 안 걸린 방이면 비번 false로 바꿔서 클라이언트 줌
					if (originalSetting.get("roomPassword").equals("") == true) {
						settingCopy.replace("roomPassword", "false");
					} else {
						// 비번 걸려 있는 방이면 비번 true로 바꿔서 클라이언트 줌
						settingCopy.replace("roomPassword", "true");
					}
					
					roomsCopy.add(settingCopy);
				}
				
				HashMap<String, String> inputModeRooms = new HashMap<>();
				inputModeRooms.put("act", "inputMode");
				inputModeRooms.put("param", "rooms");
				ArrayList<HashMap<String, String>> inputModeStandard = new ArrayList<>();
				HashMap<String, String> inputModeStandardSetting = new HashMap<>();
				inputModeStandardSetting.put("inputMode", "standard");
				inputModeStandard.add(inputModeStandardSetting);
				
				client.send(inputModeRooms); // 클라이언트 인풋 모드 방 설정 전송 모드로 변경
				client.sendRoom(roomsCopy); // 클라이언트로 방 설정들 보냄
				client.sendRoom(inputModeStandard); // 클라이언트 인풋 모드 원상복구
			} else if (order.get("act").equals("start") == true) {
				// 방에 참가가 되어있는지 확인
				int joined = -1;
				Loop1 :
				for (int room = 0; room < rooms.size(); room++) {
					for (int cl = 0; cl < rooms.get(room).size(); cl++) {
						if (rooms.get(room).get(cl) == client) {
							joined = room;
							break Loop1;
						}
					}
				}
				
				// 참가가 되어있다면
				if (joined != -1) {
					if (roomSetting.get(joined).get("roomOwner").equals(clientInfo.get("identifier")) == true) {
						ArrayList<Client> room = rooms.get(joined);
						
						HashMap<String, String> startMsg = new HashMap<>();
						startMsg.put("act", "startMessage");
						for (int cl = 0; cl < room.size(); cl++) {
							room.get(cl).send(startMsg);
						}
					} else {
						System.out.println(clientInfo.get("identifier") + ": 게임 시작에 실패하였습니다 - 방장이 아닙니다");
					}
				} else {
					System.out.println(clientInfo.get("identifier") + ": 게임 시작에 실패하였습니다 - 참가되어 있지 않습니다");
				}
			} else if (order.get("act").equals("profile") == true) {
				if (order.get("param") != null) {
					clientInfo.replace("profile", order.get("param"));
				}
			}
		} else {
			System.out.println("요청 실패: " + clientInfo + " - 요청 목적이 없습니다");
		}
	}
}