import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class Auctest extends JFrame {

    // Global variables class
    public static class Global {
        public static String userEmail;
        public static int userId;
        public static String winnerName;
        public static String sellerName;
        public static String sellerEmail;
        public static double amount;
    }

    Connection con;

    public Auctest() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "root");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setSize(600, 400);
        setTitle("Main Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(128, 128, 128));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(400, -50));
        spacerPanel.setOpaque(false);
        panel.add(spacerPanel);

        JLabel appNameLabel = new JLabel("AuctioNear");
        appNameLabel.setFont(new Font("Arial", Font.BOLD, 32));
        appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        appNameLabel.setForeground(new Color(0, 0, 0));
        panel.add(appNameLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterWindow registerWindow = new RegisterWindow(con);
                registerWindow.setVisible(true);
                dispose();
            }
        });
        registerButton.setBackground(new Color(50, 50, 50));
        registerButton.setForeground(new Color(240, 240, 240));
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(registerButton);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginWindow loginWindow = new LoginWindow(con);
                loginWindow.setVisible(true);
                dispose();
            }
        });
        loginButton.setBackground(new Color(50, 50, 50));
        loginButton.setForeground(new Color(240, 240, 240));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(loginButton);

        panel.add(buttonPanel);

        add(panel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Auctest().setVisible(true);
        });
    }
}
