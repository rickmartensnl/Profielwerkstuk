package nl.rickmartens.profielwerkstuk.api.v1.users.sessions;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import nl.rickmartens.profielwerkstuk.api.v1.auth.V1LoginController;
import nl.rickmartens.profielwerkstuk.database.impl.QuestionManager;
import nl.rickmartens.profielwerkstuk.database.impl.UserHistoryManager;
import nl.rickmartens.profielwerkstuk.database.impl.UserManager;
import nl.rickmartens.profielwerkstuk.exceptions.DatabaseOfflineException;
import nl.rickmartens.profielwerkstuk.exceptions.InvalidSyntaxException;
import nl.rickmartens.profielwerkstuk.exceptions.TokenCreateException;
import nl.rickmartens.profielwerkstuk.middlewares.AuthMiddleware;
import nl.rickmartens.profielwerkstuk.utils.AllowMethods;
import nl.rickmartens.profielwerkstuk.utils.QuestionUtil;
import nl.rickmartens.profielwerkstuk.utils.UseMiddleware;
import nl.rickmartens.profielwerkstuk.utils.UserController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.activej.http.HttpMethod;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.sentry.Sentry;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@UseMiddleware({ AuthMiddleware.class })
@AllowMethods({ HttpMethod.GET, HttpMethod.POST, HttpMethod.PATCH })
public class V1SessionController implements UserController {

    @Getter @Setter public UserManager.User user;

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        if (httpRequest.getMethod() == HttpMethod.POST) {
            return submitAnswer(httpRequest);
        }

        SessionRequestType sessionRequestType = SessionRequestType.getFromQuery(httpRequest);

        try {
            if (sessionRequestType == SessionRequestType.NEW) {
                return HttpResponse.ok201().withJson(UserHistoryManager.getUserHistoryManager().createNewUserHistory(getUser()).getJsonObject());
            } else {
                UserHistoryManager.UserHistory[] userHistory = UserHistoryManager.getUserHistoryManager().getUnfinished(getUser());

                if (userHistory.length == 0) {
                    return HttpResponse.ok201().withJson(UserHistoryManager.getUserHistoryManager().createNewUserHistory(getUser()).getJsonObject());
                }

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();

                return HttpResponse.ok200().withJson(gson.toJson(userHistory));
            }
        } catch (DatabaseOfflineException exception) {
            return HttpResponse.ofCode(500).withJson("{\"message\":\"500: Internal Server Error\",\"code\":\"" + Sentry.getLastEventId() + "\"}");
        }
    }

    private HttpResponse submitAnswer(HttpRequest httpRequest) {
        try {
            SubmitBody submitBody = new Gson().fromJson(JsonParser.parseString(httpRequest.getBody().getString(StandardCharsets.UTF_8)), SubmitBody.class);
            UserManager.User user = this.getUser();
            
            UserHistoryManager.UserHistory session = UserHistoryManager.getUserHistoryManager().getSessionByUuid(submitBody.getSessionId());
    
            if (user.getUuid() != session.getUser().getUuid()) {
                return HttpResponse.ofCode(403).withJson("{\"message\":\"403: Forbidden\",\"code\":0}");
            }
    
            String calculation = session.getQuestion().getAnswer().getCalculation();
            for (Map.Entry<String, QuestionManager.QuestionVariable> entry : session.getVariableValues().entrySet()) {
                calculation = calculation.replaceAll("%%" + entry.getKey() + "%%", entry.getValue().getTheValue());
            }
            
            int answer = QuestionUtil.calculateAnswer(calculation);
    
            session.setAnswer(submitBody.answer);
            session.setFlags(0);
            session.setCorrectPercentage(0);
            
            int parsedAnswer = Integer.parseInt(submitBody.answer);
            
            if (answer == parsedAnswer) {
                session.setCorrectPercentage(100);
            } else if (answer + 1 == parsedAnswer || answer - 1 == parsedAnswer) {
                session.setCorrectPercentage(80);
            }
            
            session.save();
            
            return HttpResponse.ok200().withJson("{\"percentage\":\"" + session.getCorrectPercentage() + "\"}");
        } catch (JsonSyntaxException exception) {
            return HttpResponse.ofCode(400).withJson("{\"message\":\"400: Bad Request\",\"code\":0}");
        } catch (DatabaseOfflineException exception) {
            return HttpResponse.ofCode(500).withJson("{\"message\":\"500: Internal Server Error\",\"code\":\"" + Sentry.getLastEventId() + "\"}");
        } catch (ScriptException e) {
            e.printStackTrace();
            return HttpResponse.ofCode(500).withJson("{\"message\":\"500: Internal Server Error\",\"code\":\"" + Sentry.getLastEventId() + "\"}");
        }
    }
    
    @Getter
    private class SubmitBody {
        
        protected final String answer;
        protected final UUID sessionId;
        
        public SubmitBody(String answer, UUID sessionId) {
            this.answer = answer;
            this.sessionId = sessionId;
        }
        
    }

    enum SessionRequestType {

        NEW,
        UNFINISHED;

        public static SessionRequestType getFromQuery(HttpRequest httpRequest) {
            String queryTypeParam = httpRequest.getQueryParameter("type");

            if (queryTypeParam == null) {
                return SessionRequestType.UNFINISHED;
            }

            try {
                return SessionRequestType.valueOf(queryTypeParam.toUpperCase());
            } catch (IllegalArgumentException exception) {
                return SessionRequestType.UNFINISHED;
            }
        }

    }

}
