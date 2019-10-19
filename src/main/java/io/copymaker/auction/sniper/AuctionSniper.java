package io.copymaker.auction.sniper;

import io.copymaker.auction.sniper.listener.AuctionEventListener;
import io.copymaker.auction.sniper.listener.SniperListener;

public class AuctionSniper implements AuctionEventListener {

    private final Auction auction;
    private final SniperListener sniperListener;

    public AuctionSniper(Auction auction, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
    }

    @Override
    public void auctionClosed() {
        sniperListener.sniperLost();
    }

    @Override
    public void currentPrice(int price, int increment) {
        auction.bid(price + increment);
        sniperListener.sniperBidding();
    }
}
