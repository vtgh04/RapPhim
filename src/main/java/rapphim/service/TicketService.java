package rapphim.service;

import rapphim.dao.TicketDao;

public class TicketService {

    private final TicketDao ticketDao;

    public TicketService() {
        this.ticketDao = new TicketDao();
    }

}
