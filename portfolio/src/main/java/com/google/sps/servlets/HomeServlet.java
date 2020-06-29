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

/** Checks the current status and acts accordingly to display the right interface. */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");
        HashMap<String,String> loginHash = new HashMap<>();
        UserService userService = UserServiceFactory.getUserService();
        if (userService.isUserLoggedIn())
        {
            String userEmail = userService.getCurrentUser().getEmail();
            String urlToRedirectToAfterUserLogsOut = "/";
            String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
            loginHash.put("loginStatus","true");
            loginHash.put("url", logoutUrl);
        }

        else
        {
            String urlToRedirectToAfterUserLogsIn = "/";
            String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
            loginHash.put("loginStatus","false");
            loginHash.put("url", loginUrl);
        }
        response.setContentType("application/json");
        String json = new Gson().toJson(loginHash);
        response.getWriter().println(json);
    }
}