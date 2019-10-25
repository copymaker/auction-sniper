package io.copymaker.auctionsniper.xmpp;

import io.copymaker.auctionsniper.Auction;
import io.copymaker.auctionsniper.Main;
import io.copymaker.auctionsniper.ApplicationRunner;
import io.copymaker.auctionsniper.FakeAuctionServer;
import io.copymaker.auctionsniper.AuctionEventListener;
import org.jivesoftware.smack.XMPPException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class XMPPAuctionTest {

    private final String ITEM_ID = "item-54321";
    private final FakeAuctionServer auctionServer = new FakeAuctionServer(ITEM_ID);
    private XMPPAuctionHouse auctionHouse;

    @BeforeEach
    public void startSellingItem() throws XMPPException {
        auctionHouse = XMPPAuctionHouse.connect(Main.XMPP_HOSTNAME, ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD);
        auctionServer.startSellingItem();
    }

    @AfterEach
    public void closeConnection() {
        if (auctionHouse != null)
            auctionHouse.disconnect();
    }

    @Test
    void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        Auction auction = auctionHouse.auctionFor(ITEM_ID);
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

        auction.join();
        auctionServer.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auctionServer.announceClosed();

        assertTrue(auctionWasClosed.await(2, TimeUnit.SECONDS));
    }

    private AuctionEventListener auctionClosedListener(CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            @Override
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource priceSource) {
                // 구현하지 않음
            }
        };
    }

}