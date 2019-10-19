package io.copymaker.auction.sniper.listener;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperBidding();

    void sniperWinning();

    void sniperLost();

    void sniperWon();
}
