/* Copyright 2022 Google LLC

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     https://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package cloudcode.helloworld.web;

public class Prompt {
    private String response;
    private String selectedResponse;
    private String description;
    private String style;
    private String show;

    public String getSelectedResponse() {
        return selectedResponse;
    }

    public void setSelectedResponse(String selectedResponse) {
        this.selectedResponse = selectedResponse;
    }

    
    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public String getResponse() {
        return response;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}