package io.copymaker.auction.sniper.ui;

import javax.swing.*;

public class MainWindow extends JFrame {

    public static final String MAIN_WINDOW_NAME = "Main Window";
    public static final String SNIPER_STATUS_NAME = "Status Label";

    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_LOST = "Lost";

    public MainWindow() {
        super("Auction Sniper");
        setName(MAIN_WINDOW_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
