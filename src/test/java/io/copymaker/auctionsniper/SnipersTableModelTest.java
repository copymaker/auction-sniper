package io.copymaker.auctionsniper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static io.copymaker.auctionsniper.SnipersTableModel.textFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SnipersTableModelTest {

    private AuctionSniper auctionSniper1 = new AuctionSniper(new Item("item-54321", Integer.MAX_VALUE), null);
    private AuctionSniper auctionSniper2 = new AuctionSniper(new Item("item-65432", Integer.MAX_VALUE), null);

    private TableModelListener listener;
    private SnipersTableModel model;

    @Captor
    private ArgumentCaptor<TableModelEvent> tableModelEventCaptor;

    @BeforeEach
    void setUp() {
        listener = Mockito.mock(TableModelListener.class);
        model = new SnipersTableModel();
        model.addTableModelListener(listener);
    }

    @Test
    void hasEnoughColumns() {
        assertThat(model.getColumnCount()).isEqualTo(Column.values().length);
    }

    @Test
    void setsSniperValuesInColumns() {
        SniperSnapshot joining = SniperSnapshot.joining(auctionSniper1.getItemId());
        SniperSnapshot bidding = joining.bidding(555, 666);

        model.sniperAdded(auctionSniper1);
        model.sniperStateChanged(bidding);

        verify(listener, atLeastOnce()).tableChanged(any(TableModelEvent.class));
        assertRowMatchesSnapShot(0, bidding);
    }

    @Test
    void setsUpColumnHeadings() {
        for (Column column : Column.values()) {
            assertThat(model.getColumnName(column.ordinal())).isEqualTo(column.getName());
        }
    }

    @Test
    void notifiesListenersWhenAddingASniper() {
        SniperSnapshot joining = SniperSnapshot.joining(auctionSniper1.getItemId());
        assertThat(model.getRowCount()).isEqualTo(0);

        model.sniperAdded(auctionSniper1);

        verify(listener, atLeastOnce()).tableChanged(tableModelEventCaptor.capture());
        assertThat(tableModelEventCaptor.getValue().getType()).isEqualTo(TableModelEvent.INSERT);
        assertThat(model.getRowCount()).isEqualTo(1);
        assertRowMatchesSnapShot(0, joining);
    }

    @Test
    void holdsSnipersInAdditionOrder() {
        model.sniperAdded(auctionSniper1);
        model.sniperAdded(auctionSniper2);

        assertThat(model.getValueAt(0, Column.ITEM_IDENTIFIER.ordinal())).isEqualTo(auctionSniper1.getItemId());
        assertThat(model.getValueAt(1, Column.ITEM_IDENTIFIER.ordinal())).isEqualTo(auctionSniper2.getItemId());
    }

    private void assertRowMatchesSnapShot(int row, SniperSnapshot sniperSnapShot) {
        assertColumnEquals(row, Column.ITEM_IDENTIFIER, sniperSnapShot.itemId);
        assertColumnEquals(row, Column.LAST_PRICE, sniperSnapShot.lastPrice);
        assertColumnEquals(row, Column.LAST_BID, sniperSnapShot.lastBid);
        assertColumnEquals(row, Column.SNIPER_STATE, textFor(sniperSnapShot.sniperState));
    }

    private void assertColumnEquals(int rowIndex, Column column, Object expected) {
        final int columnIndex = column.ordinal();
        assertThat(model.getValueAt(rowIndex, columnIndex)).isEqualTo(expected);
    }
}