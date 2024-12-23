import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class PaymentWindow extends JFrame {

    int itemId;
    double currentMax;
    double minIncrement;

    public PaymentWindow(int itemId, double currentMax, double minIncrement, Connection con) {
        this.itemId = itemId;
        this.currentMax = currentMax;
        this.minIncrement = minIncrement;

        setSize(400, 300);
        setTitle("Payment");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel creditLabel = new JLabel("Enter your credit info:");
        panel.add(creditLabel);

        JTextField creditField = new JTextField();
        panel.add(creditField);

        JLabel cvvLabel = new JLabel("Enter your CVV:");
        panel.add(cvvLabel);

        JPasswordField cvvField = new JPasswordField();
        panel.add(cvvField);


        int user_id = Auctest.Global.userId;

        JButton payButton = new JButton("Pay");
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    long creditInfo = Long.parseLong(creditField.getText());
                    int cvv = Integer.parseInt(cvvField.getText());

                    String query = "SELECT credit_info, cvv, balance, name FROM auctest WHERE user_id = ?";
                    PreparedStatement pstmt = con.prepareStatement(query);
                    pstmt.setInt(1, user_id);

                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        long dbCreditInfo = rs.getLong("credit_info");
                        int dbCvv = rs.getInt("cvv");
                        double balance = rs.getDouble("balance");
                        String name = rs.getString("name");

                        if (creditInfo == dbCreditInfo && cvv == dbCvv) {
                            String itemQuery = "SELECT description, start_price, payout_price, end_date, seller_id FROM items WHERE item_id = ?";
                            PreparedStatement itemPstmt = con.prepareStatement(itemQuery);
                            itemPstmt.setInt(1, itemId);
                            ResultSet itemRs = itemPstmt.executeQuery();
                            if (itemRs.next()) {
                                String description = itemRs.getString("description");
                                double startPrice = itemRs.getDouble("start_price");
                                double payoutPrice = itemRs.getDouble("payout_price");
                                String endDate = itemRs.getString("end_date");
                                int sellerId = itemRs.getInt("seller_id");

                                String bidderQuery = "SELECT * FROM bidder WHERE bidder_id = ? AND item_id = ?";
                                PreparedStatement bidderPstmt = con.prepareStatement(bidderQuery);
                                bidderPstmt.setInt(1, user_id);
                                bidderPstmt.setInt(2, itemId);
                                ResultSet bidderRs = bidderPstmt.executeQuery();

                                if (bidderRs.next()) {
                                    String updateBidderQuery = "UPDATE bidder SET max_bid = ? WHERE bidder_id = ? AND item_id = ?";
                                    PreparedStatement updateBidderPstmt = con.prepareStatement(updateBidderQuery);
                                    updateBidderPstmt.setDouble(1, currentMax + minIncrement);
                                    updateBidderPstmt.setInt(2, user_id);
                                    updateBidderPstmt.setInt(3, itemId);
                                    updateBidderPstmt.executeUpdate();
                                } else {
                                    String insertBidderQuery = "INSERT INTO bidder (bidder_id, item_id, max_bid) VALUES (?, ?, ?)";
                                    PreparedStatement insertBidderPstmt = con.prepareStatement(insertBidderQuery);
                                    insertBidderPstmt.setInt(1, user_id);
                                    insertBidderPstmt.setInt(2, itemId);
                                    insertBidderPstmt.setDouble(3, currentMax + minIncrement); // Use correct index (3, not 7)
                                    insertBidderPstmt.executeUpdate();
                                }



                                AmountWindow amountWindow = new AmountWindow(itemId, balance, currentMax, minIncrement, name, description, startPrice, payoutPrice, endDate, sellerId, con);
                                amountWindow.setVisible(true);
                                dispose();
                            } else {
                                JOptionPane.showMessageDialog(null, "Item not found!");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid credit info or cvv!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "User not found!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid credit info or cvv!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error retrieving balance! " + ex.getMessage());
                }
            }
        });

        panel.add(payButton);

        add(panel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }
}