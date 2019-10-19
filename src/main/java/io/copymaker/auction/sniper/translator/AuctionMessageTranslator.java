package io.copymaker.auction.sniper.translator;

import io.copymaker.auction.sniper.listener.AuctionEventListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.HashMap;
import java.util.Map;

public class AuctionMessageTranslator implements MessageListener {

    private final AuctionEventListener listener;

    public AuctionMessageTranslator(AuctionEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        Map<String, String> event = unpackEventFrom(message);

        String eventType = event.get("Event");
        if ("CLOSE".equals(eventType)) {
            listener.auctionClosed();
        } else if ("PRICE".equals(eventType)) {
            listener.currentPrice(Integer.parseInt(event.get("CurrentPrice")), Integer.parseInt(event.get("Increment")));
        }
    }

    private Map<String, String> unpackEventFrom(Message message) {
        Map<String, String> event = new HashMap<>();
        for (String element : message.getBody().split(";")) {
            String[] pair = element.split(":");
            event.put(pair[0].trim(), pair[1].trim());
        }
        return event;
    }
}
