package io.copymaker.auction.sniper;

import io.copymaker.auction.sniper.listener.AuctionEventListener;
import io.copymaker.auction.sniper.translator.AuctionMessageTranslator;
import io.copymaker.auction.sniper.ui.MainWindow;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

public class Main implements AuctionEventListener {

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    public static final String JOIN_COMMAND_FORMAT = "";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d";

    private MainWindow mainWindow;

    // ChatManager 문서에 채팅 객체 자체에 대한 참조를 유지해야 한다고 함. 가비지 컬렉션 대상에서 제외하기 위함
    @SuppressWarnings("unused")
    private Chat notToBeGCd;

    public Main() throws InvocationTargetException, InterruptedException {
        startUserInterface();
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.joinAuction(connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]), args[ARG_ITEM_ID]);
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
        disconnectWhenUICloses(connection);
        final Chat chat = connection.getChatManager().createChat(
                auctionId(itemId, connection),
                new AuctionMessageTranslator(this));
        chat.sendMessage(JOIN_COMMAND_FORMAT);
        this.notToBeGCd = chat;
    }

    @Override
    public void auctionClosed() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainWindow.showStatus(MainWindow.STATUS_LOST);
            }
        });
    }

    @Override
    public void currentPrice(int price, int increment) {

    }

    private void startUserInterface() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                mainWindow = new MainWindow();
            }
        });
    }

    private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    private void disconnectWhenUICloses(final XMPPConnection connection) {
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }
}
