package io.copymaker.auctionsniper;

public class SniperPortfolio implements SniperCollector {

    private final Announcer<PortfolioListener> portfolioListeners = Announcer.to(PortfolioListener.class);

    @Override
    public void addSniper(AuctionSniper auctionSniper) {
        portfolioListeners.announce().sniperAdded(auctionSniper);
    }

    public void addPortfolioListener(PortfolioListener portfolioListener) {
        portfolioListeners.addListener(portfolioListener);
    }
}
