package io.copymaker.auction.sniper;

import io.copymaker.auction.sniper.listener.AuctionEventListener;
import io.copymaker.auction.sniper.listener.SniperListener;

public class AuctionSniper implements AuctionEventListener {

    private final Auction auction;
    private final SniperListener sniperListener;

    private SniperSnapShot sniperSnapShot;

    public AuctionSniper(String itemId, Auction auction, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
        this.sniperSnapShot = SniperSnapShot.joining(itemId);
    }

    @Override
    public void auctionClosed() {
        sniperSnapShot = sniperSnapShot.closed();
        notifyChange();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FROM_SNIPER:
                sniperSnapShot = sniperSnapShot.winning(price);
                break;
            case FROM_OTHER_BIDDER:
                int bid = price + increment;
                auction.bid(bid);
                sniperSnapShot = sniperSnapShot.bidding(price, bid);
                break;
        }
        notifyChange();
    }

    private void notifyChange() {
        sniperListener.sniperStateChanged(sniperSnapShot);
    }
}
