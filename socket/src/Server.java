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
		serverStart("", 8888);
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
			
			Runnable thread = new Runnable() {
				@Override
				public void run() {
					BufferedReader cmdInput = new BufferedReader(new InputStreamReader(System.in));
					try {
						while (true) {
							String cmd = cmdInput.readLine();
							if (cmd.equals("shutdown") == true) {
								shutdown();
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
						clients.add(client);
						
						String clientInfo = connection.getInetAddress().toString() + ":" + connection.getPort();
						
						HashMap<String, String> clData = new HashMap<String, String>();
						clData.put("identifier", clientInfo);
						clData.put("username", "익명");
						clientData.add(clData);
						//System.out.println("접속: " + (InetSocketAddress)connection.getRemoteSocketAddress());
						System.out.println("접속: " + clientInfo);
					}
				}
			} catch (Exception error) {
				if (server.isClosed() == false) {
					shutdown();
				}
				System.out.println("서버 소켓 상태: " + server.isClosed());
				//error.printStackTrace();
			}
		} catch (Exception error) {
			error.printStackTrace();
			// 서버에 오류 났는데 열려있으면 닫기
			if (server.isClosed() == false) {
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

	public static void clientProcess(Client client, HashMap<String, String> order) {
		int clientInd = clients.indexOf(client);
		HashMap<String, String> clientInfo = clientData.get(clientInd);
		if (order.get("act") != null) {
			System.out.println("요청: " + clientInfo + " - " + order.get("act"));
			if (order.get("act").equals("create")) {
				// 방 생성
				String roomType = order.get("roomType");
				String roomName = order.get("roomName");
				
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
					if (roomType != null && roomName != null) {
						boolean roomTypeFound = false;
						for (int ind = 0; ind < availableRoomType.length; ind++) {
							if (availableRoomType[ind].equals(roomType) == true) {
								roomTypeFound = true;
								break;
							}
						}
						
						if (roomTypeFound == true) {
							ArrayList<Client> room = new ArrayList<>();
							//room.add(client);
							rooms.add(room);
							
							HashMap<String, String> setting = new HashMap<>();
							setting.put("roomName", roomName);
							setting.put("roomType", roomType);
							setting.put("roomOwner", clientData.get(clientInd).get("identifier"));
							setting.put("roomPassword", "");
							roomSetting.add(setting);
							System.out.println("방 생성: " + clientInfo);
							
							// 만든 방에 클라이언트 참가시킴
							HashMap<String, String> joinMap = new HashMap<>();
							joinMap.put("act", "join");
							joinMap.put("param", Integer.toString(rooms.indexOf(room)));
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
				for (int room = 0; room < rooms.size(); room++) {
					for (int cl = 0; cl < rooms.get(room).size(); cl++) {
						System.out.println("room checking " + room + " - " + cl);
						if (rooms.get(room).get(cl) == client) {
							joined = room;
						}
					}
				}
				
				// 방에 참가가 안 되어있어야함
				if (joined == -1 && joiningRoom != -1 && joiningRoom < rooms.size()) {
					boolean pwDone = false;
					if (roomSetting.get(joiningRoom).get("roomPassword").equals("") == true) {
						pwDone = true;
					} else if (roomSetting.get(joiningRoom).get("roomPassword").equals(order.get("password")) == true) {
						pwDone = true;
					}
					
					if (pwDone == true) {
						// 클라이언트 소켓을 해당 방에 추가
						rooms.get(joiningRoom).add(client);
						System.out.println("참가: " + clientInfo + " - " + joiningRoom + ": " + roomSetting.get(joiningRoom).get("roomName"));
					} else {
						System.out.println(clientInfo.get("identifier") + ": 비밀번호가 일치하지 않습니다");
					}
				} else {
					System.out.println(clientInfo.get("identifier") + ": 방 입장에 실패하였습니다 - 이미 참가되어있거나 방을 찾을 수 없습니다");
				}
			} else if (order.get("act").equals("leave")) {
				// 방 퇴장
				
				// 방에 참가되어 있는지 확인
				int joined = -1;
				for (int room = 0; room < rooms.size(); room++) {
					for (int cl = 0; room < rooms.get(room).size(); cl++) {
						if (rooms.get(room).get(cl) == client) {
							joined = room;
							break;
						}
					}
				}
				
				// 참가되어 있지 않으면
				if (joined == -1) {
					System.out.println(clientInfo.get("identifier") + ": 방 퇴장에 실패하였습니다 - 참가되어 있지 않습니다");
				} else {
					var room = rooms.get(joined);
					room.remove(room.indexOf(client));
					
					// 인원 없는 방이면
					if (room.size() <= 0) {
						rooms.remove(joined); // 방 삭제
					} else if (roomSetting.get(joined).get("roomOwner").equals(clientInfo.get("identifier"))) {
						// 만약 방장으로 나간거라면
						Client altClient = room.get(0); // 다음으로 들어온 사람으로 방장 넘김
						roomSetting.get(joined).replace("roomOwner", clientData.get(clients.indexOf(altClient)).get("identifier"));
					}
					System.out.println(clientInfo.get("identifier") + ": 퇴장 - " + roomSetting.get(joined).get("roomName"));
					
					for (int cl = 0; cl < room.size(); cl++) {
						HashMap<String, String> leaveMsg = new HashMap<>();
						leaveMsg.put("act", "leave");
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
					for (int room = 0; room < rooms.size(); room++) {
						for (int cl = 0; cl < rooms.get(room).size(); cl++) {
							if (rooms.get(room).get(cl) == client) {
								joined = room;
							}
						}
					}
					
					// 참가가 되어있다면
					if (joined != 1) {
						ArrayList<Client> room = rooms.get(joined);
						for (int cl = 0; cl < room.size(); cl++) {
							HashMap<String, String> sendMsg = new HashMap<>();
							sendMsg.put("param", clientInfo.get("username"));
							sendMsg.put("msg", msg);
							
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
			}
		} else {
			System.out.println("요청 실패: " + clientInfo + " - 요청 목적이 없습니다");
		}
	}
}