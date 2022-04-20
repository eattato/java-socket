import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

// 서버 켜기/끄기 제어 및 클라이언트 접속 관리를 위한 클래스
public class Server {
	// 스레드 풀 생성
	public static ExecutorService threadPool;
	
	// 클라이언트 리스트 생성
	public static ArrayList<Client> clients = new ArrayList<Client>();;
	
	// 서버 소켓 생성
	static ServerSocket server;
	
	public static void main(String[] args) throws IOException {
		serverStart("", 8888);
	}
	
	public static void serverStart(String ip, int port) throws SocketException {
		try {
			if (ip.equals("")) {
				// 로컬 호스트 서버 소켓 객체
				server = new ServerSocket(8888);
				System.out.println("서버가 localhost:" + 8888 + "에서 열렸습니다.");
			} else {
				// 서버 소켓 객체 생성
				server = new ServerSocket();
				// 서버 소켓에 서버 컴의 IP, 포트 할당
				server.bind(new InetSocketAddress(ip, port));
				System.out.println("서버가 " + ip + ":" + port + "에서 열렸습니다.");
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
						// 클라이언트 연결 요청 수락
						Socket connection = server.accept();
						Client client = new Client(connection);
						clients.add(client);
						System.out.println("접속: " + (InetSocketAddress)connection.getRemoteSocketAddress());
					} else {
						System.out.println("클라이언트 접속 허가 루프 종료");
						break;
					}
				}
			} catch (Exception error) {
				System.out.println("서버 소켓 상태: " + server.isClosed());
			}
		} catch (Exception error) {
			error.printStackTrace();
			// 서버에 오류 났는데 열려있으면 닫기
			if (server.isClosed() == false) {
				shutdown();
			}
		}
	}
	
	public static void shutdown() {
		try {
			System.out.println("서버가 종료되었습니다.");
			Iterator<Client> iter = clients.iterator();
			while (iter.hasNext()) {
				Client client = iter.next();
				System.out.println("접속 해제: " + (InetSocketAddress)client.connection.getRemoteSocketAddress());
				
				// 클라이언트 소켓 연결 종료
				client.connection.close();
				iter.remove();
			}
			
			if (server != null && server.isClosed() == false) {
				// 서버 소켓 종료
				server.close();
			}
			
			if (threadPool != null && threadPool.isShutdown() == false) {
				// 스레드 풀 종료
				threadPool.shutdown();
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public static void clientProcess(Client client, HashMap<String, String> order) {
		if (order.get("act") == "create") {
			// 방 생성
		} else if (order.get("act") == "join") {
			// 방 입장
		} else if (order.get("act") == "leave") {
			// 방 퇴장
		} else if (order.get("act") == "type") {
			// 타이핑
		}
	}
}