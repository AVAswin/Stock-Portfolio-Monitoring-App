package org.cg.stockportfoliomonitoringapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockPortfolioMonitoringAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockPortfolioMonitoringAppApplication.class, args);
    }

}
