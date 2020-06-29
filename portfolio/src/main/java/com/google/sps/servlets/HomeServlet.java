package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.HashMap;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Page that displays login status of the user.*/
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    public HashMap<String,String> addToHash(String status, String urlText)
    {
        HashMap<String,String> loginHash = new HashMap<>();
        loginHash.put("loginStatus",status);
        loginHash.put("url", urlText);
        return loginHash;
    }
/** Checks the current status and acts accordingly to display the right interface. */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        UserService userService = UserServiceFactory.getUserService();
        HashMap<String,String> loginHash;
        if (userService.isUserLoggedIn())
        {
            String logoutUrl = userService.createLogoutURL("/");
            loginHash = addToHash("true",logoutUrl);
        }
        else
        {
            String loginUrl = userService.createLoginURL("/");
            loginHash = addToHash("false",loginUrl);
        }
        response.setContentType("application/json");
        String json = new Gson().toJson(loginHash);
        response.getWriter().println(json);
    }


}