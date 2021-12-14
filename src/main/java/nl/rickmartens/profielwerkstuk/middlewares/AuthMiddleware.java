package nl.rickmartens.profielwerkstuk.middlewares;

import com.auth0.jwt.interfaces.DecodedJWT;
import nl.rickmartens.profielwerkstuk.database.impl.UserManager;
import nl.rickmartens.profielwerkstuk.exceptions.DatabaseOfflineException;
import nl.rickmartens.profielwerkstuk.exceptions.TokenVerifyException;
import nl.rickmartens.profielwerkstuk.utils.AuthenticationUtil;
import nl.rickmartens.profielwerkstuk.utils.Controller;
import nl.rickmartens.profielwerkstuk.utils.UserController;
import io.activej.http.HttpHeaders;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.sentry.Sentry;
import io.sentry.protocol.User;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AuthMiddleware extends Middleware {

    @Override
    public HttpResponse handle(HttpRequest httpRequest, Controller controller) {
        UUID uuid = getSubject(httpRequest);

        if (uuid == null) {
            return HttpResponse.ofCode(401).withJson("{\"message\":\"401: Unauthorized\",\"code\":0}");
        }

        try {
            UserManager.User myUser = UserManager.getUserManager().getUser(uuid);

            if (myUser == null) {
                return HttpResponse.ofCode(401).withJson("{\"message\":\"401: Unauthorized\",\"code\":0}");
            }

            if (controller instanceof UserController) {
                UserController userController = (UserController) controller;

                userController.setUser(myUser);
            }

            Sentry.configureScope(scope -> {
                User sentryUser = scope.getUser();
                assert sentryUser != null;
                sentryUser.setEmail(myUser.getEmail());
                sentryUser.setId(myUser.getUuid().toString());
                sentryUser.setUsername(myUser.getUsername());
                Sentry.setUser(sentryUser);
            });
        } catch (DatabaseOfflineException e) {
            return HttpResponse.ofCode(500).withJson("{\"message\":\"500: Internal Server Error\",\"code\":\"" + Sentry.getLastEventId() + "\"}");
        }

        return super.handle(httpRequest, controller);
    }

    public static @Nullable UUID getSubject(HttpRequest httpRequest) {
        String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            DecodedJWT decodedJWT = AuthenticationUtil.tokenToBody(authHeader);

            return decodedJWT != null ? UUID.fromString(decodedJWT.getSubject()) : null;
        } catch (TokenVerifyException ignored) {
        }

        return null;
    }

}
