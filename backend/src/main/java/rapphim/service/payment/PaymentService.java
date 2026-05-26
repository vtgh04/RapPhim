package rapphim.service.payment;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final Map<String, PaymentStrategy> strategies;

    public PaymentService(List<PaymentStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getPaymentMethodName().toUpperCase(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
    }

    public void processPayment(String method, double amount, String orderId) {
        PaymentStrategy strategy = strategies.get(method.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Phương thức thanh toán không hỗ trợ: " + method);
        }
        strategy.pay(amount, orderId);
    }
}
