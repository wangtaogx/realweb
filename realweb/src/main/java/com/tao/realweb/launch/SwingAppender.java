package com.tao.realweb.launch;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;


public class SwingAppender extends AppenderSkeleton{

	@Override
	public void close() {
		
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	@Override
	protected void append(LoggingEvent arg0) {
		if(Level.ERROR.equals(arg0.getLevel()))
			MyFrame.getInstance().insertError(layout.format(arg0));
		else
			MyFrame.getInstance().insertINfo(layout.format(arg0));
	}

}
