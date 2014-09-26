/*package models;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import static org.elasticsearch.node.NodeBuilder.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.yaml.snakeyaml.nodes.Node;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

import com.fasterxml.jackson.databind.JsonNode;

public class getResult {
	public JSONObject getAll() {
	JSONObject res = new JSONObject(); 
	StringBuilder sb = new StringBuilder();
	HttpClient client = new DefaultHttpClient();
    HttpGet request = new HttpGet("http://localhost:9200/mongoindex/_search?scroll=20s&size=1");
    HttpResponse response = null;
	try {
		response = client.execute(request);
	} catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    BufferedReader rd = null;
	try {
		rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
	} catch (IllegalStateException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    String line = "";
    try {
		while ((line = rd.readLine()) != null) {
		sb.append(line + "\n");
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    System.out.println(sb);
	
		try {
			res = new JSONObject(sb.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	

	}
	
	public JSONObject getaptResult(String query){
		JSONObject res = new JSONObject(); 
		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
	    HttpGet request = new HttpGet("http://localhost:9200/mongoindex/_search?q=title:*"+query+"*&scroll=20s&size=10");
	    HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    BufferedReader rd = null;
		try {
			rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    String line = "";
	    try {
			while ((line = rd.readLine()) != null) {
			sb.append(line + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println(sb);
		
			try {
				res = new JSONObject(sb.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return res;
		
		
	}
	
	public void getResultFromJavaApi()
	{
		org.elasticsearch.node.Node node = nodeBuilder().node();
		Client el_client = node.client();
		GetResponse Java_res = el_client.prepareGet("mongoindex","catalog",null).execute().actionGet();
		System.out.println(Java_res);
	}
	
	public JSONObject getcategories()
	{
		JSONObject res = new JSONObject(); 
		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
	    HttpGet request = new HttpGet("http://localhost:9200/productpage/cats/_search");
	    HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    BufferedReader rd = null;
		try {
			rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    String line = "";
	    try {
			while ((line = rd.readLine()) != null) {
			sb.append(line + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println(sb);
		
			try {
				res = new JSONObject(sb.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return res;
	}
	
	public JSONObject getsubcategories(String category)
	{
		JSONObject res = new JSONObject(); 
		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		System.out.println("request URL: http://localhost:9200/productpage/cats/_search?q=page:"+category);
	    HttpGet request= new HttpGet();
		try {
			request = new HttpGet("http://localhost:9200/productpage/cats/_search?q=page:"+URLEncoder.encode(category,"UTF-8"));
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	   // System.out.println("http://localhost:9200/productpage/cats/_search?q=page:"+category);
	    HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    BufferedReader rd = null;
		try {
			rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    String line = "";
	    try {
			while ((line = rd.readLine()) != null) {
			sb.append(line + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println(sb);
	    System.out.println("model result over");
		
			try {
				res = new JSONObject(sb.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return res;
	}
}
*/