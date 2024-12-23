import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class LoginWindow extends JFrame {
    public LoginWindow(Connection con) {
        setSize(400, 300);
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(128, 128, 128));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setForeground(new Color(50, 50, 50));
        panel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBackground(new Color(200, 200, 200)); // Set grey background
        emailField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        emailField.setPreferredSize(new Dimension(360, 30));
        panel.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(50, 50, 50));
        panel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBackground(new Color(200, 200, 200)); // Set grey background
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        passwordField.setPreferredSize(new Dimension(360, 30));
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    String query = "SELECT * FROM auctest WHERE email = ? AND password = ?";
                    PreparedStatement pstmt = con.prepareStatement(query);
                    pstmt.setString(1, email);
                    pstmt.setString(2, password);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        Auctest.Global.userEmail = email;
                        Auctest.Global.userId = rs.getInt("user_id");
                        if (Auctest.Global.winnerName != null) {
                            JOptionPane.showMessageDialog(null, "Congratulations, " + Auctest.Global.winnerName + "! You have won an auction.\n\nThe seller's name is " + Auctest.Global.sellerName + " and their email is " + Auctest.Global.sellerEmail + ".");
                        }
                        JOptionPane.showMessageDialog(null, "Login successful!");
                        dispose();

                        BidSellWindow bidSellWindow = new BidSellWindow(Auctest.Global.userId, con);
                        bidSellWindow.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid email or password!");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        loginButton.setBackground(new Color(50, 50, 50));
        loginButton.setForeground(new Color(240, 240, 240));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(loginButton);

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
        panel.add(registerButton);

        add(panel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }
}
