import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import javax.swing.*;

public class SellerWindow extends JFrame {

    private File selectedImageFile = null;

    public SellerWindow(int userId, Connection con) {
        setSize(400, 400);
        setTitle("Seller");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(128, 128, 128));

        // Item Name
        JTextField nameField = new JTextField(10);
        nameField.setBackground(new Color(200, 200, 200));
        panel.add(new JLabel("Item Name:"));
        panel.add(nameField);

        // Item Description
        JTextField descriptionField = new JTextField(10);
        descriptionField.setBackground(new Color(200, 200, 200));
        panel.add(new JLabel("Item Description:"));
        panel.add(descriptionField);

        // Start Price
        JTextField startPriceField = new JTextField(10);
        startPriceField.setBackground(new Color(200, 200, 200));
        panel.add(new JLabel("Start Price:"));
        panel.add(startPriceField);

        // Minimum Increment
        JTextField minIncrementField = new JTextField(10);
        minIncrementField.setBackground(new Color(200, 200, 200));
        panel.add(new JLabel("Minimum Increment:"));
        panel.add(minIncrementField);

        // Payout Price
        JTextField payoutPriceField = new JTextField(10);
        payoutPriceField.setBackground(new Color(200, 200, 200));
        panel.add(new JLabel("Payout Price:"));
        panel.add(payoutPriceField);

        // End Date
        JTextField endDateField = new JTextField(10);
        endDateField.setBackground(new Color(200, 200, 200));
        panel.add(new JLabel("End Date (YYYY-MM-DD):"));
        panel.add(endDateField);

        // Upload Image Button
        JButton uploadImageButton = new JButton("Upload Image");
        uploadImageButton.setBackground(new Color(50, 50, 50));
        uploadImageButton.setForeground(new Color(240, 240, 240));
        uploadImageButton.setFont(new Font("Arial", Font.BOLD, 14));
        uploadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedImageFile = fileChooser.getSelectedFile();
                    JOptionPane.showMessageDialog(null, "Image selected: " + selectedImageFile.getName());
                }
            }
        });
        panel.add(uploadImageButton);

        // Sell button
        JButton sellButton = new JButton("Sell");
        sellButton.setBackground(new Color(50, 50, 50));
        sellButton.setForeground(new Color(240, 240, 240));
        sellButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        sellButton.setFont(new Font("Arial", Font.BOLD, 14));
        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    String description = descriptionField.getText();
                    double startPrice = Double.parseDouble(startPriceField.getText());
                    double minIncrement = Double.parseDouble(minIncrementField.getText());
                    double payoutPrice = Double.parseDouble(payoutPriceField.getText());
                    String endDate = endDateField.getText();

                    // Validate image selection
                    if (selectedImageFile == null) {
                        JOptionPane.showMessageDialog(null, "Please upload an image!");
                        return;
                    }


                    String query = "INSERT INTO items (name, description, start_price, min_incr, payout_price, end_date, seller_id, image_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = con.prepareStatement(query);
                    pstmt.setString(1, name);
                    pstmt.setString(2, description);
                    pstmt.setDouble(3, startPrice);
                    pstmt.setDouble(4, minIncrement);
                    pstmt.setDouble(5, payoutPrice);
                    pstmt.setString(6, endDate);
                    pstmt.setInt(7, userId);
                    pstmt.setString(8, selectedImageFile.getAbsolutePath());

                    pstmt.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Item listed successfully!");

                    BidSellWindow bidSellWindow = new BidSellWindow(Auctest.Global.userId, con);
                    bidSellWindow.setVisible(true);
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid price or increment!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error listing item!");
                }
            }
        });
        panel.add(sellButton);

        add(panel, BorderLayout.CENTER);
        setLocationRelativeTo(null); // Center the window on the screen
    }
}
