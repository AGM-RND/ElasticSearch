/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ES.web.bean;

import com.ES.main.ProductBean;
import com.ES.main.ProductResult;
import com.ES.service.ClientProvider;
import com.ES.web.document.Article;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.wildcardQuery;

import java.util.Collections;

/**
 * 
 * @author Ashish Awasthi
 */

@Stateless
@javax.ws.rs.Path("/")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Produces({ MediaType.APPLICATION_JSON })
public class ElasticSearchBean implements Serializable {

	private static final long serialVersionUID = -5035184411643496811L;
	private String tag;
	private Article selectArticle;
	private Article article = new Article();
	private List<Article> articleList = new ArrayList<Article>();

	private static final String INDEX_NAME = "products";
	private static final String TYPE_NAME = "product";

	private static Client client = null;

	private String wildCardQuery;
	private SearchRequestBuilder searchRequestBuilder;

	public ElasticSearchBean() {
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build();
		client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("gcvs4199", 9300));
		
		searchRequestBuilder = client.prepareSearch(INDEX_NAME);
		//searchRequestBuilder.setFetchSource(includes, null);
		searchRequestBuilder.setTypes(TYPE_NAME);
		searchRequestBuilder.setSearchType(SearchType.DEFAULT);
		searchRequestBuilder.setFrom(0).setSize(10);
		//prepareDocumentList();
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Article getSelectArticle() {
		return selectArticle;
	}

