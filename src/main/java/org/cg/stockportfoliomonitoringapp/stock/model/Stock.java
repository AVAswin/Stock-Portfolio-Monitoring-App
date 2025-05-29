package org.cg.stockportfoliomonitoringapp.stock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id; // Assuming Xano ID will be your primary key
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id // Use Xano's ID as your primary key for easier mapping
    private Long id;

    @Column(nullable = false, unique = true)
    private String symbol;

    @Column(nullable = false)
    private String name;

    private String sector;
    // Current price is dynamic, usually fetched from external APIs, not stored here for long-term
    // unless you have a separate price history mechanism.
}