package pl.codepot.butelkatr.bottling.service;

import java.util.function.Consumer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created by mszarlinski on 2015-08-28.
 */
@Component
public class BottlingAsyncExecutor {

    public static final float BOTTLING_SUCCESS_RATIO = 0.8f;

    @Async
    public void bottle(final int expected, final Consumer<Integer> callback) {
        sleep(2000);
        callback.accept((int) (expected * BOTTLING_SUCCESS_RATIO));
    }

    private void sleep(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
