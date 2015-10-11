package com.chooshine.fep.newtcpchannel;

import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TerminalChannelSet {
	/**
	 * 通道表(未命名的通道表, 保存到是SocketChannel创建的SelectionKey)
	 */
	@SuppressWarnings("rawtypes")
	ConcurrentHashMap	 unNamedChannelSet;
	
	/**
	 * 通道表(已和终端逻辑地址绑定的通道表, 保存到是SocketChannel创建的SelectionKey)
	 */
	@SuppressWarnings("rawtypes")
	ConcurrentHashMap  namedChannelSet;
	
	
	public static final int DEFAULT_SIZE = 5000;
	
	@SuppressWarnings("rawtypes")
	TerminalChannelSet(){
		unNamedChannelSet = new ConcurrentHashMap(DEFAULT_SIZE);
		namedChannelSet = new ConcurrentHashMap(DEFAULT_SIZE);
	}
	
	@SuppressWarnings("unchecked")
	public void addNamedChannel(String logicAddr, SelectionKey sc){
		namedChannelSet.put(logicAddr, sc);
//		HiChannelContext cc = (HiChannelContext)sc.attachment();
//		this.termChannelsContextData.put(logicAddr, cc.getChannelCtxData());
	}
	
	public void deleteNamedChannel(String logicAddr){
		if ( logicAddr != null ){
			namedChannelSet.remove(logicAddr);
		}
	}
	
	public SelectionKey getNamedChannel(String logicAddr){
		return (SelectionKey)namedChannelSet.get(logicAddr);
	}
	
	@SuppressWarnings("unchecked")
	public void addUnNamedChannel(String ip, SelectionKey sc){
		unNamedChannelSet.put(ip, sc);
	}
	
	public SelectionKey getUnNamedChannel(String ip){
		return (SelectionKey)unNamedChannelSet.get(ip);
	}
	
	public void deleteUnNamedChannel(String ip){
		if ( ip != null ){
			unNamedChannelSet.remove(ip);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Map getNamedChannelSet() {
		return namedChannelSet;
	}

	@SuppressWarnings("rawtypes")
	public void setNamedChannelSet(ConcurrentHashMap namedChannelSet) {
		this.namedChannelSet = namedChannelSet;
	}

	@SuppressWarnings("rawtypes")
	public Map getUnNamedChannelSet() {
		return unNamedChannelSet;
	}

	@SuppressWarnings("rawtypes")
	public void setUnNamedChannelSet(ConcurrentHashMap unNamedChannelSet) {
		this.unNamedChannelSet = unNamedChannelSet;
	}
}
