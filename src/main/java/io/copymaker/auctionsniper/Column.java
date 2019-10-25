package io.copymaker.auctionsniper;

public enum Column {
    ITEM_IDENTIFIER("Item") {
        @Override
        public Object valueIn(SniperSnapshot sniperSnapshot) {
            return sniperSnapshot.itemId;
        }
    },
    LAST_PRICE("Last Price") {
        @Override
        public Object valueIn(SniperSnapshot sniperSnapshot) {
            return sniperSnapshot.lastPrice;
        }
    },
    LAST_BID("Last Bid") {
        @Override
        public Object valueIn(SniperSnapshot sniperSnapshot) {
            return sniperSnapshot.lastBid;
        }
    },
    SNIPER_STATE("State") {
        @Override
        public Object valueIn(SniperSnapshot sniperSnapshot) {
            return SnipersTableModel.textFor(sniperSnapshot.sniperState);
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

    public abstract Object valueIn(SniperSnapshot sniperSnapshot);
}
