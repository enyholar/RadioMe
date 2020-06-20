package com.behruz.radiome.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Radio implements Serializable {

	@SerializedName("radioUrl")
	private String radioUrl;

	@SerializedName("radioImage")
	private String radioImage;

	@SerializedName("listener")
	private int listener;

	@SerializedName("category")
	private String category;

	@SerializedName("radioName")
	private String radioName;

	@SerializedName("key")
	private String key;

	public void setRadioUrl(String radioUrl){
		this.radioUrl = radioUrl;
	}

	public String getRadioUrl(){
		return radioUrl;
	}

	public void setRadioImage(String radioImage){
		this.radioImage = radioImage;
	}

	public String getRadioImage(){
		return radioImage;
	}

	public void setListener(int listener){
		this.listener = listener;
	}

	public int getListener(){
		return listener;
	}

	public void setCategory(String category){
		this.category = category;
	}

	public String getCategory(){
		return category;
	}

	public void setRadioName(String radioName){
		this.radioName = radioName;
	}

	public String getRadioName(){
		return radioName;
	}

	public void setKey(String key){
		this.key = key;
	}

	public String getKey(){
		return key;
	}

	@Override
 	public String toString(){
		return 
			"Response{" + 
			"radioUrl = '" + radioUrl + '\'' + 
			",radioImage = '" + radioImage + '\'' + 
			",listener = '" + listener + '\'' + 
			",category = '" + category + '\'' + 
			",radioName = '" + radioName + '\'' + 
			",key = '" + key + '\'' + 
			"}";
		}
}