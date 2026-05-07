package com.rapphim.service;

import com.rapphim.dao.TicketDao;

public class TicketService {
    
    private final TicketDao ticketDao;

    public TicketService() {
        this.ticketDao = new TicketDao();
    }

    /**
     * Xử lý các logic nghiệp vụ liên quan đến vé (Tickets),
     * ví dụ: kiểm tra vé có hợp lệ không, tìm vé theo mã, hoặc lấy thông tin ghế của vé.
     */
    // Thêm các phương thức nghiệp vụ ở đây...
}
