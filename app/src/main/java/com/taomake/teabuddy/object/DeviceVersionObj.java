package com.taomake.teabuddy.object;

import java.io.Serializable;

public class DeviceVersionObj implements Serializable{

	private static final long serialVersionUID = 1L;

	public String head;
	public String id;
	public String ver;
	public String url;
	public String downloadsize;
	public String content;
	public String time;


//	{"rcode":1,"rmsg":"ok","obj":[{"id":"4","head":"CA","ver":"1.0",
// "url":"www.teabuddy.cn","downloadsize":"2.3Mb","content":"- \u6ca1\u6709\u5566","time":"2017-05-01 12:59:18"}]}
}
