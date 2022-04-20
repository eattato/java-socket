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
	public static ArrayList<Client> clients = new ArrayList<Client>();;
	
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
			
			Thread thread = new Thread() {
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
			thread.start();
			try {
				while (server.isClosed() == false) {
					if (server.isClosed() == false) {
						// Ŭ���̾�Ʈ ���� ��û ����
						Socket connection = server.accept();
						Client client = new Client(connection);
						clients.add(client);
						System.out.println("����: " + (InetSocketAddress)connection.getRemoteSocketAddress());
					} else {
						System.out.println("Ŭ���̾�Ʈ ���� �㰡 ���� ����");
						break;
					}
				}
			} catch (Exception error) {
				System.out.println("���� ���� ����: " + server.isClosed());
			}
		} catch (Exception error) {
			error.printStackTrace();
			// ������ ���� ���µ� ���������� �ݱ�
			if (server.isClosed() == false) {
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

	public static void clientProcess(Client client, HashMap<String, String> order) {
		if (order.get("act") == "create") {
			// �� ����
		} else if (order.get("act") == "join") {
			// �� ����
		} else if (order.get("act") == "leave") {
			// �� ����
		} else if (order.get("act") == "type") {
			// Ÿ����
		}
	}
}