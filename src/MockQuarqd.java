import java.io.*;
import java.net.*;

public class MockQuarqd {
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("Usage: " + MockQuarqd.class.getSimpleName()
                    + " <port number> <data file>");
            System.exit(1);
        }

        int socketNumber = Integer.parseInt(args[0]);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(socketNumber);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + socketNumber
                    + ".");
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
        } finally {
            out.close();
            clientSocket.close();
            serverSocket.close();
        }
    }

    static void writeFile(String filename, PrintWriter out) {
        File file = new File(filename);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        double prevTimestamp = 0.0;
        
        try {
            fis = new FileInputStream(file);

            // Here BufferedInputStream is added for fast reading.
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);

            // dis.available() returns 0 if the file does not have more lines.
            while (dis.available() != 0) {

                // this statement reads the line from the file and print it to
                // the console. We need to parse the timestamp from the line so
                // we can wait the correct time before emitting the line
                String line = dis.readLine();
                
                double timestamp = parseTimestamp(line);
                if (timestamp != Double.NaN) {

                    try {
                        long delay_ms;
                        if (prevTimestamp != 0.0) {
                            delay_ms = (long) ((timestamp - prevTimestamp) * 1000.0);
                        } else {
                            delay_ms = 1000;
                        }
                        prevTimestamp = timestamp;
                        // Thread.currentThread();
                        Thread.sleep(delay_ms);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                out.println(line);
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

    final static String timestamp = "timestamp='";
    final static int tsLen = timestamp.length();

    private static double parseTimestamp(String line) {
        /* Lines are of the form
         *   <Speed id='42881p' timestamp='1294080070.97' RPM='123.13' />
         * or
         *   <Power id='42881p' timestamp='1294080071.46' watts='88' />
         */
        int start = line.indexOf(timestamp);
        if (start == -1) return Double.NaN;
        start += tsLen;
        
        String timeStr = line.substring(start, line.indexOf("'", start));
        
        return Double.parseDouble(timeStr);
    }
}
