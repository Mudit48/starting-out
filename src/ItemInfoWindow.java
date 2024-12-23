import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class ItemInfoWindow extends JFrame {

    int itemId;
    String name;
    String description;
    double startPrice;
    double minIncrement;
    double payoutPrice;
    String endDate;
    int sellerId;
    double currentMax;
    String maxHolder;
    String imagePath;
    JLabel imageLabel;

    public ItemInfoWindow(int itemId, String name, String description, double startPrice, double minIncrement, double payoutPrice, String endDate, int sellerId, double currentMax, String maxHolder, String imagePath, Connection con) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.startPrice = startPrice;
        this.minIncrement = minIncrement;
        this.payoutPrice = payoutPrice;
        this.endDate = endDate;
        this.sellerId = sellerId;
        this.currentMax = currentMax;
        this.maxHolder = maxHolder;
        this.imagePath = imagePath;

        setSize(400, 500);
        setTitle("Item Info");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(128, 128, 128));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        imageLabel = new JLabel();
        updateImageLabel(imagePath);
        panel.add(imageLabel);

        Font labelFont = new Font("Arial", Font.PLAIN, 16);

        panel.add(createLabel("Item ID: " + itemId, labelFont, Color.BLACK));
        panel.add(createLabel("Name: " + name, labelFont, Color.BLACK));
        panel.add(createLabel("Description: " + description, labelFont, Color.BLACK));
        panel.add(createLabel("Start Price: ₹" + startPrice, labelFont, Color.BLACK));
        panel.add(createLabel("Minimum Increment: ₹" + minIncrement, labelFont, Color.BLACK));
        panel.add(createLabel("Payout Price: ₹" + payoutPrice, labelFont, Color.BLACK));
        panel.add(createLabel("End Date: " + endDate, labelFont, Color.BLACK));
        panel.add(createLabel("Current Maximum: ₹" + currentMax, labelFont, Color.BLACK));
        panel.add(createLabel("Max Holder: " + maxHolder, labelFont, Color.BLACK));

        JButton bidButton = new JButton("Bid");
        bidButton.setBackground(new Color(50, 50, 50));
        bidButton.setForeground(new Color(240, 240, 240));
        bidButton.setFont(new Font("Arial", Font.BOLD, 18));
        bidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PaymentWindow paymentWindow = new PaymentWindow(itemId, currentMax, minIncrement, con);
                paymentWindow.setVisible(true);
                dispose();
            }
        });
        panel.add(bidButton);

        add(panel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private void updateImageLabel(String imagePath) {
        if (imagePath != null) {
            try {
                // Load the image from the specified path
                ImageIcon imageIcon = new ImageIcon(imagePath);
                Image img = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH); // Scale image to fit label
                imageLabel.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                imageLabel.setText("Image not available");
            }
        } else {
            imageLabel.setText("No image available");
        }
    }

    public void refreshItemInfo(Connection con) {
        try {
            String query = "SELECT curr_max, max_holder FROM items WHERE item_id = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                currentMax = rs.getDouble("curr_max");
                maxHolder = rs.getString("max_holder");

                for (Component component : getContentPane().getComponents()) {
                    if (component instanceof JPanel) {
                        JPanel panel = (JPanel) component;
                        for (Component panelComponent : panel.getComponents()) {
                            if (panelComponent instanceof JLabel) {
                                JLabel label = (JLabel) panelComponent;
                                if (label.getText().startsWith("Current Maximum:")) {
                                    label.setText("Current Maximum: ₹" + currentMax);
                                } else if (label.getText().startsWith("Max Holder:")) {
                                    label.setText("Max Holder: " + maxHolder);
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error refreshing item info!");
        }
    }
}
