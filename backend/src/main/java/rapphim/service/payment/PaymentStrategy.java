package rapphim.service.payment;

public interface PaymentStrategy {
    void pay(double amount, String orderId);
    String getPaymentMethodName();
}
