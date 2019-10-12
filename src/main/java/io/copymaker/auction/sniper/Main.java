package io.copymaker.auction.sniper;

import io.copymaker.auction.sniper.ui.MainWindow;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class Main {

    private MainWindow mainWindow;

    public Main() throws InvocationTargetException, InterruptedException {
        startUserInterface();
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
    }

    private void startUserInterface() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                mainWindow = new MainWindow();
            }
        });
    }
}
