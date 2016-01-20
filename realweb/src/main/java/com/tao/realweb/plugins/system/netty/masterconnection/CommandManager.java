package com.tao.realweb.plugins.system.netty.masterconnection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandManager {

	private Logger logger = LoggerFactory.getLogger(CommandManager.class); 
	private Map<String,CommandHandler> handlersMap = new ConcurrentHashMap<String, CommandHandler>();
	private static CommandManager instance = new CommandManager();
	private CommandManager(){
	}
	public static CommandManager getInstance(){
		return instance;
	}
	public void putHandler(String command,CommandHandler handler){
		handlersMap.put(command, handler);
	}
	public void removeHandler(String command){
		handlersMap.remove(command);
	}
	public CommandHandler getHandler(String command){
		return handlersMap.get(command);
	}
	public boolean containsHandler(String command){
		return handlersMap.containsKey(command);
	}
	
	public void init() {
	}
}
