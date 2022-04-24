import java.net.*;
import java.io.*;
import java.util.*;

// 서버 측에서 클라이언트 처리를 위한 클래스 (서버에서 돌아감)
public class Client {
	// 클라이언트와의 연결 (소켓)
	Socket connection;
	
	public ObjectInputStream input;
	public ObjectOutputStream output;
	
	Client(Socket connection) throws IOException {
		this.connection = connection;
		// 중요! inputStream은 연결된 outputStream이 나올 때까지 대기타다가 생성되기 때문에 input을 뒤에 두기
		output = new ObjectOutputStream(connection.getOutputStream());
		input = new ObjectInputStream(connection.getInputStream());
		recv();
	}
	
	public static void main(String[] args) throws IOException {
		
	}
	
	// 클라이언트에서 입력 받음
	public void recv() {
		// 스레드 풀 사용을 위해 Runnable로 스레드 만듬
		Runnable thread = new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			// 스레드 실행
			public void run() {
				System.out.println(connection.getInetAddress() + "을 듣는중..");
				try {
					// while로 계속해서 입력을 받음
					while (true) {
						if (connection.isClosed() == false) {
							var freight = (HashMap<String, String>) input.readObject();

							// 서버에 클라이언트가 요청한 작업처리를 맏김
							//System.out.println(freight.get("act") + "를 받음");
							Server.clientProcess(Client.this, freight);
						} else {
							System.out.println("통신을 종료합니다");
							break;
						}
					}
				} catch (SocketException error) {
					HashMap<String, String> clInfo = Server.clientData.get(Server.clients.indexOf(Client.this));
					System.out.println("접속 해제 - 소켓 종료: " + clInfo.get("identifier"));
				} catch (Exception error) {
					// 입력받은게 딕셔너리 형태가 아니거나 수신이 잘못됨
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
	
	// 클라이언트로 메세지 전송
	public void send(HashMap<String, String> freight) {
		try {
			// 화물(행동 딕셔너리)을 보냄
			output.writeObject(freight);
			output.flush();
		} catch (Exception error) {
			// 클라이언트와 연결이 끊김, 연결 종료
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