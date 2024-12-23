import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class BidSellWindow extends JFrame {

    public BidSellWindow(int userId, Connection con) {
        setSize(400, 300);
        setTitle("User Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(128, 128, 128)); // Set background color to grey
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Welcome!");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the title
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));  // Set title font size
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Bid button
        JButton bidButton = new JButton("Bid");
        bidButton.setBackground(new Color(50, 50, 50));
        bidButton.setForeground(new Color(240, 240, 240));
        bidButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bidButton.setFont(new Font("Arial", Font.BOLD, 14));
        bidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ActiveBidsWindow activeBidsWindow = new ActiveBidsWindow(Auctest.Global.userId, con);
                activeBidsWindow.setVisible(true);
                dispose();
            }
        });
        mainPanel.add(bidButton);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton sellButton = new JButton("Sell");
        sellButton.setBackground(new Color(50, 50, 50));
        sellButton.setForeground(new Color(240, 240, 240));
        sellButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        sellButton.setFont(new Font("Arial", Font.BOLD, 14));
        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SellerWindow sellerWindow = new SellerWindow(Auctest.Global.userId, con);
                sellerWindow.setVisible(true);
                dispose();
            }
        });
        mainPanel.add(sellButton);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Inventory button
        JButton inventoryButton = new JButton("Inventory");
        inventoryButton.setBackground(new Color(50, 50, 50));
        inventoryButton.setForeground(new Color(240, 240, 240));
        inventoryButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        inventoryButton.setFont(new Font("Arial", Font.BOLD, 14));
        inventoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InventoryWindow inventoryWindow = new InventoryWindow(Auctest.Global.userId, con);
                inventoryWindow.setVisible(true);
                dispose();
            }
        });
        mainPanel.add(inventoryButton);

        // Add vertical spacing between buttons
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Rebid button
        JButton rebidButton = new JButton("Rebid");
        rebidButton.setBackground(new Color(50, 50, 50));
        rebidButton.setForeground(new Color(240, 240, 240));
        rebidButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        rebidButton.setFont(new Font("Arial", Font.BOLD, 14));
        rebidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RebidWindow rebidWindow = new RebidWindow(Auctest.Global.userId, con);
                rebidWindow.setVisible(true);
                dispose();
            }
        });
        mainPanel.add(rebidButton);

        // Add the main panel to the frame
        add(mainPanel, BorderLayout.CENTER);

        // Center the window on the screen
        setLocationRelativeTo(null);
    }
}
