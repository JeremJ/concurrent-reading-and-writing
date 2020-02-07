package com.streaming.thread;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.ExecutorService;

import static com.streaming.file.FileService.retrieveSplitFiles;
import static java.lang.Long.MAX_VALUE;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

@RequiredArgsConstructor
public class ThreadService {

    private final ExecutorService executorService;

    public <T extends Runnable> void runThreads(int numberOfThreads, T threadObject) {
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(new Thread(threadObject));
        }
    }

    public void waitForTasksToBeCompleted() {
        try {
            executorService.awaitTermination(MAX_VALUE, NANOSECONDS);
            retrieveSplitFiles();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdownThreadPool() {
        executorService.shutdown();
    }
}
