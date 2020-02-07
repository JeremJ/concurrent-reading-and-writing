package com.streaming.thread;

import com.streaming.file.FileService;
import com.streaming.http.StreamingResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;

import static com.streaming.Main.producersFinish;
import static java.lang.Long.MAX_VALUE;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ThreadServiceTest {

    private final ExecutorService executor = newCachedThreadPool();

    @Mock
    private FileService fileService;
    @Mock
    private StreamingResource streamingResource;

    @Test
    void shouldExecuteConsumerRunMethodOnce() {
        //given
        producersFinish = true;

        //when
        executor.execute(new Thread(fileService));
        shutdownAndWaitForTermination();

        //then
        verify(fileService).run();
    }

    @Test
    void shouldExecuteProducerRunMethodOnce() {

        //when
        executor.execute(new Thread(streamingResource));
        shutdownAndWaitForTermination();

        //then
        verify(streamingResource).run();
    }

    private void shutdownAndWaitForTermination() {
        try {
            executor.shutdown();
            executor.awaitTermination(MAX_VALUE, NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}