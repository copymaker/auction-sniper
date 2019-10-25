package io.copymaker.auctionsniper;

import javax.swing.*;

public class SwingThreadSniperListener implements SniperListener {

    private final SnipersTableModel snipers;

    public SwingThreadSniperListener(SnipersTableModel snipers) {
        this.snipers = snipers;
    }

    @Override
    public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
        SwingUtilities.invokeLater(() -> snipers.sniperStateChanged(sniperSnapshot));
    }
}
