package com.streaming;

import com.streaming.exception.InputException;
import com.streaming.file.FileService;
import com.streaming.http.StreamingResource;
import com.streaming.share.SharedResource;
import com.streaming.thread.ThreadService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Long.valueOf;
import static java.util.concurrent.Executors.newCachedThreadPool;

public class Main {

    public static AtomicInteger counter = new AtomicInteger();
    public static boolean producersFinish = false;
    private static final String RETRIEVED_FILENAME = "video.mp4";

    public static void main(String[] args) {
        int numberOfProducers = 5;
        int numberOfConsumers = 2;

        if (args.length != 3)
            throw new InputException("incorrect number of parameters");

        BlockingQueue<SharedResource> sharedQueue = new LinkedBlockingQueue<>();
        StreamingResource streamingResource = new StreamingResource(args[0], valueOf(args[1]), sharedQueue);
        FileService fileService = new FileService(sharedQueue, valueOf(args[2]));

        ExecutorService executorService = newCachedThreadPool();
        ThreadService threadService = new ThreadService(executorService);

        threadService.runThreads(numberOfProducers, streamingResource);
        threadService.runThreads(numberOfConsumers, fileService);
        threadService.shutdownThreadPool();
        threadService.waitForTasksToBeCompleted();
        fileService.retrieveSplitFiles(RETRIEVED_FILENAME);

    }
}
