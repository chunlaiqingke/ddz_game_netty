package com.handsome;

import com.handsome.proxy.WebSocketProxy;

public class SimpleServer {

    public static void main(String[] args) throws InterruptedException {
        if (args != null && args.length > 1) {
            if (args[0].equalsIgnoreCase("-p") || args[0].equalsIgnoreCase("-port")) {
                ServerContains.port = Integer.parseInt(args[1]);
            }
        }
        new WebSocketProxy().start(ServerContains.port);

    }
}