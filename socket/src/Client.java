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
		Runnable thread = new Runnable() {
			@Override
			// ������ ����
			public void run() {
				try {
					// while�� ����ؼ� �Է��� ����
					while (true) {
						// ����� Ŭ���̾�Ʈ�� �Է��� �޾ƿ� (Ŭ���̾�Ʈ���� ���� ȭ��)
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
	}
	
	// Ŭ���̾�Ʈ�� �޼��� ����
	public void send(HashMap<String, String> freight) {
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						// ����� Ŭ���̾�Ʈ�� ����� �޾ƿ� (�� ��� ��Ʈ�� ������ Ŭ���̾�Ʈ�� ������)
						OutputStream output = connection.getOutputStream();
						
						// ������Ʈ�� ��� ��Ʈ��
						ObjectOutputStream outputOBJ = new ObjectOutputStream(output);
						
						// ȭ��(�ൿ ��ųʸ�)�� ����
						outputOBJ.writeObject(freight);
						outputOBJ.flush();
					}
				} catch (Exception error) {
					// Ŭ���̾�Ʈ�� ������ ����, ���� ����
					Server.clients.remove(Client.this);
				}
			}
		};
	}
}