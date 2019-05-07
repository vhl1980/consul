package com.vhl.consul;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vhl.consul.connexion.FactoryConsul;



public class Main {

	private final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		String urls = "192.168.0.170:8500,192.168.0.171:8500,192.168.0.172:8500";



		ConsulTool ct = ConsulTool.getInstance();
		FactoryConsul builder = new FactoryConsul(urls);
		
		ct.setFactory(builder);
		
		List<String> lst = ct.getListInstanceFilter("^consul_dadm.*");
		
		lst.stream().forEach(System.out::println);
		
//		logger.info("GET CATALOG SERVICES");
//		CatalogClient catalog = builder.getBuilder().build().catalogClient();
//		logger.info("LIST ALL OF SERVICES WITHOUT FILTER");
//		ConsulResponse<Map<String, List<String>>> services = catalog.getServices();
//
//		services.getResponse().entrySet().stream().forEach(x -> {
//			logger.info(x.toString());
//			logger.info("-------------");
//		});


	}

}
