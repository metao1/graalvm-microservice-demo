package com.metao.graalvm.twitter.api.model;

import java.util.List;
import lombok.Data;

public @Data class User{
	private Object utcOffset;
	private int friendsCount;
	private String profileImageUrlHttps;
	private int listedCount;
	private String profileBackgroundImageUrl;
	private boolean defaultProfileImage;
	private int favouritesCount;
	private String description;
	private String createdAt;
	private boolean isTranslator;
	private List<Object> withheldInCountries;
	private String profileBackgroundImageUrlHttps;
	private boolean jsonMemberProtected;
	private String screenName;
	private String idStr;
	private String profileLinkColor;
	private String translatorType;
	private long id;
	private boolean geoEnabled;
	private String profileBackgroundColor;
	private Object lang;
	private String profileSidebarBorderColor;
	private String profileTextColor;
	private boolean verified;
	private String profileImageUrl;
	private Object timeZone;
	private String url;
	private boolean contributorsEnabled;
	private boolean profileBackgroundTile;
	private String profileBannerUrl;
	private int statusesCount;
	private Object followRequestSent;
	private int followersCount;
	private boolean profileUseBackgroundImage;
	private boolean defaultProfile;
	private Object following;
	private String name;
	private Object location;
	private String profileSidebarFillColor;
	private Object notifications;
}