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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    private void addEntity(String text, long timestamp)
    {
        Entity commentEntity = new Entity("Comments");
        commentEntity.setProperty("commentInput", text);
        commentEntity.setProperty("timestamp", timestamp);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);
    }

    private HashMap<String,ArrayList<String>> hashComment(PreparedQuery results, int nComments)
    {
        HashMap<String,ArrayList<String>> entityHash = new HashMap<>();
        ArrayList<String> cArray = new ArrayList<>();
        int count = 0;
        for (Entity entity : results.asIterable()) 
        {
            if (nComments != count)
            {
            String eachComment = (String) entity.getProperty("commentInput");
            cArray.add(eachComment);
            entityHash.put("comments",cArray);
            count++;
            }
            else
            {
                break;
            }
        }
        return entityHash;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        Query query = new Query("Comments").addSort("timestamp", SortDirection.DESCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        int num = Integer.parseInt(request.getParameter("nComment"));
        HashMap<String,ArrayList<String>> entityHash = hashComment(results,num);
        response.setContentType("application/json");
        String json = new Gson().toJson(entityHash);
        response.getWriter().println(json);

    }

    //push values to datastore
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String text = getParameter(request, "comment", "");
        long timestamp = System.currentTimeMillis();
        addEntity(text, timestamp);
        response.sendRedirect("/");
    }

    //reading text input from HTML
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}