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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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
    private void addEntity(String text, long timestamp, String email)
    {
        Entity commentEntity = new Entity("Comments");
        if (text != "")
        {
            commentEntity.setProperty("commentInput", text);
            commentEntity.setProperty("timestamp", timestamp);
            commentEntity.setProperty("email", email);
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(commentEntity);
        }
    }

    /** Iterates through all comments in datastore to append only "numComments" amount of elements*/
    private HashMap<String,ArrayList<String>> hashComment(PreparedQuery results, int numComments)
    {
        HashMap<String,ArrayList<String>> entityHash = new HashMap<>();
        ArrayList<String> cArray = new ArrayList<>();
        int count = 0;
        for (Entity entity : results.asIterable()) 
        {
            if (numComments == count)
            {
                break;
            }
            String email = (String) entity.getProperty("email");
            String eachComment = (String) entity.getProperty("commentInput");
            String combine = String.format("%1$s: %2$s", email, eachComment);
            //String combine = email + ": " + eachComment;
            cArray.add(combine);
            count++;
        }
        entityHash.put("comments",cArray);
        return entityHash;
    }

    /** Get entity from datastore in a specific order and quantity to convert to json. */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        Query query = new Query("Comments").addSort("timestamp", SortDirection.DESCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);
        //"inputNumComments" will always be a numerical value due to restrictions on HTML
        int numComments = Integer.parseInt(request.getParameter("inputNumComments"));
        HashMap<String,ArrayList<String>> entityHash = hashComment(results,numComments);
        response.setContentType("application/json");
        String json = new Gson().toJson(entityHash);
        response.getWriter().println(json);

    }

    /** Stores the user's comment in datastore with the current time. */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        UserService userService = UserServiceFactory.getUserService();
        String email = userService.getCurrentUser().getEmail();
        String text = getParameter(request, "comment", "");
        long timestamp = System.currentTimeMillis();
        addEntity(text, timestamp, email);
        response.sendRedirect("/");
    }

    /* *Get value from html */
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}