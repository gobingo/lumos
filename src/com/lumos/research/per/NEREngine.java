package com.lumos.research.per;

import com.lumos.research.per.dm.DataManager;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.ngram.NGramModel;
import opennlp.tools.util.StringList;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.SVMLightLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

public class NEREngine {

  private final int NGRAM_START = 2;
  private final int NGRAM_END = 6;

  public void buildFeatureVectorsHelper() throws IOException{
    DataManager dataMgr = new DataManager();

    TreeMap<String, Integer> currentTokens = new TreeMap<String, Integer>();
    TreeMap<String, Integer> pre_1_Tokens = new TreeMap<String, Integer>();
    TreeMap<String, Integer> pre_2_Tokens = new TreeMap<String, Integer>();
    TreeMap<String, Integer> post_1_Tokens = new TreeMap<String, Integer>();
    TreeMap<String, Integer> post_2_Tokens = new TreeMap<String, Integer>();
    TreeMap<String, Integer> currentTokensNgrams = new TreeMap<String, Integer>();
    TreeMap<String, Integer> pre_1_TokensNgrams = new TreeMap<String, Integer>();
    TreeMap<String, Integer> pre_2_TokensNgrams = new TreeMap<String, Integer>();
    TreeMap<String, Integer> post_1_TokensNgrams = new TreeMap<String, Integer>();
    TreeMap<String, Integer> post_2_TokensNgrams = new TreeMap<String, Integer>();

    buildTokenAndCharNgramMaps(
        NEREngine.class
        .getResourceAsStream("/resources/Brand_Product_Listing_Scoped.txt"),
        "brand_product_listing", currentTokens, pre_1_Tokens, pre_2_Tokens,
        post_1_Tokens, post_2_Tokens, currentTokensNgrams, pre_1_TokensNgrams,
        pre_2_TokensNgrams, post_1_TokensNgrams, post_2_TokensNgrams);

    buildTokenAndCharNgramMaps(
        NEREngine.class
        .getResourceAsStream("/resources/Color_Product_Listing_Scoped.txt"),
        "color_product_listing", currentTokens, pre_1_Tokens, pre_2_Tokens,
        post_1_Tokens, post_2_Tokens, currentTokensNgrams, pre_1_TokensNgrams,
        pre_2_TokensNgrams, post_1_TokensNgrams, post_2_TokensNgrams);

    buildTokenAndCharNgramMaps(
        NEREngine.class
        .getResourceAsStream("/resources/Size_Product_Listing_Scoped.txt"),
        "size_product_listing", currentTokens, pre_1_Tokens, pre_2_Tokens,
        post_1_Tokens, post_2_Tokens, currentTokensNgrams, pre_1_TokensNgrams,
        pre_2_TokensNgrams, post_1_TokensNgrams, post_2_TokensNgrams);

    buildTokenAndCharNgramMaps(
        NEREngine.class
        .getResourceAsStream("/resources/Type_Product_Listing_Scoped.txt"),
        "type_product_listing", currentTokens, pre_1_Tokens, pre_2_Tokens,
        post_1_Tokens, post_2_Tokens, currentTokensNgrams, pre_1_TokensNgrams,
        pre_2_TokensNgrams, post_1_TokensNgrams, post_2_TokensNgrams);

    buildTokenAndCharNgramMaps(
        NEREngine.class
        .getResourceAsStream("/resources/NA_Brand_Product_Listing_Scoped.txt"),
        "na_product_listing", currentTokens, pre_1_Tokens, pre_2_Tokens,
        post_1_Tokens, post_2_Tokens, currentTokensNgrams, pre_1_TokensNgrams,
        pre_2_TokensNgrams, post_1_TokensNgrams, post_2_TokensNgrams);


    FileWriter writer = new FileWriter(
        ".../trainingData_v19_5class.dat"); //training date file  - which needs to be passed to a learning algorithm

    buildFeatureVectors(dataMgr.getBrandProductListing(),"brand_product_listing",currentTokens,pre_1_Tokens,pre_2_Tokens,post_1_Tokens,post_2_Tokens,currentTokensNgrams,pre_1_TokensNgrams,pre_2_TokensNgrams,post_1_TokensNgrams,post_2_TokensNgrams,writer);
    buildFeatureVectors(dataMgr.getColorProductListing(),"color_product_listing",currentTokens,pre_1_Tokens,pre_2_Tokens,post_1_Tokens,post_2_Tokens,currentTokensNgrams,pre_1_TokensNgrams,pre_2_TokensNgrams,post_1_TokensNgrams,post_2_TokensNgrams,writer);
    buildFeatureVectors(dataMgr.getSizeProductListing(),"size_product_listing",currentTokens,pre_1_Tokens,pre_2_Tokens,post_1_Tokens,post_2_Tokens,currentTokensNgrams,pre_1_TokensNgrams,pre_2_TokensNgrams,post_1_TokensNgrams,post_2_TokensNgrams,writer);
    buildFeatureVectors(dataMgr.getTypeProductListing(),"type_product_listing",currentTokens,pre_1_Tokens,pre_2_Tokens,post_1_Tokens,post_2_Tokens,currentTokensNgrams,pre_1_TokensNgrams,pre_2_TokensNgrams,post_1_TokensNgrams,post_2_TokensNgrams,writer);
    buildFeatureVectors(dataMgr.getNaBrandProductListing(),"na_product_listing",currentTokens,pre_1_Tokens,pre_2_Tokens,post_1_Tokens,post_2_Tokens,currentTokensNgrams,pre_1_TokensNgrams,pre_2_TokensNgrams,post_1_TokensNgrams,post_2_TokensNgrams,writer);

    writer.close();
  }

