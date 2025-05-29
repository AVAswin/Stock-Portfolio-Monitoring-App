package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {

    // Find a specific holding by user ID and stock ID
	 Optional<Holding> findByUserIdAndStockSymbolIgnoreCase(Long userId, String stockSymbol);

    // Find all holdings for a specific user
    List<Holding> findByUserId(Long userId);
}