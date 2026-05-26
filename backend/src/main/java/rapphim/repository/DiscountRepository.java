package rapphim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rapphim.model.Discount;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String> {
    List<Discount> findByDiscountIdContainingOrDiscountNameContaining(String idKeyword, String nameKeyword);
}
