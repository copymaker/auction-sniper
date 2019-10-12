package io.copymaker.auction.sniper;

import org.jivesoftware.smack.XMPPException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 어떤 메서드가 이벤트를 발생시켜 테스트를 이끈다면 해당 메서드의 이름은 명령(command)
 * 어떤 메서드에서 어떤 일이 일어나야 한다고 단정(assert) 한다면 해당 메서드의 이름은 서술형
 */

/**
 * 1. 경매에서 품목을 판매하고
 * 2. 경매 스나이퍼가 해당 경매에서 입찰을 시작하면
 * 3. 경매에서는 경매 스나이퍼로부터 Join 요청을 받을 것이다.
 * 4. 경매가 Close 됐다고 선언되면
 * 5. 경매 스나이퍼는 경매에서 낙찰에 실패했음을 보여줄 것이다.
 */
class AuctionSniperEndToEndTest {

    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @AfterEach
    void tearDown() {
        auction.stop();
        application.stop();
    }

    @Test
    void sniperJoinsAuctionUntilAuctionCloses() throws XMPPException, InterruptedException {
        auction.startSellingItem();                 // 1단계
        application.startBiddingIn(auction);        // 2단계
        auction.hasReceivedJoinRequestFromSniper(); // 3단계
        auction.announceClosed();                   // 4단계
        application.showsSniperHasLostAuction();    // 5단계
    }
}
