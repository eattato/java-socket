import java.net.*;
import java.io.*;
import java.util.*;

// ���� ������ Ŭ���̾�Ʈ ó���� ���� Ŭ���� (�������� ���ư�)
public class Client {
	// Ŭ���̾�Ʈ���� ���� (����)
	Socket connection;
	
	public ObjectInputStream input;
	public ObjectOutputStream output;
	
	Client(Socket connection) throws IOException {
		this.connection = connection;
		// �߿�! inputStream�� ����� outputStream�� ���� ������ ���Ÿ�ٰ� �����Ǳ� ������ input�� �ڿ� �α�
		output = new ObjectOutputStream(connection.getOutputStream());
		input = new ObjectInputStream(connection.getInputStream());
		recv();
	}
	
	public static void main(String[] args) throws IOException {
		
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
							var freight = (HashMap<String, String>) input.readObject();

							// ������ Ŭ���̾�Ʈ�� ��û�� �۾�ó���� ����
							//System.out.println(freight.get("act") + "�� ����");
							Server.clientProcess(Client.this, freight);
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
						}						
					}
					
					int clInd = Server.clients.indexOf(Client.this);
					Server.clientData.remove(clInd);
					Server.clients.remove(clInd);
					
					int joined = -1;
					for (int room = 0; room < Server.rooms.size(); room++) {
						for (int cl = 0; cl < Server.rooms.get(room).size(); cl++) {
							if (Server.rooms.get(room).get(cl) == Client.this) {
								joined = room;
							}
						}
					}
					
					if (joined != -1) {
						Server.rooms.get(clInd).remove(Server.rooms.get(clInd).indexOf(Client.this));
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
				Server.clients.remove(Client.this);
				connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
} 