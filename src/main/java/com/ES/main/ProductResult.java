package com.ES.main;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement(name="ProductResult")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResult {
	
	private double secondsTook = 0;
	private long totalRecords = 0;
	
	private List<ProductBean> products = new ArrayList<ProductBean>();
	
	public double getSecondsTook() {
		return secondsTook;
	}
	public void setSecondsTook(double secondsTook) {
		this.secondsTook = secondsTook;
	}
	public long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}
	public List<ProductBean> getProducts() {
		return products;
	}
	public void setProducts(List<ProductBean> products) {
		this.products = products;
	}
}
