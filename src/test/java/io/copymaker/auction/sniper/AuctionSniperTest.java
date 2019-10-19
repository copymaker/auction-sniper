package io.copymaker.auction.sniper;

import io.copymaker.auction.sniper.listener.SniperListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionSniperTest {

    private final Auction auction = Mockito.mock(Auction.class);
    private final SniperListener sniperListener = Mockito.mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);

    @Test
    void reportsLostWhenAuctionClosed() {
        sniper.auctionClosed();

        verify(sniperListener, times(1)).sniperLost();
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;

        sniper.currentPrice(price, increment);

        verify(auction, times(1)).bid(eq(price + increment));
        verify(sniperListener, atLeast(1)).sniperBidding();
    }

}