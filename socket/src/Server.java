import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

// ���� �ѱ�/���� ���� �� Ŭ���̾�Ʈ ���� ������ ���� Ŭ����
public class Server {
	// ������ Ǯ ����
	public static ExecutorService threadPool;
	
	// Ŭ���̾�Ʈ ����Ʈ ����
	public static ArrayList<Client> clients = new ArrayList<Client>();
	public static ArrayList<HashMap<String, String>> clientData = new ArrayList<>();
	public static ArrayList<ArrayList<Client>> rooms = new ArrayList<>();
	public static ArrayList<HashMap<String, String>> roomSetting = new ArrayList<>();
	
	// ���� ���� ����
	static ServerSocket server;
	
	public static void main(String[] args) throws IOException {
		serverStart("", 8888);
	}
	
	public static void serverStart(String ip, int port) throws SocketException {
		try {
			if (ip.equals("")) {
				// ���� ȣ��Ʈ ���� ���� ��ü
				server = new ServerSocket(8888);
				System.out.println("������ localhost:" + 8888 + "���� ���Ƚ��ϴ�.");
			} else {
				// ���� ���� ��ü ����
				server = new ServerSocket();
				// ���� ���Ͽ� ���� ���� IP, ��Ʈ �Ҵ�
				server.bind(new InetSocketAddress(ip, port));
				System.out.println("������ " + ip + ":" + port + "���� ���Ƚ��ϴ�.");
			}
			
			
			ArrayList<Client> room = new ArrayList<>();
			//room.add(client);
			rooms.add(room);
			HashMap<String, String> setting = new HashMap<>();
			setting.put("roomName", "������");
			setting.put("roomType", "justchat");
			setting.put("roomCurrent", "0");
			setting.put("roomCapacity", "3");
			setting.put("roomInd", "0");
			setting.put("roomOwner", "system");
			setting.put("roomPassword", "");
			roomSetting.add(setting);
			
			
			Runnable thread = new Runnable() {
				@Override
				public void run() {
					BufferedReader cmdInput = new BufferedReader(new InputStreamReader(System.in));
					try {
						while (true) {
							String cmd = cmdInput.readLine();
							if (cmd.equals("shutdown") == true) {
								System.out.println("������� ���� ������ �����մϴ�.");
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
						// Ŭ���̾�Ʈ ���� ��û ����
						Socket connection = server.accept();
						Client client = new Client(connection);
						
						//System.out.println("����: " + (InetSocketAddress)connection.getRemoteSocketAddress());
					}
				}
			} catch (Exception error) {
				if (server.isClosed() == false) {
					System.out.println("���� ����� ������ �����մϴ�.");
					error.printStackTrace();
					shutdown();
				}
				System.out.println("���� ���� ����: " + server.isClosed());
				//error.printStackTrace();
			}
		} catch (Exception error) {
			error.printStackTrace();
			// ������ ���� ���µ� ���������� �ݱ�
			if (server.isClosed() == false) {
				System.out.println("���� ������ ������ �����մϴ�.");
				shutdown();
			}
		}
	}
	
	public static void shutdown() {
		try {
			System.out.println("������ ����Ǿ����ϴ�.");
			Iterator<Client> iter = clients.iterator();
			while (iter.hasNext()) {
				Client client = iter.next();
				System.out.println("���� ����: " + (InetSocketAddress)client.connection.getRemoteSocketAddress());
				
				// Ŭ���̾�Ʈ ���� ���� ����
				client.connection.close();
				iter.remove();
			}
			
			if (server != null && server.isClosed() == false) {
				// ���� ���� ����
				server.close();
			}
			
			if (threadPool != null && threadPool.isShutdown() == false) {
				// ������ Ǯ ����
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
			originalSetting.forEach((key, val) -> {
				if (key.equals("password") == false) {
					settingCopy.put(key, val);
				} else {
					// ��� �� �ɸ� ���̸� ��� false�� �ٲ㼭 Ŭ���̾�Ʈ ��
					if (originalSetting.get("password").equals("") == true) {
						settingCopy.put(key, "false");
					} else {
						// ��� �ɷ� �ִ� ���̸� ��� true�� �ٲ㼭 Ŭ���̾�Ʈ ��
						settingCopy.put(key, "true");
					}
				}
			});
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
			client.send(inputModeRooms); // Ŭ���̾�Ʈ ��ǲ ��� �� ���� ���� ���� ����
			client.sendRoom(roomsCopy); // Ŭ���̾�Ʈ�� �� ������ ����
			client.sendRoom(inputModeStandard); // Ŭ���̾�Ʈ ��ǲ ��� ���󺹱�
		}
	}
	
	public static void clientProcess(Client client, HashMap<String, String> order) {
		int clientInd = clients.indexOf(client);
		HashMap<String, String> clientInfo = clientData.get(clientInd);
		if (order.get("act") != null) {
			System.out.println("��û: " + clientInfo + " - " + order.get("act"));
			if (order.get("act").equals("create")) {
				// �� ����
				String roomType = order.get("roomType");
				String roomName = order.get("roomName");
				String roomCapacity = order.get("roomCapacity");
				int capacity = 0;
				try {
					capacity = Integer.parseInt(roomCapacity);					
				} catch (Exception error) {
					
				}
				
				String[] availableRoomType = {"justchat", "mafia", "wordbomb"};
				
				// �̹� �����Ǿ��ִ��� Ȯ��
				int joined = -1;
				for (int room = 0; room < rooms.size(); room++) {
					for (int cl = 0; cl < rooms.get(room).size(); cl++) {
						if (rooms.get(room).get(cl) == client) {
							joined = room;
						}
					}
				}
				
				if (joined == -1) {
					if (roomType != null && roomName != null && capacity >= 1) {
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
							setting.put("roomCurrent", "0");
							setting.put("roomCapacity", roomCapacity);
							setting.put("roomOwner", clientData.get(clientInd).get("identifier"));
							setting.put("roomPassword", "");
							roomSetting.add(setting);
							System.out.println("�� ����: " + clientInfo);
							
							// ���� �濡 Ŭ���̾�Ʈ ������Ŵ
							HashMap<String, String> joinMap = new HashMap<>();
							joinMap.put("act", "join");
							joinMap.put("param", Integer.toString(rooms.size() - 1));
							clientProcess(client, joinMap);
						} else {
							System.out.println(clientInfo.get("identifier") + ": �� ������ �����Ͽ����ϴ� - �߸��� �� Ÿ��");
						}
					} else {
						System.out.println(clientInfo.get("identifier") + ": �� ������ �����Ͽ����ϴ� - �� �̸��̳� �� Ÿ���� �������� �ʾҽ��ϴ�");
					}
				} else {
					System.out.println(clientInfo.get("identifier") + ": �� ������ �����Ͽ����ϴ� - �̹� �����Ǿ� �ֽ��ϴ�");
				}
			} else if (order.get("act").equals("join")) {
				// �� ����
				
				// �����ϴ� �� �ε��� Ȯ��
				int joiningRoom = -1;
				if (order.get("param") != null) {
					try {
						joiningRoom = Integer.parseInt(order.get("param"));
					} catch (NumberFormatException error) {
						joiningRoom = -1;
						System.out.println(clientInfo.get("identifier") + ": �� ���忡 �����Ͽ����ϴ� - �߸��� �� �ε���");
					}
				}
				
				// �̹� �����Ǿ��ִ��� Ȯ��
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
				
				// �濡 ������ �� �Ǿ��־����
				if (joined == -1 && joiningRoom != -1 && joiningRoom < rooms.size()) {
					ArrayList<Client> room = rooms.get(joiningRoom);
					boolean pwDone = false;
					if (roomSetting.get(joiningRoom).get("roomPassword").equals("") == true) {
						pwDone = true;
					} else if (roomSetting.get(joiningRoom).get("roomPassword").equals(order.get("password")) == true) {
						pwDone = true;
					}
					
					if (pwDone == true) {
						int capacity = Integer.parseInt(roomSetting.get(joiningRoom).get("roomCapacity"));
						if (capacity > room.size()) {
							// Ŭ���̾�Ʈ ������ �ش� �濡 �߰�
							rooms.get(joiningRoom).add(client);
							roomSetting.get(joiningRoom).replace("roomCurrent", Integer.toString(room.size()));
							System.out.println("����: " + clientInfo + " - " + joiningRoom + ": " + roomSetting.get(joiningRoom).get("roomName"));
							HashMap<String, String> joinMap = new HashMap<>();
							joinMap.put("act", "join");
							joinMap.put("roomName", roomSetting.get(joiningRoom).get("roomName"));
							if (roomSetting.get(joiningRoom).get("roomPassword").equals("") == true) {
								joinMap.put("roomSetting", "���� ����");
							} else {
								joinMap.put("roomSetting", "��ȣ ����");
							}
							joinMap.put("roomCurrent", roomSetting.get(joiningRoom).get("roomCurrent"));
							joinMap.put("roomCapacity", roomSetting.get(joiningRoom).get("roomCapacity"));
							client.send(joinMap);
							sendRoomDatas();
							
							for (int cl = 0; cl < room.size(); cl++) {
								HashMap<String, String> leaveMsg = new HashMap<>();
								leaveMsg.put("act", "joinMessage");
								leaveMsg.put("param", clientInfo.get("username"));
								
								if (room.get(cl) != client) {
									// ���� ������ �ƴ϶��
									room.get(cl).send(leaveMsg);
								}
							}
						} else {
							System.out.println(clientInfo.get("identifier") + ": ���� �� á���ϴ�");
						}
					} else {
						System.out.println(clientInfo.get("identifier") + ": ��й�ȣ�� ��ġ���� �ʽ��ϴ�");
					}
				} else {
					System.out.println(clientInfo.get("identifier") + ": �� ���忡 �����Ͽ����ϴ� - �̹� �����Ǿ��ְų� ���� ã�� �� �����ϴ�");
					System.out.println(joined + " " + joiningRoom + " " + order.get("param"));
				}
			} else if (order.get("act").equals("leave")) {
				// �� ����
				
				// �濡 �����Ǿ� �ִ��� Ȯ��
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
				
				// �����Ǿ� ���� ������
				if (joined == -1) {
					System.out.println(clientInfo.get("identifier") + ": �� ���忡 �����Ͽ����ϴ� - �����Ǿ� ���� �ʽ��ϴ�");
				} else {
					var room = rooms.get(joined);
					room.remove(room.indexOf(client));
					System.out.println(clientInfo.get("identifier") + ": ���� - " + roomSetting.get(joined).get("roomName"));
					
					// �ο� ���� ���̸�
					if (room.size() <= 0) {
						// �������� ���� �� �ƴϸ�
						if (roomSetting.get(joined).get("roomOwner").equals("system") == false) {
							rooms.remove(joined); // �� ����
							roomSetting.remove(joined); // �� ����
						}
					} else if (roomSetting.get(joined).get("roomOwner").equals(clientInfo.get("identifier"))) {
						// ���� �������� �����Ŷ��
						Client altClient = room.get(0); // �������� ���� ������� ���� �ѱ�
						roomSetting.get(joined).replace("roomOwner", clientData.get(clients.indexOf(altClient)).get("identifier"));
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
							// ���� ������ �ƴ϶��
							room.get(cl).send(leaveMsg);
						}
					}
				}
			} else if (order.get("act").equals("type")) {
				// Ÿ����
			} else if (order.get("act").equals("msg")) {
				// �޼��� ����
				String msg = order.get("param");
				if (msg != null) {
					// �濡 ������ �Ǿ��ִ��� Ȯ��
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
					
					// ������ �Ǿ��ִٸ�
					if (joined != -1) {
						ArrayList<Client> room = rooms.get(joined);
						for (int cl = 0; cl < room.size(); cl++) {
							HashMap<String, String> sendMsg = new HashMap<>();
							sendMsg.put("param", clientInfo.get("username"));
							sendMsg.put("author", clientInfo.get("identifier"));
							sendMsg.put("msg", msg);
							
							if (room.get(cl) != client) {
								// ���� ������ �ƴ϶��
								sendMsg.put("act", "msg");
							} else {
								sendMsg.put("act", "selfmsg");
							}
							room.get(cl).send(sendMsg);
						}
						System.out.println("�޼��� ����: " + clientInfo.get("identifier"));
					} else {
						System.out.println(clientInfo.get("identifier") + ": �޼��� ���ۿ� �����Ͽ����ϴ� - �����Ǿ� ���� �ʽ��ϴ�");
					}
				} else {
					System.out.println(clientInfo.get("identifier") + ": �޼��� ���ۿ� �����Ͽ����ϴ� - ���� �޼����� �����ϴ�");
				}
			} else if (order.get("act").equals("reload")) {
				ArrayList<HashMap<String, String>> roomsCopy = new ArrayList<>();
				
				for (int ind = 0; ind < roomSetting.size(); ind++) {
					HashMap<String, String> originalSetting = roomSetting.get(ind);
					HashMap<String, String> settingCopy = new HashMap<>();
					settingCopy.put("current", Integer.toString(rooms.get(ind).size()));
					originalSetting.forEach((key, val) -> {
						if (key.equals("password") == false) {
							settingCopy.put(key, val);
						} else {
							// ��� �� �ɸ� ���̸� ��� false�� �ٲ㼭 Ŭ���̾�Ʈ ��
							if (originalSetting.get("password").equals("") == true) {
								settingCopy.put(key, "false");
							} else {
								// ��� �ɷ� �ִ� ���̸� ��� true�� �ٲ㼭 Ŭ���̾�Ʈ ��
								settingCopy.put(key, "true");
							}
						}
					});
					roomsCopy.add(settingCopy);
				}
				
				HashMap<String, String> inputModeRooms = new HashMap<>();
				inputModeRooms.put("act", "inputMode");
				inputModeRooms.put("param", "rooms");
				ArrayList<HashMap<String, String>> inputModeStandard = new ArrayList<>();
				HashMap<String, String> inputModeStandardSetting = new HashMap<>();
				inputModeStandardSetting.put("inputMode", "standard");
				inputModeStandard.add(inputModeStandardSetting);
				
				client.send(inputModeRooms); // Ŭ���̾�Ʈ ��ǲ ��� �� ���� ���� ���� ����
				client.sendRoom(roomsCopy); // Ŭ���̾�Ʈ�� �� ������ ����
				client.sendRoom(inputModeStandard); // Ŭ���̾�Ʈ ��ǲ ��� ���󺹱�
			}
		} else {
			System.out.println("��û ����: " + clientInfo + " - ��û ������ �����ϴ�");
		}
	}
}