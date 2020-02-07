package com.streaming.fixture;

import java.util.Arrays;
import java.util.List;

public class CollectionFixture {

    public static List<String> createListFromStringArray(String... strings) {
        return Arrays.asList(strings);
    }
}
