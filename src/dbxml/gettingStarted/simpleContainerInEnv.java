//
// See the file LICENSE for redistribution information.
//
// Copyright (c) 2004, 2014 Oracle and/or its affiliates.  All rights reserved.
//

package dbxml.gettingStarted;

import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlManager;

import java.io.File;

//Very simple program that opens (creates) a database environment and
// then opens several containers in that environment.
class simpleContainerInEnv
{
    public static void main(String args[]) 
	throws Throwable {
	//The path the directory where you want to place the environment
	// must exist!!
	String environmentPath = "/path/to/environment/directory";

	//Environment configuration is minimal:
	// create + 50MB cache
	// no transactions, logging, or locking
        EnvironmentConfig config = new EnvironmentConfig();
        config.setCacheSize(50 * 1024 * 1024);
        config.setAllowCreate(true);
        config.setInitializeCache(true);
        Environment env = new Environment(new File(environmentPath), config);

	//Create XmlManager using that environment, no DBXML flags
	XmlManager mgr = new XmlManager(env, null);

	//multiple containers can be opened in the same database environment
	XmlContainer container1 = mgr.createContainer("myContainer1");
	
	XmlContainer container2 = mgr.createContainer("myContainer2");
	
	XmlContainer container3 = mgr.createContainer("myContainer3");

	// do work here //

	// clean up
	container1.delete();
	container2.delete();
	container3.delete();
	mgr.delete();
	env.close();
    }
}
