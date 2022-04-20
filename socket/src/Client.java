import java.net.*;
import java.io.*;
import java.util.*;

// ���� ������ Ŭ���̾�Ʈ ó���� ���� Ŭ���� (�������� ���ư�)
public class Client {
	// Ŭ���̾�Ʈ���� ���� (����)
	Socket connection;
	
	Client(Socket connection) {
		this.connection = connection;
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
				try {
					// while�� ����ؼ� �Է��� ����
					while (true) {
						// ����� Ŭ���̾�Ʈ�� �Է� ��Ʈ���� �޾ƿ� (Ŭ���̾�Ʈ���� ���� ȭ��)
						InputStream input = connection.getInputStream();
						
						// ������Ʈ�� �Է� ��Ʈ��
						ObjectInputStream inputOBJ = new ObjectInputStream(input);
						@SuppressWarnings("unchecked")
						HashMap<String, String> freight = (HashMap<String, String>) inputOBJ.readObject();
						
						// ������ Ŭ���̾�Ʈ�� ��û�� �۾�ó���� ����
						Server.clientProcess(Client.this, freight);
					}
				} catch (Exception error) {
					// �Է¹����� ��ųʸ� ���°� �ƴϰų� ������ �߸���
				}
			}
		};
		Server.threadPool.submit(thread);
	}
	
	// Ŭ���̾�Ʈ�� �޼��� ����
	public void send(HashMap<String, String> freight) {
		Thread thread = new Thread() {
			public void run() {
				try {
					// ����� Ŭ���̾�Ʈ�� ��� ��Ʈ���� �޾ƿ� (�� ��� ��Ʈ�� ������ Ŭ���̾�Ʈ�� ������)
					OutputStream output = connection.getOutputStream();
					
					// ������Ʈ�� ��� ��Ʈ��
					ObjectOutputStream outputOBJ = new ObjectOutputStream(output);
					
					// ȭ��(�ൿ ��ųʸ�)�� ����
					outputOBJ.writeObject(freight);
					outputOBJ.flush();
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