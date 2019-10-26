package io.copymaker.auctionsniper;

import org.jivesoftware.smack.XMPPException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * 어떤 메서드가 이벤트를 발생시켜 테스트를 이끈다면 해당 메서드의 이름은 명령(command)
 * 어떤 메서드에서 어떤 일이 일어나야 한다고 단정(assert) 한다면 해당 메서드의 이름은 서술형
 */
class AuctionSniperEndToEndTest {

    private final FakeAuctionServer auction1 = new FakeAuctionServer("item-54321");
    private final FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");
    private final ApplicationRunner application = new ApplicationRunner();

    @AfterEach
    void tearDown() {
        auction1.stop();
        auction2.stop();
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
        auction1.startSellingItem();                 // 1단계
        application.startBiddingIn(auction1);        // 2단계
        auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID); // 3단계
        auction1.announceClosed();                   // 4단계
        application.showsSniperHasLostAuction(auction1,0, 0);    // 5단계
    }

    @Test
    void sniperMakesAHigherBidButLoses() throws XMPPException, InterruptedException {
        auction1.startSellingItem();

        application.startBiddingIn(auction1);
        auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction1.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction1,1000, 1098);

        auction1.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction1.announceClosed();
        application.showsSniperHasLostAuction(auction1,1000, 1098);
    }

    @Test
    void sniperWinsAnAuctionByBiddingHigher() throws XMPPException, InterruptedException {
        auction1.startSellingItem();

        application.startBiddingIn(auction1);
        auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction1.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction1,1000, 1098);    // 최종 가격과 입찰

        auction1.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction1.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        application.hasShownSniperIsWinning(auction1, 1098);  // 낙찰

        auction1.announceClosed();
        application.showsSniperHasWonAuction(auction1, 1098); // 최종 가격
    }

    @Test
    void sniperBidsForMultipleItems() throws XMPPException, InterruptedException {
        auction1.startSellingItem();
        auction2.startSellingItem();

        application.startBiddingIn(auction1, auction2);
        auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction1.reportPrice(1000, 86, "other bidder");
        auction1.hasReceivedBid(1086, ApplicationRunner.SNIPER_XMPP_ID);

        auction2.reportPrice(500, 21, "other bidder");
        auction2.hasReceivedBid(521, ApplicationRunner.SNIPER_XMPP_ID);

        auction1.reportPrice(1086, 97, ApplicationRunner.SNIPER_XMPP_ID);
        auction2.reportPrice(521, 22, ApplicationRunner.SNIPER_XMPP_ID);

        application.hasShownSniperIsWinning(auction1, 1086);
        application.hasShownSniperIsWinning(auction2, 521);

        auction1.announceClosed();
        auction2.announceClosed();

        application.showsSniperHasWonAuction(auction1, 1086);
        application.showsSniperHasWonAuction(auction2, 521);
    }

    @Test
    void sniperLosesAnAuctionWhenThePriceIsTooHigh() throws XMPPException, InterruptedException {
        auction1.startSellingItem();

        application.startBiddingWithStopPrice(auction1, 1100);
        auction1.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction1.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction1, 1000, 1098);

        auction1.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction1.reportPrice(1197, 10, "third party");
        application.hasShownSniperIsLosing(auction1, 1197, 1098);

        auction1.reportPrice(1207, 10, "fourth party");
        application.hasShownSniperIsLosing(auction1, 1207, 1098);

        auction1.announceClosed();
        application.showsSniperHasLostAuction(auction1, 1207, 1098);
    }
}
