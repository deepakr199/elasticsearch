/*package controllers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.getResult;

import org.apache.commons.lang3.*;
import org.elasticsearch.common.jackson.JsonFactory;

import play.api.cache.Cache;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.search;
import views.html.cookie;
import play.api.libs.json.JsValue;
import play.api.libs.json.Json;

import org.json.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Search extends Controller {

    public static Result search() {
    	
    	JSONObject jsonResult = new JSONObject();
    	JSONObject ress = new JSONObject();
    	JSONObject ress_i = new JSONObject();
    	List<String> titles = new ArrayList<String>();
    	JSONArray titleArray=new JSONArray();
    	getResult result = new getResult();
    	jsonResult = result.getAll();
    	
    	
    	int i=0;
    	try {
			titleArray = jsonResult.getJSONObject("hits").getJSONArray("hits");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//System.out.println(titleArray);
    	for(i = 0 ; i<titleArray.length();i++)
    	{
    		try {
    			ress = new JSONObject(titleArray.get(i).toString());
    			ress_i  = new JSONObject(ress.getJSONObject("_source").toString());
    			
    			System.out.println(ress_i.getString("title"));
    			titles.add(ress_i.getString("title").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	System.out.println(titles.getClass().getName());
    	
        return ok(search.render(titles));
    }
    
    public static Result getResultArray(String query) {
    	
    	JSONObject jsonResult = new JSONObject();
    	JSONObject ress = new JSONObject();
    	JSONObject ress_i = new JSONObject();
    	List<String> titles = new ArrayList<String>();
    	JSONArray titleArray=new JSONArray();
    	getResult result = new getResult();
    	jsonResult = result.getaptResult(query);
    	//result.getResultFromJavaApi();
    	
    	
    	int i=0;
    	try {
			titleArray = jsonResult.getJSONObject("hits").getJSONArray("hits");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//System.out.println(titleArray);
    	for(i = 0 ; i<titleArray.length();i++)
    	{
    		try {
    			ress = new JSONObject(titleArray.get(i).toString());
    			ress_i  = new JSONObject(ress.getJSONObject("_source").toString());
    			
    			System.out.println(ress_i.getString("title"));
    			titles.add(ress_i.getString("title").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
  	System.out.println(titles);
    	
        return ok(cookie.render(titles));
		
    	
    	
    }
  
    public static Result getCategories() {
    	
    	JSONObject jsonResult = new JSONObject();
    	JSONObject ress = new JSONObject();
    	JSONObject ress_i = new JSONObject();
    	List<String> titles = new ArrayList<String>();
    	JSONArray titleArray=new JSONArray();
    	getResult result = new getResult();
    	jsonResult = result.getcategories();
    	//result.getResultFromJavaApi();
    	
    	
    	int i=0;
    	try {
			titleArray = jsonResult.getJSONObject("hits").getJSONArray("hits");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//System.out.println(titleArray);
    	for(i = 0 ; i<titleArray.length();i++)
    	{
    		try {
    			ress = new JSONObject(titleArray.get(i).toString());
    			ress_i  = new JSONObject(ress.getJSONObject("_source").toString());
    			
    			System.out.println(ress_i.getString("page"));
    			titles.add(ress_i.getString("page").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
  	System.out.println(titles);
    	
        return ok(cookie.render(titles));
    }
    
    public static Result getSubCategories(String category) {
    	
    	JSONObject jsonResult = new JSONObject();
    	JSONObject ress = new JSONObject();
    	JSONObject ress_i = new JSONObject();
    	//JSONObject subcats = new JSONObject();
    	List<String> titles = new ArrayList<String>();
    	JSONArray titleArray=new JSONArray();
    	getResult result = new getResult();
    	jsonResult = result.getsubcategories(category);        
        System.out.println(jsonResult);
    	
    	int i=0;
    	try {
			titleArray = jsonResult.getJSONObject("hits").getJSONArray("hits");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//System.out.println(titleArray);
    	for(i = 0 ; i<titleArray.length();i++)
    	{
    		try {
    			ress = new JSONObject(titleArray.get(i).toString());
    			ress_i  = new JSONObject(ress.getJSONObject("_source").toString());
    			System.out.println(ress_i.getString("categories").getClass().getName());
    			String[] subcats = ress_i.getString("categories").split(",");
    			System.out.println("after 2");
    			System.out.println(Arrays.toString(subcats));
    			
    			titles.add(ress_i.getString("categories").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
  	//System.out.println(titles);
    	
        return ok(cookie.render(titles));
    }
}

*/