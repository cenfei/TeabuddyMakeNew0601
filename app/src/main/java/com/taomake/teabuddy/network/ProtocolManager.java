package com.taomake.teabuddy.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public final class ProtocolManager {

	HashMap<Protocol.CallBack, Set<Protocol>> mQueueHashMap = null;

	private static ProtocolManager instance = null;
	private ProtocolManager()
	{
		mQueueHashMap = new HashMap<Protocol.CallBack, Set<Protocol>>();
	}
	
	//only way to get this singleton Manager ishuashua.cn.shuashua.object
	public static ProtocolManager getProtocolManager()
	{
		if(instance == null) instance = new ProtocolManager();
		return instance;
	}
	
	//This method is to add an ongoing request into the queue, do not call this method, it's only for internal usage
	protected void addToQueue(Protocol p, Protocol.CallBack cb)
	{
		Set<Protocol> aSet = mQueueHashMap.get(cb);
		if(aSet == null)
		{
			aSet = new HashSet<Protocol>();
			mQueueHashMap.put(cb, aSet);
		}
		aSet.add(p);
	}
	
	//This method removes a finished request from the queue, do not call this method, it's only for internal usage
	protected void removeFromQueue(Protocol p, Protocol.CallBack cb)
	{
		Set<Protocol> aSet = mQueueHashMap.get(cb);
		if(aSet == null) return;
		aSet.remove(p);
	}
	
	//This method removes(and also cancels) all ongoing requests associated with a callback ishuashua.cn.shuashua.object
	@SuppressWarnings("unused")
	public void removeRequestsByCB(Protocol.CallBack cb)
	{
		Set<Protocol> aSet = mQueueHashMap.get(cb);
		if(aSet == null) return; //already gone, do nothing
		Iterator<Protocol> iterator = aSet.iterator();
		while (iterator.hasNext()) {
			Protocol aProtocol = iterator.next();
		}
		mQueueHashMap.remove(cb);
	}

	//This method removes(and also cancels) all ongoing requests in the queue, suggest to call before finishing an App
	public void removeAllRequests()
	{
		Set<Protocol.CallBack> aSet = mQueueHashMap.keySet();
		Iterator<Protocol.CallBack> iterator = aSet.iterator();
		while (iterator.hasNext()) {
			Protocol.CallBack aCallback = iterator.next();
			removeRequestsByCB(aCallback);
		}
	}
}

