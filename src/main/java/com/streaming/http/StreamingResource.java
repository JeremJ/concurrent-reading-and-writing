package com.streaming.http;

import com.streaming.exception.HttpException;
import com.streaming.share.SharedResource;
import lombok.Getter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import static com.streaming.Main.counter;
import static com.streaming.Main.producersFinish;
import static java.lang.Long.valueOf;

@Getter
public class StreamingResource implements Runnable {

    private final BlockingQueue<SharedResource> resourceQueue;
    private final URL resourceUrl;
    private final Long chunkSize;
    private Long resourceLength;

    public StreamingResource(String resourceUrl, Long chunkSize, BlockingQueue<SharedResource> resourceQueue) {
        this.resourceUrl = getUrlFromString(resourceUrl);
        this.chunkSize = chunkSize;
        this.resourceQueue = resourceQueue;
    }

    private byte[] getRequestOutput(Long startAt) {
        var resourceLength = Optional.ofNullable(this.resourceLength);
        if (resourceLength.isPresent() && startAt > resourceLength.get()) {
            return null;
        } else if (resourceLength.isPresent() && (resourceLength.get() - startAt) < chunkSize) {
            return getRequestedChunk(startAt, resourceLength.get() - 1);
        } else {
            return getRequestedChunk(startAt, startAt + chunkSize - 1);
        }

    }

    private byte[] getRequestedChunk(Long startedFrom, Long endedAt) {
        var connection = setUpConnectionForGetMethod(openConnection());

        connection.setRequestProperty("Range", String.format("bytes=%s" + "-%s", startedFrom, endedAt));
        System.out.println(String.format("Request: %s, Thread: %s bytes=%s" + "-%s", counter.get(), Thread.currentThread().getName(), startedFrom, endedAt));
        var content = getServerResponse(connection);
        setResourceLength(connection);
        closeConnection(connection);

        return content;
    }

    private byte[] getServerResponse(HttpURLConnection connection) {
        try {
            return connection.getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new HttpException("Error occured during reading server response", e);
        }
    }

    private void setResourceLength(HttpURLConnection connection) {
        if (resourceLength == null) {
            var headerValue = connection.getHeaderField("Content-Range");
            resourceLength = valueOf(headerValue.substring(headerValue.lastIndexOf("/") + 1));
        }
    }

    private HttpURLConnection setUpConnectionForGetMethod(HttpURLConnection connection) {
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new HttpException("Problem with http protocol occured", e);
        }
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        return connection;
    }

    private HttpURLConnection openConnection() {
        try {
            return (HttpURLConnection) resourceUrl.openConnection();
        } catch (IOException e) {
            throw new HttpException("Connection cannot be open", e);
        }
    }

    private void closeConnection(HttpURLConnection connection) {
        connection.disconnect();
    }

    private URL getUrlFromString(String url) {
        try {
            return new URL(url);
        } catch (
                MalformedURLException e) {
            throw new HttpException("incorrect url", e);
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            while (true) {
                var startedAt = chunkSize * counter.getAndIncrement();
                var content = getRequestOutput(startedAt);
                if (content == null) {
                    break;
                }
                resourceQueue.add(new SharedResource(content, startedAt));
                System.out.println(String.format("Thread %s , startedAt: %s", Thread.currentThread().getName(), startedAt));
            }
            producersFinish = true;
        }
    }
}
