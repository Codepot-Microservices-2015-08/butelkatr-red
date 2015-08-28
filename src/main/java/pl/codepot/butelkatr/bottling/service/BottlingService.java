package pl.codepot.butelkatr.bottling.service;

import static com.netflix.hystrix.HystrixCommand.Setter.withGroupKey;
import static com.netflix.hystrix.HystrixCommandGroupKey.Factory.asKey;

import com.nurkiewicz.asyncretry.RetryExecutor;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.codepot.butelkatr.bottling.model.BottlesOrder;

/**
 * Created by mszarlinski on 2015-08-28.
 */
@Service
public class BottlingService {

    public static final int BOTTLING_STARTED_THRESHOLD = 1000;

    private final ServiceRestClient serviceRestClient;

    private final RetryExecutor retryExecutor;

    private AtomicInteger bottlesOrdered = new AtomicInteger(0);

    @Autowired
    public BottlingService(final ServiceRestClient serviceRestClient, final RetryExecutor retryExecutor) {
        this.serviceRestClient = serviceRestClient;
        this.retryExecutor = retryExecutor;
    }

    public void orderBottles(final BottlesOrder bottlesOrder) {

        final int orderdSum = bottlesOrdered.addAndGet(bottlesOrder.getWort());
        //TODO: synchronization
        if (orderdSum > BOTTLING_STARTED_THRESHOLD) {
            notifyBottlingStarted();

            int divid = orderdSum / BOTTLING_STARTED_THRESHOLD;
            bottlesOrdered.addAndGet(-divid * BOTTLING_STARTED_THRESHOLD);
            sleep(divid * 2000);
            int bottled = (int) ((divid * BOTTLING_STARTED_THRESHOLD) * 0.8);

            notifyBottlingFinished(bottled);
        }
    }

//    private int doBottling(final int orderdSum) {
//        int divid = orderdSum / BOTTLING_STARTED_THRESHOLD;
//        bottlesOrdered.addAndGet(-divid * BOTTLING_STARTED_THRESHOLD);
//        sleep(divid * 2000);
//        return (int) ((divid * BOTTLING_STARTED_THRESHOLD) * 0.8);
//    }

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


    private void sleep(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
