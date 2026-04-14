package com.afa.fitadapt.data.local.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.afa.fitadapt.data.local.dao.ArticleDao;
import com.afa.fitadapt.data.local.dao.ArticleDao_Impl;
import com.afa.fitadapt.data.local.dao.DiaryDao;
import com.afa.fitadapt.data.local.dao.DiaryDao_Impl;
import com.afa.fitadapt.data.local.dao.ExerciseDao;
import com.afa.fitadapt.data.local.dao.ExerciseDao_Impl;
import com.afa.fitadapt.data.local.dao.ExportLogDao;
import com.afa.fitadapt.data.local.dao.ExportLogDao_Impl;
import com.afa.fitadapt.data.local.dao.GoalDao;
import com.afa.fitadapt.data.local.dao.GoalDao_Impl;
import com.afa.fitadapt.data.local.dao.PatientProfileDao;
import com.afa.fitadapt.data.local.dao.PatientProfileDao_Impl;
import com.afa.fitadapt.data.local.dao.ScaleEntryDao;
import com.afa.fitadapt.data.local.dao.ScaleEntryDao_Impl;
import com.afa.fitadapt.data.local.dao.SessionDao;
import com.afa.fitadapt.data.local.dao.SessionDao_Impl;
import com.afa.fitadapt.data.local.dao.TrainingCardDao;
import com.afa.fitadapt.data.local.dao.TrainingCardDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AfaDatabase_Impl extends AfaDatabase {
  private volatile ExerciseDao _exerciseDao;

  private volatile TrainingCardDao _trainingCardDao;

  private volatile SessionDao _sessionDao;

  private volatile DiaryDao _diaryDao;

  private volatile ScaleEntryDao _scaleEntryDao;

  private volatile ArticleDao _articleDao;

  private volatile ExportLogDao _exportLogDao;

  private volatile PatientProfileDao _patientProfileDao;

  private volatile GoalDao _goalDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `exercises` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL, `description` TEXT NOT NULL, `videoUri` TEXT, `defaultDurationSec` INTEGER, `defaultRepetitions` INTEGER, `defaultIntensity` TEXT NOT NULL, `notes` TEXT, `isActive` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `training_cards` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `durationWeeks` INTEGER, `targetSessions` INTEGER, `status` TEXT NOT NULL, `startDate` INTEGER, `endDate` INTEGER, `orderIndex` INTEGER NOT NULL, `autoAdvance` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `card_exercises` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cardId` INTEGER NOT NULL, `exerciseId` INTEGER NOT NULL, `orderIndex` INTEGER NOT NULL, `customDurationSec` INTEGER, `customRepetitions` INTEGER, `customIntensity` TEXT, `customNotes` TEXT, FOREIGN KEY(`cardId`) REFERENCES `training_cards`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`exerciseId`) REFERENCES `exercises`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_card_exercises_cardId` ON `card_exercises` (`cardId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_card_exercises_exerciseId` ON `card_exercises` (`exerciseId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cardId` INTEGER NOT NULL, `date` INTEGER NOT NULL, `completed` INTEGER NOT NULL, `partial` INTEGER NOT NULL, `actualDurationMin` INTEGER, `perceivedEffort` INTEGER, `mood` INTEGER, `sleepQuality` INTEGER, `notes` TEXT, `createdAt` INTEGER NOT NULL, FOREIGN KEY(`cardId`) REFERENCES `training_cards`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sessions_cardId` ON `sessions` (`cardId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `session_exercises` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sessionId` INTEGER NOT NULL, `cardExerciseId` INTEGER NOT NULL, `completed` INTEGER NOT NULL, FOREIGN KEY(`sessionId`) REFERENCES `sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`cardExerciseId`) REFERENCES `card_exercises`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_session_exercises_sessionId` ON `session_exercises` (`sessionId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_session_exercises_cardExerciseId` ON `session_exercises` (`cardExerciseId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `diary_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL, `text` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `scale_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL, `asthenia` INTEGER, `osteoarticularPain` INTEGER, `restDyspnea` INTEGER, `exertionDyspnea` INTEGER, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `articles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `category` TEXT NOT NULL, `summary` TEXT NOT NULL, `body` TEXT NOT NULL, `isFeatured` INTEGER NOT NULL, `weekNumber` INTEGER, `year` INTEGER, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `export_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `format` TEXT NOT NULL, `hash` TEXT, `recordCount` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `patient_profile` (`id` INTEGER NOT NULL, `patientCode` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `appInitialized` INTEGER NOT NULL, `biometricsEnabled` INTEGER NOT NULL, `protectedSectionConfigured` INTEGER NOT NULL, `lastAccessAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `goals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `description` TEXT, `targetType` TEXT NOT NULL, `targetValue` REAL NOT NULL, `currentValue` REAL NOT NULL, `isActive` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '65aa8fe4535c0ff0dc16d749f6add650')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `exercises`");
        db.execSQL("DROP TABLE IF EXISTS `training_cards`");
        db.execSQL("DROP TABLE IF EXISTS `card_exercises`");
        db.execSQL("DROP TABLE IF EXISTS `sessions`");
        db.execSQL("DROP TABLE IF EXISTS `session_exercises`");
        db.execSQL("DROP TABLE IF EXISTS `diary_entries`");
        db.execSQL("DROP TABLE IF EXISTS `scale_entries`");
        db.execSQL("DROP TABLE IF EXISTS `articles`");
        db.execSQL("DROP TABLE IF EXISTS `export_logs`");
        db.execSQL("DROP TABLE IF EXISTS `patient_profile`");
        db.execSQL("DROP TABLE IF EXISTS `goals`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsExercises = new HashMap<String, TableInfo.Column>(11);
        _columnsExercises.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("videoUri", new TableInfo.Column("videoUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("defaultDurationSec", new TableInfo.Column("defaultDurationSec", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("defaultRepetitions", new TableInfo.Column("defaultRepetitions", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("defaultIntensity", new TableInfo.Column("defaultIntensity", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExercises.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExercises = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExercises = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExercises = new TableInfo("exercises", _columnsExercises, _foreignKeysExercises, _indicesExercises);
        final TableInfo _existingExercises = TableInfo.read(db, "exercises");
        if (!_infoExercises.equals(_existingExercises)) {
          return new RoomOpenHelper.ValidationResult(false, "exercises(com.afa.fitadapt.data.local.entity.ExerciseEntity).\n"
                  + " Expected:\n" + _infoExercises + "\n"
                  + " Found:\n" + _existingExercises);
        }
        final HashMap<String, TableInfo.Column> _columnsTrainingCards = new HashMap<String, TableInfo.Column>(10);
        _columnsTrainingCards.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrainingCards.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrainingCards.put("durationWeeks", new TableInfo.Column("durationWeeks", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrainingCards.put("targetSessions", new TableInfo.Column("targetSessions", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrainingCards.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrainingCards.put("startDate", new TableInfo.Column("startDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrainingCards.put("endDate", new TableInfo.Column("endDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrainingCards.put("orderIndex", new TableInfo.Column("orderIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrainingCards.put("autoAdvance", new TableInfo.Column("autoAdvance", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrainingCards.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTrainingCards = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTrainingCards = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTrainingCards = new TableInfo("training_cards", _columnsTrainingCards, _foreignKeysTrainingCards, _indicesTrainingCards);
        final TableInfo _existingTrainingCards = TableInfo.read(db, "training_cards");
        if (!_infoTrainingCards.equals(_existingTrainingCards)) {
          return new RoomOpenHelper.ValidationResult(false, "training_cards(com.afa.fitadapt.data.local.entity.TrainingCardEntity).\n"
                  + " Expected:\n" + _infoTrainingCards + "\n"
                  + " Found:\n" + _existingTrainingCards);
        }
        final HashMap<String, TableInfo.Column> _columnsCardExercises = new HashMap<String, TableInfo.Column>(8);
        _columnsCardExercises.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardExercises.put("cardId", new TableInfo.Column("cardId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardExercises.put("exerciseId", new TableInfo.Column("exerciseId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardExercises.put("orderIndex", new TableInfo.Column("orderIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardExercises.put("customDurationSec", new TableInfo.Column("customDurationSec", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardExercises.put("customRepetitions", new TableInfo.Column("customRepetitions", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardExercises.put("customIntensity", new TableInfo.Column("customIntensity", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCardExercises.put("customNotes", new TableInfo.Column("customNotes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCardExercises = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysCardExercises.add(new TableInfo.ForeignKey("training_cards", "CASCADE", "NO ACTION", Arrays.asList("cardId"), Arrays.asList("id")));
        _foreignKeysCardExercises.add(new TableInfo.ForeignKey("exercises", "CASCADE", "NO ACTION", Arrays.asList("exerciseId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesCardExercises = new HashSet<TableInfo.Index>(2);
        _indicesCardExercises.add(new TableInfo.Index("index_card_exercises_cardId", false, Arrays.asList("cardId"), Arrays.asList("ASC")));
        _indicesCardExercises.add(new TableInfo.Index("index_card_exercises_exerciseId", false, Arrays.asList("exerciseId"), Arrays.asList("ASC")));
        final TableInfo _infoCardExercises = new TableInfo("card_exercises", _columnsCardExercises, _foreignKeysCardExercises, _indicesCardExercises);
        final TableInfo _existingCardExercises = TableInfo.read(db, "card_exercises");
        if (!_infoCardExercises.equals(_existingCardExercises)) {
          return new RoomOpenHelper.ValidationResult(false, "card_exercises(com.afa.fitadapt.data.local.entity.CardExerciseEntity).\n"
                  + " Expected:\n" + _infoCardExercises + "\n"
                  + " Found:\n" + _existingCardExercises);
        }
        final HashMap<String, TableInfo.Column> _columnsSessions = new HashMap<String, TableInfo.Column>(11);
        _columnsSessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("cardId", new TableInfo.Column("cardId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("date", new TableInfo.Column("date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("completed", new TableInfo.Column("completed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("partial", new TableInfo.Column("partial", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("actualDurationMin", new TableInfo.Column("actualDurationMin", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("perceivedEffort", new TableInfo.Column("perceivedEffort", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("mood", new TableInfo.Column("mood", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("sleepQuality", new TableInfo.Column("sleepQuality", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSessions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSessions.add(new TableInfo.ForeignKey("training_cards", "CASCADE", "NO ACTION", Arrays.asList("cardId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSessions = new HashSet<TableInfo.Index>(1);
        _indicesSessions.add(new TableInfo.Index("index_sessions_cardId", false, Arrays.asList("cardId"), Arrays.asList("ASC")));
        final TableInfo _infoSessions = new TableInfo("sessions", _columnsSessions, _foreignKeysSessions, _indicesSessions);
        final TableInfo _existingSessions = TableInfo.read(db, "sessions");
        if (!_infoSessions.equals(_existingSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "sessions(com.afa.fitadapt.data.local.entity.SessionEntity).\n"
                  + " Expected:\n" + _infoSessions + "\n"
                  + " Found:\n" + _existingSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsSessionExercises = new HashMap<String, TableInfo.Column>(4);
        _columnsSessionExercises.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessionExercises.put("sessionId", new TableInfo.Column("sessionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessionExercises.put("cardExerciseId", new TableInfo.Column("cardExerciseId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSessionExercises.put("completed", new TableInfo.Column("completed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSessionExercises = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysSessionExercises.add(new TableInfo.ForeignKey("sessions", "CASCADE", "NO ACTION", Arrays.asList("sessionId"), Arrays.asList("id")));
        _foreignKeysSessionExercises.add(new TableInfo.ForeignKey("card_exercises", "CASCADE", "NO ACTION", Arrays.asList("cardExerciseId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSessionExercises = new HashSet<TableInfo.Index>(2);
        _indicesSessionExercises.add(new TableInfo.Index("index_session_exercises_sessionId", false, Arrays.asList("sessionId"), Arrays.asList("ASC")));
        _indicesSessionExercises.add(new TableInfo.Index("index_session_exercises_cardExerciseId", false, Arrays.asList("cardExerciseId"), Arrays.asList("ASC")));
        final TableInfo _infoSessionExercises = new TableInfo("session_exercises", _columnsSessionExercises, _foreignKeysSessionExercises, _indicesSessionExercises);
        final TableInfo _existingSessionExercises = TableInfo.read(db, "session_exercises");
        if (!_infoSessionExercises.equals(_existingSessionExercises)) {
          return new RoomOpenHelper.ValidationResult(false, "session_exercises(com.afa.fitadapt.data.local.entity.SessionExerciseEntity).\n"
                  + " Expected:\n" + _infoSessionExercises + "\n"
                  + " Found:\n" + _existingSessionExercises);
        }
        final HashMap<String, TableInfo.Column> _columnsDiaryEntries = new HashMap<String, TableInfo.Column>(4);
        _columnsDiaryEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("date", new TableInfo.Column("date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDiaryEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDiaryEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDiaryEntries = new TableInfo("diary_entries", _columnsDiaryEntries, _foreignKeysDiaryEntries, _indicesDiaryEntries);
        final TableInfo _existingDiaryEntries = TableInfo.read(db, "diary_entries");
        if (!_infoDiaryEntries.equals(_existingDiaryEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "diary_entries(com.afa.fitadapt.data.local.entity.DiaryEntryEntity).\n"
                  + " Expected:\n" + _infoDiaryEntries + "\n"
                  + " Found:\n" + _existingDiaryEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsScaleEntries = new HashMap<String, TableInfo.Column>(7);
        _columnsScaleEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScaleEntries.put("date", new TableInfo.Column("date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScaleEntries.put("asthenia", new TableInfo.Column("asthenia", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScaleEntries.put("osteoarticularPain", new TableInfo.Column("osteoarticularPain", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScaleEntries.put("restDyspnea", new TableInfo.Column("restDyspnea", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScaleEntries.put("exertionDyspnea", new TableInfo.Column("exertionDyspnea", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScaleEntries.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysScaleEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesScaleEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoScaleEntries = new TableInfo("scale_entries", _columnsScaleEntries, _foreignKeysScaleEntries, _indicesScaleEntries);
        final TableInfo _existingScaleEntries = TableInfo.read(db, "scale_entries");
        if (!_infoScaleEntries.equals(_existingScaleEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "scale_entries(com.afa.fitadapt.data.local.entity.ScaleEntryEntity).\n"
                  + " Expected:\n" + _infoScaleEntries + "\n"
                  + " Found:\n" + _existingScaleEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsArticles = new HashMap<String, TableInfo.Column>(9);
        _columnsArticles.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsArticles.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsArticles.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsArticles.put("summary", new TableInfo.Column("summary", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsArticles.put("body", new TableInfo.Column("body", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsArticles.put("isFeatured", new TableInfo.Column("isFeatured", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsArticles.put("weekNumber", new TableInfo.Column("weekNumber", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsArticles.put("year", new TableInfo.Column("year", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsArticles.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysArticles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesArticles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoArticles = new TableInfo("articles", _columnsArticles, _foreignKeysArticles, _indicesArticles);
        final TableInfo _existingArticles = TableInfo.read(db, "articles");
        if (!_infoArticles.equals(_existingArticles)) {
          return new RoomOpenHelper.ValidationResult(false, "articles(com.afa.fitadapt.data.local.entity.ArticleEntity).\n"
                  + " Expected:\n" + _infoArticles + "\n"
                  + " Found:\n" + _existingArticles);
        }
        final HashMap<String, TableInfo.Column> _columnsExportLogs = new HashMap<String, TableInfo.Column>(5);
        _columnsExportLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExportLogs.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExportLogs.put("format", new TableInfo.Column("format", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExportLogs.put("hash", new TableInfo.Column("hash", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExportLogs.put("recordCount", new TableInfo.Column("recordCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExportLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExportLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExportLogs = new TableInfo("export_logs", _columnsExportLogs, _foreignKeysExportLogs, _indicesExportLogs);
        final TableInfo _existingExportLogs = TableInfo.read(db, "export_logs");
        if (!_infoExportLogs.equals(_existingExportLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "export_logs(com.afa.fitadapt.data.local.entity.ExportLogEntity).\n"
                  + " Expected:\n" + _infoExportLogs + "\n"
                  + " Found:\n" + _existingExportLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsPatientProfile = new HashMap<String, TableInfo.Column>(7);
        _columnsPatientProfile.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPatientProfile.put("patientCode", new TableInfo.Column("patientCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPatientProfile.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPatientProfile.put("appInitialized", new TableInfo.Column("appInitialized", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPatientProfile.put("biometricsEnabled", new TableInfo.Column("biometricsEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPatientProfile.put("protectedSectionConfigured", new TableInfo.Column("protectedSectionConfigured", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPatientProfile.put("lastAccessAt", new TableInfo.Column("lastAccessAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPatientProfile = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPatientProfile = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPatientProfile = new TableInfo("patient_profile", _columnsPatientProfile, _foreignKeysPatientProfile, _indicesPatientProfile);
        final TableInfo _existingPatientProfile = TableInfo.read(db, "patient_profile");
        if (!_infoPatientProfile.equals(_existingPatientProfile)) {
          return new RoomOpenHelper.ValidationResult(false, "patient_profile(com.afa.fitadapt.data.local.entity.PatientProfileEntity).\n"
                  + " Expected:\n" + _infoPatientProfile + "\n"
                  + " Found:\n" + _existingPatientProfile);
        }
        final HashMap<String, TableInfo.Column> _columnsGoals = new HashMap<String, TableInfo.Column>(9);
        _columnsGoals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("targetType", new TableInfo.Column("targetType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("targetValue", new TableInfo.Column("targetValue", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("currentValue", new TableInfo.Column("currentValue", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGoals.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGoals = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGoals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGoals = new TableInfo("goals", _columnsGoals, _foreignKeysGoals, _indicesGoals);
        final TableInfo _existingGoals = TableInfo.read(db, "goals");
        if (!_infoGoals.equals(_existingGoals)) {
          return new RoomOpenHelper.ValidationResult(false, "goals(com.afa.fitadapt.data.local.entity.GoalEntity).\n"
                  + " Expected:\n" + _infoGoals + "\n"
                  + " Found:\n" + _existingGoals);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "65aa8fe4535c0ff0dc16d749f6add650", "47ffef28a545c0cbd9cb83fd236c3373");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "exercises","training_cards","card_exercises","sessions","session_exercises","diary_entries","scale_entries","articles","export_logs","patient_profile","goals");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `exercises`");
      _db.execSQL("DELETE FROM `training_cards`");
      _db.execSQL("DELETE FROM `card_exercises`");
      _db.execSQL("DELETE FROM `sessions`");
      _db.execSQL("DELETE FROM `session_exercises`");
      _db.execSQL("DELETE FROM `diary_entries`");
      _db.execSQL("DELETE FROM `scale_entries`");
      _db.execSQL("DELETE FROM `articles`");
      _db.execSQL("DELETE FROM `export_logs`");
      _db.execSQL("DELETE FROM `patient_profile`");
      _db.execSQL("DELETE FROM `goals`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ExerciseDao.class, ExerciseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TrainingCardDao.class, TrainingCardDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SessionDao.class, SessionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DiaryDao.class, DiaryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ScaleEntryDao.class, ScaleEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ArticleDao.class, ArticleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExportLogDao.class, ExportLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PatientProfileDao.class, PatientProfileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GoalDao.class, GoalDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ExerciseDao exerciseDao() {
    if (_exerciseDao != null) {
      return _exerciseDao;
    } else {
      synchronized(this) {
        if(_exerciseDao == null) {
          _exerciseDao = new ExerciseDao_Impl(this);
        }
        return _exerciseDao;
      }
    }
  }

  @Override
  public TrainingCardDao trainingCardDao() {
    if (_trainingCardDao != null) {
      return _trainingCardDao;
    } else {
      synchronized(this) {
        if(_trainingCardDao == null) {
          _trainingCardDao = new TrainingCardDao_Impl(this);
        }
        return _trainingCardDao;
      }
    }
  }

  @Override
  public SessionDao sessionDao() {
    if (_sessionDao != null) {
      return _sessionDao;
    } else {
      synchronized(this) {
        if(_sessionDao == null) {
          _sessionDao = new SessionDao_Impl(this);
        }
        return _sessionDao;
      }
    }
  }

  @Override
  public DiaryDao diaryDao() {
    if (_diaryDao != null) {
      return _diaryDao;
    } else {
      synchronized(this) {
        if(_diaryDao == null) {
          _diaryDao = new DiaryDao_Impl(this);
        }
        return _diaryDao;
      }
    }
  }

  @Override
  public ScaleEntryDao scaleEntryDao() {
    if (_scaleEntryDao != null) {
      return _scaleEntryDao;
    } else {
      synchronized(this) {
        if(_scaleEntryDao == null) {
          _scaleEntryDao = new ScaleEntryDao_Impl(this);
        }
        return _scaleEntryDao;
      }
    }
  }

  @Override
  public ArticleDao articleDao() {
    if (_articleDao != null) {
      return _articleDao;
    } else {
      synchronized(this) {
        if(_articleDao == null) {
          _articleDao = new ArticleDao_Impl(this);
        }
        return _articleDao;
      }
    }
  }

  @Override
  public ExportLogDao exportLogDao() {
    if (_exportLogDao != null) {
      return _exportLogDao;
    } else {
      synchronized(this) {
        if(_exportLogDao == null) {
          _exportLogDao = new ExportLogDao_Impl(this);
        }
        return _exportLogDao;
      }
    }
  }

  @Override
  public PatientProfileDao patientProfileDao() {
    if (_patientProfileDao != null) {
      return _patientProfileDao;
    } else {
      synchronized(this) {
        if(_patientProfileDao == null) {
          _patientProfileDao = new PatientProfileDao_Impl(this);
        }
        return _patientProfileDao;
      }
    }
  }

  @Override
  public GoalDao goalDao() {
    if (_goalDao != null) {
      return _goalDao;
    } else {
      synchronized(this) {
        if(_goalDao == null) {
          _goalDao = new GoalDao_Impl(this);
        }
        return _goalDao;
      }
    }
  }
}
