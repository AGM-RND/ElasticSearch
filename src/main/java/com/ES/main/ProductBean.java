package com.ES.main;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement(name="product")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductBean {
	
	private String midpid;
	private String mid;
	private String pid;
	
	@JsonProperty("iconurl")
	private String iconUrl;
	
	@JsonProperty("producturl")
	private String productUrl;
	
	private String category;
	private String currency;
	
	@JsonProperty("saleprice")
	private String salePrice;
	
	@JsonProperty("retailprice")
	private String retailPrice;
	
	private String skunumber;
	private String shortdesp;
	private String longdesp;
	
	public String getMidpid() {
		return midpid;
	}
	public void setMidpid(String midpid) {
		this.midpid = midpid;
	}
	public String getSkunumber() {
		return skunumber;
	}
	public void setSkunumber(String skunumber) {
		this.skunumber = skunumber;
	}
	public String getShortdesp() {
		return shortdesp;
	}
	public void setShortdesp(String shortdesp) {
		this.shortdesp = shortdesp;
	}
	public String getLongdesp() {
		return longdesp;
	}
	public void setLongdesp(String longdesp) {
		this.longdesp = longdesp;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getProductUrl() {
		return productUrl;
	}
	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}
	public String getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(String retailPrice) {
		this.retailPrice = retailPrice;
	}
	

}
