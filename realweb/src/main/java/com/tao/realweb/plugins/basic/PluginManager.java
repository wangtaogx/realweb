package com.tao.realweb.plugins.basic;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tao.realweb.container.RealWebServer;
import com.tao.realweb.util.Dom4JSupport;
import com.tao.realweb.util.ResourceClassLoader;

public class PluginManager {

	private static Logger logger = LoggerFactory.getLogger(PluginManager.class);
	private static PluginManager instance = null;
	private Map<String,Plugin> pluginsMap = new ConcurrentHashMap<String, Plugin>();
	private List<Element> plugins = null;
	private static Object lock = new Object();
	private RealWebServer realWebServer;
	private File pluginDirectory;
	private Map<Plugin, PluginClassLoader> classloadersMap;
	private Map<Plugin, File> pluginDirsMap;
	private Map<String, File> pluginFilesMap;
	private PluginMonitor pluginMonitor;
	private ScheduledExecutorService executor = null;
	
	private PluginManager(RealWebServer server){
		realWebServer =server;
	    pluginDirsMap = new HashMap<Plugin, File>();
        pluginFilesMap = new HashMap<String, File>();
        pluginMonitor = new PluginMonitor();
        classloadersMap = new HashMap<Plugin, PluginClassLoader>();
		logger.info("加载插件--开始...");
		Document document = this.realWebServer.getRealWebConfig().getDocument();
		if(document != null){
			plugins= document.selectNodes("/application/plugins/plugin");
			if(plugins != null){
				for(Element ele : plugins){
					String className = ele.elementText("classname");
					try{
					Class clazz = Class.forName(className);
					Object plugin = clazz.newInstance();
					pluginsMap.put(className, (Plugin)plugin);
					}catch(Exception e){
						e.printStackTrace();
						logger.info("加载插件失败:"+className);
					}
					
				}
			}
		}
		logger.info("加载插件--结束");
	}
	public static PluginManager getInstance(RealWebServer server){
		if(instance == null){
			synchronized (lock) {
				if(instance == null){
					instance = new PluginManager(server);
				}
			}
		}
		return instance;
	}
	public void init(RealWebServer server){
		logger.info("初始化--插件开始...");
		if(plugins != null){
			for(Element ele : plugins){
				String pluginName = ele.elementText("pluginname");
				String className = ele.elementText("classname");
				String description = ele.elementText("description");
				Element parameters = ele.element("parameters");
				try{
					PluginInfo info = new PluginInfo();
					info.setPluginName(pluginName);
					info.setDescription(description);
					info.setClassName(className);
					if(parameters != null){
						List<Element> elements = parameters.elements();
						for(Element e : elements){
							info.addParameter(e.getName(), e.getTextTrim());
						}
					}
					Plugin plugin = pluginsMap.get(className);
					if(plugin != null)
						plugin.init(this, info);
				}catch(Exception e){
					e.printStackTrace();
					pluginsMap.remove(className);
					logger.info("初始化插件错误"+className);
				}
			}
		}
		logger.info("初始化--插件结束");
	}
	public void startPlugins(){
		for(String key : pluginsMap.keySet()){
			pluginsMap.get(key).start();
		}
		
		executor = new ScheduledThreadPoolExecutor(1);
        // See if we're in development mode. If so, check for new plugins once every 5 seconds.
        // Otherwise, default to every 20 seconds.
        executor.scheduleWithFixedDelay(pluginMonitor, 0, 5, TimeUnit.SECONDS);
	}
	public void stopPlugins(){
		for(String key : pluginsMap.keySet()){
			pluginsMap.get(key).destroy();
		}
		executor.shutdown();
		executor = null;
		pluginsMap.clear();
		instance = null;
	}
	public Plugin getPlugin(String pluginKey){
		return pluginsMap.get(pluginKey);
	}
	 /**
     * A service that monitors the plugin directory for plugins. It periodically
     * checks for new plugin JAR files and extracts them if they haven't already
     * been extracted. Then, any new plugin directories are loaded.
     */
    private class PluginMonitor implements Runnable {

        /**
         * Tracks if the monitor is currently running.
         */
        private boolean running = false;

        /**
         * True if the monitor has been executed at least once. After the first iteration in {@link #run}
         * this variable will always be true.
         * */
        private boolean executed = false;

        /**
         * True when it's the first time the plugin monitor process runs. This is helpful for
         * bootstrapping purposes.
         */
        private boolean firstRun = true;

        public void run() {
            // If the task is already running, return.
            synchronized (this) {
                if (running) {
                    return;
                }
                running = true;
            }
            try {
                running = true;
                // Look for extra plugin directories specified as a system property.
               /* String pluginDirs = System.getProperty("pluginDirs");
                if (pluginDirs != null) {
                    StringTokenizer st = new StringTokenizer(pluginDirs, ", ");
                    while (st.hasMoreTokens()) {
                        String dir = st.nextToken();
                        for(File f : pluginDirsMap.values()){
                        	if(f.getPath().equals(dir)){
                        		
                        	}
                        }
                    }
                }
*/
                File[] jars = pluginDirectory.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        String fileName = pathname.getName().toLowerCase();
                        return (fileName.endsWith(".jar"));
                    }
                });

