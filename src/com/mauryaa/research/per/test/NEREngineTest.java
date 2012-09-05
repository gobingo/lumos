package com.mauryaa.research.per.test;

import com.mauryaa.research.per.NEREngine;
import com.mauryaa.research.per.dm.DataManager;
import weka.classifiers.Classifier;
import java.io.ObjectInputStream;


public class NEREngineTest {

  /**
   * @param args
   */

  public static void main(String[] args){
    try{

      //Data collection phase
      DataManager dataManager = new DataManager();
      dataManager.collectNERData();
      dataManager.collectNAListingData();

      //Build feature vector space
      NEREngine nerEngine = new NEREngine();
      nerEngine.buildFeatureVectorsHelper();


      //deserialize the model
      ObjectInputStream ois = new ObjectInputStream(
          NEREngineTest.class
          .getResourceAsStream("/resources/per_5class900I_v3.model"));
      Classifier classifier = (Classifier) ois.readObject();
      ois.close();

      // Test data:
      // O'Neill Monster Freak Black 4-Way Stretch Boardshort - amazon
      // NutraLuxe Lash MD 4.5-ml Eyelash Conditioner - overstock
      //next blue petite bootcut jeans size 12 BNWT - ebay
      // mo7 men's denim shorts - overstock
      //PUMA Women's Sunny 2 WNS Golf Shirts
      //Champion Men's Long Mesh Short With Pockets - amazon
      // Robert Rodriguez Women's Pleated Paper Bag Short
      // Calvin Klein Girls 2-6x Cks 3 Piece Long Pant And Long Sleeve Top Set - amazon

      // Product entity extraction with learnt model
      String productListingWithPER = nerEngine.extractProductEntities(
          "nike jog shoes", classifier);
      System.out.println(productListingWithPER);

    }catch(Exception ex){
      ex.printStackTrace();
    }


  }
}
