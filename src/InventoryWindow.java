import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class InventoryWindow extends JFrame {
    private int userId;
    private Connection con;
    private JPanel inventoryPanel;

    public InventoryWindow(int userId, Connection con) {
        this.userId = userId;
        this.con = con;

        setSize(800, 600);
        setTitle("Inventory");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));

        loadUserInventory();
        JScrollPane scrollPane = new JScrollPane(inventoryPanel);
        add(scrollPane, BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    private void loadUserInventory() {
        try {
            String query = "SELECT * FROM items WHERE status = 'sold' AND buyer_id = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            inventoryPanel.removeAll();

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
                String maxHolder = rs.getString("max_holder");
                String imagePath = rs.getString("image_path");

                JPanel itemPanel = new JPanel();
                itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
                itemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

                JLabel imageLabel;
                try {
                    ImageIcon imageIcon = new ImageIcon(imagePath);
                    Image img = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);  // Scale image
                    imageLabel = new JLabel(new ImageIcon(img));
                } catch (Exception e) {
                    imageLabel = new JLabel("No image available");
                }
                itemPanel.add(imageLabel);

                // Display item information
                itemPanel.add(new JLabel("Name: " + name));
                itemPanel.add(new JLabel("Description: " + description));
                itemPanel.add(new JLabel("Starting Price: ₹" + startPrice));
                itemPanel.add(new JLabel("End Date: " + endDate));

                // "Item Info" button to open ItemInfoWindow
                JButton itemInfoButton = new JButton("Item Info");
                itemInfoButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        OnlyInfo itemInfoWindow = new OnlyInfo(itemId, name, description, startPrice, minIncrement, payoutPrice, endDate, sellerId, currentMax, maxHolder, imagePath, con);
                        itemInfoWindow.setVisible(true);
                    }
                });
                itemPanel.add(itemInfoButton);

                JButton sellerInfoButton = new JButton("seller Info");
                sellerInfoButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SellerInfoWindow sellerinfoWindow = new SellerInfoWindow(userId, con);
                        sellerinfoWindow.setVisible(true);
                    }
                });
                itemPanel.add(sellerInfoButton);
                inventoryPanel.add(itemPanel);
                inventoryPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            inventoryPanel.revalidate();
            inventoryPanel.repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading user inventory!");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Connection con = null;
        int userId = 1;

        InventoryWindow inventoryWindow = new InventoryWindow(userId, con);
        inventoryWindow.setVisible(true);
    }
}
