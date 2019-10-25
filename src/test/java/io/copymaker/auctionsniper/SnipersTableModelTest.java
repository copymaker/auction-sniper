package io.copymaker.auctionsniper;

import io.copymaker.auctionsniper.AuctionSniper;
import io.copymaker.auctionsniper.Column;
import io.copymaker.auctionsniper.SniperSnapshot;
import io.copymaker.auctionsniper.SnipersTableModel;
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

    private final String ITEM_ID = "item-54321";
    private final String ITEM_ID2 = "item-65432";

    private TableModelListener listener;
    private SnipersTableModel model;
    private AuctionSniper auctionSniper;
    private AuctionSniper auctionSniper2;

    @Captor
    private ArgumentCaptor<TableModelEvent> argumentTableModelEvent;

    @BeforeEach
    void setUp() {
        listener = Mockito.mock(TableModelListener.class);
        model = new SnipersTableModel();
        model.addTableModelListener(listener);

        auctionSniper = new AuctionSniper(ITEM_ID, null);
        auctionSniper2 = new AuctionSniper(ITEM_ID2, null);
    }

    @Test
    void hasEnoughColumns() {
        assertThat(model.getColumnCount()).isEqualTo(Column.values().length);
    }

    @Test
    void setsSniperValuesInColumns() {
        SniperSnapshot joining = SniperSnapshot.joining(ITEM_ID);
        SniperSnapshot bidding = joining.bidding(555, 666);

        model.sniperAdded(auctionSniper);
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
        SniperSnapshot joining = SniperSnapshot.joining(ITEM_ID);
        assertThat(model.getRowCount()).isEqualTo(0);

        model.sniperAdded(auctionSniper);

        verify(listener, atLeastOnce()).tableChanged(argumentTableModelEvent.capture());
        assertThat(argumentTableModelEvent.getValue().getType()).isEqualTo(TableModelEvent.INSERT);
        assertThat(model.getRowCount()).isEqualTo(1);
        assertRowMatchesSnapShot(0, joining);
    }

    @Test
    void holdsSnipersInAdditionOrder() {
        model.sniperAdded(auctionSniper);
        model.sniperAdded(auctionSniper2);

        assertThat(model.getValueAt(0, Column.ITEM_IDENTIFIER.ordinal())).isEqualTo(auctionSniper.getItemId());
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