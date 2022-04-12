import java.net.*;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.ArrayList;

public class Server {
	// 스레드 풀 생성
	public static ExecutorService threadPool;
	
	// 클라이언트 리스트 생성
	public static ArrayList<Client> clients = new ArrayList<Client>();;
	
	// 서버 소켓 생성
	ServerSocket server;
	
	public static void main(String[] args) throws IOException {
		
	}
	
	public void serverStart(String ip, int port) {
		try {
			if (ip == null) {
				// 로컬 호스트 서버 소켓 객체
				ServerSocket server = new ServerSocket(8888);
			} else {
				// 서버 소켓 객체 생성
				ServerSocket server = new ServerSocket();
				// 서버 소켓에 서버 컴의 IP, 포트 할당
				server.bind(new InetSocketAddress(ip, port));
			}
			
			while (true) {
				// 클라이언트 연결 요청 수락
				Socket connection = server.accept();
				Client client = new Client(connection);
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}