/*
 * Copyright 2015, FixStream Networks, Inc. All rights reserved.
 */
package test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Kish
 */
public class NagiosConnectionTester {

	private static final String NAGIOS_SERVICE_STATUS_SUFFIX_URL = "/nagiosxi/api/v1/objects/servicestatus";

	private static final Map<String, String> NAGIOS_SERVICE_TRIGGER_COMMAND_MAP = new HashMap<String, String>() {{
		put("NAGIOS_APACHE_MONITORING", "check_apache");
		put("NAGIOS_MYSQL_MONITORING", "check_xi_mysql");
		put("NAGIOS_ORACLE_MONITORING", "check_xi_oracle");
		put("NAGIOS_WEBLOGIC_MONITORING", "check_weblogic");
	}};
    
    void test() throws Exception {
        String baseUrl = "http://192.168.128.205";
        String apiKey = "inaas7v2";
        String monitoringType = "NAGIOS_ORACLE_MONITORING";
        String responseString;

        URI uri = new URIBuilder(baseUrl).setPath(NAGIOS_SERVICE_STATUS_SUFFIX_URL).setParameter("apikey", apiKey)
                .setParameter("check_command", "lk:" + NAGIOS_SERVICE_TRIGGER_COMMAND_MAP.get(monitoringType)).build();
        CloseableHttpResponse response1 = null;
        // Set up connection so that we can handle both http and https

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000)
                .setSocketTimeout(10000).build();
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig)
                .setSSLSocketFactory(new SSLConnectionSocketFactory(
                                SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build()))
                .build();) {

            //get hosts running the desired service from Nagios
            System.out.println("preparing http get - path: " + uri.getRawPath() + " | query: " + uri.getQuery());
            HttpGet httpGet = new HttpGet(uri);
            response1 = httpClient.execute(httpGet);
            if (response1.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
                throw new Exception("Error code in response from Nagios");
            } else {
                responseString = new BasicResponseHandler().handleResponse(response1);

                System.out.println("responseString :: " + responseString);

                if (responseString == null) {
                    throw new Exception("Invalid request");
                }

                if (responseString.contains("error")) {
                    throw new Exception("Error from Nagios :: " + responseString);
                }

                JsonNode arrNode = new ObjectMapper().readTree(responseString).get("servicestatuslist").get("servicestatus");
                System.out.println("arrNode :: " + arrNode);
                Set<String> serviceHosts = new HashSet<>();
                if (arrNode.isArray()) {
                    for (final JsonNode objNode : arrNode) {
                        System.out.println("adding address :: " + objNode.get("host_address").textValue());
                        serviceHosts.add(objNode.get("host_address").textValue());
                    }
                }
            }
        } catch (java.net.UnknownHostException ex) {
            throw new Exception("Invalid host name");
        } finally {
            if (response1 != null) {
                response1.close();
            }
        }
    }
    

    public static void main(String[] args) {
        try {
            new NagiosConnectionTester().test();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
