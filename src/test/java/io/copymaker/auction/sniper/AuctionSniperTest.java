package io.copymaker.auction.sniper;

import io.copymaker.auction.sniper.listener.AuctionEventListener;
import io.copymaker.auction.sniper.listener.SniperListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionSniperTest {

    private final String ITEM_ID = "item-54321";
    private final Auction auction = Mockito.mock(Auction.class);
    private final SniperListener sniperListener = Mockito.mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(ITEM_ID, auction, sniperListener);

    @Captor
    private ArgumentCaptor<SniperSnapShot> snapShotArgumentCaptor;

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
                .sniperStateChanged(new SniperSnapShot(ITEM_ID, 135, 135, SniperState.WINNING));
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