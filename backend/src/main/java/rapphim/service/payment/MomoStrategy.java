package rapphim.service.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MomoStrategy implements PaymentStrategy {
    private static final Logger log = LoggerFactory.getLogger(MomoStrategy.class);

    @Override
    public void pay(double amount, String orderId) {
        log.info("[MOMO] Đang quét mã QR MoMo cho hoá đơn: {}", orderId);
        log.info("[MOMO] Đang xử lý thanh toán. Số tiền: {} VND", amount);
        // Simulating MoMo payment approval
        log.info("[MOMO] Thanh toán thành công qua MoMo cho hoá đơn: {}", orderId);
    }

    @Override
    public String getPaymentMethodName() {
        return "CARD";
    }
}
