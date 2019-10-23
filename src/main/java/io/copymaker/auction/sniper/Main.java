package io.copymaker.auction.sniper;

import io.copymaker.auction.sniper.listener.UserRequestListener;
import io.copymaker.auction.sniper.translator.AuctionMessageTranslator;
import io.copymaker.auction.sniper.ui.MainWindow;
import io.copymaker.auction.sniper.ui.SnipersTableModel;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d";

    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow mainWindow;

    // ChatManager 문서에 채팅 객체 자체에 대한 참조를 유지해야 한다고 함. 가비지 컬렉션 대상에서 제외하기 위함
    @SuppressWarnings("unused")
    private List<Chat> notToBeGCds = new ArrayList<>();

    public Main() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> mainWindow = new MainWindow(snipers));
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        XMPPConnection connection = connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(connection);
        main.addUserRequestListenerFor(connection);
    }

    private void addUserRequestListenerFor(final XMPPConnection connection) {
        mainWindow.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(String itemId) {
                snipers.addSniper(SniperSnapShot.joining(itemId));
                final Chat chat = connection.getChatManager().createChat(auctionId(itemId, connection),null);
                notToBeGCds.add(chat);

                Auction auction = new XMPPAuction(chat);
                chat.addMessageListener(
                        new AuctionMessageTranslator(connection.getUser(),
                                new AuctionSniper(itemId, auction,
                                        new SwingThreadSniperListener(snipers))));
                auction.join();
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
