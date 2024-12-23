import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class AmountWindow extends JFrame {

    int itemId;
    double balance;
    double currentMax;
    double minIncrement;
    String name;
    String description;
    double startPrice;
    double payoutPrice;
    String endDate;
    int sellerId;

    private class Seller {
        int itemId;
        double payoutPrice;

        public Seller(int itemId, double payoutPrice) {
            this.itemId = itemId;
            this.payoutPrice = payoutPrice;
        }

        public void endBidding(Connection con) {
            try {
                String query = "SELECT max_bid, bidder_id FROM bidder WHERE item_id = ? ORDER BY max_bid DESC LIMIT 1";
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setInt(1, itemId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    double maxBid = rs.getDouble("max_bid");
                    double amount = Auctest.Global.amount;
                    int bidderId = rs.getInt("bidder_id");

                    if (amount >= payoutPrice) {
                        // Retrieve the bidder's name
                        String status = "sold";
                        query = "SELECT name FROM auctest WHERE user_id = ?";
                        pstmt = con.prepareStatement(query);
                        pstmt.setInt(1, bidderId);
                        rs = pstmt.executeQuery();
                        rs.next();
                        String bidderName = rs.getString("name");

                        query = "SELECT name, email, ph_no, balance FROM auctest WHERE user_id = (SELECT seller_id FROM items WHERE item_id = ?)";
                        pstmt = con.prepareStatement(query);
                        pstmt.setInt(1, itemId);
                        rs = pstmt.executeQuery();
                        rs.next();
                        String sellerName = rs.getString("name");
                        String sellerEmail = rs.getString("email");
                        long ph_no = rs.getLong("ph_no");
                        double sellerBalance = rs.getDouble("balance");

                        // Subtract bid amount from bidder balance and update seller balance
                        String updateBidderBalance = "UPDATE auctest SET balance = balance - ? WHERE user_id = ?";
                        PreparedStatement updateBidderStmt = con.prepareStatement(updateBidderBalance);
                        updateBidderStmt.setDouble(1, amount);
                        updateBidderStmt.setInt(2, bidderId);
                        updateBidderStmt.executeUpdate();

                        String updateSellerBalance = "UPDATE auctest SET balance = balance + ? WHERE user_id = (SELECT seller_id FROM items WHERE item_id = ?)";
                        PreparedStatement updateSellerStmt = con.prepareStatement(updateSellerBalance);
                        updateSellerStmt.setDouble(1, amount);
                        updateSellerStmt.setInt(2, itemId);
                        updateSellerStmt.executeUpdate();

                        // Mark the item as sold and update the corresponding tables
                        String updateActiveBidsQuery = "UPDATE active_bids SET status = ?, bidder_id = ? WHERE item_id = ?";
                        PreparedStatement updateActiveBidsStmt = con.prepareStatement(updateActiveBidsQuery);
                        updateActiveBidsStmt.setString(1, status);
                        updateActiveBidsStmt.setInt(2, bidderId);
                        updateActiveBidsStmt.setInt(3, itemId);
                        updateActiveBidsStmt.executeUpdate();

                        String updateItemsQuery = "UPDATE items SET status = ?, buyer_id = ? WHERE item_id = ?";
                        PreparedStatement updateItemsStmt = con.prepareStatement(updateItemsQuery);
                        updateItemsStmt.setString(1, status);
                        updateItemsStmt.setInt(2, bidderId);
                        updateItemsStmt.setInt(3, itemId);
                        updateItemsStmt.executeUpdate();

                        Auctest.Global.winnerName = bidderName;
                        Auctest.Global.sellerName = sellerName;
                        Auctest.Global.sellerEmail = sellerEmail;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error ending bidding: " + ex.getMessage());
            }
        }

    }

    public AmountWindow(int itemId, double balance, double currentMax, double minIncrement, String name, String description, double startPrice, double payoutPrice, String endDate, int sellerId, Connection con) {
        this.itemId = itemId;
        this.balance = balance;

        this.currentMax = currentMax;
        this.minIncrement = minIncrement;
        this.name = name;
        this.description = description;
        this.startPrice = startPrice;
        this.payoutPrice = payoutPrice;
        this.endDate = endDate;
        this.sellerId = sellerId;

        setSize(400, 300);
        setTitle("Amount");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel amountLabel = new JLabel("Enter your bid amount:");
        panel.add(amountLabel);

        JTextField amountField = new JTextField();
        panel.add(amountField);

        JButton bidButton = new JButton("Bid");
        bidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    Auctest.Global.amount = amount;

                    if (amount > balance) {
                        JOptionPane.showMessageDialog(null, "Insufficient balance!");
                        return;
                    }
                    double nextMax = currentMax + minIncrement;
                    if (amount <= currentMax + minIncrement) {
                        JOptionPane.showMessageDialog(null, "Bid amount must be greater than " + nextMax);
                        return;
                    }

                    int user_id = Auctest.Global.userId;

                    // Update bidder's balance
                    String updateQuery = "UPDATE auctest SET balance = ? WHERE user_id = ?";
                    PreparedStatement pstmt = con.prepareStatement(updateQuery);
                    pstmt.setDouble(1, balance);
                    pstmt.setInt(2, user_id);
                    pstmt.executeUpdate();

                    // Get the bidder's name
                    String query = "SELECT name FROM auctest WHERE user_id = ?";
                    pstmt = con.prepareStatement(query);
                    pstmt.setInt(1, user_id);
                    ResultSet rs = pstmt.executeQuery();
                    rs.next();
                    String maxHolder = rs.getString("name");

                    // Update the item's current max bid
                    String updateItemQuery = "UPDATE items SET curr_max = ?, max_holder = ? WHERE item_id = ?";
                    pstmt = con.prepareStatement(updateItemQuery);
                    pstmt.setDouble(1, amount);
                    pstmt.setString(2, maxHolder);
                    pstmt.setInt(3, itemId);
                    pstmt.executeUpdate();

                    // If the payout price is met or exceeded, finalize the transaction
                    if (amount >= payoutPrice || endDate.equals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
                        Seller seller = new Seller(itemId, payoutPrice);
                        seller.endBidding(con);
                        JOptionPane.showMessageDialog(null, "Congratulations, you have won the auction! You can access the seller information in inventory.");
                    }

                    // Update the active bids with the new max bid
                    String updateActbidQuery = "UPDATE active_bids SET curr_max = ?, max_holder = ? WHERE item_id = ?";
                    pstmt = con.prepareStatement(updateActbidQuery);
                    pstmt.setDouble(1, amount);
                    pstmt.setString(2, maxHolder);
                    pstmt.setInt(3, itemId);
                    pstmt.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Bid placed successfully!");

                    // Refresh the active bids window
                    ActiveBidsWindow activeBidsWindow = new ActiveBidsWindow(Auctest.Global.userId, con);
                    activeBidsWindow.refreshActiveBids(con);
                    activeBidsWindow.setVisible(true);

                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid bid amount!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error updating database!");
                }
            }
        });

        panel.add(bidButton);
        
        add(panel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }
}