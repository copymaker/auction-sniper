package io.copymaker.auctionsniper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class SniperLauncherTest {

    private final String itemId = "item 123";
    @Mock private Auction auction;
    @Mock private AuctionHouse auctionHouse;
    @Mock private SniperCollector sniperCollector;

    private SniperLauncher launcher;

    @Captor
    private ArgumentCaptor<AuctionSniper> auctionSniperCaptor;

    @BeforeEach
    void setUp() {
        launcher = new SniperLauncher(auctionHouse, sniperCollector);
    }

    @Test
    void addsNewSniperToCollectorAndThenJoinsAuction() {
        given(auctionHouse.auctionFor(itemId)).willReturn(auction);

        launcher.joinAuction(itemId);

        InOrder inOrder = inOrder(auction, sniperCollector);

        inOrder.verify(sniperCollector, atLeastOnce()).addSniper(auctionSniperCaptor.capture());
        inOrder.verify(auction, atLeastOnce()).addAuctionEventListener(any(AuctionEventListener.class));
        inOrder.verify(auction).join();

        assertThat(auctionSniperCaptor.getValue().getItemId()).isEqualTo(itemId);
    }
}