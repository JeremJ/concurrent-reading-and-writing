package com.streaming.file;

import com.streaming.share.SharedResource;
import lombok.RequiredArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;

import static com.streaming.Main.producersFinish;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

@RequiredArgsConstructor
public class FileService implements Runnable {

    private static final String FILENAME_PATTERN = "%s.dat";
    private static final String RETRIEVED_FILENAME = "video.mp4";
    public static Set<String> savedFileNames = new TreeSet<>();

    private final BlockingQueue<SharedResource> resourceQueue;
    private final Long fileSize;

    public static void writeBytesToFile(byte[] bytes) {
        try (var outputStream = new FileOutputStream(RETRIEVED_FILENAME, true)) {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void retrieveSplitFiles() {
        savedFileNames.forEach(currentFile -> {
            try {
                writeBytesToFile(readAllBytes(get(currentFile)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void writeToFileAndStoreFileName(String fileName, byte[] content) {
        try {
            FileOutputStream outputStream = new FileOutputStream(fileName, true);
            outputStream.write(content);
            savedFileNames.add(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            while (true) {
                try {
                    if (resourceQueue.isEmpty() && producersFinish) {
                        break;
                    }
                    var sharedResource = resourceQueue.take();
                    System.out.println(format("Reading: Thread %s, startedAt: %s", Thread.currentThread().getName(), sharedResource.getStartedAt()));
                    var fileName = Math.floorDiv(sharedResource.getStartedAt(), fileSize) * fileSize;
                    writeToFileAndStoreFileName(format(FILENAME_PATTERN, fileName), sharedResource.getContent());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
