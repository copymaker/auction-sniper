package io.copymaker.auction.sniper.ui;

import io.copymaker.auction.sniper.SniperSnapShot;
import io.copymaker.auction.sniper.SniperState;
import io.copymaker.auction.sniper.listener.SniperListener;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {

    private static final String[] STATUS_TEXT = {"Joining", "Bidding", "Winning", "Lost", "Won"};

    private SniperSnapShot sniperSnapShot = SniperSnapShot.joining("");

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(sniperSnapShot);
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).getName();
    }

    @Override
    public void sniperStateChanged(SniperSnapShot sniperSnapShot) {
        this.sniperSnapShot = sniperSnapShot;
        fireTableRowsUpdated(0, 0);
    }

    public static String textFor(SniperState sniperState) {
        return STATUS_TEXT[sniperState.ordinal()];
    }
}
