package io.copymaker.auctionsniper.ui;

import io.copymaker.auctionsniper.Item;
import io.copymaker.auctionsniper.SniperPortfolio;
import io.copymaker.auctionsniper.AuctionSniperDriver;
import io.copymaker.auctionsniper.UserRequestListener;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.*;

class MainWindowTest {

    private SniperPortfolio sniperPortfolio;
    private MainWindow mainWindow;
    private AuctionSniperDriver driver;

    @BeforeEach
    void setUp() {
        sniperPortfolio = new SniperPortfolio();
        mainWindow = new MainWindow(sniperPortfolio);
        driver = new AuctionSniperDriver(100);
    }

    @AfterEach
    void tearDown() {
        mainWindow.dispose();
        driver.dispose();
    }

    @Test
    void makesUserRequestWhenJoinButtonClicked() {
        Item anItem = new Item("an item-id", 789);

        mainWindow.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(Item item) {
                assertThat(item).isEqualTo(anItem);
            }
        });

        driver.startBiddingFor("an item-id", 789);
    }

}