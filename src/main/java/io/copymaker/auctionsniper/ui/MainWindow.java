package io.copymaker.auctionsniper.ui;

import io.copymaker.auctionsniper.*;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class MainWindow extends JFrame {

    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPERS_TABLE_NAME = "Sniper table";
    public static final String NEW_ITEM_ID_NAME = "item id";
    public static final String NEW_ITEM_STOP_PRICE_NAME = "stop price";
    public static final String JOIN_BUTTON_NAME = "join button";

    private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

    public MainWindow(SniperPortfolio sniperPortfolio) {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(sniperPortfolio), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void addUserRequestListener(UserRequestListener userRequestListener) {
        userRequests.addListener(userRequestListener);
    }

    private void fillContentPane(JTable table, JPanel panel) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable(SniperPortfolio sniperPortfolio) {
        SnipersTableModel tableModel = new SnipersTableModel();
        sniperPortfolio.addPortfolioListener(tableModel);

        JTable snipersTable = new JTable(tableModel);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());
        final JTextField itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        controls.add(itemIdField);

        final JFormattedTextField itemStopPriceField = new JFormattedTextField();
        DecimalFormat numericFormat = (DecimalFormat) DecimalFormat.getNumberInstance();
        numericFormat.setMaximumIntegerDigits(10);
        itemStopPriceField.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(numericFormat)));

        itemStopPriceField.setColumns(25);
        itemStopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
        controls.add(itemStopPriceField);

        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        controls.add(joinAuctionButton);

        joinAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userRequests.announce().joinAuction(new Item(itemId(), stopPrice()));
            }

            private String itemId() {
                return itemIdField.getText();
            }

            private int stopPrice() {
                return ((Number) itemStopPriceField.getValue()).intValue();
            }
        });

        return controls;
    }
}
