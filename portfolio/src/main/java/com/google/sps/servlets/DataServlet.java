// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import java.util.HashMap;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    private String convertToJson(HashMap<String, String> commentsHash) {
        Gson gson = new Gson();
        String json = gson.toJson(commentsHash);
        return json;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        HashMap<String, String> comments = new HashMap<String, String>(); 
        comments.put("Year", "Summer 2020");
        comments.put("Position", "STEP Intern");
        comments.put("Team", "Croupier");
        response.setContentType("application/json;");
        String json = convertToJson(comments);
        response.getWriter().println(json);
    }
}
