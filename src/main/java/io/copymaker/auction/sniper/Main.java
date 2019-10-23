package io.copymaker.auction.sniper;

import io.copymaker.auction.sniper.listener.UserRequestListener;
import io.copymaker.auction.sniper.ui.MainWindow;
import io.copymaker.auction.sniper.ui.SnipersTableModel;
import io.copymaker.auction.sniper.xmpp.XMPPAuctionHouse;

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

    public static final String XMPP_HOSTNAME = "localhost";

    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow mainWindow;

    // ChatManager 문서에 채팅 객체 자체에 대한 참조를 유지해야 한다고 함. 가비지 컬렉션 대상에서 제외하기 위함
    @SuppressWarnings("unused")
    private List<Auction> notToBeGCds = new ArrayList<>();

    public Main() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> mainWindow = new MainWindow(snipers));
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }

    private void addUserRequestListenerFor(final AuctionHouse auctionHouse) {
        mainWindow.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(String itemId) {
                snipers.addSniper(SniperSnapShot.joining(itemId));
                Auction auction = auctionHouse.auctionFor(itemId);
                notToBeGCds.add(auction);
                auction.addAuctionEventListener(
                        new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers))
                );
                auction.join();
            }
        });
    }

    private void disconnectWhenUICloses(final XMPPAuctionHouse auctionHouse) {
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                auctionHouse.disconnect();
            }
        });
    }
}
