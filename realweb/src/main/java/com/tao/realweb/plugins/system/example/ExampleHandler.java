package com.tao.realweb.plugins.system.example;

import com.alibaba.fastjson.JSONObject;
import com.tao.realweb.bean.Packet;
import com.tao.realweb.modules.system.handlers.basic.AbstractHandler;

public class ExampleHandler extends AbstractHandler {

	public static final String TEXT = "test";
	public static final String namespace = "exampleHandler";
	public Packet processPacket(Packet iq) {
		Packet result = null;
		JSONObject jsoObject = new JSONObject();
		jsoObject.put(TEXT, "test");
		result = Packet.createResultPacket(iq);
		result.setBody(jsoObject);
		System.out.println("Excmple Handler 正在处理");
		return result;
	}
	public String getNamespace() {
		return namespace;
	}

}
