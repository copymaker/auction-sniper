package io.copymaker.auction.sniper.ui;

import io.copymaker.auction.sniper.SniperSnapShot;
import io.copymaker.auction.sniper.SniperState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static io.copymaker.auction.sniper.ui.SnipersTableModel.textFor;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SnipersTableModelTest {

    private TableModelListener listener;
    private SnipersTableModel model;

    @Captor
    private ArgumentCaptor<TableModelEvent> argumentTableModelEvent;

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
        SniperSnapShot joining = SniperSnapShot.joining("item id");
        SniperSnapShot bidding = joining.bidding(555, 666);

        model.addSniper(joining);
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
        SniperSnapShot joining = SniperSnapShot.joining("item123");
        assertThat(model.getRowCount()).isEqualTo(0);

        model.addSniper(joining);

        verify(listener, atLeast(1)).tableChanged(argumentTableModelEvent.capture());
        assertThat(argumentTableModelEvent.getValue().getType()).isEqualTo(TableModelEvent.INSERT);
        assertThat(model.getRowCount()).isEqualTo(1);
        assertRowMatchesSnapShot(0, joining);
    }

    @Test
    void holdsSnipersInAdditionOrder() {
        model.addSniper(SniperSnapShot.joining("item 0"));
        model.addSniper(SniperSnapShot.joining("item 1"));

        assertThat(model.getValueAt(0, Column.ITEM_IDENTIFIER.ordinal())).isEqualTo("item 0");
        assertThat(model.getValueAt(1, Column.ITEM_IDENTIFIER.ordinal())).isEqualTo("item 1");
    }

    private void assertRowMatchesSnapShot(int row, SniperSnapShot sniperSnapShot) {
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