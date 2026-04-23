package backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.FormContentFilter;

@Configuration
public class WebConfig {

    @Bean
    public FormContentFilter formContentFilter() {
        return new FormContentFilter();
    }
}
