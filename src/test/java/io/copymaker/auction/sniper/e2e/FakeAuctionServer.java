package io.copymaker.auction.sniper.e2e;

import io.copymaker.auction.sniper.Main;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import static org.assertj.core.api.Assertions.assertThat;

public class FakeAuctionServer {

    public static final String XMPP_HOSTNAME = "localhost";

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_PASSWORD = "auction";

    private final XMPPConnection connection;
    private final SingleMessageListener messageListener = new SingleMessageListener();
    private Chat currentChat;

    private final String itemId;

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        this.connection = new XMPPConnection(XMPP_HOSTNAME);
    }

    public void startSellingItem() throws XMPPException {
        connection.connect();
        connection.login(String.format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
        connection.getChatManager().addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                currentChat = chat;
                chat.addMessageListener(messageListener);
            }
        });
    }

    public void reportPrice(int price, int increment, String bidder) throws XMPPException {
        currentChat.sendMessage(String.format("SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;",
                price, increment, bidder));
    }

    public void hasReceivedJoinRequestFrom(String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, Main.JOIN_COMMAND_FORMAT);
    }

    public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, String.format(Main.BID_COMMAND_FORMAT, bid));
    }

    private void receivesAMessageMatching(String sniperId, String receivedMessage) throws InterruptedException {
        messageListener.receivesAMessage(receivedMessage);
        assertThat(currentChat.getParticipant()).isEqualTo(sniperId);
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
    }

    public void stop() {
        connection.disconnect();
    }

    public String getItemId() {
        return itemId;
    }
}
