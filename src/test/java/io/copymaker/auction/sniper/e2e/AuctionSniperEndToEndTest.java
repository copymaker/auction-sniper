package io.copymaker.auction.sniper.e2e;

import org.jivesoftware.smack.XMPPException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * 어떤 메서드가 이벤트를 발생시켜 테스트를 이끈다면 해당 메서드의 이름은 명령(command)
 * 어떤 메서드에서 어떤 일이 일어나야 한다고 단정(assert) 한다면 해당 메서드의 이름은 서술형
 */
class AuctionSniperEndToEndTest {

    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @AfterEach
    void tearDown() {
        auction.stop();
        application.stop();
    }

    /**
     * 1. 경매에서 품목을 판매하고
     * 2. 경매 스나이퍼가 해당 경매에서 입찰을 시작하면
     * 3. 경매에서는 경매 스나이퍼로부터 Join 요청을 받을 것이다.
     * 4. 경매가 Close 됐다고 선언되면
     * 5. 경매 스나이퍼는 경매에서 낙찰에 실패했음을 보여줄 것이다.
     */
    @Test
    void sniperJoinsAuctionUntilAuctionCloses() throws XMPPException, InterruptedException {
        auction.startSellingItem();                 // 1단계
        application.startBiddingIn(auction);        // 2단계
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID); // 3단계
        auction.announceClosed();                   // 4단계
        application.showsSniperHasLostAuction(0, 0);    // 5단계
    }

    @Test
    void sniperMakesAHigherBidButLoses() throws XMPPException, InterruptedException {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(1000, 1098);

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.announceClosed();
        application.showsSniperHasLostAuction(1000, 1098);
    }

    @Test
    void sniperWinsAnAuctionByBiddingHigher() throws XMPPException, InterruptedException {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(1000, 1098);    // 최종 가격과 입찰

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        application.hasShownSniperIsWinning(1098);  // 낙찰

        auction.announceClosed();
        application.showsSniperHasWonAuction(1098); // 최종 가격
    }

}
