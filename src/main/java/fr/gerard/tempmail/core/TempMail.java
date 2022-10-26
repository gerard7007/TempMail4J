package fr.gerard.tempmail.core;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public abstract class TempMail {

    /**
     * @return The new email address
     */
    public abstract String generateNewAddress() throws IOException;

    /**
     * @return The current email address
     */
    public abstract String emailAddress();

    /**
     * @return Fetch incoming messages
     */
    public abstract List<IMessage> fetchMessages() throws IOException;

    /**
     * @return The awaited message
     */
    public CompletableFuture<IMessage> awaitMessage(Predicate<IMessage> condition) {
        int delay = 1000;

        return CompletableFuture.supplyAsync(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    List<IMessage> messages = fetchMessages();

                    for (IMessage message : messages) {
                        if (condition.test(message)) {
                            return message;
                        }
                    }

                    Thread.sleep(delay);
                }

                throw new InterruptedException("Await message loop interrupted");
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        });
    }

}
