package com.mauryaa.research.per.ui;

import com.mauryaa.research.per.NEREngine;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import opennlp.tools.tokenize.Tokenizer;
import weka.classifiers.Classifier;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings({ "deprecation", "serial" })
public class PERFrame extends JFrame implements ActionListener
{
  JButton submitButton;
  DefaultFormBuilder queryPanel, resultPanel;
  JLabel queryLabel;
  JTextArea resultLabel;
  final JTextField  queryText;
  private Classifier classifier;
  private Tokenizer tokenizer;

  public PERFrame(){
    queryLabel = new JLabel();
    queryLabel.setText("Enter product listing: ");
    queryLabel.setFont(new Font("Calibri", Font.PLAIN, 20));
    queryLabel.setForeground(new Color(0xfff9ee));
    queryText = new JTextField(50);
    resultLabel = new JTextArea();
    resultLabel.setFont(new Font("Calibri", Font.PLAIN, 20));
    resultLabel.setForeground(new Color(0xfff9ee));
    resultLabel.setSize(50, 50);
    resultLabel.setBackground(new Color(0x2b3856));

    submitButton=new JButton("Submit");

    queryPanel = new DefaultFormBuilder(new FormLayout(""));
    queryPanel.getContainer().setBackground(new Color(0x2b3856));
    queryPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    queryPanel.appendColumn("right:pref");
    queryPanel.appendColumn("3dlu");
    queryPanel.appendColumn("fill:max(pref; 100px)");
    queryPanel.appendColumn("5dlu");
    queryPanel.appendColumn("right:pref");
    queryPanel.appendColumn("3dlu");
    queryPanel.appendColumn("fill:max(pref; 100px)");
    queryPanel.append(queryLabel,queryText);
    queryPanel.nextLine();
    queryPanel.append(ButtonBarFactory.buildCenteredBar(submitButton),5);
    add(queryPanel.getPanel(),BorderLayout.NORTH);
    resultPanel = new DefaultFormBuilder(new FormLayout(""));
    resultPanel.getContainer().setBackground(new Color(0x2b3856));
    resultPanel.setBorder(BorderFactory.createTitledBorder(null,"Result",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION,new Font("Calibri", Font.BOLD, 20),new Color(0xfff9ee)));
    resultPanel.appendColumn("right:pref");
    resultPanel.append(resultLabel);
    add(resultPanel.getPanel(),BorderLayout.CENTER);

    submitButton.addActionListener(this);
    setTitle("Product Entity Extractor");
  }


  public void setClassifier(Classifier classifier) {
    this.classifier = classifier;
  }


  public void setTokenizer(Tokenizer tokenizer) {
    this.tokenizer = tokenizer;
  }


  @Override
  public void actionPerformed(ActionEvent ae)
  {
    String queryString=queryText.getText();
    if(queryString.split(" ").length>=3){
      NEREngine nerEngine = new NEREngine();
      String productListingWithPER = nerEngine.extractProductEntities(queryString, this.classifier);//"mo7 mens denim shorts"
      System.out.println(productListingWithPER);
      resultLabel.setText(productListingWithPER);
    }else{
      System.out.println("Product listing too short, please try again with a valid product listing.");
      resultLabel.setText("Product listing too short, please try again with a valid product listing.");
    }
  }
}