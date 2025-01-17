package cloudcode.helloworld.web;
/*
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
*/

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import java.util.HashMap;
import java.util.ArrayList;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.generativeai.preview.ContentMaker;
import com.google.cloud.vertexai.generativeai.preview.GenerativeModel;
import com.google.cloud.vertexai.generativeai.preview.PartMaker;
import com.google.cloud.vertexai.generativeai.preview.ResponseHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import java.io.*;
import java.nio.file.Paths;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import jakarta.xml.bind.DatatypeConverter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import com.google.gson.Gson;

import java.io.OutputStream;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.Base64;
import cloudcode.helloworld.web.GenerateImageSample;

/** Defines a controller to handle HTTP requests */
@RestController
public class HelloWorldController {

  private static final Logger logger = Logger.getLogger(HelloWorldController.class.getName());
  public static final String PROJECT_ID = "abis-345004";
  public static final String LOCATION = "us-central1";

  /**
   * Create an endpoint for the landing page
   *
   */
  @GetMapping("/")
    public ModelAndView home(ModelMap map, Prompt prompt) throws Exception{
      map.addAttribute("response", "");
      map.addAttribute("description", "");
      return new ModelAndView("index", map);
    }
    

  @PostMapping("/style")
  public ModelAndView descPic(ModelMap map, Prompt prompt) throws Exception {
    String response = prompt.getResponse();
    String description = prompt.getDescription();
    String style = prompt.getStyle();
    String show = prompt.getShow();
   // String selectedResponse = prompt.getSelectedResponse();
    String imageString = "";
    System.out.println("BACKEND CALLED. Here are the 4 values:");
    System.out.println(description);
    //System.out.println(response);
    System.out.println(style);
    System.out.println(show);
    
    if(response != null && !response.equals("")){
      System.out.println("NONULL RESPONSE");
      response = response.replace("data:image/jpeg;base64,", "");
      String base64Image = response;
      byte[] decodedImage = DatatypeConverter.parseBase64Binary(base64Image);
      System.out.println("DESCRIPTION: " + description);
      System.out.println("STYLE: " + style);
      System.out.println("SHOW: " + show);
      if(show != null && !show.equals("ON")){
        System.out.println("INSIDE SHOW OFF: ");
        description = validate(decodedImage);
        if(description.contains("STYLE RECOMMENDATION:")){
          String searchText = "";
          System.out.println("INSIDE SHOW OFF AND DESC CONTAINS DESIRED TEXT: ");
          try{
            searchText = description.split("STYLE RECOMMENDATION: ")[1].toString();
            ArrayList<ArrayList<String>> recommendationresults = databaseRecommendationEngine(searchText);
            ArrayList<String> recommendation = recommendationresults.get(0);
            ArrayList<String> recommendationdesc = recommendationresults.get(1);
            
            map.addAttribute("recommendation", recommendation);
            map.addAttribute("recommendationdesc", recommendationdesc);
            map.addAttribute("description", description); 
          }catch(Exception e){
            System.out.println(e);
            map.addAttribute("description", "Please try again for a good style recommendation."); 
          }
       }
      }else{
        System.out.println("INSIDE SHOW ON OR NULL: ");
        if(show != null && show.equals("ON") && description != null && !description.equals("")){
          try{
            GenerateImageSample imageGen = new GenerateImageSample();
            String project = "abis-345004";
            String location = "us-central1";
            String imageInput = Base64.getEncoder().encodeToString(decodedImage);
            String base64 = imageGen.generateImage(project, location, imageInput, description);
            map.addAttribute("description", description); 
            map.addAttribute("imagestring", "data:image/jpg;base64,"  + base64);
        }catch(Exception e){
          System.out.println(e);
          map.addAttribute("description", "Please try again."); 
        }
        } else{
          map.addAttribute("description", "Missing styling detail. Please try again."); 
        }
        
      }
      
  }else{
    map.addAttribute("description", "Image not recognized. Please try again."); 
    return new ModelAndView("index", map);
  }
    
     // map.addAttribute("description", "Please try again."); 
      return new ModelAndView("index", map);
    
  }



