import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class RebidItemWindow extends JFrame {

    int itemId;
    String name;
    String description;
    double startPrice;
    double minIncrement;
    double payoutPrice;
    String endDate;
    int sellerId;
    double currentMax;

    public RebidItemWindow(int itemId, String name, String description, double startPrice, double minIncrement, double payoutPrice, String endDate, int sellerId, double currentMax, Connection con) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.startPrice = startPrice;
        this.minIncrement = minIncrement;
        this.payoutPrice = payoutPrice;
        this.endDate = endDate;
        this.sellerId = sellerId;
        this.currentMax = currentMax;

        setSize(400, 300);
        setTitle("Rebid Item");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("Item ID: " + itemId));
        panel.add(new JLabel(" Name: " + name));
        panel.add(new JLabel("Description: " + description));
        panel.add(new JLabel("Start Price: " + startPrice));
        panel.add(new JLabel("Minimum Increment: " + minIncrement));
        panel.add(new JLabel("Payout Price: " + payoutPrice));
        panel.add(new JLabel("End Date: " + endDate));
        panel.add(new JLabel("Seller ID: " + sellerId));
        panel.add(new JLabel("Current Maximum: " + currentMax));


        panel.add(new JLabel("Enter new bid:"));


        JButton rebidButton = new JButton("Rebid");
        rebidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PaymentWindow paymentWindow = new PaymentWindow(itemId, currentMax + minIncrement, minIncrement, con);
                paymentWindow.setVisible(true);
                dispose();
            }
        });
        panel.add(rebidButton);

        add(panel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }
}