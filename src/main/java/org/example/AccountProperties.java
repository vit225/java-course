package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class AccountProperties {
    private final double defaultAmount;
    private final double transferCommission;

    public AccountProperties(@Value("${account.default-amount}") double
                                     defaultAmount,
                             @Value("${account.transfer-commission}") double
                                     transferCommission
    ) {
        this.defaultAmount = defaultAmount;
        this.transferCommission = transferCommission;
    }

    public double getDefaultAmount() {
        return defaultAmount;
    }

    public double getTransferCommission() {
        return transferCommission;
    }
}
