package io.copymaker.auction.sniper;

import io.copymaker.auction.sniper.listener.AuctionEventListener;
import io.copymaker.auction.sniper.listener.SniperListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionSniperTest {

    private final Auction auction = Mockito.mock(Auction.class);
    private final SniperListener sniperListener = Mockito.mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);

    private SniperState sniperState = SniperState.IDLE;
    private enum SniperState {
        IDLE, WINNING, BIDDING
    }

    @Test
    void reportsLostWhenAuctionClosedImmediately() {
        sniper.auctionClosed();

        verify(sniperListener, times(1)).sniperLost();
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;

        sniper.currentPrice(price, increment, AuctionEventListener.PriceSource.FROM_OTHER_BIDDER);

        verify(auction, times(1)).bid(eq(price + increment));
        verify(sniperListener, atLeast(1)).sniperBidding();
    }

    @Test
    void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FROM_SNIPER);

        verify(sniperListener, atLeast(1)).sniperWinning();
    }

    @Test
    void reportsLostIfAuctionClosesWhenBidding() {
        doAnswer(invocation -> sniperState = SniperState.BIDDING).when(sniperListener).sniperBidding();

        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FROM_OTHER_BIDDER);
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperLost();
        assertThat(sniperState).isEqualTo(SniperState.BIDDING);
    }

    @Test
    void reportsWonIfAuctionClosesWhenWinning() {
        doAnswer(invocation -> sniperState = SniperState.WINNING).when(sniperListener).sniperWinning();

        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FROM_SNIPER);
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperWon();
        assertThat(sniperState).isEqualTo(SniperState.WINNING);
    }

}