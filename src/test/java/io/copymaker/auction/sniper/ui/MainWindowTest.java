package io.copymaker.auction.sniper.ui;

import io.copymaker.auction.sniper.e2e.AuctionSniperDriver;
import io.copymaker.auction.sniper.listener.UserRequestListener;
import org.junit.Ignore;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.*;

class MainWindowTest {

    private SnipersTableModel tableModel;
    private MainWindow mainWindow;
    private AuctionSniperDriver driver;

    @BeforeEach
    void setUp() {
        tableModel = new SnipersTableModel();
        mainWindow = new MainWindow(tableModel);
        driver = new AuctionSniperDriver(100);
    }

    @AfterEach
    void tearDown() {
        mainWindow.dispose();
        driver.dispose();
    }

    @Test
    void makesUserRequestWhenJoinButtonClicked() {
        mainWindow.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(String itemId) {
                assertThat(itemId).isEqualTo("an item-id");
            }
        });

        driver.startBiddingFor("an item-id");
    }

}