package service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import play.Logger;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

public class AppService {

	public static String elasticTest() {
		// on startup
/*		Settings settings = ImmutableSettings.settingsBuilder().put("http.enabled", "false").put("transport.tcp.port", "9300-9400")
				.put("discovery.zen.ping.multicast.enabled", "false").put("discovery.zen.ping.unicast.hosts", "localhost").build();
		Node node = nodeBuilder().client(true).settings(settings).clusterName("elasticsearch").node();
		Client client = node.client();*/

		/*
		 * Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch_deepakr").build();
		 * TransportClient transportClient = new TransportClient(settings); Client client =
		 * transportClient.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		 */
/*		String query = "{\"query\": {\"match_all\": {}},\"sort\": [{\"pricing.savings\": {\"order\": \"desc\"}}]}";
		
		QueryBuilder queryBuilder = QueryBuilders.queryString(query);
		SearchResponse response = client.prepareSearch("catalogidx").setTypes("page").setSearchType(SearchType.QUERY_AND_FETCH)
				.setQuery(queryBuilder)
				.setFrom(0).setSize(10).setExplain(true).execute().actionGet();
		System.out.println("4");
		SearchHit[] results = response.getHits().getHits();

		System.out.println("Current results: " + results.length);
		for (SearchHit hit : results) {
			System.out.println("------------------------------");
			Map<String, Object> result = hit.getSource();
			System.out.println(result);
		}

		// on shutdown

		client.close();
		return "works";*/
		
		
		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		
		String query = "{\"query\": {\"match_all\": {}},\"sort\": [{\"pricing.savings\": {\"order\": \"desc\"}}]}";
		try {
			request = new HttpPost("http://localhost:9200/testmulti/_search");
			StringEntity params =new StringEntity(query);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HttpResponse response = null;
		String responseString = null;
		try {
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
		} catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		return responseString;
	}


	public static String getItems(String searchText, String sortBy, String sortOrder, String page, String filter, String id) {

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
		if(StringUtils.isBlank(filter) || StringUtils.equals(filter, "undefined")){
			filter = "{ \"match\": { \"details.status\":  \"1\" }},{ \"prefix\": { \"title\": \""+searchText.toLowerCase()+"\"   }}";
		}else{
			filter = "{ \"match\": { \"details.status\":  \"1\" }},{ \"match\": { \""+filter+"\":  \""+id+"\" }},{ \"prefix\": { \"title\": \""+searchText.toLowerCase()+"\"   }}";
		}
		
		String query = "{\"query\": {\"bool\": {\"must\":["+filter+"]}},\"sort\": [{\""+sortBy+"\": {\"order\": \""+sortOrder+"\"}}], \"from\": "+page+",\"size\":12}";
		System.out.println("Items - "+query);
		try {
			request = new HttpPost("http://localhost:9200/testmulti/_search");
			StringEntity params =new StringEntity(query);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
		} catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		return responseString;
	}
	
	
	public static JSONArray getOnSaleNewImported(String searchText, String sortBy, String sortOrder, String filter, String id) throws JSONException {

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String isNewResponseString = null;
		String isImportedResponseString = null;
		String onSaleResponseString = null;
		JSONArray jsonArray = new JSONArray();
		if(StringUtils.isBlank(filter) || StringUtils.equals(filter, "undefined")){
			filter = "{ \"match\": { \"details.status\":  \"1\" }},{ \"prefix\": { \"title\": \""+searchText.toLowerCase()+"\"   }}";
		}else{
			filter = "{ \"match\": { \"details.status\":  \"1\" }},{ \"match\": { \""+filter+"\":  \""+id+"\" }},{ \"prefix\": { \"title\": \""+searchText.toLowerCase()+"\"   }}";
		}
		
		String isNewQuery = "{\"query\": {\"bool\": {\"must\":["+filter+"]}},\"sort\": [{\""+sortBy+"\": {\"order\": \""+sortOrder+"\"}}], "
				+ "\"aggs\": {\"isnew\": {\"terms\": {\"field\": \"details.is_new\"}}}}";
		
		String isImportedQuery = "{\"query\": {\"bool\": {\"must\":["+filter+"]}},\"sort\": [{\""+sortBy+"\": {\"order\": \""+sortOrder+"\"}}], "
				+ "\"aggs\": {\"imported\": {\"terms\": {\"field\": \"details.is_imported\"}}}}";
		
		String onSaleQuery = "{\"query\": {\"bool\": {\"must\":["+filter+"]}},\"sort\": [{\""+sortBy+"\": {\"order\": \""+sortOrder+"\"}}], "
				+ "\"aggs\": {\"onsale\": {\"terms\": {\"field\": \"pricing.on_sale\"}}}}";
		try {
			request = new HttpPost("http://localhost:9200/testmulti/_search?search_type=count");
			StringEntity params =new StringEntity(isNewQuery);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			isNewResponseString = EntityUtils.toString(entity, "UTF-8");
			
			
			request = new HttpPost("http://localhost:9200/testmulti/_search?search_type=count");
		    params =new StringEntity(isImportedQuery);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
			response = client.execute(request);
		    entity = response.getEntity();
		    isImportedResponseString = EntityUtils.toString(entity, "UTF-8");
			
		    
			request = new HttpPost("http://localhost:9200/testmulti/_search?search_type=count");
		    params =new StringEntity(onSaleQuery);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
			response = client.execute(request);
		    entity = response.getEntity();
		    onSaleResponseString = EntityUtils.toString(entity, "UTF-8");
		    
		    
		    jsonArray = getCounts(isNewResponseString, isImportedResponseString, onSaleResponseString);	
		    
		    
		} catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		return jsonArray;
	}

	private static JSONArray getCounts(String isNewResponseString, String isImportedResponseString, String onSaleResponseString) throws JSONException {
		
		JsonNode isNewNode = Json.parse(isNewResponseString).findPath("aggregations").findPath("isnew").findPath("buckets");;
		JsonNode isImportedNode = Json.parse(isImportedResponseString).findPath("aggregations").findPath("imported").findPath("buckets");
		JsonNode OnSaleNode = Json.parse(onSaleResponseString).findPath("aggregations").findPath("onsale").findPath("buckets");

		JSONArray aggrArray = new JSONArray();
		if(isNewNode.isArray()){
			for(JsonNode node : isNewNode){
				String count = "0";
				boolean isNew = node.findPath("key").asBoolean();
				if(isNew){
				count = node.findPath("doc_count").asText();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", "New");
				jsonObject.put("count", count);
				aggrArray.put(jsonObject);
				}

			}
		}
		
		if(isImportedNode.isArray()){
			for(JsonNode node : isImportedNode){
				String count = "0";
				String isImported = node.findPath("key").asText();
				if(isImported.equals("T")){
				count = node.findPath("doc_count").asText();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", "Imported");
				jsonObject.put("count", count);
				aggrArray.put(jsonObject);
				}
			}
		}
		
		if(OnSaleNode.isArray()){
			for(JsonNode node : OnSaleNode){
				String count = "0";
				boolean onSale = node.findPath("key").asBoolean();
				if(onSale){
				count = node.findPath("doc_count").asText();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", "On Sale");
				jsonObject.put("count", count);
				aggrArray.put(jsonObject);
				}
			}
		}
		
		return aggrArray;
	}

	public static String getBrands(String searchText, String sortBy, String sortOrder, String filter, String id) {

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
		
		if(StringUtils.isBlank(filter) || StringUtils.equals(filter, "undefined")){
			filter = "{ \"match\": { \"details.status\":  \"1\" }},{ \"prefix\": { \"title\": \""+searchText.toLowerCase()+"\"   }}";
		}else{
			filter = "{ \"match\": { \"details.status\":  \"1\" }},{ \"match\": { \""+filter+"\":  \""+id+"\" }},{ \"prefix\": { \"title\": \""+searchText.toLowerCase()+"\"   }}";
		}
		
		String query = "{\"query\": {\"bool\": {\"must\":["+filter+"]}},\"sort\": [{\""+sortBy+"\": {\"order\": \""+sortOrder+"\"}}], "
				+ "\"aggs\": {\"brands\": {\"terms\": {\"field\": \"filters.brand\",\"size\" : 1000}}}}";
		System.out.println("Brands - "+query);
		try {
			request = new HttpPost("http://localhost:9200/testmulti/_search?search_type=count");
			StringEntity params =new StringEntity(query);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
		} catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		return responseString;
	}
	
	public static String getCategories(String searchText, String sortBy, String sortOrder, String filter, String id) {

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
		
		if(StringUtils.isBlank(filter) || StringUtils.equals(filter, "undefined")){
			filter = "{ \"match\": { \"details.status\":  \"1\" }},{ \"prefix\": { \"title\": \""+searchText.toLowerCase()+"\"   }}";
		}else{
			filter = "{ \"match\": { \"details.status\":  \"1\" }},{ \"match\": { \""+filter+"\":  \""+id+"\" }},{ \"prefix\": { \"title\": \""+searchText.toLowerCase()+"\"   }}";
		}
		
		String query = "{\"query\": {\"bool\": {\"must\":["+filter+"]}},\"sort\": [{\""+sortBy+"\": {\"order\": \""+sortOrder+"\"}}], "
				+ "\"aggs\": {\"category\": {\"terms\": {\"field\": \"categories\",\"size\" : 100}}}}";
		
		System.out.println("Category - "+query);
		try {
			request = new HttpPost("http://localhost:9200/testmulti/_search?search_type=count");
			StringEntity params =new StringEntity(query);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
		} catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		return responseString;
	}
	
	
	public static String getProductTypes(String searchText, String sortBy, String sortOrder, String filter, String id) {

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
		
		if(StringUtils.isBlank(filter) || StringUtils.equals(filter, "undefined")){
			filter = "{ \"match\": { \"details.status\":  \"1\" }},{ \"prefix\": { \"title\": \""+searchText.toLowerCase()+"\"   }}";
		}else{
			filter = "{ \"match\": { \"details.status\":  \"1\" }},{ \"match\": { \""+filter+"\":  \""+id+"\" }},{ \"prefix\": { \"title\": \""+searchText.toLowerCase()+"\"   }}";
		}
		
		String query = "{\"query\": {\"bool\": {\"must\":["+filter+"]}},\"sort\": [{\""+sortBy+"\": {\"order\": \""+sortOrder+"\"}}], "
				+ "\"aggs\": {\"types\": {\"terms\": {\"field\": \"types\",\"size\" : 100}}}}";
		try {
			request = new HttpPost("http://localhost:9200/testmulti/_search?search_type=count");
			StringEntity params =new StringEntity(query);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
		} catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		return responseString;
	}
	
	public static String getBrand(String id) {

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
		
		String query = "{\"query\" : {\"term\" : { \"id\" : "+id+" }}}";
		try {
			request = new HttpPost("http://localhost:9200/brandidx/_search");
			StringEntity params =new StringEntity(query);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
		} catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		return responseString;
	}
	
	
	public static String getProductType(String id) {

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
		
		String query = "{\"query\" : {\"term\" : { \"id\" : "+id+" }}}";
		try {
			request = new HttpPost("http://localhost:9200/typeidx/_search");
			StringEntity params =new StringEntity(query);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
		} catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		return responseString;
	}

	public static String getCategory(String id) {
		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
		String category = "";
		String query = "{\"query\" : {\"term\" : { \"id\" : "+id+" }}}";
		try {
			request = new HttpPost("http://localhost:9200/categoryindex/_search");
			StringEntity params =new StringEntity(query);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
			
			JsonNode categoryNode = Json.parse(responseString);
		    category = categoryNode.findPath("hits").findPath("hits").findPath("_source").findPath("title").asText();
		} catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		return category;
	}

	public static JSONObject getSubCategory(String id) throws JSONException {
		
	@SuppressWarnings("resource")
	HttpClient client = new DefaultHttpClient();
	HttpPost request = null;
	HttpResponse response = null;
	String responseString = null;
	JSONObject jsonObject = new JSONObject();
	
	String query = "{\"query\" : {\"term\" : { \"sub_categories.id\" : "+id+" }}}";
	try {
		request = new HttpPost("http://localhost:9200/categoryindex/_search");
		StringEntity params =new StringEntity(query);
        request.addHeader("content-type", "application/x-www-form-urlencoded");
        request.setEntity(params);
		response = client.execute(request);
		HttpEntity entity = response.getEntity();
		responseString = EntityUtils.toString(entity, "UTF-8");
		
		JsonNode subCategoryNode = Json.parse(responseString);
	    JsonNode categoryNode = subCategoryNode.findPath("hits").findPath("hits").findPath("_source").findPath("sub_categories");
	    String category = subCategoryNode.findPath("hits").findPath("hits").findPath("_source").findPath("title").asText();
	    String categoryid = subCategoryNode.findPath("hits").findPath("hits").findPath("_source").findPath("id").asText();

		if(categoryNode.isArray()){
			for(JsonNode key : categoryNode){
				String title = key.findPath("title").asText();
				String subid = key.findPath("id").asText();
				if(id.equals(key.findPath("id").asText())){
					jsonObject.put("subid", subid);
					jsonObject.put("subcategory", title);
					jsonObject.put("category", category);
					jsonObject.put("categoryid", categoryid);

				}
			}
		}
	    
	    
	} catch (ParseException | IOException e) {
		Logger.error("IOException/ParseException - " + e.getMessage());
		e.printStackTrace();
	}
	return jsonObject;
	}


	public static JSONArray getLookup(String searchText) throws JSONException {
		
		JSONArray jsonArray = new JSONArray();

		List<String> keywords = getKeywords(searchText, false);
		if(keywords.isEmpty()){
			keywords = getKeywords(searchText, true);
			Integer count = 0;
			for(String keyword: keywords){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", "keyword");
				jsonObject.put("display", keyword+"???");
				jsonObject.put("id", count.toString());
				jsonObject.put("title", searchText + " "+ jsonObject);
				jsonObject.put("searchText", keyword);
				jsonObject.put("breadcrumb", "");
				jsonArray.put(jsonObject);
				count = count + 1;
			}
		}else{
			Integer count = 0;
			for(String keyword: keywords){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", "keyword");
				jsonObject.put("display", keyword);
				jsonObject.put("id", count.toString());
				jsonObject.put("title", keyword);
				jsonObject.put("searchText", keyword);
				jsonObject.put("breadcrumb", "");
				jsonArray.put(jsonObject);
				count = count + 1;
			}
		}
		JSONArray jsonArrayCat = getCategories(keywords);
		JSONArray jsonArrayProd = getProducts(searchText);
		
		for(int i = 0; i < jsonArrayCat.length(); i++){
			JSONObject jsonObject = new JSONObject();
			JSONObject object = jsonArrayCat.getJSONObject(i);
			jsonObject.put("type", "categories");
			jsonObject.put("display", object.get("keyword") + " in <b style=\"color:#694F4F\">" + object.get("category")+"</b>");
			jsonObject.put("id", object.get("categoryid"));
			jsonObject.put("title", searchText + " in <b>" + object.get("category")+"</b> "+ object.get("keyword"));
			jsonObject.put("searchText", object.get("keyword"));
			jsonObject.put("breadcrumb", object.get("category"));
			jsonArray.put(jsonObject);

		}
		
		for(int i = 0; i < jsonArrayProd.length(); i++){
			JSONObject jsonObject = new JSONObject();
			JSONObject object = jsonArrayProd.getJSONObject(i);
			jsonObject.put("type", "product");
			if(StringUtils.equalsIgnoreCase(object.get("onsale").toString(),"true")){
				jsonObject.put("display", "<img src =\""+object.get("img")+ "\" height=40 />&nbsp;&nbsp; " +object.get("title")+ " <b style=\"color:#ee4054\">" +object.get("sellingprice")+ "</b> <strike>" + object.get("strikedoutprice")+"</strike>");
			}else{
				jsonObject.put("display", "<img src =\""+object.get("img")+ "\" height=40 />&nbsp;&nbsp; " +object.get("title")+ " " +object.get("sellingprice")+ " <strike>" + object.get("strikedoutprice")+"</strike>");
			}

			jsonObject.put("id", object.get("sku"));
			jsonObject.put("title", object.get("title"));
			jsonObject.put("searchText", object.get("title"));
			jsonObject.put("breadcrumb", "");
			jsonArray.put(jsonObject);

		}
		
		return jsonArray;
	}


	private static JSONArray getProducts(String searchText) throws JSONException {

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
		JSONArray jsonArray = new JSONArray();
		
		String query = "{\"query\": {\"bool\": {\"must\":[{ \"match\": {\"details.status\":  \"1\" }},{ \"prefix\": { \"title\": \""+searchText.toLowerCase()+"\"}}]}},\"sort\": [{\"pricing.on_sale\": {\"order\": \"desc\"}}], \"from\": 0,\"size\":3}";

		System.out.println(query);
		try {
			request = new HttpPost("http://localhost:9200/testmulti/_search");
			StringEntity params =new StringEntity(query);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
			
			JsonNode jsonNode = Json.parse(responseString);
			JsonNode node = jsonNode.findPath("hits").findPath("hits");
			if (node.isArray()) {
				for (JsonNode source : node) {
					String sku = source.findPath("_source").findPath("sku").asText();
					String title = source.findPath("_source").findPath("title").asText();
					String img = source.findPath("_source").findPath("img").findPath("name").asText();
					boolean onSale = source.findPath("_source").findPath("pricing").findPath("on_sale").asBoolean();
					String price = "$" + source.findPath("_source").findPath("pricing").findPath("price").asText();
					String promoPrice = "$" + source.findPath("_source").findPath("pricing").findPath("promo_price").asText();
					String strikedOutPrice = "";
					if (onSale) {
						strikedOutPrice = price;
						price = promoPrice;
					}

					if (img.charAt(0) != '/') {
						img = "https://s3-ap-southeast-1.amazonaws.com/media.redmart.com/newmedia/150x/" + img;
					} else {
						img = "https://s3-ap-southeast-1.amazonaws.com/media.redmart.com/newmedia/150x" + img;
					}
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("sku", sku);
					jsonObject.put("title", title);
					jsonObject.put("img", img);
					jsonObject.put("strikedoutprice", strikedOutPrice);
					jsonObject.put("sellingprice", price);
					jsonObject.put("onsale", onSale);
					jsonArray.put(jsonObject);
				}
			}
		} catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		return jsonArray;
			
	}


/*	private static JSONArray getSubCategories(String keyword) {
		System.out.println("keyword - "+keyword);
		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
	    JSONArray jsonArray = new JSONArray();

		String query = "{\"query\": {\"query_string\": {\"fields\": [\"sub_categories.types.name\"],\"query\": \""+keyword+"\" }}}";
		System.out.println(query);
		try {
			request = new HttpPost("http://localhost:9200/categoryindex/_search");
			StringEntity params =new StringEntity(query);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
			
			JsonNode jsonNode = Json.parse(responseString);
		    JsonNode keywordNode = jsonNode.findPath("hits").findPath("hits");
		    System.out.println("keywordNode - "+keywordNode);
			if (keywordNode.isArray()) {
				for (JsonNode source : keywordNode) {
					JsonNode subCategoryNode = source.findPath("_source").findPath("sub_categories");
					System.out.println("subCategoryNode - "+subCategoryNode);
					if (subCategoryNode.isArray()) {
						for (JsonNode subCategory : subCategoryNode) {
							System.out.println(subCategory.findPath("title"));
						}
					}

				}
			}
		} catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		return jsonArray;		
		
	}
*/

	private static JSONArray getCategories(List<String> keyword) throws JSONException {
	    JSONArray jsonArray = new JSONArray();
		int count = 0;
		for(String searchText: keyword){
			
		String responseString = getCategories(searchText, "pricing.on_sale", "desc", "", "");
		JsonNode jsonNode = Json.parse(responseString);

		JsonNode aggrNode = jsonNode.findPath("aggregations").findPath("category").findPath("buckets");
		
		if(aggrNode.isArray()){
			for(JsonNode key : aggrNode){
				String category = AppService.getCategory(key.findPath("key").asText());
				if(StringUtils.isNotBlank(category)){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("category", category);
					jsonObject.put("categoryid", key.findPath("key").asText());
					jsonObject.put("keyword", searchText);
					jsonArray.put(jsonObject);			
					count = count + 1;
					if(count > 3){
						return jsonArray;
					}
				}
			}
		}
		}
		
		
		return jsonArray;
		
	/*	@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
	    JSONArray jsonArray = new JSONArray();

		String query = "{\"query\": {\"query_string\": {\"fields\": [\"types.name\"],\"query\": \""+keyword+"*\" }}}";
		System.out.println("category "+query);
		try {
			request = new HttpPost("http://localhost:9200/categoryindex/_search");
			StringEntity params =new StringEntity(query);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
			int count = 0;
			JsonNode jsonNode = Json.parse(responseString);
		    JsonNode keywordNode = jsonNode.findPath("hits").findPath("hits");
			if (keywordNode.isArray()) {
				for (JsonNode source : keywordNode) {
					String mainCategory = source.findPath("_source").findPath("title").toString();
					String mainCategoryId = source.findPath("_source").findPath("id").toString();
					String typeName = "";
					JsonNode typeNode = source.findPath("_source").findPath("types");
					if (typeNode.isArray()) {
						for (JsonNode type : typeNode) {
							System.out.println(type+"---"+type.findPath("name").asText()+"--"+keyword+"---"+type.findPath("name").asText().startsWith(keyword));
							if(type.findPath("name").asText().toLowerCase().startsWith(keyword)){
								 typeName = type.findPath("name").asText();
									System.out.println("true "+typeName);

							}
						}
					}	
					count = count + 1;
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("category", mainCategory);
					jsonObject.put("categoryid", mainCategoryId);
					jsonObject.put("keyword", typeName);
					jsonArray.put(jsonObject);
					if(count == 3){
						return jsonArray;
					}
				}
			}
		} catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		return jsonArray;		*/
	}


	private static List<String> getKeywords(String searchText, boolean empty) {
		
		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
		
		List<String> keywords = new ArrayList<String>();
		Set<String> temp = null;
		
		String query = "{\"fields\" : [\"title\"], \"query\": {\"query_string\": {\"fields\": [\"title\"], \"query\": \""+searchText+"*\"}},"+ 
				"\"highlight\": {\"fields\": {\"title\": {}}}}";
		
		if(empty){
			query = "{\"query\": {\"match\": {\"title\": {\"query\": \""+searchText+"\",\"fuzziness\": 1,\"prefix_length\": 1}}},\"highlight\":{\"fields\":{\"title\":{}}}}}";
		}	
		System.out.println(query);
		try {
			request = new HttpPost("http://localhost:9200/testmulti/_search");
			StringEntity params =new StringEntity(query);
	        request.addHeader("content-type", "application/x-www-form-urlencoded");
	        request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
			
			JsonNode jsonNode = Json.parse(responseString);
		    JsonNode keywordNode = jsonNode.findPath("hits").findPath("hits");
		    
			if (keywordNode.isArray()) {
				for (JsonNode source : keywordNode) {
					String[] words = StringUtils.substringsBetween(source.findPath("highlight").findPath("title").toString(), "<em>", "</em>");
					keywords.addAll(Arrays.asList(words));
				}
			}
			temp = new LinkedHashSet<String>(keywords);
			keywords = new ArrayList<String>(temp);
		} catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		if(keywords.size() > 5){
			return keywords.subList(0, 5);
		}
		return keywords;		
		
	}

}
