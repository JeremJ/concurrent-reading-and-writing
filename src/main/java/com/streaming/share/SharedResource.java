package com.streaming.share;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SharedResource {
    private byte[] content;
    private Long startedAt;
}
