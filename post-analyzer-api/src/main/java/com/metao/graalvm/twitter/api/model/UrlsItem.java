package com.metao.graalvm.twitter.api.model;

import java.util.List;
import lombok.Data;

public @Data class UrlsItem{
	private String displayUrl;
	private List<Integer> indices;
	private String expandedUrl;
	private String url;
}