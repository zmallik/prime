interface Notification {
    void send();
}

class EmailNotification implements Notification {
    public void send() {
        System.out.println("Email sent");
    }
}

class SMSNotification implements Notification {
    public void send() {
        System.out.println("SMS sent");
    }
}

class NotificationFactory {
    public static Notification create(String type) {
        if ("EMAIL".equals(type)) return new EmailNotification();
        if ("SMS".equals(type)) return new SMSNotification();
        throw new IllegalArgumentException("Invalid type");
    }
}


/**

Notification notification = NotificationFactory.create("EMAIL");
notification.send();

**/
