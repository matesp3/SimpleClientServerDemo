package sockets;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.GregorianCalendar;


public class Server {
    public static final String STOP_STR = "##";
    public static final int PORT = 3030;
    private Socket clientSocket;
    private ServerSocket server;
    private DataInputStream in;
    private FileWriter fw;

    public Server(String logFileName) {
        try {
            server = new ServerSocket(PORT);
            System.out.println("Server is now ON");
            System.out.println("Waiting for client connection...");
            initConnections(logFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initConnections(String fileName) throws IOException {
        this.clientSocket = server.accept();
        System.out.println("Client accepted!");
        this.fw = new FileWriter(fileName, true);
        in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        readMessages();
        System.out.println("Closing connection...");
        close();
    }

    private void close() throws IOException {
        in.close();
        this.fw.close();
        server.close();
    }

    private void readMessages() throws IOException {
        String line = "";
        while(!line.equals(STOP_STR)) {
            line = in.readUTF();
            if (!line.equals(STOP_STR))
                this.logToFile(line);
        }
    }

    private void logToFile(String line) throws IOException {
        this.fw.append(
                String.format("[%s - PoljakMatej]: %s\n", calendarToStr(new GregorianCalendar()), line)
        );
    }

    private static String calendarToStr(GregorianCalendar cal) {
        if (cal == null)
            return null;
        return String.format("%04d-%02d-%02d %02d:%02d", cal.get(GregorianCalendar.YEAR),
                cal.get(GregorianCalendar.MONTH) + 1,
                cal.get(GregorianCalendar.DAY_OF_MONTH),
                cal.get(GregorianCalendar.HOUR_OF_DAY),
                cal.get(GregorianCalendar.MINUTE));
    }

    /**
     * Creates instance of <code>GregorianCalendar</code> from given string representation
     * @param str format <code>'YYYY-MM-DD hh:mi'</code>
     * @return converted <code>GregorianCalendar</code>
     */
    private static GregorianCalendar strToCalendar(String str) {
        if (str == null || str.isBlank())
            return null;
        str = str.trim();
        String[] parts = str.split(" ");
        if (parts.length != 2)
            return null;
        GregorianCalendar cal = new GregorianCalendar();
        String[] dateParts = parts[0].split("-");
        if (dateParts.length != 3)
            return null;
        cal.set(GregorianCalendar.YEAR, Integer.parseInt(dateParts[0]));
        cal.set(GregorianCalendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
        cal.set(GregorianCalendar.DAY_OF_MONTH, Integer.parseInt(dateParts[2]));
        String[] timeParts = parts[1].split(":");
        if (timeParts.length != 2) {
            cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
            cal.set(GregorianCalendar.MINUTE, 0);
            cal.set(GregorianCalendar.SECOND, 0);
            return cal;
        }
        cal.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        cal.set(GregorianCalendar.MINUTE, Integer.parseInt(timeParts[1]));
        cal.set(GregorianCalendar.SECOND, 0);
        return cal;
    }

    public static void main(String[] args) throws IOException {
        new Server("client-msg.txt");
    }
}
