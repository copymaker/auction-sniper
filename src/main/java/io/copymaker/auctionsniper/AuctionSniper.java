package io.copymaker.auctionsniper;

public class AuctionSniper implements AuctionEventListener {

    private final Item item;
    private final Auction auction;
    private SniperSnapshot sniperSnapshot;

    private final Announcer<SniperListener> sniperListeners = Announcer.to(SniperListener.class);

    public AuctionSniper(Item item, Auction auction) {
        this.item = item;
        this.auction = auction;
        this.sniperSnapshot = SniperSnapshot.joining(item.getId());
    }

    @Override
    public void auctionClosed() {
        sniperSnapshot = sniperSnapshot.closed();
        notifyChange();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FROM_SNIPER:
                sniperSnapshot = sniperSnapshot.winning(price);
                break;
            case FROM_OTHER_BIDDER:
                int bid = price + increment;
                if (item.allowsBid(bid)) {
                    auction.bid(bid);
                    sniperSnapshot = sniperSnapshot.bidding(price, bid);
                } else {
                    sniperSnapshot = sniperSnapshot.losing(price);
                }
                break;
        }
        notifyChange();
    }

    public String getItemId() {
        return item.getId();
    }

    public SniperSnapshot getSnapshot() {
        return sniperSnapshot;
    }

    public void addSniperListener(SniperListener sniperListener) {
        sniperListeners.addListener(sniperListener);
    }

    private void notifyChange() {
        sniperListeners.announce().sniperStateChanged(sniperSnapshot);
    }
}
