package io.copymaker.auction.sniper.xmpp;

import io.copymaker.auction.sniper.listener.AuctionEventListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.*;

public class AuctionMessageTranslator implements MessageListener {

    private final String sniperId;
    private final List<AuctionEventListener> listeners;

    public AuctionMessageTranslator(String sniperId, List<AuctionEventListener> listeners) {
        this.sniperId = sniperId;
        this.listeners = listeners;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        AuctionEvent event = AuctionEvent.from(message.getBody());

        String eventType = event.type();
        if ("CLOSE".equals(eventType)) {
            listeners.forEach(listener -> listener.auctionClosed());
        } else if ("PRICE".equals(eventType)) {
            listeners.forEach(listener ->
                    listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId)));
        }
    }

    private static class AuctionEvent {
        private final Map<String, String> fields = new HashMap<>();

        static AuctionEvent from(String messageBody) {
            AuctionEvent event = new AuctionEvent();
            for (String field : fieldsIn(messageBody)) {
                event.addField(field);
            }
            return event;
        }

        public String type() {
            return get("Event");
        }

        public int currentPrice() {
            return getInt("CurrentPrice");
        }

        public int increment() {
            return getInt("Increment");
        }

        public AuctionEventListener.PriceSource isFrom(String sniperId) {
            return sniperId.equals(bidder()) ? AuctionEventListener.PriceSource.FROM_SNIPER : AuctionEventListener.PriceSource.FROM_OTHER_BIDDER;
        }

        private String get(String fieldName) {
            return fields.get(fieldName);
        }

        private int getInt(String fieldName) {
            return Integer.parseInt(get(fieldName));
        }

        private String bidder() {
            return get("Bidder");
        }

        private void addField(String field) {
            String[] pair = field.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }

        private static String[] fieldsIn(String messageBody) {
            return messageBody.split(";");
        }
    }
}
