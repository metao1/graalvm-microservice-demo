package com.metao.graalvm.twitter.api.model;

import java.util.List;
import lombok.Data;

public @Data class HashtagsItem{
	private List<Integer> indices;
	private String text;
}