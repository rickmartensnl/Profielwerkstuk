/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

package nl.rickmartens.profielwerkstuk.utils;

import nl.rickmartens.profielwerkstuk.database.impl.UserManager;

public interface UserController extends Controller {

    UserManager.User getUser();
    void setUser(UserManager.User user);

}
