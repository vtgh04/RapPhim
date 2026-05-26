package rapphim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rapphim.model.Invoice;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    
    @Query("SELECT MAX(i.invoiceId) FROM Invoice i")
    String findMaxInvoiceId();

    List<Invoice> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);

    List<Invoice> findByCreatedAtBetweenAndEmployeeIdOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end, String employeeId);

    List<Invoice> findAllByOrderByCreatedAtDesc();
}
