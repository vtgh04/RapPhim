package rapphim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rapphim.model.Ticket;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    List<Ticket> findByInvoiceId(String invoiceId);

    @Query("SELECT MAX(t.ticketId) FROM Ticket t")
    String findMaxTicketId();
}
