package com.bnq.api.config;

import com.ibm.mq.spring.boot.MQConfigurationProperties;
import jakarta.jms.JMSException;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ErrorHandler;

@Slf4j
@Configuration
@EnableJms
@EnableRetry
@RequiredArgsConstructor
@EnableConfigurationProperties(MQConfigurationProperties.class)
public class JmsConfig {

    private final MQConfigurationProperties mqProperties;

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            jakarta.jms.ConnectionFactory connectionFactory,
            ErrorHandler errorHandler
    ) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory(connectionFactory);
        factory.setConnectionFactory(cachingConnectionFactory);
        factory.setConcurrency("3-10");
        factory.setSessionTransacted(true);
        factory.setErrorHandler(errorHandler);
        factory.setTransactionManager(jmsTransactionManager(connectionFactory));
        return factory;
    }

    @Bean
    public PlatformTransactionManager jmsTransactionManager(jakarta.jms.ConnectionFactory connectionFactory) {
        return new JmsTransactionManager(connectionFactory);
    }

    @Bean
    public ErrorHandler jmsErrorHandler() {
        return new JmsErrorHandler();
    }

    @Slf4j
    private static class JmsErrorHandler implements ErrorHandler {
        @Override
        public void handleError(Throwable t) {
            if (t.getCause() instanceof JMSException) {
                log.error("JMS Error occurred: {}", t.getCause().getMessage());
                // Additional error handling logic can be added here
            } else {
                log.error("Non-JMS Error in message listener: {}", t.getMessage(), t);
            }
        }
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}