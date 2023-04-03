package io.cryptorush.userservice;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class ExternalGeoIpServiceConfig {

    @Value("${external-geoip-service.socket-connection-timeout-millis: 5000}")
    private int socketConnectionTimeoutMillis;
    @Value("${external-geoip-service.read-timeout-millis: 2000}")
    private int readTimeoutMillis;
    @Value("${external-geoip-service.write-timeout-millis: 2000}")
    private int writeTimeoutMillis;

    @Value("${external-geoip-service.base-url: }")
    private String baseUrl;

    @PostConstruct
    public void postConstruct() {
        Preconditions.checkArgument(StringUtils.hasText(baseUrl), "external-geoip-service.base-url can't be empty, check your configs!");
    }

    @Bean
    public WebClient webClient() {
        log.info("Creating external geoip service WebClient, baseUrl={}, socketConnectionTimeoutMillis={}, " +
                        "readTimeoutMillis={}, writeTimeoutMillis={}", baseUrl,
                socketConnectionTimeoutMillis, readTimeoutMillis, writeTimeoutMillis);

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, socketConnectionTimeoutMillis)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeoutMillis, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeoutMillis, TimeUnit.MILLISECONDS)));

        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(connector)
                .build();
        log.info("External geoip service WebClient was created.");
        return webClient;
    }
}
