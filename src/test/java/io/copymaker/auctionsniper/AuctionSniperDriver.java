package io.copymaker.auctionsniper;

import io.copymaker.auctionsniper.ui.MainWindow;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.Robot;
import org.assertj.swing.data.TableCellInRowByValue;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.assertj.swing.fixture.JTextComponentFixture;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class AuctionSniperDriver {

    private final Robot robot = BasicRobot.robotWithCurrentAwtHierarchy();
    private final FrameFixture window;

    public AuctionSniperDriver(int timeoutMillis) {
        window = WindowFinder.findFrame(getMatcher())
                .withTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                .using(robot);
    }

    private GenericTypeMatcher<JFrame> getMatcher() {
        return new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return frame.getName().equals(MainWindow.MAIN_WINDOW_NAME) && frame.isShowing();
            }
        };
    }

    public void startBiddingFor(String itemId, int stopPrice) {
        JTextComponentFixture itemIdField = textField(MainWindow.NEW_ITEM_ID_NAME);
        JTextComponentFixture itemStopPriceField = textField(MainWindow.NEW_ITEM_STOP_PRICE_NAME);

        if (!itemIdField.text().isEmpty()) {
            itemIdField.deleteText();
        }
        if (!itemStopPriceField.text().isEmpty()) {
            itemStopPriceField.deleteText();
        }
        itemIdField.enterText(itemId);
        itemStopPriceField.enterText(String.valueOf(stopPrice));
        bidButton().click();
    }

    public void hasTitle(String expectedTitle) {
        window.requireTitle(expectedTitle);
    }

    public void hasColumnTitles() {
        for (Column column : Column.values()) {
            window.table().requireColumnNamed(column.getName());
        }
    }

    public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String statusText) {
        try {
            Thread.sleep(300);
            JTableFixture tableFixture = window.table();

            TableCellInRowByValue.TableCellBuilder tableCellBuilder =
                    TableCellInRowByValue.rowWithValue(itemId, String.valueOf(lastPrice), String.valueOf(lastBid), statusText);

            tableFixture.cell(tableCellBuilder.column(Column.ITEM_IDENTIFIER.ordinal())).requireValue(itemId);
            tableFixture.cell(tableCellBuilder.column(Column.LAST_PRICE.ordinal())).requireValue(String.valueOf(lastPrice));
            tableFixture.cell(tableCellBuilder.column(Column.LAST_BID.ordinal())).requireValue(String.valueOf(lastBid));
            tableFixture.cell(tableCellBuilder.column(Column.SNIPER_STATE.ordinal())).requireValue(statusText);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        window.cleanUp();
    }

    private JTextComponentFixture textField(String fieldName) {
        return window.textBox(fieldName);
    }

    private JButtonFixture bidButton() {
        return window.button(MainWindow.JOIN_BUTTON_NAME);
    }
}
