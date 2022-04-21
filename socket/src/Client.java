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
			@Override
			// ������ ����
			public void run() {
				System.out.println(connection.getInetAddress() + "�� �����..");
				try {
					// while�� ����ؼ� �Է��� ����
					while (true) {
						@SuppressWarnings("unchecked")
						HashMap<String, String> freight = (HashMap<String, String>)input.readObject();
						
						// ������ Ŭ���̾�Ʈ�� ��û�� �۾�ó���� ����
						System.out.println(freight.get("act") + "�� ����");
						Server.clientProcess(Client.this, freight);
					}
				} catch (Exception error) {
					// �Է¹����� ��ųʸ� ���°� �ƴϰų� ������ �߸���
					error.printStackTrace();
				}
			}
		};
		Server.threadPool.submit(thread);
	}
	
	// Ŭ���̾�Ʈ�� �޼��� ����
	public void send(HashMap<String, String> freight) {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
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
					}
					Server.clients.remove(Client.this);
				}
			}
		};
		Server.threadPool.submit(thread);
		
		//Runnable thread = new Runnable() {
			//@Override
			//public void run() {
				//try {
					//while (true) {
						// ����� Ŭ���̾�Ʈ�� ��� ��Ʈ���� �޾ƿ� (�� ��� ��Ʈ�� ������ Ŭ���̾�Ʈ�� ������)
						//OutputStream output = connection.getOutputStream();
						
						// ������Ʈ�� ��� ��Ʈ��
						//ObjectOutputStream outputOBJ = new ObjectOutputStream(output);
						
						// ȭ��(�ൿ ��ųʸ�)�� ����
						//outputOBJ.writeObject(freight);
						//outputOBJ.flush();
					//}
				//} catch (Exception error) {
					// Ŭ���̾�Ʈ�� ������ ����, ���� ����
					//Server.clients.remove(Client.this);
				//}
			//}
		//};
		//Server.threadPool.submit(thread);
	}
}