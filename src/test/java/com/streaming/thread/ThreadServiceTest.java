package com.streaming.thread;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import static com.streaming.Main.producersFinish;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThreadServiceTest {

    @Mock
    private ExecutorService executorService;

    @InjectMocks
    private ThreadService threadService;

    @Test
    void shouldExecuteThreadRunMethodTwice() {
        //given
        producersFinish = true;

        //when
        threadService.runThreads(2, any());

        //then
        verify(executorService, times(2)).execute(any(Runnable.class));
    }

    @Test
    void shouldThrownNullPointerExceptionWhenRunMethodFailed() throws InterruptedException {
        //given
        doThrow(RejectedExecutionException.class).when(executorService).execute(any());

        //when
        Throwable throwable = catchThrowable(() -> threadService.runThreads(1, any()));

        //then
        assertThat(throwable)
                .isInstanceOf(RejectedExecutionException.class);
    }


}