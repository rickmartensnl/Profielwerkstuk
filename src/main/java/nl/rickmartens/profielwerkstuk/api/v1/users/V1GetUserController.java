/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

package nl.rickmartens.profielwerkstuk.api.v1.users;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import lombok.Getter;
import nl.rickmartens.profielwerkstuk.api.v1.users.sessions.V1SessionController;
import nl.rickmartens.profielwerkstuk.database.impl.UserManager;
import nl.rickmartens.profielwerkstuk.exceptions.DatabaseOfflineException;
import nl.rickmartens.profielwerkstuk.exceptions.DuplicateEmailException;
import nl.rickmartens.profielwerkstuk.exceptions.InvalidSyntaxException;
import nl.rickmartens.profielwerkstuk.middlewares.AuthMiddleware;
import nl.rickmartens.profielwerkstuk.utils.AllowMethods;
import nl.rickmartens.profielwerkstuk.utils.AuthenticationUtil;
import nl.rickmartens.profielwerkstuk.utils.Controller;
import io.activej.http.HttpMethod;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.sentry.Sentry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllowMethods({ HttpMethod.GET, HttpMethod.PATCH })
public class V1GetUserController implements Controller {

    public final Map<String, Controller> controllers = new HashMap<>();
    private final UUID uuid;

    public V1GetUserController(String uuid, HttpRequest httpRequest) {
        controllers.put("sessions", new V1SessionController());

        if (uuid.equalsIgnoreCase("@me")) {
            this.uuid = AuthMiddleware.getSubject(httpRequest);
        } else {
            this.uuid = UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
        }
    }

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        try {
            if (uuid == null) {
                if (AuthMiddleware.getSubject(httpRequest) == null) {
                    return HttpResponse.ofCode(401).withJson("{\"message\":\"401: Unauthorized\",\"code\":\"no (valid) token\"}");
                } else {
                    return HttpResponse.ofCode(400).withJson("{\"message\":\"400: Bad Request\",\"code\":\"no uuid\"}");
                }
            }

            UserManager.User user = UserManager.getUserManager().getUser(uuid);

            if (user == null) {
                return HttpResponse.notFound404().withJson("{\"message\":\"404: Not Found\",\"code\":\"user not found\"}");
            }

            String[] path = httpRequest.getRelativePath().toLowerCase().split("/");
            String controllerName;
            if (isVersioned(httpRequest)) {
                controllerName = path.length >= 4 ? path[3] : null;
            } else {
                controllerName = path.length >= 3 ? path[2] : null;
            }

            Controller controller = controllers.get(controllerName);

            if (controller != null) {
                if (!isAllowedMethod(httpRequest, controller)) {
                    return methodNotAllowed405();
                }

                HttpResponse res = callMiddleware(httpRequest, controller);

                if (res == null) {
                    return controller.runRequest(httpRequest);
                } else {
                    return res;
                }
            } else {
                UUID authUUID = AuthMiddleware.getSubject(httpRequest);

                if (authUUID == null) {
                    return HttpResponse.ok200().withJson(user.getJsonObject());
                }

                if (user.getUuid().equals(authUUID)) {
                    switch (httpRequest.getMethod()) {
                        case GET:
                            return HttpResponse.ok200().withJson(user.getJsonObject(true));
                        case PATCH:
                            return modifyUser(httpRequest, user);
                    }
                }

                return HttpResponse.ok200().withJson(user.getJsonObject());
            }
        } catch (DatabaseOfflineException exception) {
            return HttpResponse.ofCode(500).withJson("{\"message\":\"500: Internal Server Error\",\"code\":\"" + Sentry.getLastEventId() + "\"}");
        }
    }


    public @NotNull HttpResponse modifyUser(HttpRequest httpRequest, UserManager.User user) {
        ModifyUserBody modifyUserBody = new Gson().fromJson(JsonParser.parseString(httpRequest.getBody().getString(StandardCharsets.UTF_8)), ModifyUserBody.class);
    
        int previousFlags = user.getFlags();
        
        if (modifyUserBody.getDarkMode() != null) {
            if (modifyUserBody.getDarkMode()) {
                previousFlags = previousFlags | 0x2;
            } else {
                previousFlags &= ~0x2;
            }
        }
        
        if (modifyUserBody.getDyslexiaMode() != null) {
            if (modifyUserBody.getDyslexiaMode()) {
                previousFlags = previousFlags | 0x1;
            } else {
                previousFlags &= ~0x1;
            }
        }
    
        user.setFlags(previousFlags);
        
        if (modifyUserBody.getEmail() != null) {
            if (!AuthenticationUtil.isValidEmail(modifyUserBody.getEmail())) {
                return HttpResponse.ofCode(400).withJson("{\"message\":\"400: Bad Request\",\"code\":0}");
            }
            
            // TODO: Fix update email.
        }
    
        try {
            user.save();
        } catch (DatabaseOfflineException e) {
            e.printStackTrace();
        }
    
        return HttpResponse.ok200().withJson(user.getJsonObject(true));
    }
    
    @Getter
    private class ModifyUserBody {
        
        protected final Boolean darkMode;
        protected final Boolean dyslexiaMode;
        protected final String email;
        
        public ModifyUserBody(@Nullable Boolean darkMode, @Nullable Boolean dyslexiaMode, @Nullable String email) {
            this.darkMode = darkMode;
            this.dyslexiaMode = dyslexiaMode;
            this.email = email;
        }
        
    }

}
