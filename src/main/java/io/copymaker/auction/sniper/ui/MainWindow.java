package io.copymaker.auction.sniper.ui;

import io.copymaker.auction.sniper.listener.UserRequestListener;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {

    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPERS_TABLE_NAME = "Sniper table";
    public static final String NEW_ITEM_ID_NAME = "item id";
    public static final String JOIN_BUTTON_NAME = "join button";

    private final List<UserRequestListener> userRequests = new ArrayList<>();

    public MainWindow(TableModel tableModel) {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(tableModel), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void addUserRequestListener(UserRequestListener userRequestListener) {
        userRequests.add(userRequestListener);
    }

    private void fillContentPane(JTable table, JPanel panel) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable(TableModel tableModel) {
        final JTable snipersTable = new JTable(tableModel);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());
        final JTextField itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        controls.add(itemIdField);

        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        controls.add(joinAuctionButton);

        joinAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userRequests.forEach(userRequestListener -> userRequestListener.joinAuction(itemIdField.getText()));
            }
        });

        return controls;
    }

}
