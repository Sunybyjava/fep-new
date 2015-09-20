package com.chooshine.fep.communicate;

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
	  
  public static void main(String args[]) {
    String Debug = "";
    if (args.length == 1) {  
      Debug = args[0];
    }
    MessageExchange mx = new MessageExchange(CommunicationServerConstants.
                                             COMMSERVICE_MESSAGEEXCHANGE_PORT,
                                             CommunicationServerConstants.
                                             COMMSERVICE_MESSAGEEXCHANGE_MAXCOUNT,
                                             CommunicationServerConstants.
                                             COMMSERVICE_MESSAGEEXCHANGE_TIMEOUT,
                                             Debug);
    mx.start();
    //utils.PrintDebugMessage("Start CommunicationScheduler Object.....", Debug);
    CommunicationServerConstants.Trc1.TraceLog("Start CommunicationScheduler Object.....");

    CommunicationScheduler cs = new CommunicationScheduler(
        CommunicationServerConstants.COMMSERVICE_COMMUNICATIONSCHEDULER_PORT,
        CommunicationServerConstants.
        COMMSERVICE_COMMUNICATIONSCHEDULER_MAXCOUNT,
        CommunicationServerConstants.
        COMMSERVICE_COMMUNICATIONSCHEDULER_TIMEOUT,
        CommunicationServerConstants.
        COMMSERVICE_COMMUNICATIONSCHEDULER_TIMEOUT230M,
        CommunicationServerConstants.
        COMMSERVICE_COMMUNICATIONSCHEDULER_BatchSave
        , Debug);
    cs.start();

    UpdateFramePushThread fpt = new UpdateFramePushThread();
    fpt.start();
    DebugInfoInputThread dt = new DebugInfoInputThread(mx, cs);
    dt.start();
  }

  public CommunicationServer() {
  }
}
