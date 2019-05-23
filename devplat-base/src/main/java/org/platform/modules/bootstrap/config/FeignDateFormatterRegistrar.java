package org.platform.modules.bootstrap.config;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

@Component
public class FeignDateFormatterRegistrar implements FeignFormatterRegistrar {
	
	public FeignDateFormatterRegistrar() {}

	@Override
	public void registerFormatters(FormatterRegistry registry) {
		registry.addConverter(Date.class, String.class, new Date2StringConverter());
	}
	
	private class Date2StringConverter implements Converter<Date, String> {
		
		SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
        @Override
        public String convert(Date source) {
            return SDF.format(source);
        }
    }

}
