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
	
	public static void serverStart(String ip, int port) {
		try {
			if (ip == null) {
				// ���� ȣ��Ʈ ���� ���� ��ü
				ServerSocket server = new ServerSocket(8888);
			} else {
				// ���� ���� ��ü ����
				ServerSocket server = new ServerSocket();
				// ���� ���Ͽ� ���� ���� IP, ��Ʈ �Ҵ�
				server.bind(new InetSocketAddress(ip, port));
			}
			
			while (true) {
				// Ŭ���̾�Ʈ ���� ��û ����
				Socket connection = server.accept();
				Client client = new Client(connection);
				clients.add(client);
				System.out.println("����: " + (InetSocketAddress)connection.getRemoteSocketAddress());
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