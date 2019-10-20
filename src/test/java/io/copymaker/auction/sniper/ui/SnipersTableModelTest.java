package io.copymaker.auction.sniper.ui;

import io.copymaker.auction.sniper.SniperSnapShot;
import io.copymaker.auction.sniper.SniperState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static io.copymaker.auction.sniper.ui.SnipersTableModel.textFor;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

class SnipersTableModelTest {

    private TableModelListener listener;
    private SnipersTableModel model;

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
        model.sniperStateChanged(new SniperSnapShot("item id", 555, 666, SniperState.BIDDING));

        verify(listener, only()).tableChanged(any(TableModelEvent.class));

        assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
        assertColumnEquals(Column.LAST_PRICE, 555);
        assertColumnEquals(Column.LAST_BID, 666);
        assertColumnEquals(Column.SNIPER_STATE, textFor(SniperState.BIDDING));
    }

    @Test
    void setsUpColumnHeadings() {
        for (Column column : Column.values()) {
            assertThat(model.getColumnName(column.ordinal())).isEqualTo(column.getName());
        }
    }

    private void assertColumnEquals(Column column, Object expected) {
        final int rowIndex = 0;
        final int columnIndex = column.ordinal();
        assertThat(model.getValueAt(rowIndex, columnIndex)).isEqualTo(expected);
    }

}