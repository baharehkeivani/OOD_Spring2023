/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.libs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.junit.Test;

public class ResourcesTest {

  @Test
  public void testAsyncTryWithResource() throws Exception {

    InputStream inputStream = mock(InputStream.class);
    CompletionStage<Void> completionStage =
        Resources.asyncTryWithResource(inputStream, is -> CompletableFuture.completedFuture(null));

    completionStage.toCompletableFuture().get();
    verify(inputStream).close();
  }

  @Test
  public void testAsyncTryWithResourceExceptionInFuture() throws Exception {
    InputStream inputStream = mock(InputStream.class);
    CompletionStage<Void> completionStage =
        Resources.asyncTryWithResource(
            inputStream,
            is ->
                CompletableFuture.runAsync(
                    () -> {
                      throw new RuntimeException("test exception");
                    }));

    try {
      completionStage.toCompletableFuture().get();
    } catch (Exception ignored) {
      // print this so we can diagnose why it failed
      ignored.printStackTrace();
    }

    verify(inputStream).close();
  }

  @Test
  public void testAsyncTryWithResourceException() throws Exception {
    InputStream inputStream = mock(InputStream.class);
    try {
      CompletionStage<Void> completionStage =
          Resources.asyncTryWithResource(
              inputStream,
              is -> {
                throw new RuntimeException();
              });
      completionStage.toCompletableFuture().get();
    } catch (Exception ignored) {
    }

    verify(inputStream).close();
  }
}
