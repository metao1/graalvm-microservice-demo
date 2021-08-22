package com.metao.graalvm.twitter.api.model;

import java.util.List;
import lombok.Data;

public @Data class Entities{
	private List<Object> urls;
	private List<HashtagsItem> hashtags;
	private List<Object> userMentions;
	private List<Object> symbols;
}