import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ActiveBidsWindow extends JFrame {

    private JPanel panel;

    private void updateActiveBids(Connection con) {
        try {
            String query = "INSERT INTO active_bids (item_id, name, description, start_price, min_incr, payout_price, end_date, seller_id, curr_max, image_path) " +
                    "SELECT item_id, name, description, start_price, min_incr, payout_price, end_date, seller_id, curr_max, image_path " +
                    "FROM items WHERE end_date > NOW() AND item_id NOT IN (SELECT item_id FROM active_bids)";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.executeUpdate();

            query = "DELETE FROM active_bids WHERE end_date <= NOW()";
            pstmt = con.prepareStatement(query);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error updating active bids!");
            ex.printStackTrace();
        }
    }

    public ActiveBidsWindow(int userId, Connection con) {
        updateActiveBids(con);

        setSize(800, 600);
        setTitle("Active Bids");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(128, 128, 128)); // Background color set to RGB(128, 128, 128)

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));

        loadActiveBids(con);

        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshActiveBids(con);
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(refreshButton);

        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void loadActiveBids(Connection con) {
        try {
            String query = "SELECT * FROM active_bids WHERE end_date > NOW() AND status IS NULL";
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            panel.removeAll();

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
                itemPanel.setBackground(new Color(240, 240, 240));

                JButton itemButton;
                try {
                    Image itemImage = ImageIO.read(new File(imagePath));
                    ImageIcon imageIcon = new ImageIcon(itemImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                    itemButton = new JButton(imageIcon);
                } catch (IOException ex) {
                    itemButton = new JButton(name);
                }

                itemButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ItemInfoWindow itemInfoWindow = new ItemInfoWindow(itemId, name, description, startPrice, minIncrement, payoutPrice, endDate, sellerId, currentMax, maxHolder, imagePath, con);
                        itemInfoWindow.setVisible(true);
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

                panel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            panel.revalidate();
            panel.repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading active bids!");
            ex.printStackTrace();
        }
    }

    public void refreshActiveBids(Connection con) {
        updateActiveBids(con);
        loadActiveBids(con);
    }
}