  private void buildTokenAndCharNgramMaps(InputStream productListingFileIS,
      String listingType, TreeMap<String, Integer> currentTokens,
      TreeMap<String, Integer> pre_1_Tokens,
      TreeMap<String, Integer> pre_2_Tokens,
      TreeMap<String, Integer> post_1_Tokens,
      TreeMap<String, Integer> post_2_Tokens,
      TreeMap<String, Integer> currentTokensNgrams,
      TreeMap<String, Integer> pre_1_TokensNgrams,
      TreeMap<String, Integer> pre_2_TokensNgrams,
      TreeMap<String, Integer> post_1_TokensNgrams,
      TreeMap<String, Integer> post_2_TokensNgrams) {
    try{
      BufferedReader bfr = new BufferedReader(new InputStreamReader(
          productListingFileIS));
      String productListingsJSON = "";
      NGramModel ngModel;
      Dictionary ngrams;
      while((productListingsJSON = bfr.readLine())!=null){
        JSONObject pListingsJSONObject = new JSONObject(productListingsJSON.trim());
        JSONObject brandProductListingJSONObject = pListingsJSONObject.getJSONObject(listingType);
        JSONArray brandProductListingJSONArr =  brandProductListingJSONObject.getJSONArray("docs");
        for(int index=0;index<brandProductListingJSONArr.length();index++){
          JSONObject plDocObject = brandProductListingJSONArr.getJSONObject(index);
          String entityValue = plDocObject.getString("entity_value").trim();
          String pre_1_token = plDocObject.getString("pre_1_token").trim();
          String pre_2_token = plDocObject.getString("pre_2_token").trim();
          String post_1_token = plDocObject.getString("post_1_token").trim();
          String post_2_token = plDocObject.getString("post_2_token").trim();
          if(entityValue!=null && !entityValue.equals("")){
            currentTokens.put(entityValue, 0);

            //generate char n-grams
            ngModel = new NGramModel();
            ngModel.add(entityValue,NGRAM_START,NGRAM_END);
            ngrams = ngModel.toDictionary();
            for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
              StringList ngram = iter.next();
              currentTokensNgrams.put(ngram.getToken(0), 0);
            }
          }
          if(pre_1_token!=null && !pre_1_token.equals("")){
            pre_1_Tokens.put(pre_1_token, 0);

            //generate char n-grams
            ngModel = new NGramModel();
            ngModel.add(pre_1_token,NGRAM_START,NGRAM_END);
            ngrams = ngModel.toDictionary();
            for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
              StringList ngram = iter.next();
              pre_1_TokensNgrams.put(ngram.getToken(0), 0);
            }
          }
          if(pre_2_token!=null && !pre_2_token.equals("")){
            pre_2_Tokens.put(pre_2_token, 0);

            //generate char n-grams
            ngModel = new NGramModel();
            ngModel.add(pre_2_token,NGRAM_START,NGRAM_END);
            ngrams = ngModel.toDictionary();
            for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
              StringList ngram = iter.next();
              pre_2_TokensNgrams.put(ngram.getToken(0), 0);
            }
          }
          if(post_1_token!=null && !post_1_token.equals("")){
            post_1_Tokens.put(post_1_token, 0);

            //generate char n-grams
            ngModel = new NGramModel();
            ngModel.add(post_1_token,NGRAM_START,NGRAM_END);
            ngrams = ngModel.toDictionary();
            for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
              StringList ngram = iter.next();
              post_1_TokensNgrams.put(ngram.getToken(0), 0);
            }
          }
          if(post_2_token!=null && !post_2_token.equals("")){
            post_2_Tokens.put(post_2_token, 0);

            //generate char n-grams
            ngModel = new NGramModel();
            ngModel.add(post_2_token,NGRAM_START,NGRAM_END);
            ngrams = ngModel.toDictionary();
            for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
              StringList ngram = iter.next();
              post_2_TokensNgrams.put(ngram.getToken(0), 0);
            }
          }
        }
      }
    }catch(Exception ex){
      System.out
      .println("Caught exception at NEREngine:buildTokenAndCharNgramMaps: "
          + ex.getMessage());
    }

  }

  private void initializeTokenAndCharNgramMaps(TreeMap<String, Integer> currentTokens, TreeMap<String, Integer> pre_1_Tokens, TreeMap<String, Integer> pre_2_Tokens, TreeMap<String, Integer> post_1_Tokens, TreeMap<String, Integer> post_2_Tokens, TreeMap<String, Integer> currentTokensNgrams, TreeMap<String, Integer> pre_1_TokensNgrams, TreeMap<String, Integer> pre_2_TokensNgrams, TreeMap<String, Integer> post_1_TokensNgrams, TreeMap<String, Integer> post_2_TokensNgrams) {
    try{
      Set<String> currentTokensSet = currentTokens.keySet();
      for(Iterator<String> iter=currentTokensSet.iterator();iter.hasNext();){
        currentTokens.put(iter.next(), 0);
      }

      Set<String> pre_1_TokensSet = pre_1_Tokens.keySet();
      for(Iterator<String> iter=pre_1_TokensSet.iterator();iter.hasNext();){
        pre_1_Tokens.put(iter.next(), 0);
      }

      Set<String> pre_2_TokensSet = pre_2_Tokens.keySet();
      for(Iterator<String> iter=pre_2_TokensSet.iterator();iter.hasNext();){
        pre_2_Tokens.put(iter.next(), 0);
      }

      Set<String> post_1_TokensSet = post_1_Tokens.keySet();
      for(Iterator<String> iter=post_1_TokensSet.iterator();iter.hasNext();){
        post_1_Tokens.put(iter.next(), 0);
      }

      Set<String> post_2_TokensSet = post_2_Tokens.keySet();
      for(Iterator<String> iter=post_2_TokensSet.iterator();iter.hasNext();){
        post_2_Tokens.put(iter.next(), 0);
      }

      Set<String> currentTokensNgramsSet = currentTokensNgrams.keySet();
      for(Iterator<String> iter=currentTokensNgramsSet.iterator();iter.hasNext();){
        currentTokensNgrams.put(iter.next(), 0);
      }

      Set<String> pre_1_TokensNgramsSet = pre_1_TokensNgrams.keySet();
      for(Iterator<String> iter=pre_1_TokensNgramsSet.iterator();iter.hasNext();){
        pre_1_TokensNgrams.put(iter.next(), 0);
      }

      Set<String> pre_2_TokensNgramsSet = pre_2_TokensNgrams.keySet();
      for(Iterator<String> iter=pre_2_TokensNgramsSet.iterator();iter.hasNext();){
        pre_2_TokensNgrams.put(iter.next(), 0);
      }

      Set<String> post_1_TokensNgramsSet = post_1_TokensNgrams.keySet();
      for(Iterator<String> iter=post_1_TokensNgramsSet.iterator();iter.hasNext();){
        post_1_TokensNgrams.put(iter.next(), 0);
      }

      Set<String> post_2_TokensNgramsSet = post_2_TokensNgrams.keySet();
      for(Iterator<String> iter=post_2_TokensNgramsSet.iterator();iter.hasNext();){
        post_2_TokensNgrams.put(iter.next(), 0);
      }

    }catch(Exception ex){
      System.out.println("Caught exception at NEREngine:initializeCurrentTokensMap: "+ex.getMessage());
    }
  }

  public void buildFeatureVectors(File productListingFile, String listingType, TreeMap<String, Integer> currentTokens, TreeMap<String, Integer> pre_1_Tokens, TreeMap<String, Integer> pre_2_Tokens, TreeMap<String, Integer> post_1_Tokens, TreeMap<String, Integer> post_2_Tokens, TreeMap<String, Integer> currentTokensNgrams, TreeMap<String, Integer> pre_1_TokensNgrams, TreeMap<String, Integer> pre_2_TokensNgrams, TreeMap<String, Integer> post_1_TokensNgrams, TreeMap<String, Integer> post_2_TokensNgrams, FileWriter writer){
    try{
      int indexT=0;
      BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(productListingFile)));
      String productListingsJSON = "";
      DecimalFormat df = new DecimalFormat("#.#####");
      NGramModel ngModel;
      Dictionary ngrams;
      while((productListingsJSON = bfr.readLine())!=null){
        JSONObject pListingsJSONObject = new JSONObject(productListingsJSON.trim());
        JSONObject brandProductListingJSONObject = pListingsJSONObject.getJSONObject(listingType);
        JSONArray brandProductListingJSONArr =  brandProductListingJSONObject.getJSONArray("docs");
        for(int index=0;index<brandProductListingJSONArr.length();index++){
          String classLabel = "";

          initializeTokenAndCharNgramMaps(currentTokens,pre_1_Tokens,pre_2_Tokens,post_1_Tokens,post_2_Tokens,currentTokensNgrams,pre_1_TokensNgrams,pre_2_TokensNgrams,post_1_TokensNgrams,post_2_TokensNgrams);

          JSONObject plDocObject = brandProductListingJSONArr.getJSONObject(index);
          String productListing = plDocObject.getString("product_listing").trim();
          String entityValue = plDocObject.getString("entity_value").trim();
          String pre_1_token = plDocObject.getString("pre_1_token").trim();
          String pre_2_token = plDocObject.getString("pre_2_token").trim();
          String post_1_token = plDocObject.getString("post_1_token").trim();
          String post_2_token = plDocObject.getString("post_2_token").trim();

          //build position based features
          String normalizedPositionFromStart = getNormalizedEntityPositionFromStart(productListing, entityValue, df);
          String normalizedPositionFromEnd = getNormalizedEntityPositionFromEnd(productListing, entityValue, df);

          //build orthographic features
          String containsDigit = checkContainsDigit(productListing, entityValue);
          String containsOnlyDigits = checkContainsOnlyDigits(productListing, entityValue);
          if(currentTokens.containsKey(entityValue)){
            currentTokens.put(entityValue, 1); //Identity of the entity
          }
          ngModel = new NGramModel();
          ngModel.add(entityValue,NGRAM_START,NGRAM_END);
          ngrams = ngModel.toDictionary();	//generate char n-grams
          for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
            StringList ngram = iter.next();
            if(currentTokensNgrams.containsKey(ngram.getToken(0))){
              currentTokensNgrams.put(ngram.getToken(0), 1);
            }
          }


          //build context features
          if(pre_1_Tokens.containsKey(pre_1_token)){
            pre_1_Tokens.put(pre_1_token, 1); //Identity of the 1st word before the entity
          }
          if(pre_2_Tokens.containsKey(pre_2_token)){
            pre_2_Tokens.put(pre_2_token, 1); //Identity of the 2nd word before the entity
          }
          if(post_1_Tokens.containsKey(post_1_token)){
            post_1_Tokens.put(post_1_token, 1); //Identity of the 1st word after the entity
          }
          if(post_2_Tokens.containsKey(post_2_token)){
            post_2_Tokens.put(post_2_token, 1); //Identity of the 2nd word after the entity
          }

          ngModel = new NGramModel();
          ngModel.add(pre_1_token,NGRAM_START,NGRAM_END);
          ngrams = ngModel.toDictionary();	//generate char n-grams
          for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
            StringList ngram = iter.next();
            if(pre_1_TokensNgrams.containsKey(ngram.getToken(0))){
              pre_1_TokensNgrams.put(ngram.getToken(0), 1);
            }
          }


          ngModel = new NGramModel();
          ngModel.add(pre_2_token,NGRAM_START,NGRAM_END);
          ngrams = ngModel.toDictionary();	//generate char n-grams
          for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
            StringList ngram = iter.next();
            if(pre_2_TokensNgrams.containsKey(ngram.getToken(0))){
              pre_2_TokensNgrams.put(ngram.getToken(0), 1);
            }
          }

          ngModel = new NGramModel();
          ngModel.add(post_1_token,NGRAM_START,NGRAM_END);
          ngrams = ngModel.toDictionary();	//generate char n-grams
          for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
            StringList ngram = iter.next();
            if(post_1_TokensNgrams.containsKey(ngram.getToken(0))){
              post_1_TokensNgrams.put(ngram.getToken(0), 1);
            }
          }


          ngModel = new NGramModel();
          ngModel.add(post_2_token,NGRAM_START,NGRAM_END);
          ngrams = ngModel.toDictionary();	//generate char n-grams
          for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
            StringList ngram = iter.next();
            if(post_2_TokensNgrams.containsKey(ngram.getToken(0))){
              post_2_TokensNgrams.put(ngram.getToken(0), 1);
            }
          }


          String pre_1_IsFrom = "0", pre_1_IsBy = "0", pre_1_IsAnd = "0";
          if(pre_1_token.equalsIgnoreCase("from")){
            pre_1_IsFrom = "1";
          }
          if(pre_1_token.equalsIgnoreCase("by")){
            pre_1_IsBy = "1";
          }
          if(pre_1_token.equalsIgnoreCase("and") || pre_1_token.equals("&")){
            pre_1_IsAnd = "1";
          }


          //build dictionary features
          //String isBrand = "0", isColor = "0", isSize = "0", isType = "0", isNA = "0";
          if(listingType.contains("brand")){
            //isBrand = "1";
            classLabel = "1";
          }else if(listingType.contains("color")){
            //isColor = "1";
            classLabel = "2";
          }else if(listingType.contains("size")){
            //isSize = "1";
            classLabel = "3";
          }else if(listingType.contains("type")){
            //isType = "1";
            classLabel = "4";
          }else if(listingType.contains("na")){
            //isNA = "1";
            classLabel = "5";
          }

          //TODO: write all features to training file
          indexT = writeFeaturesToFile(classLabel,normalizedPositionFromStart,normalizedPositionFromEnd,containsDigit,containsOnlyDigits,currentTokens,pre_1_Tokens,pre_2_Tokens,post_1_Tokens,post_2_Tokens,currentTokensNgrams,pre_1_TokensNgrams,pre_2_TokensNgrams,post_1_TokensNgrams,post_2_TokensNgrams,pre_1_IsFrom,pre_1_IsBy,pre_1_IsAnd,writer);
          writer.write("\n");
        }

      }
      System.out.println("writeFeaturesToFile index count: "+indexT);
    }catch(Exception ex){
      System.out.println("Caught exception at NEREngine:buildFeatureVectors: "+ex.getMessage());
    }
  }

  public void buildTestFeatureVectors(String productListing,
      String entityValue, TreeMap<String, Integer> currentTokens,
      TreeMap<String, Integer> pre_1_Tokens,
      TreeMap<String, Integer> pre_2_Tokens,
      TreeMap<String, Integer> post_1_Tokens,
      TreeMap<String, Integer> post_2_Tokens,
      TreeMap<String, Integer> currentTokensNgrams,
      TreeMap<String, Integer> pre_1_TokensNgrams,
      TreeMap<String, Integer> pre_2_TokensNgrams,
      TreeMap<String, Integer> post_1_TokensNgrams,
      TreeMap<String, Integer> post_2_TokensNgrams, FileWriter writer) {
    try{
      DecimalFormat df = new DecimalFormat("#.#####");
      NGramModel ngModel;
      Dictionary ngrams;

      String classLabel = "6";
      String pre_1_token = DataManager.getFirstTokenBeforeEntity(
          productListing, entityValue);

      String pre_2_token = DataManager.getSecondTokenBeforeEntity(
          productListing, entityValue);

      String post_1_token = DataManager.getFirstTokenAfterEntity(
          productListing, entityValue);

      String post_2_token = DataManager.getSecondTokenAfterEntity(
          productListing, entityValue);

      initializeTokenAndCharNgramMaps(currentTokens,pre_1_Tokens,pre_2_Tokens,post_1_Tokens,post_2_Tokens,currentTokensNgrams,pre_1_TokensNgrams,pre_2_TokensNgrams,post_1_TokensNgrams,post_2_TokensNgrams);


      //build position based features
      String normalizedPositionFromStart = getNormalizedEntityPositionFromStart(productListing, entityValue, df);
      String normalizedPositionFromEnd = getNormalizedEntityPositionFromEnd(productListing, entityValue, df);

      //build orthographic features
      String containsDigit = checkContainsDigit(productListing, entityValue);
      String containsOnlyDigits = checkContainsOnlyDigits(productListing, entityValue);
      if(currentTokens.containsKey(entityValue)){
        currentTokens.put(entityValue, 1); //Identity of the entity
      }

      ngModel = new NGramModel();
      ngModel.add(entityValue,NGRAM_START,NGRAM_END);
      ngrams = ngModel.toDictionary();	//generate char n-grams
      for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
        StringList ngram = iter.next();
        if(currentTokensNgrams.containsKey(ngram.getToken(0))){
          currentTokensNgrams.put(ngram.getToken(0), 1);
        }
      }

      //build context features
      if(pre_1_Tokens.containsKey(pre_1_token)){
        pre_1_Tokens.put(pre_1_token, 1); //Identity of the 1st word before the entity
      }
      if(pre_2_Tokens.containsKey(pre_2_token)){
        pre_2_Tokens.put(pre_2_token, 1); //Identity of the 2nd word before the entity
      }
      if(post_1_Tokens.containsKey(post_1_token)){
        post_1_Tokens.put(post_1_token, 1); //Identity of the 1st word after the entity
      }
      if(post_2_Tokens.containsKey(post_2_token)){
        post_2_Tokens.put(post_2_token, 1); //Identity of the 2nd word after the entity
      }

      ngModel = new NGramModel();
      ngModel.add(pre_1_token,NGRAM_START,NGRAM_END);
      ngrams = ngModel.toDictionary();	//generate char n-grams
      for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
        StringList ngram = iter.next();
        if(pre_1_TokensNgrams.containsKey(ngram.getToken(0))){
          pre_1_TokensNgrams.put(ngram.getToken(0), 1);
        }
      }

      ngModel = new NGramModel();
      ngModel.add(pre_2_token,NGRAM_START,NGRAM_END);
      ngrams = ngModel.toDictionary();	//generate char n-grams
      for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
        StringList ngram = iter.next();
        if(pre_2_TokensNgrams.containsKey(ngram.getToken(0))){
          pre_2_TokensNgrams.put(ngram.getToken(0), 1);
        }
      }

      ngModel = new NGramModel();
      ngModel.add(post_1_token,NGRAM_START,NGRAM_END);
      ngrams = ngModel.toDictionary();	//generate char n-grams
      for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
        StringList ngram = iter.next();
        if(post_1_TokensNgrams.containsKey(ngram.getToken(0))){
          post_1_TokensNgrams.put(ngram.getToken(0), 1);
        }
      }

      ngModel = new NGramModel();
      ngModel.add(post_2_token,NGRAM_START,NGRAM_END);
      ngrams = ngModel.toDictionary();	//generate char n-grams
      for(Iterator<StringList> iter=ngrams.iterator();iter.hasNext();){
        StringList ngram = iter.next();
        if(post_2_TokensNgrams.containsKey(ngram.getToken(0))){
          post_2_TokensNgrams.put(ngram.getToken(0), 1);
        }
      }

      String pre_1_IsFrom = "0", pre_1_IsBy = "0", pre_1_IsAnd = "0";
      if(pre_1_token.equalsIgnoreCase("from")){
        pre_1_IsFrom = "1";
      }
      if(pre_1_token.equalsIgnoreCase("by")){
        pre_1_IsBy = "1";
      }
      if(pre_1_token.equalsIgnoreCase("and") || pre_1_token.equals("&")){
        pre_1_IsAnd = "1";
      }


      //build dictionary features
      //String isBrand = "0", isColor = "0", isSize = "0", isType = "0", isNA = "0";

      writeFeaturesToTestFile(classLabel,normalizedPositionFromStart,normalizedPositionFromEnd,containsDigit,containsOnlyDigits,currentTokens,pre_1_Tokens,pre_2_Tokens,post_1_Tokens,post_2_Tokens,currentTokensNgrams,pre_1_TokensNgrams,pre_2_TokensNgrams,post_1_TokensNgrams,post_2_TokensNgrams,pre_1_IsFrom,pre_1_IsBy,pre_1_IsAnd,writer);
      writer.write("\n");



    }catch(Exception ex){
      System.out.println("Caught exception at NEREngine:buildFeatureVectors: "+ex.getMessage());
    }
  }

  private int writeFeaturesToFile(String classLabel,
      String normalizedPositionFromStart,
      String normalizedPositionFromEnd, String containsDigit,
      String containsOnlyDigits, TreeMap<String, Integer> currentTokens,
      TreeMap<String, Integer> pre_1_Tokens,
      TreeMap<String, Integer> pre_2_Tokens,
      TreeMap<String, Integer> post_1_Tokens,
      TreeMap<String, Integer> post_2_Tokens,
      TreeMap<String, Integer> currentTokensNgrams,
      TreeMap<String, Integer> pre_1_TokensNgrams,
      TreeMap<String, Integer> pre_2_TokensNgrams,
      TreeMap<String, Integer> post_1_TokensNgrams,
      TreeMap<String, Integer> post_2_TokensNgrams,
      String pre_1_IsFrom, String pre_1_IsBy, String pre_1_IsAnd,
      FileWriter writer) throws IOException {

    writer.write(classLabel+" ");
    if(Float.parseFloat(normalizedPositionFromStart)!=0){
      writer.write("1:"+normalizedPositionFromStart+" ");
    }
    if(Float.parseFloat(normalizedPositionFromEnd)!=0){
      writer.write("2:"+normalizedPositionFromEnd+" ");
    }
    if(!containsDigit.equals("0")){
      writer.write("3:"+containsDigit+" ");
    }
    if(!containsOnlyDigits.equals("0")){
      writer.write("4:"+containsOnlyDigits+" ");
    }

    int index = 5;
    Set<String> currentTokensSet = currentTokens.keySet();
    for(Iterator<String> iter=currentTokensSet.iterator();iter.hasNext();){
      int val = currentTokens.get(iter.next());
      if(val!=0){
        writer.write(index+":"+val+" ");
      }
      index++;
    }

    Set<String> pre_1_TokensSet = pre_1_Tokens.keySet();
    for(Iterator<String> iter=pre_1_TokensSet.iterator();iter.hasNext();){
      int val = pre_1_Tokens.get(iter.next());
      if(val!=0){
        writer.write(index+":"+val+" ");
      }
      index++;
    }

    Set<String> pre_2_TokensSet = pre_2_Tokens.keySet();
    for(Iterator<String> iter=pre_2_TokensSet.iterator();iter.hasNext();){
      int val = pre_2_Tokens.get(iter.next());
      if(val!=0){
        writer.write(index+":"+val+" ");
      }
      index++;
    }

    Set<String> post_1_TokensSet = post_1_Tokens.keySet();
    for(Iterator<String> iter=post_1_TokensSet.iterator();iter.hasNext();){
      int val = post_1_Tokens.get(iter.next());
      if(val!=0){
        writer.write(index+":"+val+" ");
      }
      index++;
    }

    Set<String> post_2_TokensSet = post_2_Tokens.keySet();
    for(Iterator<String> iter=post_2_TokensSet.iterator();iter.hasNext();){
      int val = post_2_Tokens.get(iter.next());
      if(val!=0){
        writer.write(index+":"+val+" ");
      }
      index++;
    }

    Set<String> currentTokensNgramsSet = currentTokensNgrams.keySet();
    for(Iterator<String> iter=currentTokensNgramsSet.iterator();iter.hasNext();){
      int val = currentTokensNgrams.get(iter.next());
      if(val!=0){
        writer.write(index+":"+val+" ");
      }
      index++;
    }

    Set<String> pre_1_TokensNgramsSet = pre_1_TokensNgrams.keySet();
    for(Iterator<String> iter=pre_1_TokensNgramsSet.iterator();iter.hasNext();){
      int val = pre_1_TokensNgrams.get(iter.next());
      if(val!=0){
        writer.write(index+":"+val+" ");
      }
      index++;
    }

    Set<String> pre_2_TokensNgramsSet = pre_2_TokensNgrams.keySet();
    for(Iterator<String> iter=pre_2_TokensNgramsSet.iterator();iter.hasNext();){
      int val = pre_2_TokensNgrams.get(iter.next());
      if(val!=0){
        writer.write(index+":"+val+" ");
      }
      index++;
    }

    Set<String> post_1_TokensNgramsSet = post_1_TokensNgrams.keySet();
    for(Iterator<String> iter=post_1_TokensNgramsSet.iterator();iter.hasNext();){
      int val = post_1_TokensNgrams.get(iter.next());
      if(val!=0){
        writer.write(index+":"+val+" ");
      }
      index++;
    }

    Set<String> post_2_TokensNgramsSet = post_2_TokensNgrams.keySet();
    for(Iterator<String> iter=post_2_TokensNgramsSet.iterator();iter.hasNext();){
      int val = post_2_TokensNgrams.get(iter.next());
      if(val!=0){
        writer.write(index+":"+val+" ");
      }
      index++;
    }

    if(!pre_1_IsFrom.equals("0")){
      writer.write(index+":"+pre_1_IsFrom+" ");
    }
    index++;
    if(!pre_1_IsBy.equals("0")){
      writer.write(index+":"+pre_1_IsBy+" ");
    }
    index++;
    if(!pre_1_IsAnd.equals("0")){
      writer.write(index+":"+pre_1_IsAnd+" ");
    }

    return index;
    /*		index++;
		if(!isBrand.equals("0")){
			writer.write(index+":"+isBrand+" ");
		}
		index++;
		if(!isColor.equals("0")){
			writer.write(index+":"+isColor+" ");
		}
		index++;
		if(!isSize.equals("0")){
			writer.write(index+":"+isSize+" ");
		}
		index++;
		if(!isType.equals("0")){
			writer.write(index+":"+isType+" ");
		}*/
  }

  private void writeFeaturesToFileEx(TreeMap<String, Integer> currentTokens,
      TreeMap<String, Integer> pre_1_Tokens,
      TreeMap<String, Integer> pre_2_Tokens,
      TreeMap<String, Integer> post_1_Tokens,
      TreeMap<String, Integer> post_2_Tokens,
      TreeMap<String, Integer> currentTokensNgrams,
      TreeMap<String, Integer> pre_1_TokensNgrams,
      TreeMap<String, Integer> pre_2_TokensNgrams,
      TreeMap<String, Integer> post_1_TokensNgrams,
      TreeMap<String, Integer> post_2_TokensNgrams) throws IOException {


    int index = 5;
    Set<String> currentTokensSet = currentTokens.keySet();
    for(Iterator<String> iter=currentTokensSet.iterator();iter.hasNext();){
      iter.next();
      index++;
    }

    System.out.println(index);

    Set<String> pre_1_TokensSet = pre_1_Tokens.keySet();
    System.out.println("size="+pre_1_TokensSet.size()+" index="+index);
    for(Iterator<String> iter=pre_1_TokensSet.iterator();iter.hasNext();){
      iter.next();
      //System.out.println("size="+pre_1_TokensSet.size()+" index="+index);
      index++;
    }

    System.out.println(index);

    Set<String> pre_2_TokensSet = pre_2_Tokens.keySet();
    for(Iterator<String> iter=pre_2_TokensSet.iterator();iter.hasNext();){
      iter.next();
      index++;
    }

    System.out.println(index);

    Set<String> post_1_TokensSet = post_1_Tokens.keySet();
    for(Iterator<String> iter=post_1_TokensSet.iterator();iter.hasNext();){
      iter.next();
      index++;
    }

    System.out.println(index);

    Set<String> post_2_TokensSet = post_2_Tokens.keySet();
    for(Iterator<String> iter=post_2_TokensSet.iterator();iter.hasNext();){
      iter.next();
      index++;
    }

    System.out.println(index);

    Set<String> currentTokensNgramsSet = currentTokensNgrams.keySet();
    for(Iterator<String> iter=currentTokensNgramsSet.iterator();iter.hasNext();){
      iter.next();
      index++;
    }

    System.out.println(index);

    Set<String> pre_1_TokensNgramsSet = pre_1_TokensNgrams.keySet();
    for(Iterator<String> iter=pre_1_TokensNgramsSet.iterator();iter.hasNext();){
      iter.next();
      index++;
    }

    System.out.println(index);

    Set<String> pre_2_TokensNgramsSet = pre_2_TokensNgrams.keySet();
    for(Iterator<String> iter=pre_2_TokensNgramsSet.iterator();iter.hasNext();){
      iter.next();
      index++;
    }

    System.out.println(index);

    Set<String> post_1_TokensNgramsSet = post_1_TokensNgrams.keySet();
    for(Iterator<String> iter=post_1_TokensNgramsSet.iterator();iter.hasNext();){
      iter.next();
      index++;
    }

    System.out.println(index);

    Set<String> post_2_TokensNgramsSet = post_2_TokensNgrams.keySet();
    for(Iterator<String> iter=post_2_TokensNgramsSet.iterator();iter.hasNext();){
      iter.next();
      index++;
    }

    System.out.println(index);

    index = index+2;




    System.out.println(index);
    /*		index++;
		if(!isBrand.equals("0")){
			writer.write(index+":"+isBrand+" ");
		}
		index++;
		if(!isColor.equals("0")){
			writer.write(index+":"+isColor+" ");
		}
		index++;
		if(!isSize.equals("0")){
			writer.write(index+":"+isSize+" ");
		}
		index++;
		if(!isType.equals("0")){
			writer.write(index+":"+isType+" ");
		}*/
  }

  private void writeFeaturesToTestFile(String classLabel,
      String normalizedPositionFromStart,
      String normalizedPositionFromEnd, String containsDigit,
      String containsOnlyDigits, TreeMap<String, Integer> currentTokens,
      TreeMap<String, Integer> pre_1_Tokens,
      TreeMap<String, Integer> pre_2_Tokens,
      TreeMap<String, Integer> post_1_Tokens,
      TreeMap<String, Integer> post_2_Tokens,
      TreeMap<String, Integer> currentTokensNgrams,
      TreeMap<String, Integer> pre_1_TokensNgrams,
      TreeMap<String, Integer> pre_2_TokensNgrams,
      TreeMap<String, Integer> post_1_TokensNgrams,
      TreeMap<String, Integer> post_2_TokensNgrams,
      String pre_1_IsFrom, String pre_1_IsBy, String pre_1_IsAnd,
      FileWriter writer) throws IOException {

    writer.write(classLabel+" ");

    writer.write("1:"+normalizedPositionFromStart+" ");


    writer.write("2:"+normalizedPositionFromEnd+" ");


    writer.write("3:"+containsDigit+" ");


    writer.write("4:"+containsOnlyDigits+" ");


    int index = 5;
    Set<String> currentTokensSet = currentTokens.keySet();
    for(Iterator<String> iter=currentTokensSet.iterator();iter.hasNext();){
      int val = currentTokens.get(iter.next());

      writer.write(index+":"+val+" ");

      index++;
    }

    //System.out.println(index);

    Set<String> pre_1_TokensSet = pre_1_Tokens.keySet();
    for(Iterator<String> iter=pre_1_TokensSet.iterator();iter.hasNext();){
      int val = pre_1_Tokens.get(iter.next());

      writer.write(index+":"+val+" ");

      index++;
    }

    //System.out.println(index);

    Set<String> pre_2_TokensSet = pre_2_Tokens.keySet();
    for(Iterator<String> iter=pre_2_TokensSet.iterator();iter.hasNext();){
      int val = pre_2_Tokens.get(iter.next());

      writer.write(index+":"+val+" ");

      index++;
    }

    //System.out.println(index);

    Set<String> post_1_TokensSet = post_1_Tokens.keySet();
    for(Iterator<String> iter=post_1_TokensSet.iterator();iter.hasNext();){
      int val = post_1_Tokens.get(iter.next());

      writer.write(index+":"+val+" ");

      index++;
    }

    //System.out.println(index);

    Set<String> post_2_TokensSet = post_2_Tokens.keySet();
    for(Iterator<String> iter=post_2_TokensSet.iterator();iter.hasNext();){
      int val = post_2_Tokens.get(iter.next());

      writer.write(index+":"+val+" ");

      index++;
    }

    //System.out.println(index);

    Set<String> currentTokensNgramsSet = currentTokensNgrams.keySet();
    for(Iterator<String> iter=currentTokensNgramsSet.iterator();iter.hasNext();){
      int val = currentTokensNgrams.get(iter.next());

      writer.write(index+":"+val+" ");

      index++;
    }

    //System.out.println(index);

    Set<String> pre_1_TokensNgramsSet = pre_1_TokensNgrams.keySet();
    for(Iterator<String> iter=pre_1_TokensNgramsSet.iterator();iter.hasNext();){
      int val = pre_1_TokensNgrams.get(iter.next());

      writer.write(index+":"+val+" ");

      index++;
    }

    //System.out.println(index);

    Set<String> pre_2_TokensNgramsSet = pre_2_TokensNgrams.keySet();
    for(Iterator<String> iter=pre_2_TokensNgramsSet.iterator();iter.hasNext();){
      int val = pre_2_TokensNgrams.get(iter.next());

      writer.write(index+":"+val+" ");

      index++;
    }

    //System.out.println(index);

    Set<String> post_1_TokensNgramsSet = post_1_TokensNgrams.keySet();
    for(Iterator<String> iter=post_1_TokensNgramsSet.iterator();iter.hasNext();){
      int val = post_1_TokensNgrams.get(iter.next());

      writer.write(index+":"+val+" ");

      index++;
    }

    //System.out.println(index);

    Set<String> post_2_TokensNgramsSet = post_2_TokensNgrams.keySet();
    for(Iterator<String> iter=post_2_TokensNgramsSet.iterator();iter.hasNext();){
      int val = post_2_TokensNgrams.get(iter.next());

      writer.write(index+":"+val+" ");

      index++;
    }

    //System.out.println(index);


    writer.write(index+":"+pre_1_IsFrom+" ");

    index++;

    writer.write(index+":"+pre_1_IsBy+" ");

    index++;

    writer.write(index+":"+pre_1_IsAnd+" ");

    //System.out.println("writeFeaturesToTestFile index count: "+index);
  }

  private String checkContainsOnlyDigits(String productListing, String entityValue) {
    String containsOnlyDigits = "0";
    if(productListing.matches("[0-9]*")){
      containsOnlyDigits = "1";
    }
    return containsOnlyDigits;
  }

  private String checkContainsDigit(String productListing, String entityValue) {
    String containsDigit = "0";
    if(productListing.matches("[a-zA-Z]*[0-9]*[a-zA-Z]*") || productListing.matches("[0-9]*[a-zA-Z]*") || productListing.matches("[a-zA-Z]*[0-9]*")){
      containsDigit = "1";
    }
    return containsDigit;
  }

  private String getNormalizedEntityPositionFromStart(String productListing,	String entityValue, DecimalFormat df) {
    float normalizedPositionFromStart = (productListing.indexOf(entityValue))*1.0f/productListing.length();
    return df.format(normalizedPositionFromStart);
  }

  private String getNormalizedEntityPositionFromEnd(String productListing, String entityValue, DecimalFormat df) {
    float normalizedPositionFromEnd = (productListing.length()-productListing.indexOf(entityValue))*1.0f/productListing.length();
    return df.format(normalizedPositionFromEnd);
  }

  public void trainAndSaveModel(String trainingData, String modelName){
    try{
      //populate training data instances
      Instances data = populateDataInstances(trainingData);

      //classifier
      SMO svmClassifier = new SMO();
      svmClassifier.setOptions(weka.core.Utils.splitOptions("-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
      svmClassifier.buildClassifier(data);

      //serialize the model
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("C:/Users/satapath/Documents/EntityExtraction/"+modelName));
      oos.writeObject(svmClassifier);
      oos.flush();
      oos.close();

    }catch(Exception ex){
      System.out.println("Caught Exception at NEREngine:train: "+ex.getMessage());
    }
  }

  public String extractProductEntities(String productListing, Classifier classifier){
    StringBuilder resultWithProductEntitiesTagged = new StringBuilder();
    try{
      productListing = productListing.toLowerCase();
      String[] tokens = productListing.split(" ");

      TreeMap<String, Integer> currentTokens = new TreeMap<String, Integer>();
      TreeMap<String, Integer> pre_1_Tokens = new TreeMap<String, Integer>();
      TreeMap<String, Integer> pre_2_Tokens = new TreeMap<String, Integer>();
      TreeMap<String, Integer> post_1_Tokens = new TreeMap<String, Integer>();
      TreeMap<String, Integer> post_2_Tokens = new TreeMap<String, Integer>();
      TreeMap<String, Integer> currentTokensNgrams = new TreeMap<String, Integer>();
      TreeMap<String, Integer> pre_1_TokensNgrams = new TreeMap<String, Integer>();
      TreeMap<String, Integer> pre_2_TokensNgrams = new TreeMap<String, Integer>();
      TreeMap<String, Integer> post_1_TokensNgrams = new TreeMap<String, Integer>();
      TreeMap<String, Integer> post_2_TokensNgrams = new TreeMap<String, Integer>();

      System.out.println(Thread.currentThread().getContextClassLoader()
          .getResourceAsStream("/resources/Brand_Product_Listing_Scoped.txt"));

      buildTokenAndCharNgramMaps(
          NEREngine.class
          .getResourceAsStream("/resources/Brand_Product_Listing_Scoped.txt"),
          "brand_product_listing", currentTokens, pre_1_Tokens, pre_2_Tokens,
          post_1_Tokens, post_2_Tokens, currentTokensNgrams,
          pre_1_TokensNgrams, pre_2_TokensNgrams, post_1_TokensNgrams,
          post_2_TokensNgrams);

      buildTokenAndCharNgramMaps(
          NEREngine.class
          .getResourceAsStream("/resources/Color_Product_Listing_Scoped.txt"),
          "color_product_listing", currentTokens, pre_1_Tokens, pre_2_Tokens,
          post_1_Tokens, post_2_Tokens, currentTokensNgrams,
          pre_1_TokensNgrams, pre_2_TokensNgrams, post_1_TokensNgrams,
          post_2_TokensNgrams);

      buildTokenAndCharNgramMaps(
          NEREngine.class
          .getResourceAsStream("/resources/Size_Product_Listing_Scoped.txt"),
          "size_product_listing", currentTokens, pre_1_Tokens, pre_2_Tokens,
          post_1_Tokens, post_2_Tokens, currentTokensNgrams,
          pre_1_TokensNgrams, pre_2_TokensNgrams, post_1_TokensNgrams,
          post_2_TokensNgrams);

      buildTokenAndCharNgramMaps(
          NEREngine.class
          .getResourceAsStream("/resources/Type_Product_Listing_Scoped.txt"),
          "type_product_listing", currentTokens, pre_1_Tokens, pre_2_Tokens,
          post_1_Tokens, post_2_Tokens, currentTokensNgrams,
          pre_1_TokensNgrams, pre_2_TokensNgrams, post_1_TokensNgrams,
          post_2_TokensNgrams);

      buildTokenAndCharNgramMaps(
          NEREngine.class
          .getResourceAsStream("/resources/NA_Brand_Product_Listing_Scoped.txt"),
          "na_product_listing", currentTokens, pre_1_Tokens, pre_2_Tokens,
          post_1_Tokens, post_2_Tokens, currentTokensNgrams,
          pre_1_TokensNgrams, pre_2_TokensNgrams, post_1_TokensNgrams,
          post_2_TokensNgrams);


      int tokenIndex = 0;
      for(String token:tokens){
        FileWriter writer = new FileWriter(System.getProperty("user.home")+"/testingData.dat");

        buildTestFeatureVectors(productListing, token, currentTokens,
            pre_1_Tokens, pre_2_Tokens, post_1_Tokens, post_2_Tokens,
            currentTokensNgrams, pre_1_TokensNgrams, pre_2_TokensNgrams,
            post_1_TokensNgrams, post_2_TokensNgrams, writer);
        writer.close();

        String tokenClass = classify(System.getProperty("user.home")+"/testingData.dat", classifier);

        resultWithProductEntitiesTagged.append(token+"["+tokenClass+"]");

        if(tokenIndex==5){
          resultWithProductEntitiesTagged.append("\n");
          tokenIndex = 0;
        }else{
          resultWithProductEntitiesTagged.append(" ");
          tokenIndex++;
        }
      }

    }catch(Exception ex){
      System.out
          .println("Caught exception at NEREngine:extractProductEntities "
              + ex.getMessage());
    }

    return resultWithProductEntitiesTagged.toString();
  }

  public String classify(String testData, Classifier classifier){
    String classString = "",classLabel="";
    try{
      //populate test data instances
      Instances data = populateDataInstances(testData);
      Instance instance = data.instance(0);

      //double classDouble = classifier.classifyInstance(instance);
      // classLabel = data.classAttribute().value((int)classDouble);

      double[] probArr = classifier.distributionForInstance(instance);
      double greatestProb = 0;
      for (int i = 0; i < probArr.length; i++) {
        if (greatestProb < probArr[i]) {
          greatestProb = probArr[i];
          classLabel = i + "";
        }
      }

      DecimalFormat df = new DecimalFormat("#.#");
      if (classLabel.equals("4")
          && (df
              .format((probArr[probArr.length - 1] - probArr[probArr.length - 2]))
              .equals("0.1"))) {
        classLabel = probArr.length - 2 + "";
      }

      switch (classLabel) {
      case "0":
        classString = "brand";
        break;
      case "1":
        classString = "color";
        break;
      case "2":
        classString = "size";
        break;
      case "3":
        classString = "type";
        break;
      case "4":
        classString = "other";
        break;
      }

    }catch(Exception ex){
      System.out.println("Caught Exception at NEREngine:classify: "
          + ex.getMessage());
    }

    return classString;
  }

  public void evaluateByCrossValidation(String trainingData, String modelName){
    try{
      //populate training data instances
      Instances data = populateDataInstances(trainingData);

      //deserialize the model
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:/Users/satapath/Documents/EntityExtraction/"+modelName));
      Classifier classifier = (Classifier) ois.readObject();
      ois.close();

      //evaluate classifier and print some statistics
      Evaluation eval = new Evaluation(data);
      eval.crossValidateModel(classifier, data, 1, new Random(1));
      System.out.println(eval.toSummaryString("\nResults\n======\n", false));

    }catch(Exception ex){
      System.out.println("Caught Exception at NEREngine:evaluate: "+ex.getMessage());
    }
  }

  public void evaluateByTrainTestSplit(String trainingData, String testData, String modelName){
    try{
      //populate training data instances
      Instances traininingInstances = populateDataInstances(trainingData);
      Instances testInstances = populateDataInstances(testData);

      //deserialize the model
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:/Users/satapath/Documents/EntityExtraction/"+modelName));
      Classifier classifier = (Classifier) ois.readObject();
      ois.close();

      //evaluate classifier and print some statistics
      Evaluation eval = new Evaluation(traininingInstances);
      eval.evaluateModel(classifier, testInstances);
      System.out.println(eval.toSummaryString("\nResults\n======\n", false));

    }catch(Exception ex){
      System.out.println("Caught Exception at NEREngine:evaluate: "+ex.getMessage());
    }
  }

  public Instances populateDataInstances(String dataFile){
    Instances data = null;
    try{
      SVMLightLoader svmLightLoader = new SVMLightLoader();
      svmLightLoader.setSource(new File(dataFile));
      //DataSource source = new DataSource(dataFile);
      data = svmLightLoader.getDataSet();
      if (data.classIndex() == -1){
        data.setClassIndex(data.numAttributes() - 1);
      }
      NumericToNominal filter = new NumericToNominal();
      filter.setAttributeIndices(data.classIndex()+"");
      filter.setInputFormat(data);
      data = Filter.useFilter(data, filter);
    }catch(Exception ex){
      System.out.println("Caught exception at NEREngine:populateDataInstances: "+ex.getMessage());
    }
    return data;
  }

}
