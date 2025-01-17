/* Copyright 2022 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
*/

package cloudcode.helloworld.web;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.aiplatform.v1.EndpointName;
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GenerateImageSample {
  static String bytesBase64Encoded = "";
  static String bytesBase64EncodedOuput = "";
    public static void main(String[] args) throws IOException {
      String projectId = "abis-345004";
      String location = "us-central1";
      String prompt = "Generate a cartoon performing the following Yoga pose: Malasana, known as Garland Pose or Yogi Squat, is a deep squatting posture that offers numerous physical and energetic benefits To perform the pose, start with feet wider than hipwidth apart, toes slightly turned outwards As you bend your knees, lower your hips down between your legs, aiming to keep your heels on the floor; if your heels lift, support them with a blanket or mat The torso drops slightly forward as the upper arms come inside the knees, with elbows pressing gently against the inner thighs while you bring your palms together in prayer position at your heart center Lengthen your spine, lifting and extending the torso while keeping your shoulders relaxed, and draw the belly button towards the spine to engage the core The tailbone reaches down towards the earth, and the crown of the head reaches up The breathing pace is typically an exhale as you move into the squat, with smooth breaths throughout the hold, and a final exhale as you release the pose by bringing the fingertips to the floor and slowly straightening the legs into a forward fold The neck is relaxed, aligned with the spine.";
     // generateImage(projectId, location, prompt);
    }
  
    // Generate an image using a text prompt using an Imagen model
    public String generateImage(String projectId, String location, String imageString, String prompt)
        throws ApiException, IOException {
      final String endpoint = String.format("%s-aiplatform.googleapis.com:443", location);
      PredictionServiceSettings predictionServiceSettings =
      PredictionServiceSettings.newBuilder().setEndpoint(endpoint).build();
      String selectedResponseFlag ="";
      String selectedResponseString = "";
      String maskPath = "/img/mask_img.JPG";
      //String maskBase64 = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(maskPath)));
      //String context = "Generate a photo-realistic full-length standing image of a person similar to the one in the input image with an outfit and styling match described in the following prompt:  ";
      String context = "Generate a photo-realistic image of the person with their outfit paired with the styling match described in the following prompt:  ";
      String post_context = "  Focus on enhancing and styling the existing outfit by adding matching bottom items and other details described in the prompt. Maintain realistic features and avoid distortions or unnatural elements. Most importantly, maintain the gender, racial and facial features of the person in the uploaded image for generating your output image. Prioritize generating a high-quality, aesthetically pleasing image suitable for fashion recommendations. ";
    
      /* System.out.format("SELECTED RESPONSE: %s\n", selectedResponse);
      if (selectedResponse != null && !selectedResponse.isEmpty()) {
        selectedResponseFlag = "YES";
        //context = "Generate a photo-realistic full-length standing image of a person similar to teh one in the input image wearing an exact outfit as described in the following outfit description, paired with what they are already wearing on top:  ";
        prompt = context + selectedResponse + post_context;
        System.out.format("SELECTED RESPONSE YES");
      }else{  selectedResponseFlag = "NO"; }
      */
       
        prompt = context + prompt + post_context;
   System.out.format("PROMPT: %s\n", prompt);

      // Initialize client that will be used to send requests. This client only needs to be created
      // once, and can be reused for multiple requests.
      try (PredictionServiceClient predictionServiceClient =
          PredictionServiceClient.create(predictionServiceSettings)) {
  
        final EndpointName endpointName =
            EndpointName.ofProjectLocationPublisherModelName(
                projectId, location, "google", "imagen-3.0-generate-001"); //"imagegeneration@006"; imagen-3.0-generate-001

        Map<String, String> imageMap = new HashMap<>();
        imageMap.put("bytesBase64Encoded", imageString);
        Integer[] classes = new Integer[] {125};

        /* Map<String, Object> maskMap = new HashMap<>();
        maskMap.put("maskType", "semantic");
        maskMap.put("classes", classes);
        Map<String, Map> imageMaskMap = new HashMap<>();
        imageMaskMap.put("mask", maskMap);

        Map<String, Object> imageContextMap = new HashMap<>();
              imageContextMap.put("image", imageMap);
              imageContextMap.put("mask", maskMap); */

        Map<String, Object> imageContextMap = new HashMap<>();
        imageContextMap.put("image", imageMap);

        Map<String, Object> instancesMap = new HashMap<>();
        instancesMap.put("prompt", prompt);
        //instancesMap.put("image", imageMap);

        instancesMap.put("image_context", imageContextMap);
       //instancesMap.put("editMode", "inpainting-insert");
       //instancesMap.put("maskMode", imageMaskMap);

       Value instances = mapToValue(instancesMap);
  
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("sampleCount", 1);
        // You can't use a seed value and watermark at the same time.
       // paramsMap.put("seed", 1);
        // paramsMap.put("addWatermark", false);
        paramsMap.put("aspectRatio", "1:1");
        paramsMap.put("safetyFilterLevel", "block_few");
        paramsMap.put("personGeneration", "allow_adult");
        paramsMap.put("guidanceScale", 21);
        
      paramsMap.put("imagenControlScale", 0.95); //Setting imagenControlScale
        Value parameters = mapToValue(paramsMap);
  
        PredictResponse predictResponse =
            predictionServiceClient.predict(
                endpointName, Collections.singletonList(instances), parameters);
  
        for (Value prediction : predictResponse.getPredictionsList()) {
          Map<String, Value> fieldsMap = prediction.getStructValue().getFieldsMap();
          if (fieldsMap.containsKey("bytesBase64Encoded")) {
            bytesBase64EncodedOuput = fieldsMap.get("bytesBase64Encoded").getStringValue();
          //Path tmpPath = Files.createTempFile("imagen-", ".png");
          //Files.write(tmpPath, Base64.getDecoder().decode(bytesBase64Encoded));
          //System.out.format("Image file written to: %s\n", tmpPath.toUri());
        }
      }
      return bytesBase64EncodedOuput.toString();
    }
  }

  private static Value mapToValue(Map<String, Object> map) throws InvalidProtocolBufferException {
    Gson gson = new Gson();
    String json = gson.toJson(map);
    Value.Builder builder = Value.newBuilder();
    JsonFormat.parser().merge(json, builder);
    return builder.build();
  }
}