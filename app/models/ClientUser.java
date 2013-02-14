package models;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.bouncycastle.util.encoders.Base64;


import play.data.validation.Required;
import play.db.jpa.Model;
import twitter.UserJSONImpl;
import util.Common;

public class ClientUser extends User {


	public ClientUser(User user) {
	   super(user);
	   
	   this.id = user.twitterId;
   }


}
