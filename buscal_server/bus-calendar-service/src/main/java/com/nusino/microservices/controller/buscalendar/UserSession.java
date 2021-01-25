/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.controller.buscalendar;

import com.nusino.microservices.exception.AuthorizationException;
import com.nusino.microservices.model.buscalendar.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Deprecated
@Service
public class UserSession {
    public static String SUPER_ADMIN_ROLE = "SUPER_ADMIN_ROLE";
    public static String ADMIN_ROLE = "ADMIN_ROLE";
    public static String API_ROLE = "API_ROLE";


    public static final String USER_SESSION_ID = "USER_SESSION";

    public void storeUserInfo(HttpServletRequest request, User user) {
        request.getSession(true).setAttribute(USER_SESSION_ID, user);
    }

    public User fetchUserInfo(HttpServletRequest request) {
        return (User) request.getSession(true).getAttribute(USER_SESSION_ID);
    }

    public void saveAttribute(HttpServletRequest request, User user) {
        request.getSession(true).setAttribute(user.getClass().getName(), user);
    }

    public <T> T fetchUserInfo(HttpServletRequest request, Class<T> clazz) {
        return (T) request.getSession(true).getAttribute(clazz.getName());
    }

    public User loginCheck(HttpServletRequest request) throws AuthorizationException {
        User user = (User) request.getSession(true).getAttribute(USER_SESSION_ID);
        if (user == null) {
            throw new AuthorizationException(AuthorizationException.CODE.REQUIRED_LOGIN, "Please login again.");
        }
        return user;
    }

    public void logout(HttpServletRequest request) throws AuthorizationException {
        User userInfo = (User) request.getSession(true).getAttribute(USER_SESSION_ID);
        if (userInfo != null) {
            Enumeration<String> keys = request.getSession(true).getAttributeNames();
            while (keys.hasMoreElements()) {
                request.getSession(true).removeAttribute(keys.nextElement());
            }
        }
    }


}
