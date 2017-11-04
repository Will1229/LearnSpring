package service;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ServiceProperties.class)
public class ServiceConfiguration {
    @Bean
    public OddsService oddsService(ServiceProperties serviceProperties) {
        return new OddsService(serviceProperties.getPrizeMultiple(), serviceProperties.getWinBoundary(),
                serviceProperties.getFreeBoundary(), serviceProperties.getDefaultPrize());
    }

    @Bean
    public AccountService accountService(ServiceProperties serviceProperties) {
        return new AccountService(serviceProperties.getAccountCacheCapacity());
    }

    @Bean
    public BetService betService(ServiceProperties serviceProperties) {
        return new BetService(oddsService(serviceProperties), accountService(serviceProperties));
    }
}