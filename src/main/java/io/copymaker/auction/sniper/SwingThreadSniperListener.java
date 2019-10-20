package io.copymaker.auction.sniper;

import io.copymaker.auction.sniper.listener.SniperListener;
import io.copymaker.auction.sniper.ui.SnipersTableModel;

import javax.swing.*;

public class SwingThreadSniperListener implements SniperListener {

    private final SnipersTableModel snipers;

    public SwingThreadSniperListener(SnipersTableModel snipers) {
        this.snipers = snipers;
    }

    @Override
    public void sniperStateChanged(final SniperSnapShot sniperSnapShot) {
        SwingUtilities.invokeLater(() -> snipers.sniperStateChanged(sniperSnapShot));
    }
}
