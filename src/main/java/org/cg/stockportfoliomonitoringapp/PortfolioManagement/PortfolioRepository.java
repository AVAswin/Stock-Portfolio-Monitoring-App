package org.cg.stockportfoliomonitoringapp.PortfolioManagement;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PortfolioRepository extends CrudRepository<Portfolio, Long> {
    List<Portfolio> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
