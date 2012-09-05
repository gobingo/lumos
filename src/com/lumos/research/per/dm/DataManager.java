package com.lumos.research.per.dm;

import com.lumos.research.per.utilities.Utilities;
import com.lumos.research.per.search.SolrSearcher;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DataManager {

  private final File brandProductListing = Utilities.getFileFromURL(DataManager.class
      .getResource("/resources/Brand_Product_Listing_Scoped.txt"));
  private final File colorProductListing = Utilities.getFileFromURL(DataManager.class
      .getResource("/resources/Color_Product_Listing_Scoped.txt"));
  private final File sizeProductListing = Utilities.getFileFromURL(DataManager.class
      .getResource("/resources/Size_Product_Listing_Scoped.txt"));
  private final File typeProductListing = Utilities.getFileFromURL(DataManager.class
      .getResource("/resources/Type_Product_Listing_Scoped.txt"));

  private final File naBrandProductListing = Utilities.getFileFromURL(DataManager.class
      .getResource("/resources/NA_Brand_Product_Listing_Scoped.txt"));
  // private final File naColorProductListing = new
  // File(DataManager.class.getResource("/resources/NA_Color_Product_Listing_Scoped.txt").getFile());
  // private final File naSizeProductListing = new
  // File(DataManager.class.getResource("/resources/NA_Size_Product_Listing_Scoped.txt").getFile());
  // private final File naTypeProductListing = new
  // File(DataManager.class.getResource("/resources/NA_Type_Product_Listing_Scoped.txt").getFile());

  private final File brandEntities = Utilities.getFileFromURL(DataManager.class
      .getResource("/resources/brand_entities_processed_clothing_shoes_combined1000.txt"));
  private final File colorEntities = Utilities.getFileFromURL(DataManager.class
      .getResource("/resources/color_entities_processed_64.txt"));
  private final File sizeEntities = Utilities.getFileFromURL(DataManager.class
      .getResource("/resources/size_entities_processed_496.txt"));
  private final File typeEntities = Utilities.getFileFromURL(DataManager.class
      .getResource("/resources/type_entities_processed_105.txt"));

  private HashSet<String> brandEntitiesSet;
  private HashSet<String> colorEntitiesSet;
  private HashSet<String> sizeEntitiesSet;
  private HashSet<String> typeEntitiesSet;

  public void extractProductListing(String entityValue, FileWriter writer, String entityName, String listingType){
    try{
      //System.out.println("Entity: "+entityValue);
      SolrSearcher solrSearcher = new SolrSearcher();
      String response = solrSearcher.searchSolr(entityValue, entityName);
      //System.out.println(response);
      JSONObject jsonObject = new JSONObject(response), jsonResponse = jsonObject.getJSONObject("response"), jsonTempObject;
      JSONArray jsonDocArr = jsonResponse.getJSONArray("docs");
      String productListing="", productListingURL="";
      JSONObject responseJSON = new JSONObject();
      JSONObject pListingJSONObjects = new JSONObject();
      JSONArray pListingJSONArray = new JSONArray();
      int jsonIndex = 0;
      for(int index=0;index<jsonDocArr.length();index++){
        jsonTempObject = jsonDocArr.getJSONObject(index);
        if(jsonTempObject!=null){
          productListing = jsonTempObject.getString("prod-name");
          List<String> arrList = Arrays.asList(productListing.split(" "));
          if(arrList.contains("-")||arrList.size()<3) {
            continue;
          }
          //productListingURL = jsonTempObject.getString("page-url");
          //System.out.println("Product Listing: "+productListing);
          if(productListing!=null && !productListing.equals("")){
            if(productListing.contains(entityValue)){
              JSONObject pListingJSONObject = new JSONObject();
              pListingJSONObject.put("product_listing", productListing);
              pListingJSONObject.put("entity_value", entityValue);

              String firstTokenBeforeEntity = getFirstTokenBeforeEntity(productListing, entityValue);
              String secondTokenBeforeEntity = getSecondTokenBeforeEntity(productListing, entityValue);

              String firstTokenAfterEntity = getFirstTokenAfterEntity(productListing, entityValue);
              String secondTokenAfterEntity = getSecondTokenAfterEntity(productListing, entityValue);

              pListingJSONObject.put("pre_1_token", firstTokenBeforeEntity);
              pListingJSONObject.put("pre_2_token", secondTokenBeforeEntity);
              pListingJSONObject.put("post_1_token", firstTokenAfterEntity);
              pListingJSONObject.put("post_2_token", secondTokenAfterEntity);

              //pListingJSONObject.put("product_listing_url", productListingURL);
              pListingJSONArray.put(jsonIndex, pListingJSONObject);
              jsonIndex++;
            }
          }
        }
      }
      if(pListingJSONArray.length()>0){
        pListingJSONObjects.put("docs", pListingJSONArray);
        responseJSON.put(listingType, pListingJSONObjects);
        writer.write(responseJSON.toString());
        writer.write("\n");
      }
    }catch(Exception ex){
      System.out.println(ex.getMessage() + ":search string: "+entityValue);
      //ex.printStackTrace();
    }
  }


  public static String getSecondTokenBeforeEntity(String productListing,
      String entityValue) {
    String secondTokenBeforeEntity = "";
    String entityLookUpStr = entityValue;
    String[] entityValueTokens = entityValue.split(" ");
    if(entityValueTokens.length>1){
      entityLookUpStr = entityValueTokens[0];
    }
    String[] productListingTokens = productListing.split(" ");
    for(int index=0;index<productListingTokens.length;index++){
      if((productListingTokens[index].equalsIgnoreCase(entityLookUpStr) || productListingTokens[index].contains(entityLookUpStr))){
        if((index-2)>=0){
          secondTokenBeforeEntity = productListingTokens[index-2];
        }
        break;
      }
    }
    return secondTokenBeforeEntity;
  }

  public static String getFirstTokenBeforeEntity(String productListing,
      String entityValue) {
    String firstTokenBeforeEntity = "", entityLookUpStr = entityValue;
    String[] entityValueTokens = entityValue.split(" ");
    if(entityValueTokens.length>1){
      entityLookUpStr = entityValueTokens[0];
    }
    String[] productListingTokens = productListing.split(" ");
    for(int index=0;index<productListingTokens.length;index++){
      if((productListingTokens[index].equalsIgnoreCase(entityLookUpStr) || productListingTokens[index].contains(entityLookUpStr))){
        if((index-1)>=0){
          firstTokenBeforeEntity = productListingTokens[index-1];
        }
        break;
      }
    }
    return firstTokenBeforeEntity;
  }

  public static String getSecondTokenAfterEntity(String productListing,
      String entityValue) {
    String secondTokenAfterEntity = "", entityLookUpStr = entityValue;
    String[] entityValueTokens = entityValue.split(" ");
    if(entityValueTokens.length>1){
      entityLookUpStr = entityValueTokens[1];
    }
    String[] productListingTokens = productListing.split(" ");
    for(int index=0;index<productListingTokens.length;index++){
      if((productListingTokens[index].equalsIgnoreCase(entityLookUpStr) || productListingTokens[index].contains(entityLookUpStr))){
        if((index+2)<=(productListingTokens.length-1)){
          secondTokenAfterEntity = productListingTokens[index+2];
        }
        break;
      }
    }
    return secondTokenAfterEntity;
  }

  public static String getFirstTokenAfterEntity(String productListing,
      String entityValue) {
    String firstTokenAfterEntity = "", entityLookUpStr = entityValue;
    String[] entityValueTokens = entityValue.split(" ");
    if(entityValueTokens.length>1){
      entityLookUpStr = entityValueTokens[1];
    }
    String[] productListingTokens = productListing.split(" ");
    for(int index=0;index<productListingTokens.length;index++){
      if((productListingTokens[index].equalsIgnoreCase(entityLookUpStr) || productListingTokens[index].contains(entityLookUpStr))){
        if((index+1)<=(productListingTokens.length-1)){
          firstTokenAfterEntity = productListingTokens[index+1];
        }
        break;
      }
    }
    return firstTokenAfterEntity;
  }

  public void collectNERData(){
    try{

      // collect brand data
      FileWriter writer = new FileWriter(brandProductListing);
      BufferedReader bfr = new BufferedReader(new InputStreamReader(new
          FileInputStream(brandEntities)));
      String brand = "";
      while((brand=bfr.readLine())!=null){
        if(brand!=null && !brand.equals("")){
          extractProductListing(brand.trim(), writer, "brand",
              "brand_product_listing");
        }
      }
      writer.close();

      //collect color data
      writer = new FileWriter(colorProductListing);
      bfr = new BufferedReader(new InputStreamReader(new
          FileInputStream(colorEntities)));
      String color = "";
      while((color=bfr.readLine())!=null){
        if(color!=null && !color.equals("")){
          extractProductListing(color.trim(), writer, "color",
              "color_product_listing");
        }
      }
      writer.close();

      //collect size data
      writer = new FileWriter(sizeProductListing);
      bfr = new BufferedReader(new InputStreamReader(new
          FileInputStream(sizeEntities)));
      String size = "";
      while((size=bfr.readLine())!=null){
        if(size!=null && !size.equals("")){
          extractProductListing(size.trim(), writer, "size",
              "size_product_listing");
        }
      }
      writer.close();

      //collect type data
      writer = new FileWriter(typeProductListing);
      bfr = new BufferedReader(new InputStreamReader(
          new FileInputStream(typeEntities)));
      String type = "";
      while((type=bfr.readLine())!=null){
        if(type!=null && !type.equals("")){
          extractProductListing(type.trim(), writer, "category", "type_product_listing");
        }
      }
      writer.close();

    }catch(Exception ex){
      System.out.println(ex.getMessage());
    }
  }

  private void intializeNonNAEntitySets() throws IOException {
    brandEntitiesSet = new HashSet<String>();
    colorEntitiesSet = new HashSet<String>();
    sizeEntitiesSet = new HashSet<String>();
    typeEntitiesSet = new HashSet<String>();

    BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(brandEntities)));
    String ipStr = "";
    while((ipStr=bfr.readLine())!=null){
      brandEntitiesSet.add(ipStr);
    }

    bfr = new BufferedReader(new InputStreamReader(new FileInputStream(colorEntities)));
    ipStr = "";
    while((ipStr=bfr.readLine())!=null){
      colorEntitiesSet.add(ipStr);
    }

    bfr = new BufferedReader(new InputStreamReader(new FileInputStream(sizeEntities)));
    ipStr = "";
    while((ipStr=bfr.readLine())!=null){
      sizeEntitiesSet.add(ipStr);
    }

    bfr = new BufferedReader(new InputStreamReader(new FileInputStream(typeEntities)));
    ipStr = "";
    while((ipStr=bfr.readLine())!=null){
      typeEntitiesSet.add(ipStr);
    }

  }

  public void cleanseEntityDataEx(File file, FileWriter writer) throws IOException{
    BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
    Set<String> entitySet = new HashSet<String>();
    String entity = "";
    while((entity=bfr.readLine())!=null){
      entity = entity.trim();
      if(entity!=null && !entity.equals("")){
        String[] entValArr = entity.split(" ");
        if(entValArr.length>1){
          for(String entVal:entValArr){
            if(entVal.trim().equals("&") || entVal.trim().equals("and") || entVal.trim().equals("-")) {
              continue;
            } else{
              entitySet.add(entVal.trim());
            }
          }
        }else{
          entitySet.add(entity);
        }
      }
    }

    for(Iterator iter = entitySet.iterator();iter.hasNext();){
      writer.write((String)iter.next());
      writer.write("\n");
    }
    writer.close();
  }

  /**
   * @return the brandProductListing
   */
  public File getBrandProductListing() {
    return brandProductListing;
  }

  /**
   * @return the colorProductListing
   */
  public File getColorProductListing() {
    return colorProductListing;
  }

  /**
   * @return the sizeProductListing
   */
  public File getSizeProductListing() {
    return sizeProductListing;
  }

  /**
   * @return the typeProductListing
   */
  public File getTypeProductListing() {
    return typeProductListing;
  }

  /**
   * @return the naBrandProductListing
   */
  public File getNaBrandProductListing() {
    return naBrandProductListing;
  }

  public void collectNAListingData(){
    try{
      //collect entities for 'NA' category
      intializeNonNAEntitySets();

      FileWriter naProductListingWriter = new FileWriter(naBrandProductListing);
      buildNAProductListing(getBrandProductListing(),"brand_product_listing",naProductListingWriter);
      naProductListingWriter.close();

      /*			naProductListingWriter = new FileWriter(naColorProductListing);
			buildNAProductListing(getColorProductListing(),"color_product_listing",naProductListingWriter);
			naProductListingWriter.close();

			naProductListingWriter = new FileWriter(naSizeProductListing);
			buildNAProductListing(getSizeProductListing(),"size_product_listing",naProductListingWriter);
			naProductListingWriter.close();

			naProductListingWriter = new FileWriter(naTypeProductListing);
			buildNAProductListing(getTypeProductListing(),"type_product_listing",naProductListingWriter);
			naProductListingWriter.close();*/

    }catch(Exception ex){
      System.out.println("Caught exception at DataManager:collectNAListingData: "+ex.getMessage());
    }
  }

  private void buildNAProductListing(File productListingFile, String listingType, FileWriter naProductListingWriter) {
    try{
      BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(productListingFile)));
      String productListingsJSON = "";
      while((productListingsJSON = bfr.readLine())!=null){
        JSONObject pListingsJSONObject = new JSONObject(productListingsJSON.trim());
        JSONObject brandProductListingJSONObject = pListingsJSONObject.getJSONObject(listingType);
        JSONArray brandProductListingJSONArr =  brandProductListingJSONObject.getJSONArray("docs");

        JSONObject responseJSON = new JSONObject();
        JSONObject pListingJSONObjects = new JSONObject();
        JSONArray pListingJSONArray = new JSONArray();
        int jsonIndex = 0;

        for(int index=0;index<brandProductListingJSONArr.length();index++){
          JSONObject plDocObject = brandProductListingJSONArr.getJSONObject(index);
          String productListing = plDocObject.getString("product_listing").trim();
          //String productListingURL = plDocObject.getString("product_listing_url").trim();
          String[] productListingTokens = productListing.split(" ");

          for(String productListingToken:productListingTokens){
            productListingToken = productListingToken.trim();
            if(!containsEx(brandEntitiesSet,productListingToken,"brandEntities") && !containsEx(colorEntitiesSet,productListingToken,"colorEntities") && !containsEx(sizeEntitiesSet,productListingToken,"sizeEntities") && !containsEx(typeEntitiesSet,productListingToken,"typeEntities")){
              JSONObject pListingJSONObject = new JSONObject();
              pListingJSONObject.put("product_listing", productListing);
              pListingJSONObject.put("entity_value", productListingToken);

              String firstTokenBeforeEntity = getFirstTokenBeforeEntity(productListing, productListingToken);
              String secondTokenBeforeEntity = getSecondTokenBeforeEntity(productListing, productListingToken);

              String firstTokenAfterEntity = getFirstTokenAfterEntity(productListing, productListingToken);
              String secondTokenAfterEntity = getSecondTokenAfterEntity(productListing, productListingToken);

              pListingJSONObject.put("pre_1_token", firstTokenBeforeEntity);
              pListingJSONObject.put("pre_2_token", secondTokenBeforeEntity);
              pListingJSONObject.put("post_1_token", firstTokenAfterEntity);
              pListingJSONObject.put("post_2_token", secondTokenAfterEntity);

              //pListingJSONObject.put("product_listing_url", productListingURL);
              pListingJSONArray.put(jsonIndex, pListingJSONObject);
              jsonIndex++;
            }
          }

        }

        if(pListingJSONArray.length()>0){
          pListingJSONObjects.put("docs", pListingJSONArray);
          responseJSON.put("na_product_listing", pListingJSONObjects);
          naProductListingWriter.write(responseJSON.toString());
          naProductListingWriter.write("\n");
        }
      }
    }catch(Exception ex){
      System.out.println("Caught exception at NEREngine:buildNAProductListing: "+ex.getMessage());
    }

  }

  private boolean containsEx(HashSet<String> entitiesSet,
      String productListingToken, String entitiesSetType) {
    boolean flag = false;
    for(Iterator iter = entitiesSet.iterator(); iter.hasNext();){
      String entity = (String) iter.next();
      entity = entity.trim();
      if(entity.equalsIgnoreCase(productListingToken) || entity.contains(productListingToken)){
        flag = true;
      }
    }

    return flag;
  }

  public void extractSolrEntities(){
    try{
      String inFile = "C:/Users/satapath/Documents/seed_entities_scoped/entities_raw.txt";
      String outFile = "C:/Users/satapath/Documents/seed_entities_scoped/entities_processed.txt";
      BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inFile))));
      String str = "";
      Set<String> entSet = new HashSet<String>();
      while((str=bfr.readLine())!=null){
        str = str.trim();
        if(str.contains("|") || str.split(" ").length>1){
          continue;
        }
        entSet.add(str);
      }
      FileWriter writer = new FileWriter(new File(outFile));
      for(Iterator<String> iter = entSet.iterator();iter.hasNext();){
        writer.write(iter.next());
        writer.write("\n");
      }
      writer.close();
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }
}
