package com.ES.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptService.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.xml.sax.SAXException;

import com.scireum.open.xml.NodeHandler;
import com.scireum.open.xml.StructuredNode;
import com.scireum.open.xml.XMLReader;

/**
 * 
 * @author Ashish Awasthi
 */

public class JavaAPIFAST {
	public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException {
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build();
		final Client client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress("gcvs4199", 9300));

		final BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
			@Override
			public void afterBulk(long arg0, BulkRequest arg1, BulkResponse arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterBulk(long arg0, BulkRequest arg1, Throwable arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeBulk(long arg0, BulkRequest arg1) {
				// TODO Auto-generated method stub

			}
		}).setBulkActions(10000).setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB)).setFlushInterval(TimeValue.timeValueSeconds(60)).setConcurrentRequests(10).build();

		Calendar cal = Calendar.getInstance();
		long time = cal.getTimeInMillis();
		XMLReader r = new XMLReader();
		// We can add several handlers which are triggered for a given node name. The complete sub-dom of this node is then parsed and made available as a StructuredNode
		r.addHandler("ROW", new NodeHandler() {

			public void process(StructuredNode node) {
				try {
					// We can now conveniently query the sub-dom of each node using XPATH:
					System.out.println(node.queryValue("midpid"));
					System.out.println(node.queryValue("longdesp"));
					System.out.println(node.queryValue("shortdesp"));
					System.out.println(node.queryValue("skunumber"));
					
					System.out.println(node.queryValue("mid"));
					System.out.println(node.queryValue("pid"));
					System.out.println(node.queryValue("iconurl"));
					System.out.println(node.queryValue("producturl"));
					
					System.out.println(node.queryValue("category"));
					System.out.println(node.queryValue("currency"));
					System.out.println(node.queryValue("saleprice"));
					System.out.println(node.queryValue("retailprice"));

					ProductBean pb = new ProductBean();
					pb.setMidpid(node.queryValue("mid").asString() + "-" + node.queryValue("pid").asString());
					pb.setSkunumber(node.queryValue("skunumber").asString());
					pb.setShortdesp(node.queryValue("shortdesp").asString());
					pb.setLongdesp(node.queryValue("longdesp").asString());
					
					pb.setMid(node.queryValue("mid").asString());
					pb.setPid(node.queryValue("pid").asString());
					pb.setIconUrl(node.queryValue("iconurl").asString());
					pb.setProductUrl(node.queryValue("producturl").asString());
					
					pb.setCategory(node.queryValue("category").asString());
					pb.setCurrency(node.queryValue("currency").asString());
					pb.setSalePrice(node.queryValue("saleprice").asString());
					pb.setRetailPrice(node.queryValue("retailprice").asString());

					bulkProcessor.add(new IndexRequest("products", "product", pb.getMidpid()).source(putJsonDocument(pb)));

				} catch (XPathExpressionException e) {
					e.printStackTrace();
				}
			}
		});
		// Parse our little test file. Note, that this could be easily processed with a DOM-parser and only serves as showcase. Real life input files would be much bigger
		r.parse(new FileInputStream("C:/Users/ts-ashish.awasthi/Desktop/ESP/testBig-7.xml"));
		cal = Calendar.getInstance();
		time = (cal.getTimeInMillis() - time)/1000;
		System.out.println("Took time in seconds:" + time);
		bulkProcessor.close();

	}

	public static Map<String, Object> putJsonDocument(ProductBean pb) {
		Map<String, Object> jsonDocument = new HashMap<String, Object>();
		jsonDocument.put("midpid", pb.getMidpid());
		jsonDocument.put("skunumber", pb.getSkunumber());
		jsonDocument.put("shortdesp", pb.getShortdesp());
		jsonDocument.put("longdesp", pb.getLongdesp());
		
		jsonDocument.put("mid", pb.getMid());
		jsonDocument.put("pid", pb.getPid());
		jsonDocument.put("iconurl", pb.getIconUrl());
		jsonDocument.put("producturl", pb.getProductUrl());
		
		jsonDocument.put("category", pb.getCategory());
		jsonDocument.put("currency", pb.getCurrency());
		jsonDocument.put("saleprice", pb.getSalePrice());
		jsonDocument.put("retailprice", pb.getRetailPrice());
		return jsonDocument;
	}

	public static void getDocument(Client client, String index, String type, String id) {
		GetResponse getResponse = client.prepareGet(index, type, id).execute().actionGet();
		Map<String, Object> source = getResponse.getSource();
		System.out.println("------------------------------");
		System.out.println("Index: " + getResponse.getIndex());
		System.out.println("Type: " + getResponse.getType());
		System.out.println("Id: " + getResponse.getId());
		System.out.println("Version: " + getResponse.getVersion());
		System.out.println(source);
		System.out.println("------------------------------");
	}

	public static void updateDocument(Client client, String index, String type, String id, String field, String newValue) {
		Map<String, Object> updateObject = new HashMap<String, Object>();
		updateObject.put(field, newValue);
		client.prepareUpdate(index, type, id).setDoc(updateObject).execute().actionGet();
	}

	public static void updateDocument(Client client, String index, String type, String id, String field, String[] newValue) {
		String tags = "";
		for (String tag : newValue){
			tags += tag + ", ";
		}
		tags = tags.substring(0, tags.length() - 2);

		Map<String, Object> updateObject = new HashMap<String, Object>();
		updateObject.put(field, tags);

		client.prepareUpdate(index, type, id).setScript("ctx._source." + field + "+=" + field, ScriptType.INLINE).setScriptParams(updateObject).execute().actionGet();
	}

	public static void searchDocument(Client client, String index, String type, String field, String value) {

		SearchResponse response = client.prepareSearch(index).setTypes(type).setSearchType(SearchType.QUERY_AND_FETCH)
				.setQuery(QueryBuilders.termQuery(field, value)).setFrom(0)
				.setSize(60).setExplain(true).execute().actionGet();

		SearchHit[] results = response.getHits().getHits();

		System.out.println("Current results: " + results.length);
		for (SearchHit hit : results) {
			System.out.println("------------------------------");
			Map<String, Object> result = hit.getSource();
			System.out.println(result);
		}
	}

	public static void deleteDocument(Client client, String index, String type, String id) {
		DeleteResponse response = client.prepareDelete(index, type, id).execute().actionGet();
		System.out.println("Information on the deleted document:");
		System.out.println("Index: " + response.getIndex());
		System.out.println("Type: " + response.getType());
		System.out.println("Id: " + response.getId());
		System.out.println("Version: " + response.getVersion());
	}
}
