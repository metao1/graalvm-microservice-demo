package com.metao.graalvm.twitter.api.model;

import lombok.Data;

public @Data class RetweetedStatus{
	private ExtendedTweet extendedTweet;
	private Object inReplyToStatusIdStr;
	private Object inReplyToStatusId;
	private String createdAt;
	private Object inReplyToUserIdStr;
	private String source;
	private int retweetCount;
	private boolean retweeted;
	private GeoLocation geo;
	private String filterLevel;
	private Object inReplyToScreenName;
	private boolean isQuoteStatus;
	private String idStr;
	private Object inReplyToUserId;
	private int favoriteCount;
	private long id;
	private String text;
	private Object place;
	private String lang;
	private int quoteCount;
	private boolean favorited;
	private Object coordinates;
	private boolean truncated;
	private int replyCount;
	private Entities entities;
	private Object contributors;
	private User user;
}