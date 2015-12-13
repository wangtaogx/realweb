package com.tao.realweb.launch;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tao.realweb.conf.system.RealWebConfig;
import com.tao.realweb.conf.system.RealWebResource;
import com.tao.realweb.container.RealWebServer;
import com.tao.realweb.util.StringUtil;

public class ServerStarter {

	private RealWebServer server;

	public void start() {
		server.start();
	}

	public void init() {
		try {
			final ClassLoader parent = findParentClassLoader();
			String realWebHome = System.getProperty("realwebHome");
			System.out.println(realWebHome);
			File homeDir = new File(realWebHome);
			if (StringUtil.isEmpty(realWebHome) || !homeDir.exists()) {
				System.out.println("Home directory " + realWebHome
						+ " does not exist. ");
				return;
			}

			File libDir = new File(homeDir, "lib");
			File configDir = new File(homeDir, "config");
			File logDir = new File(homeDir, "logs");
			File pluginDir = new File(homeDir, "plugins");
			File resourcesDir = new File(homeDir, "resources");
			if (!libDir.exists()) {
				System.out.println("Lib directory does not exist. ");
				return;
			}
			if (!configDir.exists()) {
				System.out.println("configuration directory  does not exist. ");
				return;
			}
			if (!logDir.exists())
				logDir.mkdirs();
			if (!pluginDir.exists())
				pluginDir.mkdirs();
			System.setProperty("realweb_logs", logDir.getAbsolutePath());
			PropertyConfigurator.configure(configDir.getAbsolutePath()
					+ "/log4j.properties");
			RealWebClassLoader loader = new RealWebClassLoader(parent, libDir);
			loader.addDir(configDir);
			RealWebResource.getInstance().setResourceURL(resourcesDir.getAbsolutePath());
			Thread.currentThread().setContextClassLoader(loader);
			Class<?> containerClass = loader
					.loadClass("com.tao.realweb.container.RealWebServer");
			Object object = containerClass.newInstance();
			if (object instanceof RealWebServer) {
				server = (RealWebServer) object;
				RealWebConfig realWebConfig = RealWebConfig
						.getInstance(configDir.getAbsolutePath()
								+ "/application.xml");
				String pluginDirString = realWebConfig
						.getString("realweb.plugin.dir");
				if (StringUtil.isEmpty(pluginDirString)) {
					realWebConfig.putObject("realweb.plugin.dir",
							pluginDir.getAbsolutePath());
				}
				server.init(realWebConfig);
			} else {
				System.out.println("start server error");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Logger logger = LoggerFactory.getLogger(ServerStarter.class);
			logger.error("start server error:" + e.getMessage());
		}

	}

	public void stop() {
		if (server != null) {
			server.stop();
			server = null;
		}
	}

	/**
	 * Locates the best class loader based on context (see class description).
	 * 
	 * @return The best parent classloader to use
	 */
	private ClassLoader findParentClassLoader() {
		ClassLoader parent = Thread.currentThread().getContextClassLoader();
		if (parent == null) {
			parent = this.getClass().getClassLoader();
			if (parent == null) {
				parent = ClassLoader.getSystemClassLoader();
			}
		}
		return parent;
	}

}
