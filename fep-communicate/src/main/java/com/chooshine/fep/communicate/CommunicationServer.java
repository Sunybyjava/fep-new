package com.chooshine.fep.communicate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;

//import hexing.fep.communicate.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CommunicationServer {
    //Start Debug、Start LogWrite、Finish Debug、Finish LogWrite
    public static final String STARTDEBUG = "Start Debug";
    public static final String STARTLOG = "Start LogWrite";
    public static final String FINISHDEBUG = "Finish Debug";
    public static final String FINISHLOG = "Finish LogWrite";
    private MessageExchange mx;
    private CommunicationScheduler cs;
    private DebugInfoInputThread dt;
    private static ExecutorService executor = Executors.newFixedThreadPool(1);
    public static void main(String args[]) {
        new CommunicationServer().start(STARTDEBUG);
    }

    public void start(String debug) {
        if (StringUtils.isBlank(debug))
            debug = STARTLOG;
        mx = new MessageExchange(CommunicationServerConstants.COMMSERVICE_MESSAGEEXCHANGE_PORT,
                CommunicationServerConstants.COMMSERVICE_MESSAGEEXCHANGE_MAXCOUNT,
                CommunicationServerConstants.COMMSERVICE_MESSAGEEXCHANGE_TIMEOUT, debug);
        //        mx.start();
        executor.execute(mx);
        //utils.PrintDebugMessage("Start CommunicationScheduler Object.....", Debug);
        CommunicationServerConstants.Trc1.TraceLog("Start CommunicationScheduler Object.....");
        cs = new CommunicationScheduler(CommunicationServerConstants.COMMSERVICE_COMMUNICATIONSCHEDULER_PORT,
                CommunicationServerConstants.COMMSERVICE_COMMUNICATIONSCHEDULER_MAXCOUNT,
                CommunicationServerConstants.COMMSERVICE_COMMUNICATIONSCHEDULER_TIMEOUT,
                CommunicationServerConstants.COMMSERVICE_COMMUNICATIONSCHEDULER_TIMEOUT230M,
                CommunicationServerConstants.COMMSERVICE_COMMUNICATIONSCHEDULER_BatchSave, debug);
        //        cs.start();
        executor.execute(cs);
        dt = new DebugInfoInputThread(mx, cs);
        //        dt.start();
        executor.execute(dt);
    }

    public void stop() {
        executor.shutdown();
        //        if (mx != null)
        //            mx.interrupt();
        //        if (cs != null)
        //            cs.interrupt();
        //        if (dt != null)
        //            dt.interrupt();
    }

    public CommunicationServer() {
  }
}
