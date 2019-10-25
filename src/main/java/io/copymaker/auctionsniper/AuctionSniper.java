package io.copymaker.auctionsniper;

import java.util.ArrayList;
import java.util.List;

public class AuctionSniper implements AuctionEventListener {

    private final String itemId;
    private final Auction auction;
    private SniperSnapshot sniperSnapshot;

    private final Announcer<SniperListener> sniperListeners = Announcer.to(SniperListener.class);

    public AuctionSniper(String itemId, Auction auction) {
        this.itemId = itemId;
        this.auction = auction;
        this.sniperSnapshot = SniperSnapshot.joining(itemId);
    }

    @Override
    public void auctionClosed() {
        sniperSnapshot = sniperSnapshot.closed();
        notifyChange();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FROM_SNIPER:
                sniperSnapshot = sniperSnapshot.winning(price);
                break;
            case FROM_OTHER_BIDDER:
                int bid = price + increment;
                auction.bid(bid);
                sniperSnapshot = sniperSnapshot.bidding(price, bid);
                break;
        }
        notifyChange();
    }

    public String getItemId() {
        return itemId;
    }

    public SniperSnapshot getSnapshot() {
        return sniperSnapshot;
    }

    public void addSniperListener(SniperListener sniperListener) {
        sniperListeners.addListener(sniperListener);
    }

    private void notifyChange() {
        sniperListeners.announce().sniperStateChanged(sniperSnapshot);
    }
}
