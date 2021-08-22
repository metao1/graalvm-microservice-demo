package com.metao.graalvm.twitter.api.model;

import java.util.List;
import lombok.Data;

public @Data class UserMentionsItem{
	private List<Integer> indices;
	private String screenName;
	private String idStr;
	private String name;
	private long id;
}