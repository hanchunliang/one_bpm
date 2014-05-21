package com.sinosoft.one.bpm.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.drools.SystemEventListener;
import org.drools.SystemEventListenerFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.UrlResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgeAgentGenerator {
	private static final HashMap<String, KnowledgeAgent> kAgentMap = (HashMap<String, KnowledgeAgent>) new HashMap<String, KnowledgeAgent>();

	private KnowledgeAgentGenerator() {
	}

	public static synchronized KnowledgeAgent getKnowledgeAgent(
			String propertiesFilePath) {
		KnowledgeAgent kAgent = null;
		if (kAgentMap.containsKey(propertiesFilePath)) {
			kAgent = kAgentMap.get(propertiesFilePath);
		} else {
			kAgent = newKnowledgeAgent(propertiesFilePath);
			kAgentMap.put(propertiesFilePath, kAgent);
		}
		return kAgent;
	}

	private static KnowledgeAgent newKnowledgeAgent(String propertiesFilePath) {
		Properties p = new Properties();
		try {
			p.load(getFileStream(propertiesFilePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String changeSet = p.getProperty("drools.agent.changeset");
		Resource r = null;
		if (null != changeSet && changeSet.startsWith("http")) {
			r = ResourceFactory.newUrlResource(changeSet);
			if(r instanceof UrlResource) {
				UrlResource urlResource = (UrlResource) r;
				urlResource.setBasicAuthentication(p.getProperty("drools.agent.basicAuthentication", "disabled"));
				urlResource.setUsername(p.getProperty("drools.agent.username", ""));
				urlResource.setPassword(p.getProperty("drools.agent.password", ""));
			}
		} else {
			r = ResourceFactory.newClassPathResource("ChangeSet.xml");
		}
		System.setProperty("drools.resource.urlcache",
				p.getProperty("drools.resource.urlcache"));
		ResourceChangeScannerConfiguration sconf = ResourceFactory
				.getResourceChangeScannerService()
				.newResourceChangeScannerConfiguration();
		sconf.setProperty("drools.resource.scanner.interval",
				p.getProperty("drools.resource.scanner.interval"));
		ResourceFactory.getResourceChangeScannerService().configure(sconf);

		KnowledgeAgentConfiguration kaconf = KnowledgeAgentFactory
				.newKnowledgeAgentConfiguration();
		kaconf.setProperty("drools.agent.scanDirectories",
				p.getProperty("drools.agent.scanDirectories"));
		kaconf.setProperty("drools.agent.newInstance",
				p.getProperty("drools.agent.newInstance"));

		KnowledgeAgent kAgent = KnowledgeAgentFactory.newKnowledgeAgent(
				propertiesFilePath, kaconf);
		
		SystemEventListenerFactory
				.setSystemEventListener(new LogSystemEventListener());

		ResourceFactory.getResourceChangeNotifierService().start();
		ResourceFactory.getResourceChangeScannerService().start();

		kAgent.applyChangeSet(r);

		return kAgent;
	}

	private static InputStream getFileStream(String propertiesFilePath) {
		InputStream is = null;
		is = KnowledgeAgentGenerator.class.getClassLoader()
				.getResourceAsStream(propertiesFilePath);
		return is;
	}
}

class LogSystemEventListener implements SystemEventListener {
	private Logger logger = LoggerFactory.getLogger("LogSystemEventListener");

	public void info(String message) {
		// TODO Auto-generated method stub
		this.info(message, null);
	}

	public void info(String message, Object object) {
		// TODO Auto-generated method stub
		logger.info(message, object);
	}

	public void warning(String message) {
		// TODO Auto-generated method stub
		this.warning(message, null);
	}

	public void warning(String message, Object object) {
		if(object instanceof KnowledgeBuilderErrors) {
			KnowledgeBuilderErrors errors = (KnowledgeBuilderErrors )object;
			Iterator<KnowledgeBuilderError> it = errors.iterator();
			while(it.hasNext()) {
				logger.warn(it.next().getMessage());
			}
		}
		// TODO Auto-generated method stub
		logger.warn(message, object);
	}

	public void exception(String message, Throwable e) {
		// TODO Auto-generated method stub
		logger.error(message, e);
	}

	public void exception(Throwable e) {
		// TODO Auto-generated method stub
		this.exception("", e);
	}

	public void debug(String message) {
		// TODO Auto-generated method stub
		this.debug(message, null);
	}

	public void debug(String message, Object object) {
		// TODO Auto-generated method stub
		logger.debug(message, object);
	}
}