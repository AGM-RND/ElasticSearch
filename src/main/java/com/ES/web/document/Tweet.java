/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ES.web.document;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Ashish Awasthi
*/
@XmlRootElement(name="tweet")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet {
	
    private String name;
    private User user;
    private String text;
    
    @JsonProperty("created_at")
    private String createdAt;
    //private String[] hashtag;

    public Tweet(String name, String text, String createdAt) {
    	this.name =  name;
        //this.userName = userName;
        this.text =  text;
        this.createdAt = createdAt;
    }

    public Tweet(){
    	
    }

	public String getName() {
		return name;
	}

	public void setUserId(String name) {
		this.name = name;
	}

	/*public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}*/

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/*public String[] getHashtag() {
		return hashtag;
	}

	public void setHashtag(String[] hashtag) {
		this.hashtag = hashtag;
	}*/
   
}
