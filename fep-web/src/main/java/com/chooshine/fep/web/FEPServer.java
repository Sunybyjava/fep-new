package com.chooshine.fep.web;

import java.io.IOException;

import com.chooshine.fep.newtcpchannel.TCPControl;

public class FEPServer {

    private TCPControl tcp;

    public void start() {
        if (tcp == null)
            tcp = new TCPControl();
        tcp.connect();
    }

    public void stop() {
        if (tcp != null)
            try {
                tcp.disConnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
