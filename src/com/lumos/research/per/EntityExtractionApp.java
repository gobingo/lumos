package com.lumos.research.per;
import com.lumos.research.per.ui.PERFrame;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import weka.classifiers.Classifier;
import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;
import java.io.ObjectInputStream;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


public class EntityExtractionApp
{
  public static void main(String arg[])
  {
    try
    {
      JFrame loadingFrame = new JFrame();
      loadingFrame.setTitle("Product Entity Extractor");
      loadingFrame.setSize(400, 100);
      loadingFrame.setLocationRelativeTo(null);
      DefaultFormBuilder loadingFrameBuilder = new DefaultFormBuilder(new FormLayout(""));
      loadingFrameBuilder.getContainer().setBackground(new Color(0x2b3856));
      loadingFrameBuilder.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      loadingFrameBuilder.appendColumn("right:pref");
      loadingFrame.getContentPane().setBackground(new Color(0x2b3856));
      JLabel loadingFrameLabel = new JLabel();
      loadingFrameLabel.setFont(new Font("Calibri", Font.PLAIN, 20));
      loadingFrameLabel.setForeground(new Color(0xfff9ee));
      loadingFrameLabel.setText("Starting NER Engine...");
      loadingFrameBuilder.append(loadingFrameLabel);
      loadingFrame.add(loadingFrameBuilder.getPanel());
      loadingFrame.setVisible(true);
      loadingFrame.setDefaultCloseOperation(PERFrame.EXIT_ON_CLOSE);

      PERFrame perFrame=new PERFrame();
      perFrame.setSize(800,400);
      perFrame.setLocationRelativeTo(null);
      perFrame.setResizable(false);

      //deserialize the model
      ObjectInputStream ois = new ObjectInputStream(
          EntityExtractionApp.class
              .getResourceAsStream("/resources/per_5class900I_v3.model"));
      Classifier classifier = (Classifier) ois.readObject();
      ois.close();

      InputStream modelIn = EntityExtractionApp.class
          .getResourceAsStream("/resources/en-token.bin");
      TokenizerModel model = new TokenizerModel(modelIn);
      Tokenizer tokenizer = new TokenizerME(model);

      loadingFrameLabel.setText("NER Engine ready...");
      Thread.sleep(2000);
      loadingFrame.setVisible(false);

      perFrame.setClassifier(classifier);
      perFrame.setTokenizer(tokenizer);
      perFrame.setVisible(true);
      perFrame.setDefaultCloseOperation(PERFrame.EXIT_ON_CLOSE);
    }
    catch(Exception e){
      JOptionPane.showMessageDialog(null, e.getMessage());
    }
  }
}




