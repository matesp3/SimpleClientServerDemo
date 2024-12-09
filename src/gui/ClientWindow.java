package gui;

import sockets.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ClientWindow extends JFrame {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 300;
    private static final Color BG_COLOR = new Color(74, 91, 185);
    private JTextArea console;
    private Client client;

    public ClientWindow() {
        this.setSize(WIDTH, HEIGHT);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Client side");
        this.setLayout(new BorderLayout());

        JPanel panel = this.createVisual(WIDTH, HEIGHT);
        this.add(panel, BorderLayout.CENTER);
//      -----------------
        this.client = new Client();
        Component parentFrame = this;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int res = JOptionPane.showConfirmDialog(parentFrame, "Exit app?", "Close main window?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    try {
                        client.closeConnection();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    finally {
                        System.exit(0);
                    }
                }
            }
        });
        this.refreshConsole();
//        ---------------
        this.setVisible(true);
    }

    private void refreshConsole() {
        try {
            FileReader fr = new FileReader("client-msg.txt");
            BufferedReader bf = new BufferedReader(fr);
            String line = bf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = bf.readLine();
            }
            this.console.setText(sb.toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JPanel createVisual(int width, int height) {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        this.console = new JTextArea();
        this.console.setPreferredSize(new Dimension(width, height - 50));
        this.console.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(this.console);
        main.add(scrollPane);

        JPanel panelForInput = new JPanel();
        panelForInput.setPreferredSize(new Dimension(width, 50));
        panelForInput.setBackground(BG_COLOR);
//        JButton btnRefresh = new JButton("Reload file content");
//        btnRefresh.addActionListener(e -> refreshConsole());
//        panelForInput.add(btnRefresh);
        JTextField userInput = new JTextField(15);
        panelForInput.add(userInput);
        JButton btnSend = new JButton("Send");
        btnSend.addActionListener(e -> {
            if (!userInput.getText().isEmpty()) {
                try {
                    client.writeMessage(userInput.getText());
                    userInput.setText("");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        panelForInput.add(btnSend);

        main.add(panelForInput);
        return main;
    }
}
