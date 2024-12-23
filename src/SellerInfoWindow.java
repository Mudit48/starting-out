import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SellerInfoWindow extends JFrame {

    public SellerInfoWindow(int sellerId, Connection con) {
        setTitle("Seller Information");
        setSize(300, 200);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));

        // Query the database to get the seller info
        try {
            String query = "SELECT * FROM auctest WHERE user_id = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, sellerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String sellerName = rs.getString("name");
                String sellerEmail = rs.getString("email");
                String sellerCity = rs.getString("city");
                String sellerState = rs.getString("state");
                String sellerPhone = rs.getString("ph_no");

                panel.add(new JLabel("Seller Name: " + sellerName));
                panel.add(new JLabel("Email: " + sellerEmail));
                panel.add(new JLabel("Phone: " + sellerPhone));
                panel.add(new JLabel("City: " + sellerCity));
                panel.add(new JLabel("State: " + sellerState));
            } else {
                panel.add(new JLabel("No seller information found."));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            panel.add(new JLabel("Error retrieving seller info."));
        }

        add(panel, BorderLayout.CENTER);
        setLocationRelativeTo(null);  // Center the window
        setVisible(true);
    }
}
