package com.metao.graalvm.twitter.top.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class WordCount {
    private final String key;
    private final Long value;
    private final Date date;
    private final Date date1;

}