                if (jars == null) {
                    return;
                }

                for (File jarFile : jars) {
                    String pluginName = jarFile.getName().substring(0,
                        jarFile.getName().length() - 4).toLowerCase();
                    // See if the JAR has already been exploded.
                    File dir = new File(pluginDirectory, pluginName);
                    // Store the JAR/WAR file that created the plugin folder
                    pluginFilesMap.put(pluginName, jarFile);
                    // If the JAR hasn't been exploded, do so.
                    if (!dir.exists()) {
                        unzipPlugin(pluginName, jarFile, dir);
                    }
                    // See if the JAR is newer than the directory. If so, the plugin
                    // needs to be unloaded and then reloaded.
                    else if (jarFile.lastModified() > dir.lastModified()) {
                        // If this is the first time that the monitor process is running, then
                        // plugins won't be loaded yet. Therefore, just delete the directory.
                        if (firstRun) {
                            int count = 0;
                            // Attempt to delete the folder for up to 5 seconds.
                            while (!deleteDir(dir) && count < 5) {
                                Thread.sleep(1000);
                            }
                        }
                        else {
                            unloadPlugin(pluginName);
                        }
                        // If the delete operation was a success, unzip the plugin.
                        if (!dir.exists()) {
                            unzipPlugin(pluginName, jarFile, dir);
                        }
                    }
                }

