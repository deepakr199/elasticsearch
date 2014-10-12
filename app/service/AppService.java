package service;

import java.io.IOException;
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

	public static String getItems(String searchText, String sortBy, String sortOrder, String page, String categoryType, String filter) {

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
		String queryText = "";
		String filterText = "";
		String[] facets = categoryType.split("::");
		String[] filters = filter.split(":");
		
		if(StringUtils.isNotBlank(searchText)){
			queryText = ",\"must\": {\"multi_match\" : {\"query\": \""+searchText+"\",\"fields\": [\"title\",\"categories.category_name\",\"types.product_type\"]}}";
		}
		for(int i = 0; i < facets.length; i++){
			if (StringUtils.isNotBlank(facets[i])) {
				String[] eachFacet = facets[i].split(":");
				queryText = queryText + ",\"must\":{ \"match\": { \"" + eachFacet[0] + "\":  \"" + eachFacet[1] + "\" }}";
			}
		}
		
		for(int i = 0; i < filters.length; i++){
			if (StringUtils.isNotBlank(filters[i])) {
				filterText = filterText + ",\"" + filters[i] + "\"";
			}
		}
		if(StringUtils.isNotBlank(filter)){
			queryText = queryText + ",\"must\": {\"multi_match\" : {\"query\": \"1\",\"fields\": ["+filterText.replaceFirst(",","")+"]}}";
		}

		String query = null;
		if (sortBy.equals("pricing.price")) {
			query = "{\"query\": {\"bool\": {\"must\": {\"match\": {\"details.status\": \"1\"  }}" + queryText
					+ "}},\"sort\": [{\"_script\":{\"lang\":\"groovy\",\"script\" : \"doc['pricing.promo_price'].value == 0 ? "
					+ "doc['pricing.price'].value : doc['pricing.promo_price'].value\",\"type\" : \"number\",\"order\" : \"" + sortOrder
					+ "\"}},\"_score\"], \"from\": " + page + ",\"size\":12}";
		} else if (sortBy.equals("raw_title")) {
			query = "{\"query\": {\"bool\": {\"must\": {\"match\": {\"details.status\": \"1\"  }}" + queryText
					+ "}},\"sort\": [{\"_script\":{\"lang\":\"groovy\",\"script\" : \"doc['raw_title'].value.toLowerCase()\","
					+ "\"type\" : \"string\",\"order\" : \"" + sortOrder + "\"}},\"_score\"], \"from\": " + page + ",\"size\":12}";
		} else {
			query = "{\"query\": {\"bool\": {\"must\": {\"match\": {\"details.status\": \"1\"  }}" + queryText+"}},\"sort\": [{\"" + sortBy + "\": {\"order\": \"" + sortOrder
					+ "\"}},\"_score\"], \"from\": " + page + ",\"size\":12}";
		}

		Logger.info("Items - " + query);
		try {
			request = new HttpPost("http://localhost:9200/prodcatalogidx/_search");
			StringEntity params = new StringEntity(query);
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

	public static JSONArray getOnSaleNewImported(String searchText, String sortBy, String sortOrder, String categoryType, String filter)
			throws JSONException {

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String isNewResponseString = null;
		String isImportedResponseString = null;
		String onSaleResponseString = null;
		JSONArray jsonArray = new JSONArray();
		
		String queryText = "";
		String filterText = "";
		String[] facets = categoryType.split("::");
		String[] filters = filter.split(":");
		
		if(StringUtils.isNotBlank(searchText)){
			queryText = ",\"must\": {\"multi_match\" : {\"query\": \""+searchText+"\",\"fields\": [\"title\",\"categories.category_name\",\"types.product_type\"]}}";
		}
		for(int i = 0; i < facets.length; i++){
			if (StringUtils.isNotBlank(facets[i])) {
				String[] eachFacet = facets[i].split(":");
				queryText = queryText + ",\"must\":{ \"match\": { \"" + eachFacet[0] + "\":  \"" + eachFacet[1] + "\" }}";
			}
		}
		
/*		for(int i = 0; i < filters.length; i++){
			if (StringUtils.isNotBlank(filters[i])) {
				filterText = filterText + ",\"" + filters[i] + "\"";
			}
		}
		if(StringUtils.isNotBlank(filter)){
			queryText = queryText + ",\"must\": {\"multi_match\" : {\"query\": \"1\",\"fields\": ["+filterText.replaceFirst(",","")+"]}}";
		}*/

		String isNewQuery = "{\"query\": {\"bool\": {\"must\": {\"match\": {\"details.status\": \"1\"  }}" + queryText+"}},\"sort\": [{\"" + sortBy + "\": {\"order\": \"" + sortOrder
				+ "\"}}], " + "\"aggs\": {\"isnew\": {\"terms\": {\"field\": \"details.is_new\"}}}}";

		String isImportedQuery = "{\"query\": {\"bool\": {\"must\": {\"match\": {\"details.status\": \"1\"  }}" + queryText+"}},\"sort\": [{\"" + sortBy + "\": {\"order\": \"" + sortOrder
				+ "\"}}], " + "\"aggs\": {\"imported\": {\"terms\": {\"field\": \"details.is_imported\"}}}}";

		String onSaleQuery = "{\"query\": {\"bool\": {\"must\": {\"match\": {\"details.status\": \"1\"  }}" + queryText+"}},\"sort\": [{\"" + sortBy + "\": {\"order\": \"" + sortOrder
				+ "\"}}], " + "\"aggs\": {\"onsale\": {\"terms\": {\"field\": \"pricing.on_sale\"}}}}";
		
		Logger.info("new query "+isNewQuery);
		Logger.info("imported query "+isImportedQuery);
		Logger.info("on sale query "+onSaleQuery);
		
		try {
			request = new HttpPost("http://localhost:9200/prodcatalogidx/_search?search_type=count");
			StringEntity params = new StringEntity(isNewQuery);
			request.addHeader("content-type", "application/x-www-form-urlencoded");
			request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			isNewResponseString = EntityUtils.toString(entity, "UTF-8");

			request = new HttpPost("http://localhost:9200/prodcatalogidx/_search?search_type=count");
			params = new StringEntity(isImportedQuery);
			request.addHeader("content-type", "application/x-www-form-urlencoded");
			request.setEntity(params);
			response = client.execute(request);
			entity = response.getEntity();
			isImportedResponseString = EntityUtils.toString(entity, "UTF-8");

			request = new HttpPost("http://localhost:9200/prodcatalogidx/_search?search_type=count");
			params = new StringEntity(onSaleQuery);
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

	private static JSONArray getCounts(String isNewResponseString, String isImportedResponseString, String onSaleResponseString)
			throws JSONException {

		JsonNode isNewNode = Json.parse(isNewResponseString).get("aggregations").get("isnew").get("buckets");
		;
		JsonNode isImportedNode = Json.parse(isImportedResponseString).get("aggregations").get("imported").get("buckets");
		JsonNode OnSaleNode = Json.parse(onSaleResponseString).get("aggregations").get("onsale").get("buckets");

		JSONArray aggrArray = new JSONArray();
		if (isNewNode.isArray()) {
			for (JsonNode node : isNewNode) {
				String count = "0";
				boolean isNew = node.get("key").asBoolean();
				if (isNew) {
					count = node.get("doc_count").asText();
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("name", "New");
					jsonObject.put("count", count);
					aggrArray.put(jsonObject);
				} else {
					if (!isNew && isNewNode.size() == 1) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("name", "New");
						jsonObject.put("count", count);
						aggrArray.put(jsonObject);
					}
				}
			}
		}

		if (isImportedNode.isArray()) {
			for (JsonNode node : isImportedNode) {
				String count = "0";
				String isImported = node.get("key").asText();
				if (isImported.equals("T")) {
					count = node.get("doc_count").asText();
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("name", "Imported");
					jsonObject.put("count", count);
					aggrArray.put(jsonObject);
				} else {
					if (!isImported.equals("T") && isImportedNode.size() == 1) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("name", "Imported");
						jsonObject.put("count", count);
						aggrArray.put(jsonObject);
					}
				}
			}
		}

		if (OnSaleNode.isArray()) {
			for (JsonNode node : OnSaleNode) {
				String count = "0";
				boolean onSale = node.get("key").asBoolean();
				if (onSale) {
					count = node.get("doc_count").asText();
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("name", "On Sale");
					jsonObject.put("count", count);
					aggrArray.put(jsonObject);
				} else {
					if (!onSale && OnSaleNode.size() == 1) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("name", "On Sale");
						jsonObject.put("count", count);
						aggrArray.put(jsonObject);
					}
				}
			}
		}

		return aggrArray;
	}

	public static String getBrands(String searchText, String sortBy, String sortOrder, String categoryType, String filter) {

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
		
		String queryText = "";
		String filterText = "";
		String[] facets = categoryType.split("::");
		String[] filters = filter.split(":");
		
		if(StringUtils.isNotBlank(searchText)){
			queryText = ",\"must\": {\"multi_match\" : {\"query\": \""+searchText+"\",\"fields\": [\"title\",\"categories.category_name\",\"types.product_type\"]}}";
		}
		for(int i = 0; i < facets.length; i++){
			if (StringUtils.isNotBlank(facets[i])) {
				String[] eachFacet = facets[i].split(":");
				queryText = queryText + ",\"must\":{ \"match\": { \"" + eachFacet[0] + "\":  \"" + eachFacet[1] + "\" }}";
			}
		}

		for(int i = 0; i < filters.length; i++){
			if (StringUtils.isNotBlank(filters[i])) {
				filterText = filterText + ",\"" + filters[i] + "\"";
			}
		}
		if(StringUtils.isNotBlank(filter)){
			queryText = queryText + ",\"must\": {\"multi_match\" : {\"query\": \"1\",\"fields\": ["+filterText.replaceFirst(",","")+"]}}";
		}
		
		String query = "{\"query\": {\"bool\": {\"must\": {\"match\": {\"details.status\": \"1\"  }}" + queryText+"}},\"sort\": [{\"" + sortBy + "\": {\"order\": \"" + sortOrder
				+ "\"}}], " + "\"aggs\": {\"brands\": {\"terms\": {\"field\": \"filters.brand_name.raw_brand_name\",\"size\" : 1000}}}}";

		Logger.info("Brands - " + query);
		try {
			request = new HttpPost("http://localhost:9200/prodcatalogidx/_search?search_type=count");
			StringEntity params = new StringEntity(query);
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

	public static String getCategories(String searchText, String sortBy, String sortOrder, String categoryType, String filter) {

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
		
		String queryText = "";
		String filterText = "";
		String[] facets = categoryType.split("::");
		String[] filters = filter.split(":");
		
		if(StringUtils.isNotBlank(searchText)){
			queryText = ",\"must\": {\"multi_match\" : {\"query\": \""+searchText+"\",\"fields\": [\"title\",\"categories.category_name\",\"types.product_type\"]}}";
		}
		for(int i = 0; i < facets.length; i++){
			if (StringUtils.isNotBlank(facets[i])) {
				String[] eachFacet = facets[i].split(":");
				queryText = queryText + ",\"must\":{ \"match\": { \"" + eachFacet[0] + "\":  \"" + eachFacet[1] + "\" }}";
			}
		}

		for(int i = 0; i < filters.length; i++){
			if (StringUtils.isNotBlank(filters[i])) {
				filterText = filterText + ",\"" + filters[i] + "\"";
			}
		}
		if(StringUtils.isNotBlank(filter)){
			queryText = queryText + ",\"must\": {\"multi_match\" : {\"query\": \"1\",\"fields\": ["+filterText.replaceFirst(",","")+"]}}";
		}
		
		String query = "{\"query\": {\"bool\": {\"must\": {\"match\": {\"details.status\": \"1\"  }}" + queryText+"}},\"sort\": [{\"" + sortBy + "\": {\"order\": \"" + sortOrder
				+ "\"}}], " + "\"aggs\": {\"category\": {\"terms\": {\"field\": \"categories.category_name.raw_category_name\",\"size\" : 100}}}}";


		Logger.info("Category "+query);
		try {
			request = new HttpPost("http://localhost:9200/prodcatalogidx/_search?search_type=count");
			StringEntity params = new StringEntity(query);
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

	public static String getProductTypes(String searchText, String sortBy, String sortOrder, String categoryType, String filter) {

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;

		String queryText = "";
		String filterText = "";
		String[] facets = categoryType.split("::");
		String[] filters = filter.split(":");
		
		if(StringUtils.isNotBlank(searchText)){
			queryText = ",\"must\": {\"multi_match\" : {\"query\": \""+searchText+"\",\"fields\": [\"title\",\"categories.category_name\",\"types.product_type\"]}}";
		}
		for(int i = 0; i < facets.length; i++){
			if (StringUtils.isNotBlank(facets[i])) {
				String[] eachFacet = facets[i].split(":");
				queryText = queryText + ",\"must\":{ \"match\": { \"" + eachFacet[0] + "\":  \"" + eachFacet[1] + "\" }}";
			}
		}

		for(int i = 0; i < filters.length; i++){
			if (StringUtils.isNotBlank(filters[i])) {
				filterText = filterText + ",\"" + filters[i] + "\"";
			}
		}
		if(StringUtils.isNotBlank(filter)){
			queryText = queryText + ",\"must\": {\"multi_match\" : {\"query\": \"1\",\"fields\": ["+filterText.replaceFirst(",","")+"]}}";
		}
		
		String query = "{\"query\": {\"bool\": {\"must\": {\"match\": {\"details.status\": \"1\"  }}" + queryText+"}},\"sort\": [{\"" + sortBy + "\": {\"order\": \"" + sortOrder
				+ "\"}}], " + "\"aggs\": {\"types\": {\"terms\": {\"field\": \"types.product_type.raw_product_type\",\"size\" : 200}}}}";

		Logger.info("Product types "+query);
		try {
			request = new HttpPost("http://localhost:9200/prodcatalogidx/_search?search_type=count");
			StringEntity params = new StringEntity(query);
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
		String query = "{\"query\" : {\"term\" : { \"title\" : " + id + " }}}";
		try {
			request = new HttpPost("http://localhost:9200/prodcategoryidx/_search");
			StringEntity params = new StringEntity(query);
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

		String query = "{\"query\" : {\"term\" : { \"sub_categories.title\" : \"" + id + "\" }}}";
		Logger.info(query);
		try {
			request = new HttpPost("http://localhost:9200/prodcategoryidx/_search");
			StringEntity params = new StringEntity(query);
			request.addHeader("content-type", "application/x-www-form-urlencoded");
			request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");

			JsonNode subCategoryNode = Json.parse(responseString);
			JsonNode categoryNode = subCategoryNode.findPath("hits").findPath("hits").findPath("_source").findPath("sub_categories");
			String category = subCategoryNode.findPath("hits").findPath("hits").findPath("_source").findPath("title").asText();
			String categoryid = subCategoryNode.findPath("hits").findPath("hits").findPath("_source").findPath("id").asText();

			if (categoryNode.isArray()) {
				for (JsonNode key : categoryNode) {
					String title = key.get("title").asText();
					String subid = key.get("id").asText();
					if (id.equals(key.get("id").asText())) {
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
		if (keywords.isEmpty()) {
			keywords = getKeywords(searchText, true);
			Integer count = 0;
			for (String keyword : keywords) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("type", "keyword");
				jsonObject.put("display", keyword + "???");
				jsonObject.put("id", count.toString());
				jsonObject.put("title", searchText + " " + jsonObject);
				jsonObject.put("searchText", keyword);
				jsonObject.put("breadcrumb", "");
				jsonArray.put(jsonObject);
				count = count + 1;
			}
		} else {
			Integer count = 0;
			for (String keyword : keywords) {
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
		for (int i = 0; i < jsonArrayCat.length(); i++) {
			JSONObject jsonObject = new JSONObject();
			JSONObject object = jsonArrayCat.getJSONObject(i);
			jsonObject.put("type", "categories");
			jsonObject.put("display", object.get("keyword") + " in <b style=\"color:#694F4F\">" + object.get("category") + "</b>");
			jsonObject.put("id", object.get("categoryid"));
			jsonObject.put("title", searchText + " in <b>" + object.get("category") + "</b> " + object.get("keyword"));
			jsonObject.put("searchText", object.get("keyword"));
			jsonObject.put("breadcrumb", object.get("category"));
			jsonArray.put(jsonObject);

		}

		for (int i = 0; i < jsonArrayProd.length(); i++) {
			JSONObject jsonObject = new JSONObject();
			JSONObject object = jsonArrayProd.getJSONObject(i);
			jsonObject.put("type", "product");
			if (StringUtils.equalsIgnoreCase(object.get("onsale").toString(), "true")) {
				jsonObject.put("display", "<img src =\"" + object.get("img") + "\" height=40 />&nbsp;&nbsp; " + object.get("title")
						+ " <b style=\"color:#ee4054\">" + object.get("sellingprice") + "</b> <strike>" + object.get("strikedoutprice")
						+ "</strike>");
			} else {
				jsonObject.put("display", "<img src =\"" + object.get("img") + "\" height=40 />&nbsp;&nbsp; " + object.get("title") + " "
						+ object.get("sellingprice") + " <strike>" + object.get("strikedoutprice") + "</strike>");
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

		String query = "{\"query\": {\"bool\": {\"must\":[{ \"match\": {\"details.status\":  \"1\" }},{ \"prefix\": { \"title\": \""
				+ searchText.toLowerCase() + "\"}}]}},\"sort\": [{\"pricing.on_sale\": {\"order\": \"desc\"}}], \"from\": 0,\"size\":3}";

		try {
			request = new HttpPost("http://localhost:9200/prodcatalogidx/_search");
			StringEntity params = new StringEntity(query);
			request.addHeader("content-type", "application/x-www-form-urlencoded");
			request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");

			JsonNode jsonNode = Json.parse(responseString);
			JsonNode node = jsonNode.get("hits").get("hits");
			if (node.isArray()) {
				for (JsonNode source : node) {
					String sku = source.get("_source").get("sku").asText();
					String title = source.get("_source").get("title").asText();
					String img = source.get("_source").get("img").get("name").asText();
					boolean onSale = source.get("_source").get("pricing").get("on_sale").asBoolean();
					String price = "$" + source.get("_source").get("pricing").get("price").asText();
					String promoPrice = "$" + source.get("_source").get("pricing").get("promo_price").asText();
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

	private static JSONArray getCategories(List<String> keyword) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		int count = 0;
		for (String searchText : keyword) {

			String responseString = getCategories(searchText, "pricing.on_sale", "desc", "", "");
			JsonNode jsonNode = Json.parse(responseString);

			JsonNode aggrNode = jsonNode.get("aggregations").get("category").get("buckets");

			if (aggrNode.isArray()) {
				for (JsonNode key : aggrNode) {
				//	String category = AppService.getCategory(key.get("key").asText());
					if (StringUtils.isNotBlank(key.get("key").asText())) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("category", key.get("key").asText());
						jsonObject.put("categoryid", key.get("key").asText());
						jsonObject.put("keyword", searchText);
						jsonArray.put(jsonObject);
						count = count + 1;
						if (count > 3) {
							return jsonArray;
						}
					}
				}
			}
		}

		return jsonArray;

	}

	private static List<String> getKeywords(String searchText, boolean empty) {

		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;

		List<String> keywords = new ArrayList<String>();
		Set<String> temp = null;

		String query = "{\"fields\" : [\"title\"], \"query\": {\"bool\": {\"must\":[{ \"match\": { \"details.status\":  \"1\" }},"+
				"{ \"prefix\": { \"title\": \""+searchText.toLowerCase()+"\"   }}]}}," + "\"highlight\": {\"fields\": {\"title\": {}}}}";

		if (empty) {
			query = "{\"query\": {\"match\": {\"title\": {\"query\": \"" + searchText
					+ "\",\"fuzziness\": 1,\"prefix_length\": 1}}},\"highlight\":{\"fields\":{\"title\":{}}}}}";
		}
		Logger.info("KEYWORD "+query);
		try {
			request = new HttpPost("http://localhost:9200/prodcatalogidx/_search");
			StringEntity params = new StringEntity(query);
			request.addHeader("content-type", "application/x-www-form-urlencoded");
			request.setEntity(params);
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");

			JsonNode jsonNode = Json.parse(responseString);
			JsonNode keywordNode = jsonNode.get("hits").get("hits");

			if (keywordNode.isArray()) {
				for (JsonNode source : keywordNode) {
					String[] words = StringUtils.substringsBetween(source.get("highlight").get("title").toString(), "<em>", "</em>");
					keywords.addAll(Arrays.asList(words));
				}
			}
			temp = new LinkedHashSet<String>(keywords);
			keywords = new ArrayList<String>(temp);
			keywords.remove("and");
			keywords.remove("The");
		} catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		if (keywords.size() > 5) {
			return keywords.subList(0, 5);
		}
		return keywords;

	}

	public static List<String> getCategoryList() {
		
		List<String> categoryList = new ArrayList<String>(); 
		
		@SuppressWarnings("resource")
		HttpClient client = new DefaultHttpClient();
		HttpPost request = null;
		HttpResponse response = null;
		String responseString = null;
		try{
		String query = "{\"query\": {\"term\":{\"status\":1}}}";
		request = new HttpPost("http://localhost:9200/prodcategoryidx/_search");
		StringEntity params = new StringEntity(query);
		request.addHeader("content-type", "application/x-www-form-urlencoded");
		request.setEntity(params);
		response = client.execute(request);
		HttpEntity entity = response.getEntity();
		responseString = EntityUtils.toString(entity, "UTF-8");
		}catch (ParseException | IOException e) {
			Logger.error("IOException/ParseException - " + e.getMessage());
			e.printStackTrace();
		}
		JsonNode jsonNode = Json.parse(responseString);
		JsonNode keywordNode = jsonNode.get("hits").get("hits");
		
		if (keywordNode.isArray()) {
			for (JsonNode source : keywordNode) {
				categoryList.add(source.get("_source").get("title").asText());
			}
		}
		
		return categoryList;
	}

}
