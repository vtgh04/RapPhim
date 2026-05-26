package rapphim.service.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CashStrategy implements PaymentStrategy {
    private static final Logger log = LoggerFactory.getLogger(CashStrategy.class);

    @Override
    public void pay(double amount, String orderId) {
        log.info("[CASH] Thu tiền mặt tại quầy cho hoá đơn: {}", orderId);
        log.info("[CASH] Số tiền thu: {} VND", amount);
        log.info("[CASH] Đã hoàn thành thu tiền mặt cho hoá đơn: {}", orderId);
    }

    @Override
    public String getPaymentMethodName() {
        return "CASH";
    }
}
