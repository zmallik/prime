interface PaymentStrategy {
    void pay(int amount);
}

class CreditCardPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid via Credit Card: " + amount);
    }
}

class UpiPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid via UPI: " + amount);
    }
}

class PaymentService {
    private PaymentStrategy strategy;

    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void process(int amount) {
        strategy.pay(amount);
    }
}


/**  Usage

PaymentService service = new PaymentService();
service.setStrategy(new UpiPayment());
service.process(1000);


***/
