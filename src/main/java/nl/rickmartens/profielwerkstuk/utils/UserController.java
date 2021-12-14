package nl.rickmartens.profielwerkstuk.utils;

import nl.rickmartens.profielwerkstuk.database.impl.UserManager;

public interface UserController extends Controller {

    UserManager.User getUser();
    void setUser(UserManager.User user);

}
