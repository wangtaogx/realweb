/**
 * $RCSfile$
 * $Revision: 1089 $
 * $Date: 2005-03-07 02:36:27 -0300 (Mon, 07 Mar 2005) $
 *
 * Copyright (C) 2004-2008 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tao.realweb.launch;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

class RealWebClassLoader extends URLClassLoader {

    RealWebClassLoader(ClassLoader parent, File libDir) throws MalformedURLException {
        super(new URL[] { libDir.toURI().toURL() }, parent);

        File[] jars = libDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean accept = false;
                String smallName = name.toLowerCase();
                if (smallName.endsWith(".jar")) {
                    accept = true;
                }
                else if (smallName.endsWith(".zip")) {
                    accept = true;
                }
                return accept;
            }
        });

        // Do nothing if no jar or zip files were found
        if (jars == null) {
            return;
        }

        for (int i = 0; i < jars.length; i++) {
            if (jars[i].isFile()) {
                addURL(jars[i].toURI().toURL());
            }
        }
    }
  
	public void addDir(File file) throws MalformedURLException{
		if(file == null)
			return;
		if(file.isFile()){
			addURL(file.toURI().toURL());
		}else{
			File[] files = file.listFiles();
			if(files != null){
				for(File f : files){
					addDir(f);
				}
			}
		}
	}
}
