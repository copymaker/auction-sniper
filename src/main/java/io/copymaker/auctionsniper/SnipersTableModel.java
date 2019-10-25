package io.copymaker.auctionsniper;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, PortfolioListener {

    private static final String[] STATUS_TEXT = {"Joining", "Bidding", "Winning", "Lost", "Won"};

    private List<SniperSnapshot> snapshots = new ArrayList<>();

    public static String textFor(SniperState sniperState) {
        return STATUS_TEXT[sniperState.ordinal()];
    }

    @Override
    public int getRowCount() {
        return snapshots.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).getName();
    }

    @Override
    public void sniperStateChanged(SniperSnapshot sniperSnapshot) {
        int row = rowMatching(sniperSnapshot);
        snapshots.set(row, sniperSnapshot);
        fireTableRowsUpdated(row, row);
    }

    @Override
    public void sniperAdded(AuctionSniper auctionSniper) {
        addSniperSnapShot(auctionSniper.getSnapshot());
        auctionSniper.addSniperListener(new SwingThreadSniperListener(this));
    }

    private void addSniperSnapShot(SniperSnapshot sniperSnapshot) {
        snapshots.add(sniperSnapshot);
        int lastRowIndex = snapshots.size() - 1;
        fireTableRowsInserted(lastRowIndex, lastRowIndex);
    }

    private int rowMatching(SniperSnapshot newSnapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (newSnapshot.isForSameItemAs(snapshots.get(i))) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }
}
