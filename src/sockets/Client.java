package sockets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import static sockets.Server.STOP_STR;

public class Client {
    private Socket socket;
    private DataOutputStream out;
    private Scanner in;

    public Client() {
        try {
            socket = new Socket("127.0.0.1", Server.PORT);
            out = new DataOutputStream(socket.getOutputStream());
//            in = new Scanner(System.in);
//            writeMessages();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeMessages() throws IOException {
        String line = "";
        // tu sa niekde musi otvorit GUI
        while(!line.equals(STOP_STR)) {
            line = in.nextLine();
            out.writeUTF(line);
        }
        closeConnection();
    }

    public void writeMessage(String message) throws IOException {
        this.out.writeUTF(message);
    }

    public void closeConnection() throws IOException {
        this.writeMessage(STOP_STR);
        socket.close();
        out.close();
//        in.close();
    }

    public static void main(String[] args) {
        Client client = new Client();
        // todo vypisat do gui zo suboru 'client-msg.txt' predchadzajuce spravy ako v messengeri
    }
}
