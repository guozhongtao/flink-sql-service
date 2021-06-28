package org.frank.flinksql.service.util;

import java.util.concurrent.CompletableFuture;

public class FutureUtils {

    public static <T> CompletableFuture<T> completedExceptionally(Throwable cause) {
        CompletableFuture<T> result = new CompletableFuture<>();
        result.completeExceptionally(cause);

        return result;
    }

}