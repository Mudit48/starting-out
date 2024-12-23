import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class RegisterWindow extends JFrame {
    public RegisterWindow(Connection con) {

        setSize(400, 400);
        setTitle("Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(128, 128, 128));

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(50, 50, 50));
        panel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setBackground(new Color(200, 200, 200)); // Set grey background
        nameField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setForeground(new Color(50, 50, 50));
        panel.add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBackground(new Color(200, 200, 200)); // Set grey background
        emailField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(50, 50, 50));
        panel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBackground(new Color(200, 200, 200)); // Set grey background
        passwordField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(passwordField);

        JLabel cityLabel = new JLabel("City:");
        cityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        cityLabel.setForeground(new Color(50, 50, 50));
        panel.add(cityLabel);

        JTextField cityField = new JTextField();
        cityField.setFont(new Font("Arial", Font.PLAIN, 14));
        cityField.setBackground(new Color(200, 200, 200)); // Set grey background
        cityField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(cityField);

        JLabel stateLabel = new JLabel("State:");
        stateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        stateLabel.setForeground(new Color(50, 50, 50));
        panel.add(stateLabel);

        JTextField stateField = new JTextField();
        stateField.setFont(new Font("Arial", Font.PLAIN, 14));
        stateField.setBackground(new Color(200, 200, 200)); // Set grey background
        stateField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(stateField);

        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        phoneNumberLabel.setFont(new Font("Arial", Font.BOLD, 14));
        phoneNumberLabel.setForeground(new Color(50, 50, 50));
        panel.add(phoneNumberLabel);

        JTextField phoneNumberField = new JTextField();
        phoneNumberField.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneNumberField.setBackground(new Color(200, 200, 200)); // Set grey background
        phoneNumberField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(phoneNumberField);

        long creditInfo = 1000000000000000L + (long)(Math.random() * 9000000000000000L);
        int cvv = 100 + (int)(Math.random() * 900);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    RegisterWindow reg = new RegisterWindow(con);
                    String name = nameField.getText();
                    String email = emailField.getText();
                    String city = cityField.getText();
                    String state = stateField.getText();
                    String password = new String(passwordField.getPassword());
                    long ph_no = Long.parseLong(phoneNumberField.getText());
                    if (ph_no > 9999999999L || ph_no < 1000000000){
                        JOptionPane.showMessageDialog(null, "Invalid Phone number, Please try again!");

                    }
                    else if (!email.endsWith("@gmail.com")) {
                        JOptionPane.showMessageDialog(null, "Invalid email ID, please try again!");

                    }
                    else {
                        String query = "INSERT INTO auctest (name, city, state, email, ph_no, password, credit_info, cvv, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 50000)";
                        PreparedStatement pstmt = con.prepareStatement(query);
                        pstmt.setString(1, name);
                        pstmt.setString(2, city);
                        pstmt.setString(3, state);
                        pstmt.setString(4, email);
                        pstmt.setLong(5, ph_no);
                        pstmt.setString(6, password);
                        pstmt.setLong(7, creditInfo);
                        pstmt.setInt(8, cvv);

                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Registration successful!");
                        LoginWindow LoginWindow = new LoginWindow(con);
                        LoginWindow.setVisible(true);
                        dispose();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid phone number!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error registering!");
                }
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
