package models;

import play.data.validation.Required;

public class ConsumerKey {
	@Required String consumerKey;
	@Required String consumerSecret;
	@Required String tag;
}
