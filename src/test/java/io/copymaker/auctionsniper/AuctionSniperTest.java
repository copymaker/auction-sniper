package io.copymaker.auctionsniper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionSniperTest {

    private final Item item = new Item("item-54321", 1234);

    @Mock
    private Auction auction;

    @Mock
    private SniperListener sniperListener;

    private AuctionSniper sniper;

    @Captor
    private ArgumentCaptor<SniperSnapshot> snapshotCaptor;

    @BeforeEach
    void setUp() {
        sniper = new AuctionSniper(item, auction);
        sniper.addSniperListener(sniperListener);
    }

    @Test
    void reportsLostWhenAuctionClosedImmediately() {
        sniper.auctionClosed();

        verify(sniperListener, times(1)).sniperStateChanged(snapshotCaptor.capture());
        assertThat(snapshotCaptor.getValue().sniperState).isEqualTo(SniperState.LOST);
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;

        sniper.currentPrice(price, increment, AuctionEventListener.PriceSource.FROM_OTHER_BIDDER);

        verify(auction, times(1)).bid(eq(price + increment));
        verify(sniperListener, atLeast(1)).sniperStateChanged(snapshotCaptor.capture());
        assertThat(snapshotCaptor.getValue().sniperState).isEqualTo(SniperState.BIDDING);
    }

    @Test
    void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 12, AuctionEventListener.PriceSource.FROM_OTHER_BIDDER);
        sniper.currentPrice(135, 45, AuctionEventListener.PriceSource.FROM_SNIPER);

        verify(sniperListener, atLeastOnce())
                .sniperStateChanged(new SniperSnapshot(item.getId(), 135, 135, SniperState.WINNING));
    }

    @Test
    void reportsLostIfAuctionClosesWhenBidding() {
        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FROM_OTHER_BIDDER);
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperStateChanged(snapshotCaptor.capture());
        assertThat(snapshotCaptor.getValue().sniperState).isEqualTo(SniperState.LOST);
    }

    @Test
    void reportsWonIfAuctionClosesWhenWinning() {
        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FROM_SNIPER);
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperStateChanged(snapshotCaptor.capture());
        assertThat(snapshotCaptor.getValue().sniperState).isEqualTo(SniperState.WON);
    }

    @Test
    void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
        SniperSnapshot expected = new SniperSnapshot(item.getId(), 2345, 123 + 45, SniperState.LOSING);

        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FROM_OTHER_BIDDER);
        sniper.currentPrice(2345, 25, AuctionEventListener.PriceSource.FROM_OTHER_BIDDER);

        verify(sniperListener, atLeastOnce()).sniperStateChanged(snapshotCaptor.capture());
        assertThat(snapshotCaptor.getValue()).isEqualTo(expected);
    }
}