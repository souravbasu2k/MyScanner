package com.example.dexterhacks;

public class NotificationItems 
{
	String id, title, formnumber, description, pubDate, type, isAvailableDB, link;
	public NotificationItems()
	{
		
	}
	public NotificationItems(String title, String formnumber, String description, String pubDate, String type, String isAvailableDB)
	{
		this.title=title;
		this.formnumber=formnumber;
		this.description=description;
		this.pubDate=pubDate;
		this.type=type;
		this.isAvailableDB=isAvailableDB;
	}
	
	public void setTitle(String title)
	{
		this.title=title;
	}	
	public String getTitle()
	{
		return this.title;
	}
	
	public void setFormNumber(String formnumber)
	{
		this.formnumber=formnumber;
	}	
	public String getFormNumber()
	{
		return this.formnumber;
	}
	
	public void setDescription(String description)
	{
		this.description=description;
	}	
	public String getDescription()
	{
		return this.description;
	}
	
	public void setPubDate(String pubDate)
	{
		this.pubDate=pubDate;
	}	
	public String getPubDate()
	{
		return this.pubDate;
	}
	
	public void setType(String type)
	{
		this.type=type;
	}	
	public String getType()
	{
		return this.type;
	}
	
	public void setIsAvailableDB(String isAvailableDB)
	{
		this.isAvailableDB=isAvailableDB;
	}	
	public String getIsAvailableDB()
	{
		return this.isAvailableDB;
	}
	
	public void setLink(String link) {
		this.link = link;		
	}
	
	public String getLink()
	{
		return this.link;
	}
	
	public void setId(String id) {
		this.id = id;		
	}
	
	public String getId() {
		return this.id;
	}
	
}
