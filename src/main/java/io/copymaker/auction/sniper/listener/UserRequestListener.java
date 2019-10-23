package io.copymaker.auction.sniper.listener;

import java.util.EventListener;

public interface UserRequestListener extends EventListener {
    void joinAuction(String itemId);
}
