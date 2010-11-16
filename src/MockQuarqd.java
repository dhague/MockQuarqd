import java.io.*;
import java.net.*;

public class MockQuarqd {
    public static void main(String[] args) throws IOException {

    	if (args.length != 2) {
    		System.out.println("Usage: "+MockQuarqd.class.getSimpleName()+" <port number> <data file>");
    		System.exit(1);
    	}
    	
    	int socketNumber = Integer.parseInt(args[0]);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(socketNumber);
        } catch (IOException e) {
            System.err.println("Could not listen on port: "+socketNumber+".");
            System.exit(1);
        }

        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        try {
            while (true)
            	writeFile(args[1], out);
        }
        finally {
	        out.close();
	        clientSocket.close();
	        serverSocket.close();
        }
    }
    
    static void writeFile(String filename, PrintWriter out)
    {
        File file = new File(filename);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;

        try {
          fis = new FileInputStream(file);

          // Here BufferedInputStream is added for fast reading.
          bis = new BufferedInputStream(fis);
          dis = new DataInputStream(bis);

          // dis.available() returns 0 if the file does not have more lines.
          while (dis.available() != 0) {

          // this statement reads the line from the file and print it to
            // the console.
            out.println(dis.readLine());
            try {
				Thread.currentThread().sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          }

          // dispose all the resources after using them.
          fis.close();
          bis.close();
          dis.close();

        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
}
