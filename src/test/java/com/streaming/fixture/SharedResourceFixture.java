package com.streaming.fixture;

import com.streaming.share.SharedResource;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SharedResourceFixture {
    static final byte[] BYTES = new byte[1024];

    public static BlockingQueue<SharedResource> createSharedResources(int sharedResourcesToBeCreated, Long startedAt) {
        BlockingQueue<SharedResource> resources = new LinkedBlockingQueue<>();
        for (int i = 0; i < sharedResourcesToBeCreated; i++) {
            resources.add(new SharedResource(BYTES, i * startedAt));
        }
        return resources;
    }
}
