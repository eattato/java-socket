import java.net.*;
import java.io.*;
import java.util.*;

// ���� ������ Ŭ���̾�Ʈ ó���� ���� Ŭ���� (�������� ���ư�)
public class Client {
	// Ŭ���̾�Ʈ���� ���� (����)
	Socket connection;
	
	public ObjectInputStream input;
	public ObjectOutputStream output;
	public String inputMode = "standard";
	
	Client(Socket connection) throws IOException {
		this.connection = connection;
		// �߿�! inputStream�� ����� outputStream�� ���� ������ ���Ÿ�ٰ� �����Ǳ� ������ input�� �ڿ� �α�
		output = new ObjectOutputStream(connection.getOutputStream());
		input = new ObjectInputStream(connection.getInputStream());
		recv();
	}
	
	public static void main(String[] args) throws IOException {
		
	}
	
	public void disconnect() {
		// �ִ� �� ������
		HashMap<String, String> leave = new HashMap<>();
		leave.put("act", "leave");
		Server.clientProcess(Client.this, leave);
		
		int clInd = Server.clients.indexOf(Client.this);
		Server.clientData.remove(clInd);
		Server.clients.remove(clInd);
		//Server.clients.remove(Client.this);
	}
	
	// Ŭ���̾�Ʈ���� �Է� ����
	public void recv() {
		// ������ Ǯ ����� ���� Runnable�� ������ ����
		Runnable thread = new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			// ������ ����
			public void run() {
				System.out.println(connection.getInetAddress() + "�� �����..");
				try {
					// while�� ����ؼ� �Է��� ����
					while (true) {
						if (connection.isClosed() == false) {
							// �⺻ �Է� ���
							if (inputMode.equals("standard") == true) {
								var freight = (HashMap<String, String>) input.readObject();
								
								// �Է� ��� ����
								if (freight.get("act").equals("inputMode") == true) {
									inputMode = freight.get("param");
								} else {
									// ������ Ŭ���̾�Ʈ�� ��û�� �۾�ó���� ����
									//System.out.println(freight.get("act") + "�� ����");
									Server.clientProcess(Client.this, freight);									
								}
							} else if (inputMode.equals("rooms") == true) {
								// roomSetting ��ü�� ����
								var roomSetting = (ArrayList<HashMap<String, String>>) input.readObject();
								
								// �Է� ��� ���� (�������� �� ������ �� �游 ���� ����� Ű�� inputMode�� �ְ� ���� �ٲ� ���� ������ �ٲ�)
								if (roomSetting.get(0).containsKey("inputMode") && roomSetting.size() == 0 ) {
									inputMode = roomSetting.get(0).get("inputMode");
								}
							}
						} else {
							System.out.println("����� �����մϴ�");
							break;
						}
					}
				} catch (SocketException error) {
					HashMap<String, String> clInfo = Server.clientData.get(Server.clients.indexOf(Client.this));
					System.out.println("���� ���� - ���� ����: " + clInfo.get("identifier"));
				} catch (Exception error) {
					// �Է¹����� ��ųʸ� ���°� �ƴϰų� ������ �߸���
					if (connection.isClosed() == false) {
						error.printStackTrace();						
					}
				} finally {
					if (connection.isClosed() == false) {
						try {
							connection.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							disconnect();
						}
					}
				}
			}
		};
		Server.threadPool.submit(thread);
	}
	
	// Ŭ���̾�Ʈ�� �޼��� ����
	public void send(HashMap<String, String> freight) {
		try {
			// ȭ��(�ൿ ��ųʸ�)�� ����
			output.writeObject(freight);
			output.flush();
		} catch (Exception error) {
			// Ŭ���̾�Ʈ�� ������ ����, ���� ����
			try {
				connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				disconnect();
			}
		}
	}
} 