                File[] dirs = pluginDirectory.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        return pathname.isDirectory();
                    }
                });

                // Sort the list of directories so that the "admin" plugin is always
               /* // first in the list.
                Arrays.sort(dirs, new Comparator<File>() {
                    public int compare(File file1, File file2) {
                        if (file1.getName().equals("admin")) {
                            return -1;
                        }
                        else if (file2.getName().equals("admin")) {
                            return 1;
                        }
                        else {
                            return file1.compareTo(file2);
                        }
                    }
                });*/

                // Turn the list of JAR/WAR files into a set so that we can do lookups.
                Set<String> jarSet = new HashSet<String>();
                for (File file : jars) {
                    jarSet.add(file.getName().toLowerCase());
                }

                // See if any currently running plugins need to be unloaded
                // due to the JAR file being deleted (ignore admin plugin).
                // Build a list of plugins to delete first so that the plugins
                // keyset isn't modified as we're iterating through it.
                List<String> toDelete = new ArrayList<String>();
                for (File pluginDir : dirs) {
                    String pluginName = pluginDir.getName();
                    if (!jarSet.contains(pluginName + ".jar")) {
                        toDelete.add(pluginName);
                    }
                }
                for (String pluginName : toDelete) {
                    unloadPlugin(pluginName);
                }

                // Load all plugins that need to be loaded.
                for (File dirFile : dirs) {
                    // If the plugin hasn't already been started, start it.
                    if (dirFile.exists() && !pluginsMap.containsKey(dirFile.getName())) {
                        loadPlugin(dirFile);
                    }
                }
                // Set that at least one iteration was done. That means that "all available" plugins
                // have been loaded by now.
                executed = true;

                // Trigger event that plugins have been monitored
            }
            catch (Throwable e) {
            }
            // Finished running task.
            synchronized (this) {
                running = false;
            }
            // Process finished, so set firstRun to false (setting it multiple times doesn't hurt).
            firstRun = false;
        }
    }

        /**
         * Unzips a plugin from a JAR file into a directory. If the JAR file
         * isn't a plugin, this method will do nothing.
         *
         * @param pluginName the name of the plugin.
         * @param file the JAR file
         * @param dir the directory to extract the plugin to.
         */
        private void unzipPlugin(String pluginName, File file, File dir) {
            try {
                ZipFile zipFile = new JarFile(file);
                // Ensure that this JAR is a plugin.
                if (zipFile.getEntry("plugin.xml") == null) {
                    return;
                }
                dir.mkdir();
                // Set the date of the JAR file to the newly created folder
                dir.setLastModified(file.lastModified());
                for (Enumeration e = zipFile.entries(); e.hasMoreElements();) {
                    JarEntry entry = (JarEntry)e.nextElement();
                    File entryFile = new File(dir, entry.getName());
                    // Ignore any manifest.mf entries.
                    if (entry.getName().toLowerCase().endsWith("manifest.mf")) {
                        continue;
                    }
                    if (!entry.isDirectory()) {
                        entryFile.getParentFile().mkdirs();
                        FileOutputStream out = new FileOutputStream(entryFile);
                        InputStream zin = zipFile.getInputStream(entry);
                        byte[] b = new byte[512];
                        int len;
                        while ((len = zin.read(b)) != -1) {
                            out.write(b, 0, len);
                        }
                        out.flush();
                        out.close();
                        zin.close();
                    }
                }
                zipFile.close();

            }
            catch (Exception e) {
            	e.printStackTrace();
            }
        }


    /**
     * Deletes a directory.
     *
     * @param dir the directory to delete.
     * @return true if the directory was deleted.
     */
    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] childDirs = dir.list();
            // Always try to delete JAR files first since that's what will
            // be under contention. We do this by always sorting the lib directory
            // first.
            List<String> children = new ArrayList<String>(Arrays.asList(childDirs));
            Collections.sort(children, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    if (o1.equals("lib")) {
                        return -1;
                    }
                    if (o2.equals("lib")) {
                        return 1;
                    }
                    else {
                        return o1.compareTo(o2);
                    }
                }
            });
            for (String file : children) {
                boolean success = deleteDir(new File(dir, file));
                if (!success) {
                    return false;
                }
            }
        }
        boolean deleted = !dir.exists() || dir.delete();
        if (deleted) {
            // Remove the JAR/WAR file that created the plugin folder
            pluginFilesMap.remove(dir.getName());
        }
        return deleted;
    }
    public void unloadPlugin(String pluginName) {

        Plugin plugin = pluginsMap.get(pluginName);
        if (plugin != null) {
            // Wrap destroying the plugin in a try/catch block. Otherwise, an exception raised
            // in the destroy plugin process will disrupt the whole unloading process. It's still
            // possible that classloader destruction won't work in the case that destroying the plugin
            // fails. In that case, Openfire may need to be restarted to fully cleanup the plugin
            // resources.
            try {
                plugin.destroy();
            }
            catch (Exception e) {
            }
        }

        // Remove references to the plugin so it can be unloaded from memory
        // If plugin still fails to be removed then we will add references back
        // Anyway, for a few seconds admins may not see the plugin in the admin console
        // and in a subsequent refresh it will appear if failed to be removed
        pluginsMap.remove(pluginName);
        File pluginFile = pluginDirsMap.remove(plugin);
        
        PluginClassLoader pluginLoader = classloadersMap.remove(plugin);

        // try to close the cached jar files from the plugin class loader
        if (pluginLoader != null) {
        	pluginLoader.unloadJarFiles();
        } else {
        //	Log.warn("No plugin loader found for " + pluginName);
        }

        // Try to remove the folder where the plugin was exploded. If this works then
        // the plugin was successfully removed. Otherwise, some objects created by the
        // plugin are still in memory.
        File dir = new File(pluginDirectory, pluginName);
        // Give the plugin 2 seconds to unload.
        try {
            Thread.sleep(2000);
            // Ask the system to clean up references.
            System.gc();
            int count = 0;
            while (!deleteDir(dir) && count++ < 5) {
                Thread.sleep(8000);
                // Ask the system to clean up references.
                System.gc();
            }
        } catch (InterruptedException e) {
        }

       if (plugin != null && dir.exists()) {
            // Restore references since we failed to remove the plugin
            pluginsMap.put(pluginName, plugin);
            pluginDirsMap.put(plugin, pluginFile);
            classloadersMap.put(plugin, pluginLoader);
        }
    }
    private void loadPlugin(File pluginDir) {
        // Only load the admin plugin during setup mode.
        Plugin plugin;
        try {
            File pluginConfig = new File(pluginDir, "plugin.xml");
            if (pluginConfig.exists()) {
                SAXReader saxReader = new SAXReader();
                saxReader.setEncoding("UTF-8");
                Document pluginXML = saxReader.read(pluginConfig);
                String pluginName = pluginDir.getName();
                PluginClassLoader pluginLoader;
                pluginLoader = new PluginClassLoader();
                pluginLoader.addDirectory(pluginDir, false);
                PluginInfo info = new PluginInfo();
                String className = pluginXML.selectSingleNode("/plugin/classname").getText().trim();
                info.setClassName(className);
                info.setPluginName(pluginDir.getName());
                plugin = (Plugin)(pluginLoader.loadClass(className).newInstance());
                pluginsMap.put(pluginName, plugin);
                pluginDirsMap.put(plugin, pluginDir);
                classloadersMap.put(plugin, pluginLoader);
                Element parameters =  (Element) pluginXML.selectSingleNode("/plugin/parameters");
                if(parameters != null){
                	List<Element> elements = parameters.elements();
					for(Element e : elements){
						info.addParameter(e.getName(), e.getTextTrim());
					}
                }
                plugin.init(this, info);
            }
        }
        catch (Throwable e) {
        	e.printStackTrace();
        }
    }
	public File getPluginDirectory() {
		return pluginDirectory;
	}
	public void setPluginDirectory(File pluginDirectory) {
		this.pluginDirectory = pluginDirectory;
	}
	public RealWebServer getRealWebServer() {
		return realWebServer;
	}
	public void setRealWebServer(RealWebServer realWebServer) {
		this.realWebServer = realWebServer;
	}
    
    

}

