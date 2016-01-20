package com.tao.realweb.plugins.basic;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

public class PluginManager {

	private static Logger logger = LoggerFactory.getLogger(PluginManager.class);
	private static PluginManager instance = null;
	private static Object lock = new Object();
	private RealWebServer realWebServer;
	private File pluginDirectory;
	private Map<String,Plugin> pluginsMap = new ConcurrentHashMap<String, Plugin>();
	private Map<String, PluginClassLoader> classloadersMap;
	private Map<String, File> pluginDirsMap;
	private Map<String, File> pluginFilesMap;
	private PluginMonitor pluginMonitor;
	private ScheduledExecutorService executor = null;
	
	private PluginManager(){
		
	}
	public static PluginManager getInstance(){
		if(instance == null){
			synchronized (lock) {
				if(instance == null){
					instance = new PluginManager();
				}
			}
		}
		return instance;
	}
	public void init(RealWebServer server){
		this.realWebServer =server;
	    pluginDirsMap = new HashMap<String, File>();
        pluginFilesMap = new HashMap<String, File>();
        pluginMonitor = new PluginMonitor();
        classloadersMap = new HashMap<String, PluginClassLoader>();
	}
	public void startPlugins(){
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
                        	pluginFilesMap.remove(pluginName);
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
                    pluginDirsMap.put(pluginName, pluginDir);
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
            for (String file : children) {
                boolean success = deleteDir(new File(dir, file));
                if (!success) {
                    return false;
                }
            }
        }
        boolean deleted = !dir.exists() || dir.delete();
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
        File pluginFile  = pluginFilesMap.remove(pluginName);
        File pluginDir = pluginDirsMap.remove(pluginName);
        PluginClassLoader pluginLoader = classloadersMap.remove(pluginName);

        // try to close the cached jar files from the plugin class loader
        if (pluginLoader != null) {
        	pluginLoader.unloadJarFiles();
        }

        // Give the plugin 2 seconds to unload.
        try {
        	if(pluginDir != null){
	            Thread.sleep(2000);
	            // Ask the system to clean up references.
	            System.gc();
	            int count = 0;
	            while (!deleteDir(pluginDir) && count++ < 5) {
	                Thread.sleep(8000);
	                // Ask the system to clean up references.
	                System.gc();
	            }
        	}
        	 if (pluginFile != null) {
        		 deleteDir(pluginFile);
             }
        } catch (Exception e) {
        }

       if (plugin != null && pluginDir.exists()) {
            // Restore references since we failed to remove the plugin
            pluginsMap.put(pluginName, plugin);
            pluginDirsMap.put(pluginName, pluginDir);
            pluginFilesMap.put(pluginName, pluginFile);
            classloadersMap.put(pluginName, pluginLoader);
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
                PluginClassLoader pluginLoader = new PluginClassLoader();
                pluginLoader.addDirectory(pluginDir, false);
                PluginInfo info = new PluginInfo();
                String className = pluginXML.selectSingleNode("/plugin/classname").getText().trim();
                info.setClassName(className);
                info.setPluginName(pluginDir.getName());
                plugin = (Plugin)(pluginLoader.loadClass(className).newInstance());
                pluginsMap.put(pluginName, plugin);
                pluginDirsMap.put(pluginName, pluginDir);
                classloadersMap.put(pluginName, pluginLoader);
                Element parameters =  (Element) pluginXML.selectSingleNode("/plugin/parameters");
                if(parameters != null){
                	List<Element> elements = parameters.elements();
					for(Element e : elements){
						info.addParameter(e.getName(), e.getTextTrim());
					}
                }
                plugin.setDocument(pluginXML);
                plugin.init(this, info);
                plugin.start();
            }
        }
        catch (Throwable e) {
        	e.printStackTrace();
        	unloadPlugin(pluginDir.getName());
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

