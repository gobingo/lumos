package com.mauryaa.research.per.solr;

import java.net.URLEncoder;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

public class SolrSearcher {
  private static String SOLR_SERVER_URI = "http://localhost:8983/solr/collection1";
  private static int NUM_ROWS = 10;
  private static String WT = "json";
  private HttpClient httpClient;
  private GetMethod request;

  public String searchSolr(String searchString, String entityName){
    String responseStr = "";
    httpClient = new HttpClient();
    try{
      switch (entityName) {
      case "brand":
        SolrSearcher.NUM_ROWS = 1;
        break;
      case "color":
        SolrSearcher.NUM_ROWS = 26;
        break;
      case "size":
        SolrSearcher.NUM_ROWS = 20;
        break;
      case "category":
        SolrSearcher.NUM_ROWS = 46;
        break;
      }
      searchString = URLEncoder.encode(searchString, "UTF-8");
      String searchURI = "q="+searchString+"&fq="+entityName+":"+searchString;
      searchURI = SOLR_SERVER_URI + "/query?"+ searchURI + "&rows="+SolrSearcher.NUM_ROWS;
      request = new GetMethod(searchURI);
      request.setRequestHeader("accept", "application/json");
      httpClient.executeMethod(request);
      responseStr = request.getResponseBodyAsString();

    }catch(Exception ex){
      System.out.println(ex.getMessage());
    }

    return responseStr;
  }

}
