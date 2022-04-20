import java.net.*;
import java.io.*;
import java.util.*;

// 서버 측에서 클라이언트 처리를 위한 클래스 (서버에서 돌아감)
public class Client {
	// 클라이언트와의 연결 (소켓)
	Socket connection;
	
	Client(Socket connection) {
		this.connection = connection;
	}
	
	public static void main(String[] args) throws IOException {
		
	}
	
	// 클라이언트에서 입력 받음
	public void recv() {
		// 스레드 풀 사용을 위해 Runnable로 스레드 만듬
		Runnable thread = new Runnable() {
			@Override
			// 스레드 실행
			public void run() {
				try {
					// while로 계속해서 입력을 받음
					while (true) {
						// 연결된 클라이언트의 입력 스트림을 받아옴 (클라이언트에서 보낸 화물)
						InputStream input = connection.getInputStream();
						
						// 오브젝트용 입력 스트림
						ObjectInputStream inputOBJ = new ObjectInputStream(input);
						@SuppressWarnings("unchecked")
						HashMap<String, String> freight = (HashMap<String, String>) inputOBJ.readObject();
						
						// 서버에 클라이언트가 요청한 작업처리를 맏김
						Server.clientProcess(Client.this, freight);
					}
				} catch (Exception error) {
					// 입력받은게 딕셔너리 형태가 아니거나 수신이 잘못됨
				}
			}
		};
		Server.threadPool.submit(thread);
	}
	
	// 클라이언트로 메세지 전송
	public void send(HashMap<String, String> freight) {
		Thread thread = new Thread() {
			public void run() {
				try {
					// 연결된 클라이언트의 출력 스트림을 받아옴 (이 출력 루트로 보내면 클라이언트에 보내짐)
					OutputStream output = connection.getOutputStream();
					
					// 오브젝트용 출력 스트림
					ObjectOutputStream outputOBJ = new ObjectOutputStream(output);
					
					// 화물(행동 딕셔너리)을 보냄
					outputOBJ.writeObject(freight);
					outputOBJ.flush();
				} catch (Exception error) {
					// 클라이언트와 연결이 끊김, 연결 종료
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
						// 연결된 클라이언트의 출력 스트림을 받아옴 (이 출력 루트로 보내면 클라이언트에 보내짐)
						//OutputStream output = connection.getOutputStream();
						
						// 오브젝트용 출력 스트림
						//ObjectOutputStream outputOBJ = new ObjectOutputStream(output);
						
						// 화물(행동 딕셔너리)을 보냄
						//outputOBJ.writeObject(freight);
						//outputOBJ.flush();
					//}
				//} catch (Exception error) {
					// 클라이언트와 연결이 끊김, 연결 종료
					//Server.clients.remove(Client.this);
				//}
			//}
		//};
		//Server.threadPool.submit(thread);
	}
}