	public void setSelectArticle(Article selectArticle) {
		this.selectArticle = selectArticle;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public List<Article> getArticleList() {
		return articleList;
	}

	public void setArticleList(List<Article> articleList) {
		this.articleList = articleList;
	}

	public String getWildCardQuery() {
		return wildCardQuery;
	}

	public void setWildCardQuery(String wildCardQuery) {
		this.wildCardQuery = wildCardQuery;
	}

	public void articleSelect() {

		article = selectArticle;
		String[] documentTags = selectArticle.getTags();
		tag = "";

		for (int i = 0; i < documentTags.length; i++) {
			documentTags[i] = documentTags[i].replace("[", "");
			documentTags[i] = documentTags[i].replace("]", "");
			tag += documentTags[i] + ",";
		}

		tag = tag.substring(0, tag.length() - 1);
	}

	public void clearWildCardQuery() {
		wildCardQuery = "";
	}

	public void collectionSort() {

		Collections.sort(articleList, new Comparator<Article>() {

			public int compare(Article o1, Article o2) {
				return o2.getId().compareTo(o1.getId());
			}
		});
	}

	public void prepareDocumentList() {

		wildCardQuery = "";
		client.admin().indices().prepareRefresh().execute().actionGet();

		try {

			SearchResponse response = client.prepareSearch(INDEX_NAME).setTypes(TYPE_NAME).setQuery(matchAllQuery()).execute().actionGet();

			articleList.clear();

			Article temporary = null;
			String[] tags = null;

			if (response != null) {
				for (SearchHit hit : response.getHits()) {

					tags = "1,2".split(",");
					@SuppressWarnings("unchecked")
					Map<String, Object> user = (Map<String, Object>) hit.getSource().get("user");
					String imageTag = "<img src='" + user.get("profile_image_url").toString() + "'" + "</img>";
					temporary = new Article(new Long(1234), user.get("name").toString(), hit.getSource().get("text").toString(), new Date(), imageTag, tags);
					articleList.add(temporary);
				}
			}

			// collectionSort();

		} catch (IndexMissingException ex) {
			System.out.println("prepareDocumentList:IndexMissingException: " + ex.toString());
		}
	}

	@GET
	@javax.ws.rs.Path("/search/{text}/{from}/{size}/xml")
	@CrossOriginResourceSharing(allowAllOrigins = true)
	@Produces({ MediaType.APPLICATION_JSON })
	public ProductResult get1MethodXML(@PathParam("text") String text, @PathParam("from") int from, @PathParam("size") int size) {
		Calendar cal = Calendar.getInstance();
		Long time = cal.getTimeInMillis();
		ProductResult productResult = null;
		List<ProductBean> products = null;
		try {
			System.out.println("Search string:" + text);
			QueryBuilder queryBuilder = QueryBuilders.matchQuery("shortdesp", text);
					//QueryBuilders.queryStringQuery("text:" + text).analyzer("snowball");
			searchRequestBuilder.setQuery(queryBuilder);
			/*
			 * searchRequestBuilder.addHighlightedField("text") .setHighlighterPreTags("<label style='background-color:yellow;'>") .setHighlighterPostTags("</label>");
			 */
			
			SearchResponse response = searchRequestBuilder.setFrom(from).setSize(size).execute().actionGet();
			double secondsTook = response.getTook().getSecondsFrac();
			long totalRecords = response.getHits().getTotalHits();
			
			System.out.println("****Time: " + (Calendar.getInstance().getTimeInMillis() - time));
			if (response != null) {
				productResult = new ProductResult();
				products = new ArrayList<ProductBean>();
				productResult.setProducts(products);
				
				productResult.setSecondsTook(secondsTook);
				productResult.setTotalRecords(totalRecords);
				
				ObjectMapper mapper = new ObjectMapper();
				for (SearchHit hit : response.getHits()) {
					String src = hit.getSourceAsString();
					try {
						ProductBean product = mapper.readValue(src, ProductBean.class);
						products.add(product);
					} catch (JsonGenerationException e) {
						e.printStackTrace();
					} catch (JsonMappingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("products count:" + products.size());
		return productResult;
	}

	public void fullTextSearch() {

		articleList.clear();
		Article temporary = null;
		String[] tags = null;

		try {
			QueryBuilder queryBuilder = wildcardQuery("text", wildCardQuery);
			SearchRequestBuilder searchRequestBuilder = client.prepareSearch(INDEX_NAME);
			searchRequestBuilder.setTypes(TYPE_NAME);
			searchRequestBuilder.setSearchType(SearchType.DEFAULT);
			searchRequestBuilder.setQuery(queryBuilder);
			searchRequestBuilder.setFrom(0).setSize(30);
			/*
			 * searchRequestBuilder.addHighlightedField("text") .setHighlighterPreTags("<label style='background-color:yellow;'>") .setHighlighterPostTags("</label>");
			 */

			SearchResponse response = searchRequestBuilder.execute().actionGet();

			if (response != null) {

				for (SearchHit hit : response.getHits()) {

					tags = "Hi, Hello".split(","); // hit.getSource().get("tags").toString().split(",");
					@SuppressWarnings("unchecked")
					Map<String, Object> user = (Map<String, Object>) hit.getSource().get("user");
					String imageTag = "<img src='" + user.get("profile_image_url").toString() + "'" + "</img>";
					temporary = new Article(new Long(1234), user.get("name").toString(), hit.getSource().get("text").toString(),
					// Arrays.toString(hit.getHighlightFields().get("text").fragments()),
							new Date(), imageTag, tags);
					articleList.add(temporary);
				}
			}

			// collectionSort();

		} catch (IndexMissingException ex) {
			System.out.println("fullTextSearch:IndexMissingException: " + ex.toString());
		}
	}

	public static Map<String, Object> putJsonDocument(Long ID, String title, String content, Date postDate, String[] tags, String author) {

		Map<String, Object> jsonDocument = new HashMap<String, Object>();

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			String formattedDate = sdf.format(postDate);

			jsonDocument.put("id", ID);
			jsonDocument.put("title", title);
			jsonDocument.put("content", content);
			jsonDocument.put("postDate", formattedDate);
			jsonDocument.put("tags", tags);
			jsonDocument.put("author", author);
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sorry, an error has occurred", ex.toString()));
		}

		return jsonDocument;
	}

	public void saveArticle() {

		Long ID = 1l;
		try {

			CountResponse countResponse = ClientProvider.instance().getClient().prepareCount(INDEX_NAME).setQuery(termQuery("_type", TYPE_NAME)).execute().actionGet();
			ID += countResponse.getCount();

		} catch (IndexMissingException ex) {
			System.out.println("IndexMissingException: " + ex.toString());
		}

		String[] postTags = tag.split(",");

		try {

			if (null == selectArticle)
				ClientProvider.instance().getClient().prepareIndex(INDEX_NAME, TYPE_NAME, ID.toString())
						.setSource(putJsonDocument(ID, article.getTitle(), article.getContent(), article.getPostDate(), postTags, article.getAuthor())).execute().actionGet();
			else {

				Map<String, Object> updateObject = new HashMap<String, Object>();

				updateObject.put("title", selectArticle.getTitle());
				updateObject.put("content", selectArticle.getContent());
				updateObject.put("postDate", selectArticle.getPostDate());
				updateObject.put("author", selectArticle.getAuthor());
				updateObject.put("tags", postTags);

				ClientProvider.instance().getClient().prepareUpdate(INDEX_NAME, TYPE_NAME, selectArticle.getId().toString()).setScriptParams(updateObject).execute().actionGet();
			}

		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sorry, an error has occurred", ex.toString()));
		}

		prepareDocumentList();
		initArticle();

	}

	public void removeArticle() {

		try {
			ClientProvider.instance().getClient().prepareDelete(INDEX_NAME, TYPE_NAME, selectArticle.getId().toString()).execute().actionGet();
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sorry, an error has occurred", ex.toString()));
		}

		prepareDocumentList();
		initArticle();
	}

	public void initArticle() {

		tag = "";
		wildCardQuery = "";
		selectArticle = null;
		article = new Article();

	}

}
