package com.streaming;

import com.streaming.file.FileService;
import com.streaming.http.StreamingResource;
import com.streaming.share.SharedResource;
import com.streaming.thread.ThreadService;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.streaming.file.FileService.retrieveSplitFiles;
import static java.util.concurrent.Executors.newCachedThreadPool;

public class Main {

    public static AtomicInteger counter = new AtomicInteger();
    public static boolean producersFinish = false;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Provide resource Url");
        String resourceUrl = scanner.nextLine();
        System.out.println("Provide chunk size");
        long chunkSize = scanner.nextLong();
        System.out.println("Provide file size");
        long fileSize = scanner.nextLong();

        int numberOfProducers = 5;
        int numberOfConsumers = 2;

        BlockingQueue<SharedResource> sharedQueue = new LinkedBlockingQueue<>();
        StreamingResource streamingResource = new StreamingResource(resourceUrl, chunkSize, sharedQueue);
        FileService fileService = new FileService(sharedQueue, fileSize);

        ExecutorService executorService = newCachedThreadPool();
        ThreadService threadService = new ThreadService(executorService);

        threadService.runThreads(numberOfProducers, streamingResource);
        threadService.runThreads(numberOfConsumers, fileService);
        threadService.shutdownThreadPool();
        threadService.waitForTasksToBeCompleted();
        retrieveSplitFiles();

    }
}
