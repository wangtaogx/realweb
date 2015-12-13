package com.tao.realweb.plugins.system.example;

import com.tao.realweb.bean.IQ;
import com.tao.realweb.bean.IQ.Type;
import com.tao.realweb.bean.PacketError;
import com.tao.realweb.bean.PacketError.Condition;
import com.tao.realweb.modules.system.handlers.basic.AbstractHandler;

public class ExampleHandler extends AbstractHandler {

	public static final String TEXT = "test";
	public static final String namespace = "exampleHandler";
	public IQ processPacket(IQ iq) {
		IQ result = null;
		if(iq != null){
			result = iq.createResultIQ(iq);
			result.setType(Type.RESULT);
			result.setProperty(TEXT, "test");
		}else{
			result = new IQ();
			result.setType(Type.RESULT);
			result.setError(new PacketError(Condition.bad_request));
		}
		System.out.println("Excmple Handler 正在处理");
		return result;
	}
	public String getNamespace() {
		return namespace;
	}

}
