import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class RebidWindow extends JFrame {

    private JPanel panel;
    private int userId;

    public RebidWindow(int userId, Connection con) {
        this.userId = userId;

        setSize(800, 600);
        setTitle("Rebid");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(240, 240, 240));

        loadRebidItems(con);

        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                RebidWindow rebidWindow = new RebidWindow(userId, con);  // Reload the window
                rebidWindow.setVisible(true);
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void loadRebidItems(Connection con) {
        try {
            String query = "SELECT ab.item_id, ab.name, ab.description, ab.start_price, ab.min_incr, ab.payout_price, ab.end_date, ab.seller_id, ab.curr_max, ab.image_path " +
                    "FROM active_bids ab " +
                    "WHERE ab.item_id IN (SELECT item_id FROM bidder WHERE bidder_id = ?) AND status IS NULL";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            panel.removeAll();

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "No items found.");
            } else {
                while (rs.next()) {
                    int itemId = rs.getInt("item_id");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    double startPrice = rs.getDouble("start_price");
                    double minIncrement = rs.getDouble("min_incr");
                    double payoutPrice = rs.getDouble("payout_price");
                    String endDate = rs.getString("end_date");
                    int sellerId = rs.getInt("seller_id");
                    double currentMax = rs.getDouble("curr_max");
                    String imagePath = rs.getString("image_path");

                    // Create a panel for each item
                    JPanel itemPanel = new JPanel();
                    itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
                    itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    itemPanel.setBackground(new Color(240, 240, 240));

                    // Load the item image
                    JButton itemButton;
                    try {
                        Image itemImage = ImageIO.read(new File(imagePath));
                        ImageIcon imageIcon = new ImageIcon(itemImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                        itemButton = new JButton(imageIcon);  // Display the image on the button
                    } catch (IOException ex) {
                        // If image loading fails, use the item name as the button text
                        itemButton = new JButton(name);
                    }

                    itemButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            RebidItemWindow rebidItemWindow = new RebidItemWindow(itemId, name, description, startPrice, minIncrement, payoutPrice, endDate, sellerId, currentMax, con);
                            rebidItemWindow.setVisible(true);
                        }
                    });

                    itemButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                    itemButton.setPreferredSize(new Dimension(150, 100));

                    itemPanel.add(itemButton);

                    JPanel attributePanel = new JPanel();
                    attributePanel.setLayout(new BoxLayout(attributePanel, BoxLayout.Y_AXIS));
                    attributePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    attributePanel.add(new JLabel("Name: " + name));
                    attributePanel.add(new JLabel("Description: " + description));
                    attributePanel.add(new JLabel("Starting Price: ₹" + startPrice));
                    attributePanel.add(new JLabel("End Date: " + endDate));

                    itemPanel.add(attributePanel);

                    panel.add(itemPanel);

                    panel.add(Box.createRigidArea(new Dimension(10, 0)));
                }

                panel.revalidate();
                panel.repaint();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error retrieving rebid items: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
