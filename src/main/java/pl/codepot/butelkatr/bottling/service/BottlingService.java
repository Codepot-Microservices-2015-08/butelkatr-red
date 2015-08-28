package pl.codepot.butelkatr.bottling.service;

import static com.netflix.hystrix.HystrixCommand.Setter.withGroupKey;
import static com.netflix.hystrix.HystrixCommandGroupKey.Factory.asKey;

import com.nurkiewicz.asyncretry.RetryExecutor;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.codepot.butelkatr.bottling.model.BottlesOrder;

/**
 * Created by mszarlinski on 2015-08-28.
 */
@Service
public class BottlingService {

    private static Logger log = LoggerFactory.getLogger(BottlingService.class);

    @Autowired
    private BottlingAsyncExecutor bottlingAsyncExecutor;

    public static final int BOTTLING_STARTED_THRESHOLD = 1000;

    private final ServiceRestClient serviceRestClient;

    private final RetryExecutor retryExecutor;

    private AtomicInteger wortSupplied = new AtomicInteger(0);

    @Autowired
    public BottlingService(final ServiceRestClient serviceRestClient, final RetryExecutor retryExecutor) {
        this.serviceRestClient = serviceRestClient;
        this.retryExecutor = retryExecutor;
    }

    public void orderBottles(final BottlesOrder bottlesOrder) {
        synchronized (this) {
            final int totalWortSupplied = wortSupplied.addAndGet(bottlesOrder.getWort());
            log.info("######## totalWortSupplied: " + totalWortSupplied);
            if (totalWortSupplied > BOTTLING_STARTED_THRESHOLD) {

                notifyBottlingStarted();
                int expectedBottles = totalWortSupplied / BOTTLING_STARTED_THRESHOLD;
                wortSupplied.getAndAdd(-expectedBottles * BOTTLING_STARTED_THRESHOLD);

                log.info("######## bottling started");
                bottlingAsyncExecutor.bottle(expectedBottles, this::notifyBottlingFinished);
            }
        }
    }

    private void notifyBottlingFinished(final int bottled) {
        serviceRestClient.forService("prezentatr-red")
                .retryUsing(retryExecutor)
                .post()
                .withCircuitBreaker(withGroupKey(asKey("hystrix_group")))
                .onUrl("/feed/bottles/" + bottled)
                .body("")
                .withHeaders().contentType("application/vnd.pl.codepot.prezentatr.v1+json")
                .andExecuteFor()
                .anObject()
                .ofTypeAsync(Void.class);
    }

    private void notifyBottlingStarted() {
        serviceRestClient.forService("prezentatr-red")
                .retryUsing(retryExecutor)
                .post()
                .withCircuitBreaker(withGroupKey(asKey("hystrix_group")))
                .onUrl("/present/butelkatr")
                .body("")
//                .withHeaders().contentType("application/vnd.some.other.service.v1+json")
                .andExecuteFor()
                .anObject()
                .ofTypeAsync(Void.class);
    }


}
