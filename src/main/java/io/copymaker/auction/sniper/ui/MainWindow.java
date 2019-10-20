package io.copymaker.auction.sniper.ui;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

public class MainWindow extends JFrame {

    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPERS_TABLE_NAME = "Sniper table";

    public MainWindow(TableModel tableModel) {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(tableModel));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fillContentPane(JTable snipersTable) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable(TableModel tableModel) {
        final JTable snipersTable = new JTable(tableModel);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }
}
