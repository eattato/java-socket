import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.ArrayList;

public class Server {
	// ������ Ǯ ����
	public static ExecutorService threadPool;
	
	// Ŭ���̾�Ʈ ����Ʈ ����
	public static ArrayList<Client> clients = new ArrayList<Client>();;
	
	// ���� ���� ����
	ServerSocket server;
	
	public static void main(String[] args) throws IOException {
		
	}
	
	public void serverStart(String ip, int port) {
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
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}