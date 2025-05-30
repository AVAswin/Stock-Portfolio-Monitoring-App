package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = true) 
    private Long stockId;    

    @Column(nullable = false)
    private String stockSymbol;

    @Column(nullable = false)
    private String stockName;

    private String sector; //

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double buyPrice;

    @Transient
    private Double currentPrice;

    public Double calculateGain(Double currentPrice) {
        if (currentPrice == null || buyPrice == null || quantity == null) {
            return 0.0;
        }
        return (currentPrice - buyPrice) * quantity;
    }

    public Double calculateGainPercent(Double currentPrice) {
        if (currentPrice == null || buyPrice == null || quantity == null || buyPrice == 0.0) {
            return 0.0;
        }
        Double totalBuyValue = buyPrice * quantity;
        if (totalBuyValue == 0.0) {
            return 0.0;
        }
        return (calculateGain(currentPrice) / totalBuyValue) * 100.0;
    }
}