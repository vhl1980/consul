package com.vhl.consul;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.catalog.CatalogService;
import com.vhl.consul.connexion.FactoryConsul;

public class ConsulTool {

    private final Logger logger = LoggerFactory.getLogger(ConsulTool.class);

    private FactoryConsul builder;

    private ConsulTool() {
    }

    private static class ConsulToolHolder {
        private final static ConsulTool instance = new ConsulTool();
    }

    public static ConsulTool getInstance() {
        return ConsulToolHolder.instance;
    }

    public void setFactory(FactoryConsul builder) {
        this.builder = builder;
    }

    public List<String> getListInstanceFilter(String regexApp) {
        logger.info("GET CATALOG CLIENT");
        CatalogClient catalog = builder.getBuilder().build().catalogClient();
        logger.info("LIST ALL OF SERVICES WITHOUT FILTER");
        ConsulResponse<Map<String, List<String>>> services = catalog.getServices();

        services.getResponse().entrySet().stream().forEach(x -> {
            logger.debug(x.toString());
            logger.debug("-------------");
        });

        logger.info("FILTER SERVICES with regex : " + regexApp);
        List<String> lst = services.getResponse().entrySet().stream().map(e -> new String(e.getKey())).filter(isMatches(regexApp))
                .collect(Collectors.toList());

        List<String> strings = new ArrayList<>();

        return lst;

    }

    /**
     * @param patternFilter
     * @return true
     */
    private Predicate<String> isMatches(String patternFilter) {
        return p -> p.matches(patternFilter);
    }

    /**
     * @param lst Must have tag client_id
     * @return String [] example host:port/client_id
     */
    public String[] getUrlsJmx(List<String> lst, String regexPort, String regexTag) {
        logger.debug("SET LIST INSTANCE RUNNING ");
        CatalogClient catalog = builder.getBuilder().build().catalogClient();

        lst.stream().forEach(s -> {
            ConsulResponse<List<CatalogService>> service = catalog.getService(s);
            service.getResponse().stream().forEach(x -> {
                logger.debug("TO STRING : " + x.toString());
                logger.debug("--------------------");
            });
        });

        List<CatalogService> lstCs = new ArrayList<CatalogService>();
        lst.stream().forEach(x -> {
            ConsulResponse<List<CatalogService>> service = catalog.getService(x);
            service.getResponse().stream().forEach(p -> {
                lstCs.add(p);
                logger.debug("add service id in lstCs : {}", p.getServiceId());
            });
        });

        logger.info("$$$$$$$$$$$$$$$$$$$$$$");
        logger.debug("FILTER PORT JMX : {}", regexPort);
        List<CatalogService> lstCsFilterPort = lstCs.stream().filter(s -> s.getServiceId().matches(regexPort)).collect(Collectors.toList());
        logger.debug("Services id to apply map URL_JMX");
        lstCsFilterPort.stream().forEach(x -> logger.info("service id : {}", x.getServiceId()));

        logger.debug("SET URL WITH RETRIEVE TAG CLIENT_ID VALUE WITH FILTER : {}", regexTag);
        List<String> listUrlJmx = lstCsFilterPort.stream().map(x -> toUrlJmx(x, regexTag)).collect(Collectors.toList());
        List<String> final_listUrlJmx = listUrlJmx.stream().filter(x -> Objects.nonNull(x)).collect(Collectors.toList());

        logger.info("LIST URLS INSTANCES VDP RUNNING");
        final_listUrlJmx.stream().forEach(x -> logger.info(x));

        String[] urlsJmx = new String[final_listUrlJmx.size()];
        final_listUrlJmx.toArray(urlsJmx);

        return urlsJmx;

    }

    private String toUrlJmx(CatalogService service, String regexTag) {
        String tag = null;
        String sep = "=";
        for (String s : service.getServiceTags()) {
            if (s.matches(regexTag) && s.split(sep).length == 2) {
                tag = s.split(sep)[1];
            }
        }
        if (tag == null) {
            logger.error("Service ID : {} not match regex tag {} in tags {}", service.getServiceName(), regexTag, service.getServiceTags());
            return null;
        }
        return String.format("%s:%d/%s", service.getAddress(), service.getServicePort(), tag);
    }

}
