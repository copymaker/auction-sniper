package io.copymaker.auction.sniper.e2e;

import io.copymaker.auction.sniper.ui.MainWindow;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.Robot;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;

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

    public void showsSniperStatus(String statusText) {
        try {
            Thread.sleep(300);
            window.table().requireCellValue(TableCell.row(0).column(0), statusText);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void dispose() {
        window.cleanUp();
    }
}
