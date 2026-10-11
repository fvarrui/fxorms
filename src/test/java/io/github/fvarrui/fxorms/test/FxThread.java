package io.github.fvarrui.fxorms.test;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

/**
 * Starts the JavaFX toolkit once and runs code on the FX application thread.
 */
public final class FxThread {

	private static boolean started = false;

	private FxThread() {
	}

	public static synchronized void start() {
		if (started) {
			return;
		}
		CompletableFuture<Void> ready = new CompletableFuture<>();
		try {
			Platform.startup(() -> ready.complete(null));
		} catch (IllegalStateException e) {
			ready.complete(null); // toolkit already running
		}
		Platform.setImplicitExit(false);
		ready.join();
		started = true;
	}

	public static <T> T call(Callable<T> task) {
		start();
		CompletableFuture<T> result = new CompletableFuture<>();
		Platform.runLater(() -> {
			try {
				result.complete(task.call());
			} catch (Throwable e) {
				result.completeExceptionally(e);
			}
		});
		try {
			return result.get(10, TimeUnit.SECONDS);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			if (cause instanceof Error) {
				throw (Error) cause;
			}
			throw new RuntimeException(cause);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void run(Runnable task) {
		call(() -> {
			task.run();
			return null;
		});
	}

}
