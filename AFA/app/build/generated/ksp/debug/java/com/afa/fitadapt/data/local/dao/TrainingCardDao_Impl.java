package com.afa.fitadapt.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.afa.fitadapt.data.local.entity.CardExerciseEntity;
import com.afa.fitadapt.data.local.entity.CardWithExercises;
import com.afa.fitadapt.data.local.entity.TrainingCardEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TrainingCardDao_Impl implements TrainingCardDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TrainingCardEntity> __insertionAdapterOfTrainingCardEntity;

  private final EntityInsertionAdapter<CardExerciseEntity> __insertionAdapterOfCardExerciseEntity;

  private final EntityDeletionOrUpdateAdapter<TrainingCardEntity> __deletionAdapterOfTrainingCardEntity;

  private final EntityDeletionOrUpdateAdapter<CardExerciseEntity> __deletionAdapterOfCardExerciseEntity;

  private final EntityDeletionOrUpdateAdapter<TrainingCardEntity> __updateAdapterOfTrainingCardEntity;

  private final EntityDeletionOrUpdateAdapter<CardExerciseEntity> __updateAdapterOfCardExerciseEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDates;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllCardExercises;

  public TrainingCardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTrainingCardEntity = new EntityInsertionAdapter<TrainingCardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `training_cards` (`id`,`title`,`durationWeeks`,`targetSessions`,`status`,`startDate`,`endDate`,`orderIndex`,`autoAdvance`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TrainingCardEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        if (entity.getDurationWeeks() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getDurationWeeks());
        }
        if (entity.getTargetSessions() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getTargetSessions());
        }
        statement.bindString(5, entity.getStatus());
        if (entity.getStartDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getStartDate());
        }
        if (entity.getEndDate() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getEndDate());
        }
        statement.bindLong(8, entity.getOrderIndex());
        final int _tmp = entity.getAutoAdvance() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindLong(10, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfCardExerciseEntity = new EntityInsertionAdapter<CardExerciseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `card_exercises` (`id`,`cardId`,`exerciseId`,`orderIndex`,`customDurationSec`,`customRepetitions`,`customIntensity`,`customNotes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CardExerciseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCardId());
        statement.bindLong(3, entity.getExerciseId());
        statement.bindLong(4, entity.getOrderIndex());
        if (entity.getCustomDurationSec() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getCustomDurationSec());
        }
        if (entity.getCustomRepetitions() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getCustomRepetitions());
        }
        if (entity.getCustomIntensity() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getCustomIntensity());
        }
        if (entity.getCustomNotes() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getCustomNotes());
        }
      }
    };
    this.__deletionAdapterOfTrainingCardEntity = new EntityDeletionOrUpdateAdapter<TrainingCardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `training_cards` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TrainingCardEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfCardExerciseEntity = new EntityDeletionOrUpdateAdapter<CardExerciseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `card_exercises` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CardExerciseEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTrainingCardEntity = new EntityDeletionOrUpdateAdapter<TrainingCardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `training_cards` SET `id` = ?,`title` = ?,`durationWeeks` = ?,`targetSessions` = ?,`status` = ?,`startDate` = ?,`endDate` = ?,`orderIndex` = ?,`autoAdvance` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TrainingCardEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        if (entity.getDurationWeeks() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getDurationWeeks());
        }
        if (entity.getTargetSessions() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getTargetSessions());
        }
        statement.bindString(5, entity.getStatus());
        if (entity.getStartDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getStartDate());
        }
        if (entity.getEndDate() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getEndDate());
        }
        statement.bindLong(8, entity.getOrderIndex());
        final int _tmp = entity.getAutoAdvance() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindLong(10, entity.getCreatedAt());
        statement.bindLong(11, entity.getId());
      }
    };
    this.__updateAdapterOfCardExerciseEntity = new EntityDeletionOrUpdateAdapter<CardExerciseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `card_exercises` SET `id` = ?,`cardId` = ?,`exerciseId` = ?,`orderIndex` = ?,`customDurationSec` = ?,`customRepetitions` = ?,`customIntensity` = ?,`customNotes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CardExerciseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCardId());
        statement.bindLong(3, entity.getExerciseId());
        statement.bindLong(4, entity.getOrderIndex());
        if (entity.getCustomDurationSec() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getCustomDurationSec());
        }
        if (entity.getCustomRepetitions() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getCustomRepetitions());
        }
        if (entity.getCustomIntensity() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getCustomIntensity());
        }
        if (entity.getCustomNotes() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getCustomNotes());
        }
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE training_cards SET status = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateDates = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE training_cards SET startDate = ?, endDate = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllCardExercises = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM card_exercises WHERE cardId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final TrainingCardEntity card,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTrainingCardEntity.insertAndReturnId(card);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCardExercise(final CardExerciseEntity cardExercise,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCardExerciseEntity.insertAndReturnId(cardExercise);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCardExercises(final List<CardExerciseEntity> cardExercises,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCardExerciseEntity.insert(cardExercises);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final TrainingCardEntity card,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTrainingCardEntity.handle(card);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCardExercise(final CardExerciseEntity cardExercise,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCardExerciseEntity.handle(cardExercise);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final TrainingCardEntity card,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTrainingCardEntity.handle(card);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCardExercise(final CardExerciseEntity cardExercise,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCardExerciseEntity.handle(cardExercise);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final long id, final String status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDates(final long id, final Long startDate, final Long endDate,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDates.acquire();
        int _argIndex = 1;
        if (startDate == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, startDate);
        }
        _argIndex = 2;
        if (endDate == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, endDate);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateDates.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllCardExercises(final long cardId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllCardExercises.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cardId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllCardExercises.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TrainingCardEntity>> getAll() {
    final String _sql = "SELECT * FROM training_cards ORDER BY orderIndex";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"training_cards"}, new Callable<List<TrainingCardEntity>>() {
      @Override
      @NonNull
      public List<TrainingCardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDurationWeeks = CursorUtil.getColumnIndexOrThrow(_cursor, "durationWeeks");
          final int _cursorIndexOfTargetSessions = CursorUtil.getColumnIndexOrThrow(_cursor, "targetSessions");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfAutoAdvance = CursorUtil.getColumnIndexOrThrow(_cursor, "autoAdvance");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<TrainingCardEntity> _result = new ArrayList<TrainingCardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrainingCardEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final Integer _tmpDurationWeeks;
            if (_cursor.isNull(_cursorIndexOfDurationWeeks)) {
              _tmpDurationWeeks = null;
            } else {
              _tmpDurationWeeks = _cursor.getInt(_cursorIndexOfDurationWeeks);
            }
            final Integer _tmpTargetSessions;
            if (_cursor.isNull(_cursorIndexOfTargetSessions)) {
              _tmpTargetSessions = null;
            } else {
              _tmpTargetSessions = _cursor.getInt(_cursorIndexOfTargetSessions);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpEndDate;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            }
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final boolean _tmpAutoAdvance;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAutoAdvance);
            _tmpAutoAdvance = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TrainingCardEntity(_tmpId,_tmpTitle,_tmpDurationWeeks,_tmpTargetSessions,_tmpStatus,_tmpStartDate,_tmpEndDate,_tmpOrderIndex,_tmpAutoAdvance,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<TrainingCardEntity> getActiveCard() {
    final String _sql = "SELECT * FROM training_cards WHERE status = 'ACTIVE' LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"training_cards"}, new Callable<TrainingCardEntity>() {
      @Override
      @Nullable
      public TrainingCardEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDurationWeeks = CursorUtil.getColumnIndexOrThrow(_cursor, "durationWeeks");
          final int _cursorIndexOfTargetSessions = CursorUtil.getColumnIndexOrThrow(_cursor, "targetSessions");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfAutoAdvance = CursorUtil.getColumnIndexOrThrow(_cursor, "autoAdvance");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final TrainingCardEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final Integer _tmpDurationWeeks;
            if (_cursor.isNull(_cursorIndexOfDurationWeeks)) {
              _tmpDurationWeeks = null;
            } else {
              _tmpDurationWeeks = _cursor.getInt(_cursorIndexOfDurationWeeks);
            }
            final Integer _tmpTargetSessions;
            if (_cursor.isNull(_cursorIndexOfTargetSessions)) {
              _tmpTargetSessions = null;
            } else {
              _tmpTargetSessions = _cursor.getInt(_cursorIndexOfTargetSessions);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpEndDate;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            }
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final boolean _tmpAutoAdvance;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAutoAdvance);
            _tmpAutoAdvance = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new TrainingCardEntity(_tmpId,_tmpTitle,_tmpDurationWeeks,_tmpTargetSessions,_tmpStatus,_tmpStartDate,_tmpEndDate,_tmpOrderIndex,_tmpAutoAdvance,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TrainingCardEntity>> getPendingCards() {
    final String _sql = "SELECT * FROM training_cards WHERE status = 'PENDING' ORDER BY orderIndex";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"training_cards"}, new Callable<List<TrainingCardEntity>>() {
      @Override
      @NonNull
      public List<TrainingCardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDurationWeeks = CursorUtil.getColumnIndexOrThrow(_cursor, "durationWeeks");
          final int _cursorIndexOfTargetSessions = CursorUtil.getColumnIndexOrThrow(_cursor, "targetSessions");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfAutoAdvance = CursorUtil.getColumnIndexOrThrow(_cursor, "autoAdvance");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<TrainingCardEntity> _result = new ArrayList<TrainingCardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrainingCardEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final Integer _tmpDurationWeeks;
            if (_cursor.isNull(_cursorIndexOfDurationWeeks)) {
              _tmpDurationWeeks = null;
            } else {
              _tmpDurationWeeks = _cursor.getInt(_cursorIndexOfDurationWeeks);
            }
            final Integer _tmpTargetSessions;
            if (_cursor.isNull(_cursorIndexOfTargetSessions)) {
              _tmpTargetSessions = null;
            } else {
              _tmpTargetSessions = _cursor.getInt(_cursorIndexOfTargetSessions);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpEndDate;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            }
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final boolean _tmpAutoAdvance;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAutoAdvance);
            _tmpAutoAdvance = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TrainingCardEntity(_tmpId,_tmpTitle,_tmpDurationWeeks,_tmpTargetSessions,_tmpStatus,_tmpStartDate,_tmpEndDate,_tmpOrderIndex,_tmpAutoAdvance,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TrainingCardEntity>> getCompletedCards() {
    final String _sql = "SELECT * FROM training_cards WHERE status = 'COMPLETED' ORDER BY orderIndex";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"training_cards"}, new Callable<List<TrainingCardEntity>>() {
      @Override
      @NonNull
      public List<TrainingCardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDurationWeeks = CursorUtil.getColumnIndexOrThrow(_cursor, "durationWeeks");
          final int _cursorIndexOfTargetSessions = CursorUtil.getColumnIndexOrThrow(_cursor, "targetSessions");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfAutoAdvance = CursorUtil.getColumnIndexOrThrow(_cursor, "autoAdvance");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<TrainingCardEntity> _result = new ArrayList<TrainingCardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrainingCardEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final Integer _tmpDurationWeeks;
            if (_cursor.isNull(_cursorIndexOfDurationWeeks)) {
              _tmpDurationWeeks = null;
            } else {
              _tmpDurationWeeks = _cursor.getInt(_cursorIndexOfDurationWeeks);
            }
            final Integer _tmpTargetSessions;
            if (_cursor.isNull(_cursorIndexOfTargetSessions)) {
              _tmpTargetSessions = null;
            } else {
              _tmpTargetSessions = _cursor.getInt(_cursorIndexOfTargetSessions);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpEndDate;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            }
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final boolean _tmpAutoAdvance;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAutoAdvance);
            _tmpAutoAdvance = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new TrainingCardEntity(_tmpId,_tmpTitle,_tmpDurationWeeks,_tmpTargetSessions,_tmpStatus,_tmpStartDate,_tmpEndDate,_tmpOrderIndex,_tmpAutoAdvance,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final long id, final Continuation<? super TrainingCardEntity> $completion) {
    final String _sql = "SELECT * FROM training_cards WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TrainingCardEntity>() {
      @Override
      @Nullable
      public TrainingCardEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDurationWeeks = CursorUtil.getColumnIndexOrThrow(_cursor, "durationWeeks");
          final int _cursorIndexOfTargetSessions = CursorUtil.getColumnIndexOrThrow(_cursor, "targetSessions");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfAutoAdvance = CursorUtil.getColumnIndexOrThrow(_cursor, "autoAdvance");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final TrainingCardEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final Integer _tmpDurationWeeks;
            if (_cursor.isNull(_cursorIndexOfDurationWeeks)) {
              _tmpDurationWeeks = null;
            } else {
              _tmpDurationWeeks = _cursor.getInt(_cursorIndexOfDurationWeeks);
            }
            final Integer _tmpTargetSessions;
            if (_cursor.isNull(_cursorIndexOfTargetSessions)) {
              _tmpTargetSessions = null;
            } else {
              _tmpTargetSessions = _cursor.getInt(_cursorIndexOfTargetSessions);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpEndDate;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            }
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final boolean _tmpAutoAdvance;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAutoAdvance);
            _tmpAutoAdvance = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new TrainingCardEntity(_tmpId,_tmpTitle,_tmpDurationWeeks,_tmpTargetSessions,_tmpStatus,_tmpStartDate,_tmpEndDate,_tmpOrderIndex,_tmpAutoAdvance,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getNextPendingCard(final Continuation<? super TrainingCardEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM training_cards \n"
            + "        WHERE status = 'PENDING' \n"
            + "        ORDER BY orderIndex ASC \n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TrainingCardEntity>() {
      @Override
      @Nullable
      public TrainingCardEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDurationWeeks = CursorUtil.getColumnIndexOrThrow(_cursor, "durationWeeks");
          final int _cursorIndexOfTargetSessions = CursorUtil.getColumnIndexOrThrow(_cursor, "targetSessions");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfAutoAdvance = CursorUtil.getColumnIndexOrThrow(_cursor, "autoAdvance");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final TrainingCardEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final Integer _tmpDurationWeeks;
            if (_cursor.isNull(_cursorIndexOfDurationWeeks)) {
              _tmpDurationWeeks = null;
            } else {
              _tmpDurationWeeks = _cursor.getInt(_cursorIndexOfDurationWeeks);
            }
            final Integer _tmpTargetSessions;
            if (_cursor.isNull(_cursorIndexOfTargetSessions)) {
              _tmpTargetSessions = null;
            } else {
              _tmpTargetSessions = _cursor.getInt(_cursorIndexOfTargetSessions);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpStartDate;
            if (_cursor.isNull(_cursorIndexOfStartDate)) {
              _tmpStartDate = null;
            } else {
              _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            }
            final Long _tmpEndDate;
            if (_cursor.isNull(_cursorIndexOfEndDate)) {
              _tmpEndDate = null;
            } else {
              _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            }
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final boolean _tmpAutoAdvance;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAutoAdvance);
            _tmpAutoAdvance = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new TrainingCardEntity(_tmpId,_tmpTitle,_tmpDurationWeeks,_tmpTargetSessions,_tmpStatus,_tmpStartDate,_tmpEndDate,_tmpOrderIndex,_tmpAutoAdvance,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object countByStatus(final String status,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM training_cards WHERE status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<CardWithExercises> getCardWithExercises(final long cardId) {
    final String _sql = "SELECT * FROM training_cards WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cardId);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"card_exercises",
        "training_cards"}, new Callable<CardWithExercises>() {
      @Override
      @Nullable
      public CardWithExercises call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfDurationWeeks = CursorUtil.getColumnIndexOrThrow(_cursor, "durationWeeks");
            final int _cursorIndexOfTargetSessions = CursorUtil.getColumnIndexOrThrow(_cursor, "targetSessions");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
            final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
            final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
            final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
            final int _cursorIndexOfAutoAdvance = CursorUtil.getColumnIndexOrThrow(_cursor, "autoAdvance");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final LongSparseArray<ArrayList<CardExerciseEntity>> _collectionCardExercises = new LongSparseArray<ArrayList<CardExerciseEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionCardExercises.containsKey(_tmpKey)) {
                _collectionCardExercises.put(_tmpKey, new ArrayList<CardExerciseEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcardExercisesAscomAfaFitadaptDataLocalEntityCardExerciseEntity(_collectionCardExercises);
            final CardWithExercises _result;
            if (_cursor.moveToFirst()) {
              final TrainingCardEntity _tmpCard;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final Integer _tmpDurationWeeks;
              if (_cursor.isNull(_cursorIndexOfDurationWeeks)) {
                _tmpDurationWeeks = null;
              } else {
                _tmpDurationWeeks = _cursor.getInt(_cursorIndexOfDurationWeeks);
              }
              final Integer _tmpTargetSessions;
              if (_cursor.isNull(_cursorIndexOfTargetSessions)) {
                _tmpTargetSessions = null;
              } else {
                _tmpTargetSessions = _cursor.getInt(_cursorIndexOfTargetSessions);
              }
              final String _tmpStatus;
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
              final Long _tmpStartDate;
              if (_cursor.isNull(_cursorIndexOfStartDate)) {
                _tmpStartDate = null;
              } else {
                _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
              }
              final Long _tmpEndDate;
              if (_cursor.isNull(_cursorIndexOfEndDate)) {
                _tmpEndDate = null;
              } else {
                _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
              }
              final int _tmpOrderIndex;
              _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
              final boolean _tmpAutoAdvance;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfAutoAdvance);
              _tmpAutoAdvance = _tmp != 0;
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              _tmpCard = new TrainingCardEntity(_tmpId,_tmpTitle,_tmpDurationWeeks,_tmpTargetSessions,_tmpStatus,_tmpStartDate,_tmpEndDate,_tmpOrderIndex,_tmpAutoAdvance,_tmpCreatedAt);
              final ArrayList<CardExerciseEntity> _tmpCardExercisesCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpCardExercisesCollection = _collectionCardExercises.get(_tmpKey_1);
              _result = new CardWithExercises(_tmpCard,_tmpCardExercisesCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<CardWithExercises> getActiveCardWithExercises() {
    final String _sql = "SELECT * FROM training_cards WHERE status = 'ACTIVE' LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"card_exercises",
        "training_cards"}, new Callable<CardWithExercises>() {
      @Override
      @Nullable
      public CardWithExercises call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
            final int _cursorIndexOfDurationWeeks = CursorUtil.getColumnIndexOrThrow(_cursor, "durationWeeks");
            final int _cursorIndexOfTargetSessions = CursorUtil.getColumnIndexOrThrow(_cursor, "targetSessions");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
            final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
            final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
            final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
            final int _cursorIndexOfAutoAdvance = CursorUtil.getColumnIndexOrThrow(_cursor, "autoAdvance");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final LongSparseArray<ArrayList<CardExerciseEntity>> _collectionCardExercises = new LongSparseArray<ArrayList<CardExerciseEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionCardExercises.containsKey(_tmpKey)) {
                _collectionCardExercises.put(_tmpKey, new ArrayList<CardExerciseEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcardExercisesAscomAfaFitadaptDataLocalEntityCardExerciseEntity(_collectionCardExercises);
            final CardWithExercises _result;
            if (_cursor.moveToFirst()) {
              final TrainingCardEntity _tmpCard;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpTitle;
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
              final Integer _tmpDurationWeeks;
              if (_cursor.isNull(_cursorIndexOfDurationWeeks)) {
                _tmpDurationWeeks = null;
              } else {
                _tmpDurationWeeks = _cursor.getInt(_cursorIndexOfDurationWeeks);
              }
              final Integer _tmpTargetSessions;
              if (_cursor.isNull(_cursorIndexOfTargetSessions)) {
                _tmpTargetSessions = null;
              } else {
                _tmpTargetSessions = _cursor.getInt(_cursorIndexOfTargetSessions);
              }
              final String _tmpStatus;
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
              final Long _tmpStartDate;
              if (_cursor.isNull(_cursorIndexOfStartDate)) {
                _tmpStartDate = null;
              } else {
                _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
              }
              final Long _tmpEndDate;
              if (_cursor.isNull(_cursorIndexOfEndDate)) {
                _tmpEndDate = null;
              } else {
                _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
              }
              final int _tmpOrderIndex;
              _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
              final boolean _tmpAutoAdvance;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfAutoAdvance);
              _tmpAutoAdvance = _tmp != 0;
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              _tmpCard = new TrainingCardEntity(_tmpId,_tmpTitle,_tmpDurationWeeks,_tmpTargetSessions,_tmpStatus,_tmpStartDate,_tmpEndDate,_tmpOrderIndex,_tmpAutoAdvance,_tmpCreatedAt);
              final ArrayList<CardExerciseEntity> _tmpCardExercisesCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpCardExercisesCollection = _collectionCardExercises.get(_tmpKey_1);
              _result = new CardWithExercises(_tmpCard,_tmpCardExercisesCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<CardExerciseEntity>> getCardExercises(final long cardId) {
    final String _sql = "SELECT * FROM card_exercises WHERE cardId = ? ORDER BY orderIndex";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cardId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"card_exercises"}, new Callable<List<CardExerciseEntity>>() {
      @Override
      @NonNull
      public List<CardExerciseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
          final int _cursorIndexOfExerciseId = CursorUtil.getColumnIndexOrThrow(_cursor, "exerciseId");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfCustomDurationSec = CursorUtil.getColumnIndexOrThrow(_cursor, "customDurationSec");
          final int _cursorIndexOfCustomRepetitions = CursorUtil.getColumnIndexOrThrow(_cursor, "customRepetitions");
          final int _cursorIndexOfCustomIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "customIntensity");
          final int _cursorIndexOfCustomNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "customNotes");
          final List<CardExerciseEntity> _result = new ArrayList<CardExerciseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CardExerciseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final long _tmpExerciseId;
            _tmpExerciseId = _cursor.getLong(_cursorIndexOfExerciseId);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final Integer _tmpCustomDurationSec;
            if (_cursor.isNull(_cursorIndexOfCustomDurationSec)) {
              _tmpCustomDurationSec = null;
            } else {
              _tmpCustomDurationSec = _cursor.getInt(_cursorIndexOfCustomDurationSec);
            }
            final Integer _tmpCustomRepetitions;
            if (_cursor.isNull(_cursorIndexOfCustomRepetitions)) {
              _tmpCustomRepetitions = null;
            } else {
              _tmpCustomRepetitions = _cursor.getInt(_cursorIndexOfCustomRepetitions);
            }
            final String _tmpCustomIntensity;
            if (_cursor.isNull(_cursorIndexOfCustomIntensity)) {
              _tmpCustomIntensity = null;
            } else {
              _tmpCustomIntensity = _cursor.getString(_cursorIndexOfCustomIntensity);
            }
            final String _tmpCustomNotes;
            if (_cursor.isNull(_cursorIndexOfCustomNotes)) {
              _tmpCustomNotes = null;
            } else {
              _tmpCustomNotes = _cursor.getString(_cursorIndexOfCustomNotes);
            }
            _item = new CardExerciseEntity(_tmpId,_tmpCardId,_tmpExerciseId,_tmpOrderIndex,_tmpCustomDurationSec,_tmpCustomRepetitions,_tmpCustomIntensity,_tmpCustomNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipcardExercisesAscomAfaFitadaptDataLocalEntityCardExerciseEntity(
      @NonNull final LongSparseArray<ArrayList<CardExerciseEntity>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipcardExercisesAscomAfaFitadaptDataLocalEntityCardExerciseEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`cardId`,`exerciseId`,`orderIndex`,`customDurationSec`,`customRepetitions`,`customIntensity`,`customNotes` FROM `card_exercises` WHERE `cardId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "cardId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfCardId = 1;
      final int _cursorIndexOfExerciseId = 2;
      final int _cursorIndexOfOrderIndex = 3;
      final int _cursorIndexOfCustomDurationSec = 4;
      final int _cursorIndexOfCustomRepetitions = 5;
      final int _cursorIndexOfCustomIntensity = 6;
      final int _cursorIndexOfCustomNotes = 7;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<CardExerciseEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final CardExerciseEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final long _tmpCardId;
          _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
          final long _tmpExerciseId;
          _tmpExerciseId = _cursor.getLong(_cursorIndexOfExerciseId);
          final int _tmpOrderIndex;
          _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
          final Integer _tmpCustomDurationSec;
          if (_cursor.isNull(_cursorIndexOfCustomDurationSec)) {
            _tmpCustomDurationSec = null;
          } else {
            _tmpCustomDurationSec = _cursor.getInt(_cursorIndexOfCustomDurationSec);
          }
          final Integer _tmpCustomRepetitions;
          if (_cursor.isNull(_cursorIndexOfCustomRepetitions)) {
            _tmpCustomRepetitions = null;
          } else {
            _tmpCustomRepetitions = _cursor.getInt(_cursorIndexOfCustomRepetitions);
          }
          final String _tmpCustomIntensity;
          if (_cursor.isNull(_cursorIndexOfCustomIntensity)) {
            _tmpCustomIntensity = null;
          } else {
            _tmpCustomIntensity = _cursor.getString(_cursorIndexOfCustomIntensity);
          }
          final String _tmpCustomNotes;
          if (_cursor.isNull(_cursorIndexOfCustomNotes)) {
            _tmpCustomNotes = null;
          } else {
            _tmpCustomNotes = _cursor.getString(_cursorIndexOfCustomNotes);
          }
          _item_1 = new CardExerciseEntity(_tmpId,_tmpCardId,_tmpExerciseId,_tmpOrderIndex,_tmpCustomDurationSec,_tmpCustomRepetitions,_tmpCustomIntensity,_tmpCustomNotes);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
