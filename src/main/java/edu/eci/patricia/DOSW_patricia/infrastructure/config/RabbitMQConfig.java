package edu.eci.patricia.DOSW_patricia.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ infrastructure configuration.
 * Declares the {@code auth.exchange} topic exchange and wires the JSON message converter
 * into the {@link RabbitTemplate}. The {@link ApplicationRunner} tries to connect eagerly
 * at startup but swallows failures so the service boots even when RabbitMQ is unavailable.
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.auth}")
    private String authExchangeName;

    /**
     * Declares the durable, non-auto-delete topic exchange used by all auth events.
     * The exchange name is resolved from {@code rabbitmq.exchange.auth} in application properties.
     *
     * @return the auth topic exchange
     */
    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange(authExchangeName, true, false);
    }

    /**
     * JSON message converter that serialises/deserialises AMQP message bodies using Jackson.
     *
     * @return a Jackson2JsonMessageConverter
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configures the {@link RabbitTemplate} to use the JSON converter.
     *
     * @param connectionFactory the AMQP connection factory
     * @return the configured RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    /**
     * Creates a {@link RabbitAdmin} that automatically declares exchanges, queues, and bindings.
     *
     * @param connectionFactory the AMQP connection factory
     * @return the configured RabbitAdmin
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

    /**
     * Eagerly initialises RabbitMQ topology at startup.
     * If RabbitMQ is unreachable the warning is logged and the application continues —
     * the exchange will be declared on the first successful connection.
     *
     * @param rabbitAdmin the RabbitAdmin to initialise
     * @return the ApplicationRunner bean
     */
    @Bean
    public ApplicationRunner connectRabbit(RabbitAdmin rabbitAdmin) {
        return args -> {
            try {
                rabbitAdmin.initialize();
            } catch (Exception e) {
                log.warn("RabbitMQ no disponible al arrancar — exchange se declarara en la primera conexion: {}", e.getMessage());
            }
        };
    }
}
