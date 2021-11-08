import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	 
     public static void main(String args[]) {
    	 					
    	 String hostname = "JadeELB-b4f90d338140f84f.elb.us-east-1.amazonaws.com";
         int port = 10000;
         
    	 try (Socket socket = new Socket(hostname, port)) {

             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true);
             
            InputStreamReader in = new InputStreamReader(socket.getInputStream());
  			BufferedReader bf = new BufferedReader(in);
  			String str;

  			Scanner sc = new Scanner(System.in);
             int number;

             do {
                 number = sc.nextInt();

                 writer.println(number);
                 
               //Receive echoed back message here
     			str = bf.readLine();
     			System.out.println("Client printing : " + str);
     			
             } while (number != 999);

             socket.close();

         } catch (UnknownHostException ex) {

             System.out.println("Server not found: " + ex.getMessage());

         } catch (IOException ex) {

             System.out.println("I/O error: " + ex.getMessage());
         }
     }
     
}
