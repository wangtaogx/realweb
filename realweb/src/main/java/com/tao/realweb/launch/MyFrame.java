package com.tao.realweb.launch;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.PanelUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.tao.realweb.conf.system.RealWebResource;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class MyFrame extends javax.swing.JFrame  implements ActionListener{
	private JPanel header;
	private JLabel title;
	private JPanel footer;
	private RolloverButton btnStart;
	private JTextPane textPanel;
	private StyledDocument doc = null; // 非常重要插入文字样式就靠它了
	private JScrollPane scrollPanel;
	private RolloverButton btnRestart;
	private RolloverButton btnStop;
	private SimpleAttributeSet errorAttrSet = getFontAttribute(error);
	private SimpleAttributeSet infoAttrSet = getFontAttribute(info);
	public static final String error = "error";
	public static final String info = "info";
	private static final String COMMAND_SYSTRAY = "command_systray";
	private static MyFrame instance = null;
	private static String COMMAND_CLOSE = "command_close";
	private static String COMMAND_MINI = "command_mini";
	private static String COMMAND_START = "command_start";
	private static String COMMAND_STOP = "command_stop";
	private static String COMMAND_RESTART = "command_restart";
	private Point loc = null; 
	private Point tmp = null; 
	boolean isDragged = false; 
	private ServerStarter serverStarter;
	/**
	* Auto-generated main method to display this JFrame
	*/
	
	 public static void main(String [] args) {
		 MyFrame.getInstance();
	    }
	public static MyFrame getInstance(){
		if(instance == null){
			instance = new MyFrame();
		}
		return instance;
	}
	public MyFrame() {
		super();
		initServer();
		initGUI();
	}
	private void initGUI() {
		try {
			BorderLayout thisLayout = new BorderLayout();
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setUndecorated(true);
			getContentPane().setLayout(thisLayout);
			{
				header = new JPanel();
				GridBagLayout headerLayout = new GridBagLayout();
				headerLayout.rowHeights = new int[]{30,30};
				header.setUI(new PanelUI() {

					@Override
					public void paint(Graphics g, JComponent c) {
						
					}
					
				});
				getContentPane().add(header, BorderLayout.NORTH);
				{
					title = new JLabel(RealWebResource.getInstance().getString("realweb.app.name"));
					title.setIcon(RealWebResource.getInstance().getImageResource("realweb.logo"));
					header.setLayout(headerLayout);
					JPanel windowControlPanel = new JPanel();
					windowControlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
					RolloverButton mini = new RolloverButton(RealWebResource.getInstance().getImageResource("console.window.mini"));
					mini.setActionCommand(COMMAND_MINI);
					mini.addActionListener(this);
					windowControlPanel.add(mini);
					RolloverButton close = new RolloverButton(RealWebResource.getInstance().getImageResource("console.window.close"));
					close.setActionCommand(COMMAND_CLOSE);
					close.addActionListener(this);
					windowControlPanel.add(close);
					header.add(windowControlPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					header.add(title, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10,15, 10), 0, 0));
					setDragable();
				}
			}
			{
				footer = new JPanel();
				FlowLayout footerLayout = new FlowLayout();
				getContentPane().add(footer, BorderLayout.SOUTH);
				footer.setLayout(footerLayout);
				{
					btnStart = new RolloverButton(RealWebResource.getInstance().getImageResource("console.start"));
					btnStart.setActionCommand(COMMAND_START);
					btnStart.addActionListener(this);
					btnStart.setMargin(new Insets(10, 20, 10, 20));
					footer.add(btnStart);
					btnStart.setText(RealWebResource.getInstance().getString("console.start.string"));
				}
				{
					btnStop = new RolloverButton(RealWebResource.getInstance().getImageResource("console.stop"));
					btnStop.setMargin(new Insets(10, 20, 10, 20));
					btnStop.setActionCommand(COMMAND_STOP);
					btnStop.addActionListener(this);
					footer.add(btnStop);
					btnStop.setText(RealWebResource.getInstance().getString("console.stop.string"));
					btnStop.setEnabled(false);
				}
				{
					btnRestart = new RolloverButton(RealWebResource.getInstance().getImageResource("console.restart"));
					btnRestart.setMargin(new Insets(10, 20, 10, 20));
					btnRestart.setActionCommand(COMMAND_RESTART);
					btnRestart.addActionListener(this);
					footer.add(btnRestart);
					btnRestart.setText(RealWebResource.getInstance().getString("consloe.restart.string"));
					btnRestart.setEnabled(false);
				}
			}
			{
				scrollPanel = new JScrollPane();
				getContentPane().add(scrollPanel, BorderLayout.CENTER);
				{
					textPanel = new JTextPane();
					scrollPanel.setViewportView(textPanel);
					textPanel.setEditable(false); // 不可录入
					doc = textPanel.getStyledDocument(); // 获得JTextPane的Document
				}
			}
			 SystemTray tray = null;
		        try {
		            tray = SystemTray.getSystemTray();
		        }
		        catch (Throwable e) {
		            // Log to System error instead of standard error log.
		            System.err.println("Error loading system tray library, system tray support disabled.");
		        }

		        // Use the native look and feel.
		        try {
		            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		        }
		        catch (Exception e) {
		            e.printStackTrace();
		        }
		        TrayIcon trayIcon = new TrayIcon(RealWebResource.getInstance().getImageResource("realweb.logo").getImage());
		        trayIcon.setImageAutoSize(true);
		        trayIcon.setActionCommand(COMMAND_SYSTRAY);
		        trayIcon.addActionListener(this);

		        if (tray != null) {
		            tray.add(trayIcon);
		        }
			pack();
			setSize(800, 600);
			this.setVisible(true);
			//startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void setDragable() { 
		header.addMouseListener(new java.awt.event.MouseAdapter() { 
			public void mouseReleased(java.awt.event.MouseEvent e) { 
				isDragged = false; 
				MyFrame.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); 
			} 
			public void mousePressed(java.awt.event.MouseEvent e) { 
				tmp = new Point(e.getX(), e.getY()); 
				isDragged = true; 
				MyFrame.this.setCursor(new Cursor(Cursor.MOVE_CURSOR)); 
			} 
			}); 
			header.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() { 
				public void mouseDragged(java.awt.event.MouseEvent e) { 
				if(isDragged) { 
					loc = new Point(MyFrame.this.getLocation().x + e.getX() - tmp.x, 
							MyFrame.this.getLocation().y + e.getY() - tmp.y); 
					MyFrame.this.setLocation(loc); 
				} 
			} 
		 }); 
	 }
	 private void insertIconErrorString(File file,String text) {
		  textPanel.setCaretPosition(doc.getLength()); // 设置插入位置
		  textPanel.insertIcon(new ImageIcon(file.getPath())); // 插入图片
		 }
	/**
	  * 插入图片
	  * 
	  * @param icon
	  */
	 private void insertIcon(File file) {
	  textPanel.setCaretPosition(doc.getLength()); // 设置插入位置
	  textPanel.insertIcon(new ImageIcon(file.getPath())); // 插入图片
	 }
	 

	 /**
	  * 将文本插入JTextPane
	  * 
	  * @param attrib
	  */
	 public void insertINfo(String text) {
	  try { // 插入文本
		  textPanel.setCaretPosition(doc.getLength()); // 设置插入位置
		  doc.insertString(doc.getLength(), "    "+text + "\n", infoAttrSet);
	  } catch (BadLocationException e) {
	   e.printStackTrace();
	  }
	 }
	 public void insertError(String text) {
		  try { // 插入文本
			  textPanel.setCaretPosition(doc.getLength()); // 设置插入位置
			  doc.insertString(doc.getLength(), "    "+text + "\n", errorAttrSet);
		  } catch (BadLocationException e) {
		   e.printStackTrace();
		  }
		 }

	 /**
	  * 获取所需要的文字设置
	  * 
	  * @return FontAttrib
	  */
	 private SimpleAttributeSet getFontAttribute(String level) {
		 SimpleAttributeSet attrSet = new SimpleAttributeSet();
		 StyleConstants.setBold(attrSet, false);
		 StyleConstants.setFontSize(attrSet, 13);
	  if(error.equals(level))
		  StyleConstants.setForeground(attrSet, Color.RED);
	  else{
		  StyleConstants.setForeground(attrSet, Color.BLACK);
	  }
	  return attrSet;
	 }
	@Override
	public void actionPerformed(ActionEvent e) {
		if(COMMAND_CLOSE.equals(e.getActionCommand())){
			System.exit(0);
		}else if(COMMAND_MINI.equals(e.getActionCommand())){
			  if (this.isVisible()) {
                  this.setVisible(false);
                  this.setState(Frame.ICONIFIED);
              }
		}else if(COMMAND_SYSTRAY.equals(e.getActionCommand())){
			 if (this.isVisible()) {
                 this.setVisible(false);
                 this.setState(Frame.ICONIFIED);
             }
             else {
                 this.setVisible(true);
                 this.setState(Frame.NORMAL);
             }
		}else if(COMMAND_START.equals(e.getActionCommand())){
			 SwingWorker worker = new SwingWorker() {
				@Override
				public Object construct() {
					startServer();
					return null;
				}
			};
			worker.start();
			
		}else if(COMMAND_STOP.equals(e.getActionCommand())){
			SwingWorker worker = new SwingWorker() {

				public Object construct(){
					stopServer();
					return null;
				}
			};
			worker.start();
		}else if(COMMAND_RESTART.equals(e.getActionCommand())){
			SwingWorker worker = new SwingWorker() {
				@Override
				public Object construct(){
					stopServer();
					startServer();
					return null;
				}
				
			};
			worker.start();
		}
	}
	private void initServer(){
		if(serverStarter == null){
			serverStarter = new ServerStarter();
			serverStarter.init();
		}
	}
	private void stopServer() {
		if(serverStarter != null){
			serverStarter.stop();
			serverStarter = null;
			fireStop();
		}
	}
	private void startServer() {
		if(serverStarter != null){
			serverStarter.start();
			fireStart();
		}
	}

	public void fireStart(){
		if(footer == null)
			return;
		for(Component c : footer.getComponents()){
			if(c instanceof RolloverButton){
				RolloverButton b = (RolloverButton)c;
				if(COMMAND_START.equals(b.getActionCommand())){
					b.setEnabled(false);
				}
				if(COMMAND_STOP.endsWith(b.getActionCommand())){
					b.setEnabled(true);
				}
				if(COMMAND_RESTART.endsWith(b.getActionCommand())){
					b.setEnabled(true);
				}
			}
		}
	}
	public void fireStop(){
		if(footer == null)
			return;
		for(Component c : footer.getComponents()){
			if(c instanceof RolloverButton){
				RolloverButton b = (RolloverButton)c;
				if(COMMAND_START.equals(b.getActionCommand())){
					b.setEnabled(true);
				}
				if(COMMAND_STOP.endsWith(b.getActionCommand())){
					b.setEnabled(false);
				}
				if(COMMAND_RESTART.endsWith(b.getActionCommand())){
					b.setEnabled(false);
				}
			}
		}
	}
}
