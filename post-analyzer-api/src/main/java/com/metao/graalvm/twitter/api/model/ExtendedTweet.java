package com.metao.graalvm.twitter.api.model;

import java.util.List;
import lombok.Data;

public @Data class ExtendedTweet{
	private Entities entities;
	private String fullText;
	private List<Integer> displayTextRange;
}