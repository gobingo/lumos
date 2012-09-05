package com.mauryaa.research.per.utilities;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class Utilities {

  public static File getFileFromURL(URL resource) {
    File f;
    try {
      f = new File(resource.toURI());
    } catch (URISyntaxException ex) {
      f = new File(resource.getPath());
    }
    return f;
  }

}
