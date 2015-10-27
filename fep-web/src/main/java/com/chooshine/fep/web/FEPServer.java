package com.chooshine.fep.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.chooshine.fep.communicate.CommunicationServer;
import com.chooshine.fep.fepex.rwlz.CustomTaskAutomaticDemand;
import com.chooshine.fep.tcpchannel.TCPServer;

@Component
public class FEPServer implements ApplicationListener<ApplicationEvent> {
    private static Logger log = LoggerFactory.getLogger(FEPServer.class.getName());
    //    private TCPControl tcp;
    private TCPServer tcp;
    private CustomTaskAutomaticDemand td;
    private CommunicationServer cs;
    private volatile boolean isStart = false;

    public void start() {
        log.info("FEPServer start");
        if (!isStart) {
            log.info("启动前置机服务");
            if (tcp == null)
                tcp = new TCPServer("");
            tcp.connect();
            log.info("启动轮召程序");
            if (td == null)
                td = new CustomTaskAutomaticDemand();
            td.start();
            if (cs != null)
                cs.start(CommunicationServer.STARTDEBUG);
            isStart = true;
        }
    }

    public void stop() {
        log.info("FEPServer stop");
        if (tcp != null)
            tcp.interrupt();
        if (td != null)
            td.exit();
        if (cs != null)
            cs.stop();
        isStart = false;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            start();
        }
        if (event instanceof ContextClosedEvent) {
            stop();
        }
    }
}
