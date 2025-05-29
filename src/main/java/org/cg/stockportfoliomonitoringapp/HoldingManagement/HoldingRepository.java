package org.cg.stockportfoliomonitoringapp.HoldingManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {

   
	 Optional<Holding> findByUserIdAndStockSymbolIgnoreCase(Long userId, String stockSymbol);

  
    List<Holding> findByUserId(Long userId);
}