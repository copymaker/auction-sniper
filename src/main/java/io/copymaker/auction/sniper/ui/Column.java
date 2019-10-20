package io.copymaker.auction.sniper.ui;

import io.copymaker.auction.sniper.SniperSnapShot;

public enum Column {
    ITEM_IDENTIFIER("Item") {
        @Override
        public Object valueIn(SniperSnapShot sniperSnapShot) {
            return sniperSnapShot.itemId;
        }
    },
    LAST_PRICE("Last Price") {
        @Override
        public Object valueIn(SniperSnapShot sniperSnapShot) {
            return sniperSnapShot.lastPrice;
        }
    },
    LAST_BID("Last Bid") {
        @Override
        public Object valueIn(SniperSnapShot sniperSnapShot) {
            return sniperSnapShot.lastBid;
        }
    },
    SNIPER_STATE("State") {
        @Override
        public Object valueIn(SniperSnapShot sniperSnapShot) {
            return SnipersTableModel.textFor(sniperSnapShot.sniperState);
        }
    };

    private String name;

    Column(String name) {
        this.name = name;
    }

    public static Column at(int offset) {
        return values()[offset];
    }

    public String getName() {
        return name;
    }

    public abstract Object valueIn(SniperSnapShot sniperSnapShot);
}