  /* Method that is invoked to do Vector Search against database data.
        */
        public ArrayList<ArrayList<String>> databaseRecommendationEngine(String searchText) throws Exception{
          ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
          ArrayList<String> resuri = new ArrayList<String>();
          ArrayList<String> resmatch = new ArrayList<String>();
          String result = "";
          String endpoint = "https://us-central1-abis-345004.cloudfunctions.net/retail-engine-with-selected-match";
          //code to invoke the endpoint and pass the string request to retrieve Gemini validated vector search results
          System.out.println("Inside calling endpoint function*******************");
          try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Create JSON payload
            Gson gson = new Gson();
            Map<String, String> data = new HashMap<>();
            data.put("search", searchText);
            String jsonInputString = gson.toJson(data);
            

            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);			
            }

            int responseCode = conn.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                   }
                in.close(); 
                String jsonString = response.toString(); 
                // Parse JSON array response 
                try {
                  JsonArray jsonArray = JsonParser.parseString(jsonString).getAsJsonArray();
                  for (JsonElement element : jsonArray) {
                      JsonObject jsonObject = element.getAsJsonObject();
                      String uri = jsonObject.get("uri").getAsString();
                      String matchdescription = jsonObject.get("matchdescription").getAsString();
                      if(matchdescription == null || matchdescription.equals("") || matchdescription.isEmpty() ){
                          matchdescription = "No description";
                      }
                      resuri.add(uri); 
                      resmatch.add(matchdescription);
                  }
              } catch (Exception e) {  // Handle invalid JSON
                  System.err.println("Error parsing JSON: " + e.getMessage());
              }
            } else {
                System.out.println("POST request not worked");
            }
           }catch(Exception e){
             System.out.println(e);
           }
           res.add(resuri);
           res.add(resmatch);
          return res;
        }


       

   /* Method that is invoked when the user clicks the describe picture button.
        */
        public String validate(byte[] baseline_url) throws IOException{
          String res = "";
            try (VertexAI vertexAi = new VertexAI(PROJECT_ID, LOCATION); ) {
              GenerationConfig generationConfig =
                  GenerationConfig.newBuilder()
                      .setMaxOutputTokens(2048)
                      .setTemperature(0.4F)
                      .setTopK(32)
                      .setTopP(1)
                      .build();
                      
            GenerativeModel model = new GenerativeModel("gemini-pro-vision", generationConfig, vertexAi);
            String context = 
            "The attached image is an image of a person in the foreground with a specific top outfit. The image may contain other details in the background or may not be containing any outfit in the image. Ignore other background details of the image and only describe the outfit the person in the image is wearing. If the image does not show any top wear or outfit, please respond with a warning message: 'Please snap a picture with a tee shirt!'. If the image contains a teeshirt or a similar top wear outfit, then describe the image as it is without any prefix, you do not need to start with 'a photo of a' or 'a picture of a'. Just describe it. Do not make up description on your own, only describe if there is a top outfit in the picture. Example description text: A white tee shirt with blue floral patterns on it visibly cotton in material. Then for the above description text, follow it up with bottom wear outfit recommendation. Recommend 5 independent bottom wear (STRICTLY should recommend bottom wear items only- pants/skirts/jeans/shorts/trousers/leggings) outfits texts separated by commas, that go stylish and fashionably with the top wear outfit description text. Make sure the bottomwear recommendation is not very broad, but specific in terms of color, style and material. Just return the recommendation part of it starting with the prefix 'STYLE RECOMMENDATION: '. Do not return the original description of the tee shirt or top outfit. So your final response should be in the same structure as the following example: A white tee shirt with blue floral patterns on it visibly cotton in material. STYLE RECOMMENDATION: <<placeholder for your recommendations>>"; 
            Content content = ContentMaker.fromMultiModalData(
             context,
             PartMaker.fromMimeTypeAndData("image/png", readImageFile_bytes(baseline_url))
            ); 
             GenerateContentResponse response = model.generateContent(content);
             res = ResponseHandler.getText(response);
          }catch(Exception e){
            System.out.println(e);
          }
          return res;
        }
        
  public static byte[] readImageFile_bytes(byte[] url) throws IOException {
    return url;
  }
   

}
