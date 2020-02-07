package com.streaming.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static com.streaming.fixture.CollectionFixture.createListFromStringArray;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    static final byte[] BYTES = new byte[1024];
    private final String retrievedFilename = "video.mp4";
    private final List<String> splitFiles = createListFromStringArray("0.dat", "1000000.dat", "2000000.dat");

    private FileService fileService;

    @BeforeEach
    void before() {
        fileService = new FileService(null, null);
        saveFiles(BYTES, splitFiles);
    }

    @AfterEach
    void after() {
        var union = Stream.concat(splitFiles.stream(), singletonList(retrievedFilename).stream()).collect(toList());
        deleteFiles(union);
    }

    @Test
    void shouldJoinSplitFiles() {
        //given
        fileService.getSavedFileNames().addAll(splitFiles);

        //when
        fileService.retrieveSplitFiles(retrievedFilename);

        //then
        assertThat(checkIfRetrievedFileExists()).isTrue();
    }

    private boolean checkIfRetrievedFileExists() {
        File file = new File(retrievedFilename);
        return file.exists();
    }

    private void saveFiles(byte[] bytes, List<String> splitFiles) {
        splitFiles.forEach(file -> {
            try (var outputStream = new FileOutputStream(file, true)) {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void deleteFiles(List<String> files) {
        files.forEach(currentFile -> {
            new File(currentFile).deleteOnExit();
        });
    }
}