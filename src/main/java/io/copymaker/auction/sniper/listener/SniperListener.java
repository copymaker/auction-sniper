package io.copymaker.auction.sniper.listener;

import io.copymaker.auction.sniper.SniperSnapShot;

import java.util.EventListener;

public interface SniperListener extends EventListener {

    void sniperStateChanged(SniperSnapShot sniperSnapShot);

}
