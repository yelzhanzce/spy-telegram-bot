package org.telegram.spybot.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.concurrent.Executors.newScheduledThreadPool;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AsyncExecutor {

    public static <T> void sendAsync(Consumer<T> consumer, T accept) {
        var scheduler = newScheduledThreadPool(1);
        var future = new CompletableFuture<Void>();
        scheduler.schedule(() -> {
            consumer.accept(accept);
            future.complete(null);
        }, 1, TimeUnit.SECONDS);

        future.thenRun(() -> log.info("The method is executed"));
        scheduler.shutdown();
    }
}
