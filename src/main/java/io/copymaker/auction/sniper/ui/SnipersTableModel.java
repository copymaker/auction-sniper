package io.copymaker.auction.sniper.ui;

import io.copymaker.auction.sniper.SniperSnapShot;
import io.copymaker.auction.sniper.SniperState;
import io.copymaker.auction.sniper.listener.SniperListener;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {

    private static final String[] STATUS_TEXT = {"Joining", "Bidding", "Winning", "Lost", "Won"};

    private List<SniperSnapShot> snapShots = new ArrayList<>();

    @Override
    public int getRowCount() {
        return snapShots.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapShots.get(rowIndex));
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).getName();
    }

    @Override
    public void sniperStateChanged(SniperSnapShot sniperSnapShot) {
        int row = rowMatching(sniperSnapShot);
        snapShots.set(row, sniperSnapShot);
        fireTableRowsUpdated(row, row);
    }

    public void addSniper(SniperSnapShot snapShot) {
        snapShots.add(snapShot);
        int lastRowIndex = snapShots.size() - 1;
        fireTableRowsInserted(lastRowIndex, lastRowIndex);
    }

    private int rowMatching(SniperSnapShot newSnapShot) {
        for (int i = 0; i < snapShots.size(); i++) {
            if (newSnapShot.isForSameItemAs(snapShots.get(i))) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    public static String textFor(SniperState sniperState) {
        return STATUS_TEXT[sniperState.ordinal()];
    }
}
