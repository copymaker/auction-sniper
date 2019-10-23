package io.copymaker.auction.sniper.xmpp;

import io.copymaker.auction.sniper.Auction;
import io.copymaker.auction.sniper.listener.AuctionEventListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.List;

public class XMPPAuction implements Auction {
    private final List<AuctionEventListener> auctionEventListeners = new ArrayList<>();
    private final Chat chat;

    public XMPPAuction(XMPPConnection connection, String itemId) {
        this.chat = connection.getChatManager().createChat(
                auctionId(itemId, connection),
                new AuctionMessageTranslator(connection.getUser(), auctionEventListeners));
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener auctionEventListener) {
        auctionEventListeners.add(auctionEventListener);
    }

    @Override
    public void bid(int amount) {
        sendMessage(String.format(XMPPAuctionHouse.BID_COMMAND_FORMAT, amount));
    }

    @Override
    public void join() {
        sendMessage(XMPPAuctionHouse.JOIN_COMMAND_FORMAT);
    }

    private void sendMessage(final String message) {
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(XMPPAuctionHouse.AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }
}
