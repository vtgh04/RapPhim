package rapphim.service.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VNPayStrategy implements PaymentStrategy {
    private static final Logger log = LoggerFactory.getLogger(VNPayStrategy.class);

    @Override
    public void pay(double amount, String orderId) {
        log.info("[VNPAY] Đang khởi tạo cổng thanh toán VNPay cho hoá đơn: {}", orderId);
        log.info("[VNPAY] Đang xử lý giao dịch. Số tiền: {} VND", amount);
        // Simulating VNPay payment approval
        log.info("[VNPAY] Thanh toán thành công qua VNPay cho hoá đơn: {}", orderId);
    }

    @Override
    public String getPaymentMethodName() {
        return "TRANSFER";
    }
}
