package io.copymaker.auction.sniper;

import io.copymaker.auction.sniper.listener.AuctionEventListener;

public interface Auction {

    void bid(int amount);

    void join();

    void addAuctionEventListener(AuctionEventListener auctionEventListener);
}
