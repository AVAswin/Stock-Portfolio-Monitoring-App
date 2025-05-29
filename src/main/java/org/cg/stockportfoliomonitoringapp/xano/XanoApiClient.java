// src/main/java/org/cg/stockportfoliomonitoringapp/xano/XanoApiClient.java
package org.cg.stockportfoliomonitoringapp.xano;

import jakarta.annotation.PostConstruct;
import org.cg.stockportfoliomonitoringapp.holding.dto.XanoStockDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference; // Added import for List

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class XanoApiClient {

    private static final Logger log = LoggerFactory.getLogger(XanoApiClient.class);

    @Value("${xano.mockdata.stocks.url}")
    private String xanoMockDataStocksUrl;

    private final RestTemplate restTemplate;

    public XanoApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        log.info("XanoApiClient constructor finished.");
    }

    @PostConstruct
    public void init() {
        log.info("XanoApiClient initialized via @PostConstruct. Attempting to pre-fetch mock stock data.");
        log.info("xanoMockDataStocksUrl value is: {}", xanoMockDataStocksUrl);
     
        try {
            getAllMockStocks();
        } catch (Exception e) {
            log.error("Failed to pre-fetch mock stock data on startup: {}", e.getMessage());
        }
    }

    public List<XanoStockDto> getAllMockStocks() {
        log.info("Fetching all mock stock data from Xano URL: {}", xanoMockDataStocksUrl);
        try {
            ResponseEntity<List<XanoStockDto>> response = restTemplate.exchange(
                    xanoMockDataStocksUrl,
                    org.springframework.http.HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<XanoStockDto>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully fetched {} mock stocks from Xano.", response.getBody().size());
                return response.getBody();
            } else {
                log.warn("Failed to fetch mock stocks. Status: {}", response.getStatusCode());
                return List.of(); 
            }
        } catch (HttpClientErrorException e) {
            log.error("Error fetching all mock stocks from Xano URL '{}': {} {}", xanoMockDataStocksUrl, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e; 
        } catch (Exception e) {
            log.error("An unexpected error occurred while fetching all mock stocks from Xano URL '{}': {}", xanoMockDataStocksUrl, e.getMessage(), e);
            return List.of();
        }
    }

    public Optional<XanoStockDto> getStockById(Long stockId) {
        log.info("Fetching mock stock data from Xano for ID: {}", stockId);
    
        return getAllMockStocks().stream()
                .filter(stock -> stock.getId().equals(stockId))
                .findFirst();
    }

    public Optional<XanoStockDto> getStockBySymbol(String symbol) {
        log.info("Attempting to find mock stock by symbol: {}", symbol);
        List<XanoStockDto> allStocks = getAllMockStocks(); 
        return allStocks.stream()
                .filter(stock -> stock.getSymbol().equalsIgnoreCase(symbol)) 
                .findFirst();
    }
}