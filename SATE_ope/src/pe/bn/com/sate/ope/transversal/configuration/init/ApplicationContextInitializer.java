package pe.bn.com.sate.ope.transversal.configuration.init;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;	
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackages={"pe.bn.com.sate.ope.transversal.configuration",
		"pe.bn.com.sate.ope.transversal.util.anotaciones"})
@PropertySource("classpath:sate.properties")
public class ApplicationContextInitializer {

	
	 @Bean
	   public static PropertySourcesPlaceholderConfigurer propertyConfig() {
	        return new PropertySourcesPlaceholderConfigurer();
	    }
}

