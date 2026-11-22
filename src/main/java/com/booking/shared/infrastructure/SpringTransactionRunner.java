package com.booking.shared.infrastructure;

import com.booking.shared.application.TransactionRunner;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
class SpringTransactionRunner implements TransactionRunner {

    private final TransactionTemplate transactionTemplate;

    SpringTransactionRunner(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public <T> T run(Supplier<T> action) {
        return transactionTemplate.execute(status -> action.get());
    }
}