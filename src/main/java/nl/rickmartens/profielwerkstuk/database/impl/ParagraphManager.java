package nl.rickmartens.profielwerkstuk.database.impl;

import nl.rickmartens.profielwerkstuk.ProfielwerkstukServerLauncher;
import nl.rickmartens.profielwerkstuk.database.Model;
import nl.rickmartens.profielwerkstuk.exceptions.DatabaseOfflineException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import io.sentry.Sentry;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class ParagraphManager {

    @Getter private static ParagraphManager paragraphManager;

    public ParagraphManager() {
        paragraphManager = this;
    }

    public Paragraph getParagraph(UUID uuid) throws DatabaseOfflineException {
        try {
            return new Paragraph(uuid);
        } catch (SQLException exception) {
            Sentry.captureException(exception);
            throw new DatabaseOfflineException();
        }
    }

    public @NotNull ArrayList<Paragraph> getAllParagraphsByChapter(UUID chapterUuid) throws DatabaseOfflineException {
        ArrayList<Paragraph> paragraphs = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT `uuid` FROM `paragraphs` WHERE `chapter_uuid` = ?;");
            preparedStatement.setString(1, chapterUuid.toString());
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                paragraphs.add(new Paragraph(UUID.fromString(resultSet.getString("uuid"))));
            }
        } catch (SQLException exception) {
            Sentry.captureException(exception);
            throw new DatabaseOfflineException();
        }

        return paragraphs;
    }

    public class Paragraph implements Model {

        @Expose @Getter private final UUID uuid;
        @Expose @Getter private String name;
        @Expose @Getter private int flags;
        @Expose @Getter private ChapterManager.Chapter chapter;
        @Expose @Getter private UserManager.User creator;

        public Paragraph(UUID uuid) throws SQLException, DatabaseOfflineException {
            this.uuid = uuid;

            PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT * FROM `paragraphs` WHERE uuid = ?;");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                this.name = resultSet.getString("name");
                this.flags = resultSet.getInt("flags");
                this.chapter = ChapterManager.getChapterManager().getChapter(UUID.fromString(resultSet.getString("chapter_uuid")));
                this.creator = UserManager.getUserManager().getUser(UUID.fromString(resultSet.getString("creator_uuid")));
            }
        }

        @Override
        public String getJsonObject() {
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

            return gson.toJson(this);
        }

    }

}
