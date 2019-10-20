package io.copymaker.auction.sniper.listener;

import io.copymaker.auction.sniper.SniperState;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperBidding(SniperState sniperState);

    void sniperWinning();

    void sniperLost();

    void sniperWon();
}
