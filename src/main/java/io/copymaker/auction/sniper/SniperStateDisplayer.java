package io.copymaker.auction.sniper;

import io.copymaker.auction.sniper.listener.SniperListener;
import io.copymaker.auction.sniper.ui.MainWindow;

import javax.swing.*;

public class SniperStateDisplayer implements SniperListener {

    private final MainWindow mainWindow;

    public SniperStateDisplayer(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void sniperBidding() {
        showStatus(MainWindow.STATUS_BIDDING);
    }

    @Override
    public void sniperWinning() {
        showStatus(MainWindow.STATUS_WINNING);
    }

    @Override
    public void sniperLost() {
        showStatus(MainWindow.STATUS_LOST);
    }

    @Override
    public void sniperWon() {
        showStatus(MainWindow.STATUS_WON);
    }

    private void showStatus(final String status) {
        SwingUtilities.invokeLater(() -> mainWindow.showStatus(status));
    }

}
