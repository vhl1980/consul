package com.vhl.consul.connexion;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;

public class FactoryConsul {

	private final Logger logger = LoggerFactory.getLogger(FactoryConsul.class);
	private final String sepUrls =",";
	private Consul.Builder builder;
	private long TIMEOUT_CONNECT = 5000l;
	private List<HostAndPort> listHostPort;
	
	public FactoryConsul(String consulUrls) {
		 logger.info("INIT SCAN CONSUL");
	        String[] urls;
	        if (consulUrls.contains(sepUrls))
	            urls = consulUrls.split(sepUrls);
	        else
	            urls = new String[] { consulUrls };

	        if (urls == null || urls.length == 0)
	            throw new IllegalArgumentException("Url list empty");

	        setListHostPort(urls);
	        logger.info("BUILD FACTORY CONSUL");
	        builder = getBuildConsul();
	        logger.info("BUILDED FACTORY CONSUL");
	}
	public Consul.Builder getBuilder() {
        return builder;
    }

    private Consul.Builder getBuildConsul() {
        logger.debug("SET BUILD CONSUL");

        // Boolean isConnected = false;
        // for (HostAndPort url : listHostPort) {
        // try {
        // logger.info("TRY TO CONNECT WITH : " + url);
        // Consul.Builder builder_ping = Consul.builder().withHostAndPort(url);
        // builder_ping.build();
        // logger.info("SUCCESS CONNECTED WITH : " + url);
        // return builder;
        // } catch (ConsulException e) {
        // logger.error("Could not register this instance with Consul", e);
        // }
        // }

        // THE MULTIHOST CANNOT RETURN ERROR CONNEXION
        builder = Consul.builder().withConnectTimeoutMillis(TIMEOUT_CONNECT).withMultipleHostAndPort(listHostPort, TIMEOUT_CONNECT);
        builder.withPing(true);

        return builder;
    }

    private void setListHostPort(String[] urls) {
        listHostPort = new ArrayList<HostAndPort>();
        for (String url : urls) {
            HostAndPort h = HostAndPort.fromString(url);
            listHostPort.add(h);
            logger.debug("ADD URL TO CONNECT CONSUL : " + url);
        }
    }
}
