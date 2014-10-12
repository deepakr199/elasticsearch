package controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import service.AppService;

import com.fasterxml.jackson.databind.JsonNode;

public class Application extends Controller {

	public static Result index() throws JSONException {
		return ok(views.html.main.render());
	}

	public static Result getItems(String searchText, String sortBy, String sortOrder, String page, String categoryType, String filter) throws JSONException {

		String jsonString = AppService.getItems(searchText, sortBy, sortOrder, page, categoryType, filter);
		JsonNode jsonNode = Json.parse(jsonString);

		JsonNode node = jsonNode.findPath("hits").findPath("hits");
		String totalPages = jsonNode.findPath("hits").findPath("total").asText();
		JSONArray jsonArray = new JSONArray();
		if (node.isArray()) {
			for (JsonNode source : node) {
				String sku = source.findPath("_source").findPath("sku").asText();
				String title = source.findPath("_source").findPath("title").asText();
				String img = source.findPath("_source").findPath("img").findPath("name").asText();
				if (img.charAt(0) != '/') {
					img = "https://s3-ap-southeast-1.amazonaws.com/media.redmart.com/newmedia/150x/" + img;
				} else {
					img = "https://s3-ap-southeast-1.amazonaws.com/media.redmart.com/newmedia/150x" + img;
				}

				boolean onSale = source.findPath("_source").findPath("pricing").findPath("on_sale").asBoolean();
				String price = "$" + source.findPath("_source").findPath("pricing").findPath("price").asText();
				String savings = source.findPath("_source").findPath("pricing").findPath("savings").asText();
				String promoPrice = "$" + source.findPath("_source").findPath("pricing").findPath("promo_price").asText();
				boolean isnew =  source.findPath("_source").findPath("details").findPath("is_new").asBoolean();

				String strikedOutPrice = "";
				if (onSale) {
					strikedOutPrice = price;
					price = promoPrice;
				}

				JSONObject jsonObject = new JSONObject();
				jsonObject.put("sku", sku);
				jsonObject.put("title", title);
				jsonObject.put("onsale", onSale);
				jsonObject.put("img", img);
				jsonObject.put("strikedoutprice", strikedOutPrice);
				jsonObject.put("sellingprice", price);
				jsonObject.put("discount", savings);
				jsonObject.put("isnew", isnew);
				jsonArray.put(jsonObject);
			}
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("items", jsonArray);
		jsonObject.put("totalPages", totalPages);
		
		return ok(jsonObject.toString());
	}

	public static Result lookup(String searchText, String sortBy, String sortOrder) throws JSONException {
		JSONArray jsonArray = AppService.getLookup(searchText);
		return ok(jsonArray.toString());
	}


	public static Result getCategories(String searchText, String sortBy, String sortOrder, String categoryType, String filter) throws JSONException, UnsupportedEncodingException {

		String jsonString = AppService.getCategories(searchText, sortBy, sortOrder, categoryType, filter);
		JsonNode jsonNode = Json.parse(jsonString);
		List<String> categoryList = AppService.getCategoryList(); 
		JsonNode aggrNode = jsonNode.findPath("aggregations").findPath("category").findPath("buckets");
		JSONArray aggrArray = new JSONArray();
		JSONArray catArray = new JSONArray();
		JSONArray subArray = new JSONArray();
		long totalCountSub = 0;
		long totalCountCat = 0;
		
		if(aggrNode.isArray()){
			for(JsonNode key : aggrNode){
				if(!categoryList.contains(key.findPath("key").asText())){
					//JSONObject subCategory = AppService.getSubCategory(key.findPath("key").asText());
						Long count = key.findPath("doc_count").asLong();
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("subcategory", key.findPath("key").asText());
						jsonObject.put("subid", URLEncoder.encode(key.findPath("key").asText(), "UTF-8"));
						jsonObject.put("subcount", count);
						subArray.put(jsonObject);		
						totalCountSub = totalCountSub + count;
					
				}else{
					Long count = key.findPath("doc_count").asLong();
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", URLEncoder.encode(key.findPath("key").asText(), "UTF-8"));
					jsonObject.put("category", key.findPath("key").asText());
					jsonObject.put("count", count);
					catArray.put(jsonObject);
					totalCountCat = totalCountCat + count; 

				}
			}
		}
		aggrArray.put(catArray);
		aggrArray.put(subArray);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("totalCategories", totalCountCat);
		jsonObject.put("totalSubCategories", totalCountSub);
		jsonObject.put("mainObject", aggrArray);

		return ok(jsonObject.toString());
	}
	
	
	public static Result getProductTypes(String searchText, String sortBy, String sortOrder, String categoryType, String filter) throws JSONException, UnsupportedEncodingException {

		String jsonString = AppService.getProductTypes(searchText, sortBy, sortOrder, categoryType, filter);
		JsonNode jsonNode = Json.parse(jsonString);

		JsonNode aggrNode = jsonNode.findPath("aggregations").findPath("types").findPath("buckets");
		JSONArray aggrArray = new JSONArray();
		long totalCount = 0;
		
		if(aggrNode.isArray()){
			for(JsonNode key : aggrNode){

					Long count = key.findPath("doc_count").asLong();
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", URLEncoder.encode(key.findPath("key").asText(), "UTF-8"));
					jsonObject.put("type", key.findPath("key").asText());
					jsonObject.put("count", count);
					aggrArray.put(jsonObject);	
					totalCount = totalCount + count;

			}
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("totalTypes", totalCount);
		jsonObject.put("types", aggrArray);
		
		return ok(jsonObject.toString());
	}
	
	public static Result getBrands(String searchText, String sortBy, String sortOrder, String categoryType, String filter) throws JSONException {

		String jsonString = AppService.getBrands(searchText, sortBy, sortOrder, categoryType, filter);
		JsonNode jsonNode = Json.parse(jsonString);

		JsonNode aggrNode = jsonNode.findPath("buckets");
		JSONArray aggrArray = new JSONArray();
		long totalCount = 0;
		if(aggrNode.isArray()){
			for(JsonNode key : aggrNode){

				Long count = key.findPath("doc_count").asLong();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", key.findPath("key").asText());
				jsonObject.put("brand", key.findPath("key").asText());
				jsonObject.put("count", count);
				aggrArray.put(jsonObject);
				totalCount = totalCount + count;
			}
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("totalBrands", totalCount);
		jsonObject.put("brands", aggrArray);
		
		return ok(jsonObject.toString());

	}
	
	public static Result getOnSaleNewImported(String searchText, String sortBy, String sortOrder, String categoryType, String filter) throws JSONException {

		
		JSONArray jsonArray = AppService.getOnSaleNewImported(searchText, sortBy, sortOrder, categoryType, filter);
		return ok(jsonArray.toString());

	}

	public static Result elasticTest() throws JSONException {

		JSONArray jsonArray = AppService.getLookup("s");
		return ok(jsonArray.toString());
	}
}
