package io.copymaker.auction.sniper.listener;

public interface AuctionEventListener {
    void auctionClosed();

    void currentPrice(int price, int increment);
}
