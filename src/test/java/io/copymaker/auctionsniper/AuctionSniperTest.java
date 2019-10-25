package io.copymaker.auctionsniper;

import io.copymaker.auctionsniper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionSniperTest {

    private final String ITEM_ID = "item-54321";
    private Auction auction;
    private AuctionSniper sniper;
    private SniperListener sniperListener;

    @Captor
    private ArgumentCaptor<SniperSnapshot> snapShotArgumentCaptor;

    @BeforeEach
    void setUp() {
        auction = Mockito.mock(Auction.class);
        sniperListener = Mockito.mock(SniperListener.class);
        sniper = new AuctionSniper(ITEM_ID, auction);
        sniper.addSniperListener(sniperListener);
    }

    @Test
    void reportsLostWhenAuctionClosedImmediately() {
        sniper.auctionClosed();

        verify(sniperListener, times(1)).sniperStateChanged(snapShotArgumentCaptor.capture());
        assertThat(snapShotArgumentCaptor.getValue().sniperState).isEqualTo(SniperState.LOST);
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;

        sniper.currentPrice(price, increment, AuctionEventListener.PriceSource.FROM_OTHER_BIDDER);

        verify(auction, times(1)).bid(eq(price + increment));
        verify(sniperListener, atLeast(1)).sniperStateChanged(snapShotArgumentCaptor.capture());
        assertThat(snapShotArgumentCaptor.getValue().sniperState).isEqualTo(SniperState.BIDDING);
    }

    @Test
    void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 12, AuctionEventListener.PriceSource.FROM_OTHER_BIDDER);
        sniper.currentPrice(135, 45, AuctionEventListener.PriceSource.FROM_SNIPER);

        verify(sniperListener, atLeastOnce())
                .sniperStateChanged(new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
    }

    @Test
    void reportsLostIfAuctionClosesWhenBidding() {
        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FROM_OTHER_BIDDER);
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperStateChanged(snapShotArgumentCaptor.capture());
        assertThat(snapShotArgumentCaptor.getValue().sniperState).isEqualTo(SniperState.LOST);
    }

    @Test
    void reportsWonIfAuctionClosesWhenWinning() {
        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FROM_SNIPER);
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperStateChanged(snapShotArgumentCaptor.capture());
        assertThat(snapShotArgumentCaptor.getValue().sniperState).isEqualTo(SniperState.WON);
    }

}