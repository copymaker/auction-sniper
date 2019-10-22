package io.copymaker.auction.sniper;

import java.util.Objects;

public class SniperSnapShot {

    public final String itemId;
    public final int lastPrice;
    public final int lastBid;
    public final SniperState sniperState;

    public SniperSnapShot(String itemId, int lastPrice, int lastBid, SniperState sniperState) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
        this.sniperState = sniperState;
    }

    public static SniperSnapShot joining(String itemId) {
        return new SniperSnapShot(itemId, 0, 0, SniperState.JOINING);
    }

    public SniperSnapShot bidding(int newLastPrice, int newLastBid) {
        return new SniperSnapShot(itemId, newLastPrice, newLastBid, SniperState.BIDDING);
    }

    public SniperSnapShot winning(int newLastPrice) {
        return new SniperSnapShot(itemId, newLastPrice, lastBid, SniperState.WINNING);
    }

    public SniperSnapShot closed() {
        return new SniperSnapShot(itemId, lastPrice, lastBid, sniperState.whenAuctionClosed());
    }

    public boolean isForSameItemAs(SniperSnapShot other) {
        return this.itemId.equals(other.itemId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SniperSnapShot that = (SniperSnapShot) o;
        return lastPrice == that.lastPrice &&
                lastBid == that.lastBid &&
                Objects.equals(itemId, that.itemId) &&
                sniperState == that.sniperState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, lastPrice, lastBid, sniperState);
    }

    @Override
    public String toString() {
        return "SniperSnapShot{" +
                "itemId='" + itemId + '\'' +
                ", lastPrice=" + lastPrice +
                ", lastBid=" + lastBid +
                ", sniperState=" + sniperState +
                '}';
    }
}
