package com.chooshine.fep.communicate;

import java.net.*;
import java.nio.channels.*;
import java.util.*;
//import java.io.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: </p>
 *
 * <p>Company: </p>
 *
 * @author 
 * @version 1.0
 */
public class SocketCommunicationBase {
  ServerSocketChannel serverChannel;
  ServerSocket serverSocket;
  Selector selector;

  public SocketCommunicationBase() {
  }

  public void CloseListen() throws Exception {
    serverSocket.close();
    selector.close();
    serverChannel.close();
  }

  public void init(int ListenPort) throws Exception {
	    utils.PrintDebugMessage("Listening on port " + ListenPort, "D");
	    // 分配一个ServerSocketChannel
	    serverChannel = ServerSocketChannel.open();
	    // 从ServerSocketChannel里获得一个对应的Socket
	    serverSocket = serverChannel.socket();
	    // 生成一个Selector
	    selector = Selector.open();
	    // 把Socket绑定到端口上
	    serverSocket.bind(new InetSocketAddress(ListenPort));
	    //serverChannel为非block
	    serverChannel.configureBlocking(false);
	    // 通过Selector注册ServerSocetChannel
	    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	  }

	  public void startWork() throws Exception {
	    while (true) {
	      int n = selector.select(); //获得io准备就绪的channel数量
	      if (n == 0) {
	        continue; //没有channel准备就绪，继续执行
	      }
	      //用一个iterator返回selector的selectedkeys
	      Iterator it = selector.selectedKeys().iterator();
	      //处理每一个SelectionKey
	      while (it.hasNext()) {
	        SelectionKey key = (SelectionKey) it.next();
	        // 判断是否有新的连接到达
	        if (key.isValid() && key.isAcceptable()) {
	          ServerSocketChannel server =
	              (ServerSocketChannel) key.channel(); //返回SelectionKey的ServerSocketChannel。
	          SocketChannel channel = server.accept();
	          registerChannel(selector, channel,
	                          SelectionKey.OP_READ); //
	          //doWork (channel);
	        }
	        //判断是否有数据在此channel里需要读取
	        if (key.isValid() && key.isReadable()) {
	          receiveData(key);
	        }
	        //remove selectedkeys
	        it.remove();
	      }
	    }
	  }

	  private void registerChannel(Selector selector,
	                               SelectableChannel channel, int ops) throws
	      Exception {
	    if (channel == null) {
	      return; // could happen
	    }
	    // set the new channel non-blocking
	    channel.configureBlocking(false);
	    // register it with the selector
	    channel.register(selector, ops);
	  }

	  //处理接收的数据
	  private void receiveData(SelectionKey key) throws Exception {
////	    SocketChannel socketChannel = (SocketChannel) key.channel();
////	    int count;
	    /*    buffer.clear();//清空buffer
	        //读取所有的数据
	        while ( (count = socketChannel.read(buffer)) > 0) {
	          try {
	            buffer.flip();//make buffer readable
	            byte data[] = new byte[buffer.limit()];

	            buffer.get(data, 0, buffer.limit());

	            processData(data);

	            buffer.clear();//清空buffer
	          }
	          catch (Exception ex) {
	            //System.out.println(ex.getStackTrace());
	          }
	        }

	        if (count < 0) {
	          // count<0，说明已经读取完毕
	          socketChannel.close();
	        }*/
	  }
	}
