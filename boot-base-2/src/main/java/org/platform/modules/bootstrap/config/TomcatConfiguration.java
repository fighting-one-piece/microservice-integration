package org.platform.modules.bootstrap.config;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Sets;

@Configuration
public class TomcatConfiguration {
	
	@Value("${server.port}")
	private Integer serverPort = null;

	@Value("${server.additionalPorts:null}")
	private String serverAdditionalPorts = null;
	
	@Value("${management.server.port:${server.port}}")
	private Integer serverManagementPort = null;
 
	@Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection securityCollection = new SecurityCollection();
                securityCollection.addPattern("/*");
                securityCollection.addMethod("PUT");  
                securityCollection.addMethod("COPY");  
                securityCollection.addMethod("HEAD");  
                securityCollection.addMethod("TRACE");  
                securityCollection.addMethod("DELETE");  
                securityCollection.addMethod("SEARCH");  
                securityCollection.addMethod("PROPFIND");  
                securityConstraint.addCollection(securityCollection);
                context.addConstraint(securityConstraint);
            }
        };
        factory.setUriEncoding(Charset.forName("UTF-8"));
        factory.addConnectorCustomizers((connector) -> {
            int maxSize = 50000000;
            connector.setMaxPostSize(maxSize);
            connector.setMaxSavePostSize(maxSize);
            if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol) {
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(maxSize);
            }
        });
        Connector[] additionalTomcatConnectors = additionalTomcatConnectors();
        if (null != additionalTomcatConnectors && additionalTomcatConnectors.length > 0)
        	factory.addAdditionalTomcatConnectors(additionalTomcatConnectors);
        return factory;
    }

	@Bean
    public Connector[] additionalTomcatConnectors() {
		if (null == serverAdditionalPorts || "".equals(serverAdditionalPorts) ||
			"null".equals(serverAdditionalPorts)) return null;
		Set<Integer> defaultPorts = Sets.newHashSet(serverPort, serverManagementPort);
	    String[] ports = serverAdditionalPorts.split(",");
	    List<Connector> connectors = new ArrayList<Connector>();
	    for (int i = 0, len = ports.length; i < len; i++) {
	    	int port = Integer.parseInt(ports[i]);
	    	if (!defaultPorts.contains(port)) {
	    		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
	    		connector.setScheme("http");
	    		connector.setSecure(false);
	    		connector.setPort(port);
	    		connectors.add(connector);
	    	}
	   }
	   return connectors.toArray(new Connector[0]);
    }

}
