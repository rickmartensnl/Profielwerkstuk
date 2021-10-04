package com.example.utils;

import com.example.database.impl.UserManager;

public interface UserController extends Controller {

    UserManager.User getUser();
    void setUser(UserManager.User user);

}
