package org.cisiondata.modules.bootstrap;

import org.cisiondata.modules.bootstrap.annotation.ComponentScanExclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "org.cisiondata" }, excludeFilters = {@ComponentScan.Filter(
	type = FilterType.ANNOTATION, value = ComponentScanExclude.class)})
public class BaseBootstrapApplication {

	protected static Logger LOG = LoggerFactory.getLogger(BaseBootstrapApplication.class);

}
