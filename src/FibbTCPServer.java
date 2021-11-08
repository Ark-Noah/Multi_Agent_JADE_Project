import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class FibbTCPServer {
	public static void main(String[] args) throws Exception {	
		int port = 10000;
		 try (ServerSocket serverSocket = new ServerSocket(port)) {
	            System.out.println("Server is listening on port " + port);
	            while (true) {
	                Socket socket = serverSocket.accept();
	                System.out.println("New client connected");
	                new ServerThread(socket).start();
	            }
	        } catch (IOException ex) {
	            System.out.println("Server exception: " + ex.getMessage());
	            ex.printStackTrace();
	        }
	}
}
  class ServerThread extends Thread {
		private Socket socket;
		
		public ServerThread(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				ArrayList<Long> fibonacci = new ArrayList<Long>();
				InputStream input = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				PrintStream writer = new PrintStream(socket.getOutputStream());

				int number;
				do {
					number = Integer.parseInt(reader.readLine());
					fibonacci = calculateFibbonacci(number);
					writer.println(fibonacci);
					writer.flush();
					System.out.println("Response Sent");
				} while (true);
		        
			} catch (IOException ex) {
				System.out.println("Server exception: " + ex.getMessage());
				ex.printStackTrace();
			}
		}

		private ArrayList<Long> calculateFibbonacci(int number) {
	        // Print the first N numbers
			ArrayList<Long> fib = new ArrayList<Long>();
	        for (int i = 0; i < number; i++) {
	        	fib.add(fib(i));
	        }
			return fib;
		}

		static long fib(int n){
	        if (n <= 1)
	            return n;
	  
	        return fib(n - 1)
	            + fib(n - 2);
	    }
  }
