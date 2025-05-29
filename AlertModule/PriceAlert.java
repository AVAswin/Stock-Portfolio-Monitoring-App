package org.cg.stockportfoliomonitoringapp.AlertModule;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
@Entity
@Getter @Setter @NoArgsConstructor
public class PriceAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String stockSymbol;
    private Integer targetPrice;

    private boolean triggered = false;
}
