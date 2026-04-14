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
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.afa.fitadapt.data.local.entity.SessionEntity;
import com.afa.fitadapt.data.local.entity.SessionExerciseEntity;
import com.afa.fitadapt.data.local.entity.SessionWithExercises;
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
public final class SessionDao_Impl implements SessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SessionEntity> __insertionAdapterOfSessionEntity;

  private final EntityInsertionAdapter<SessionExerciseEntity> __insertionAdapterOfSessionExerciseEntity;

  private final EntityDeletionOrUpdateAdapter<SessionEntity> __updateAdapterOfSessionEntity;

  public SessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSessionEntity = new EntityInsertionAdapter<SessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sessions` (`id`,`cardId`,`date`,`completed`,`partial`,`actualDurationMin`,`perceivedEffort`,`mood`,`sleepQuality`,`notes`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SessionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCardId());
        statement.bindLong(3, entity.getDate());
        final int _tmp = entity.getCompleted() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final int _tmp_1 = entity.getPartial() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        if (entity.getActualDurationMin() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getActualDurationMin());
        }
        if (entity.getPerceivedEffort() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getPerceivedEffort());
        }
        if (entity.getMood() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getMood());
        }
        if (entity.getSleepQuality() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getSleepQuality());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getNotes());
        }
        statement.bindLong(11, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfSessionExerciseEntity = new EntityInsertionAdapter<SessionExerciseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `session_exercises` (`id`,`sessionId`,`cardExerciseId`,`completed`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SessionExerciseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSessionId());
        statement.bindLong(3, entity.getCardExerciseId());
        final int _tmp = entity.getCompleted() ? 1 : 0;
        statement.bindLong(4, _tmp);
      }
    };
    this.__updateAdapterOfSessionEntity = new EntityDeletionOrUpdateAdapter<SessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sessions` SET `id` = ?,`cardId` = ?,`date` = ?,`completed` = ?,`partial` = ?,`actualDurationMin` = ?,`perceivedEffort` = ?,`mood` = ?,`sleepQuality` = ?,`notes` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SessionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCardId());
        statement.bindLong(3, entity.getDate());
        final int _tmp = entity.getCompleted() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final int _tmp_1 = entity.getPartial() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        if (entity.getActualDurationMin() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getActualDurationMin());
        }
        if (entity.getPerceivedEffort() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getPerceivedEffort());
        }
        if (entity.getMood() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getMood());
        }
        if (entity.getSleepQuality() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getSleepQuality());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getNotes());
        }
        statement.bindLong(11, entity.getCreatedAt());
        statement.bindLong(12, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final SessionEntity session, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSessionEntity.insertAndReturnId(session);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSessionExercises(final List<SessionExerciseEntity> exercises,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSessionExerciseEntity.insert(exercises);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final SessionEntity session, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSessionEntity.handle(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SessionEntity>> getSessionsByCard(final long cardId) {
    final String _sql = "SELECT * FROM sessions WHERE cardId = ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cardId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sessions"}, new Callable<List<SessionEntity>>() {
      @Override
      @NonNull
      public List<SessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final int _cursorIndexOfPartial = CursorUtil.getColumnIndexOrThrow(_cursor, "partial");
          final int _cursorIndexOfActualDurationMin = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDurationMin");
          final int _cursorIndexOfPerceivedEffort = CursorUtil.getColumnIndexOrThrow(_cursor, "perceivedEffort");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfSleepQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepQuality");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<SessionEntity> _result = new ArrayList<SessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SessionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            final boolean _tmpPartial;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfPartial);
            _tmpPartial = _tmp_1 != 0;
            final Integer _tmpActualDurationMin;
            if (_cursor.isNull(_cursorIndexOfActualDurationMin)) {
              _tmpActualDurationMin = null;
            } else {
              _tmpActualDurationMin = _cursor.getInt(_cursorIndexOfActualDurationMin);
            }
            final Integer _tmpPerceivedEffort;
            if (_cursor.isNull(_cursorIndexOfPerceivedEffort)) {
              _tmpPerceivedEffort = null;
            } else {
              _tmpPerceivedEffort = _cursor.getInt(_cursorIndexOfPerceivedEffort);
            }
            final Integer _tmpMood;
            if (_cursor.isNull(_cursorIndexOfMood)) {
              _tmpMood = null;
            } else {
              _tmpMood = _cursor.getInt(_cursorIndexOfMood);
            }
            final Integer _tmpSleepQuality;
            if (_cursor.isNull(_cursorIndexOfSleepQuality)) {
              _tmpSleepQuality = null;
            } else {
              _tmpSleepQuality = _cursor.getInt(_cursorIndexOfSleepQuality);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SessionEntity(_tmpId,_tmpCardId,_tmpDate,_tmpCompleted,_tmpPartial,_tmpActualDurationMin,_tmpPerceivedEffort,_tmpMood,_tmpSleepQuality,_tmpNotes,_tmpCreatedAt);
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
  public Flow<List<SessionEntity>> getAllSessions() {
    final String _sql = "SELECT * FROM sessions ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sessions"}, new Callable<List<SessionEntity>>() {
      @Override
      @NonNull
      public List<SessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final int _cursorIndexOfPartial = CursorUtil.getColumnIndexOrThrow(_cursor, "partial");
          final int _cursorIndexOfActualDurationMin = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDurationMin");
          final int _cursorIndexOfPerceivedEffort = CursorUtil.getColumnIndexOrThrow(_cursor, "perceivedEffort");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfSleepQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepQuality");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<SessionEntity> _result = new ArrayList<SessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SessionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            final boolean _tmpPartial;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfPartial);
            _tmpPartial = _tmp_1 != 0;
            final Integer _tmpActualDurationMin;
            if (_cursor.isNull(_cursorIndexOfActualDurationMin)) {
              _tmpActualDurationMin = null;
            } else {
              _tmpActualDurationMin = _cursor.getInt(_cursorIndexOfActualDurationMin);
            }
            final Integer _tmpPerceivedEffort;
            if (_cursor.isNull(_cursorIndexOfPerceivedEffort)) {
              _tmpPerceivedEffort = null;
            } else {
              _tmpPerceivedEffort = _cursor.getInt(_cursorIndexOfPerceivedEffort);
            }
            final Integer _tmpMood;
            if (_cursor.isNull(_cursorIndexOfMood)) {
              _tmpMood = null;
            } else {
              _tmpMood = _cursor.getInt(_cursorIndexOfMood);
            }
            final Integer _tmpSleepQuality;
            if (_cursor.isNull(_cursorIndexOfSleepQuality)) {
              _tmpSleepQuality = null;
            } else {
              _tmpSleepQuality = _cursor.getInt(_cursorIndexOfSleepQuality);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SessionEntity(_tmpId,_tmpCardId,_tmpDate,_tmpCompleted,_tmpPartial,_tmpActualDurationMin,_tmpPerceivedEffort,_tmpMood,_tmpSleepQuality,_tmpNotes,_tmpCreatedAt);
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
  public Object getById(final long id, final Continuation<? super SessionEntity> $completion) {
    final String _sql = "SELECT * FROM sessions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SessionEntity>() {
      @Override
      @Nullable
      public SessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final int _cursorIndexOfPartial = CursorUtil.getColumnIndexOrThrow(_cursor, "partial");
          final int _cursorIndexOfActualDurationMin = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDurationMin");
          final int _cursorIndexOfPerceivedEffort = CursorUtil.getColumnIndexOrThrow(_cursor, "perceivedEffort");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfSleepQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepQuality");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final SessionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            final boolean _tmpPartial;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfPartial);
            _tmpPartial = _tmp_1 != 0;
            final Integer _tmpActualDurationMin;
            if (_cursor.isNull(_cursorIndexOfActualDurationMin)) {
              _tmpActualDurationMin = null;
            } else {
              _tmpActualDurationMin = _cursor.getInt(_cursorIndexOfActualDurationMin);
            }
            final Integer _tmpPerceivedEffort;
            if (_cursor.isNull(_cursorIndexOfPerceivedEffort)) {
              _tmpPerceivedEffort = null;
            } else {
              _tmpPerceivedEffort = _cursor.getInt(_cursorIndexOfPerceivedEffort);
            }
            final Integer _tmpMood;
            if (_cursor.isNull(_cursorIndexOfMood)) {
              _tmpMood = null;
            } else {
              _tmpMood = _cursor.getInt(_cursorIndexOfMood);
            }
            final Integer _tmpSleepQuality;
            if (_cursor.isNull(_cursorIndexOfSleepQuality)) {
              _tmpSleepQuality = null;
            } else {
              _tmpSleepQuality = _cursor.getInt(_cursorIndexOfSleepQuality);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new SessionEntity(_tmpId,_tmpCardId,_tmpDate,_tmpCompleted,_tmpPartial,_tmpActualDurationMin,_tmpPerceivedEffort,_tmpMood,_tmpSleepQuality,_tmpNotes,_tmpCreatedAt);
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
  public Object getSessionsForDay(final long startOfDay, final long endOfDay,
      final Continuation<? super List<SessionEntity>> $completion) {
    final String _sql = "SELECT * FROM sessions WHERE date >= ? AND date < ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SessionEntity>>() {
      @Override
      @NonNull
      public List<SessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final int _cursorIndexOfPartial = CursorUtil.getColumnIndexOrThrow(_cursor, "partial");
          final int _cursorIndexOfActualDurationMin = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDurationMin");
          final int _cursorIndexOfPerceivedEffort = CursorUtil.getColumnIndexOrThrow(_cursor, "perceivedEffort");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfSleepQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepQuality");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<SessionEntity> _result = new ArrayList<SessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SessionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            final boolean _tmpPartial;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfPartial);
            _tmpPartial = _tmp_1 != 0;
            final Integer _tmpActualDurationMin;
            if (_cursor.isNull(_cursorIndexOfActualDurationMin)) {
              _tmpActualDurationMin = null;
            } else {
              _tmpActualDurationMin = _cursor.getInt(_cursorIndexOfActualDurationMin);
            }
            final Integer _tmpPerceivedEffort;
            if (_cursor.isNull(_cursorIndexOfPerceivedEffort)) {
              _tmpPerceivedEffort = null;
            } else {
              _tmpPerceivedEffort = _cursor.getInt(_cursorIndexOfPerceivedEffort);
            }
            final Integer _tmpMood;
            if (_cursor.isNull(_cursorIndexOfMood)) {
              _tmpMood = null;
            } else {
              _tmpMood = _cursor.getInt(_cursorIndexOfMood);
            }
            final Integer _tmpSleepQuality;
            if (_cursor.isNull(_cursorIndexOfSleepQuality)) {
              _tmpSleepQuality = null;
            } else {
              _tmpSleepQuality = _cursor.getInt(_cursorIndexOfSleepQuality);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SessionEntity(_tmpId,_tmpCardId,_tmpDate,_tmpCompleted,_tmpPartial,_tmpActualDurationMin,_tmpPerceivedEffort,_tmpMood,_tmpSleepQuality,_tmpNotes,_tmpCreatedAt);
            _result.add(_item);
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
  public Flow<SessionWithExercises> getSessionWithExercises(final long sessionId) {
    final String _sql = "SELECT * FROM sessions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sessionId);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"session_exercises",
        "sessions"}, new Callable<SessionWithExercises>() {
      @Override
      @Nullable
      public SessionWithExercises call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
            final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
            final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
            final int _cursorIndexOfPartial = CursorUtil.getColumnIndexOrThrow(_cursor, "partial");
            final int _cursorIndexOfActualDurationMin = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDurationMin");
            final int _cursorIndexOfPerceivedEffort = CursorUtil.getColumnIndexOrThrow(_cursor, "perceivedEffort");
            final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
            final int _cursorIndexOfSleepQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepQuality");
            final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final LongSparseArray<ArrayList<SessionExerciseEntity>> _collectionExerciseCompletions = new LongSparseArray<ArrayList<SessionExerciseEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionExerciseCompletions.containsKey(_tmpKey)) {
                _collectionExerciseCompletions.put(_tmpKey, new ArrayList<SessionExerciseEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipsessionExercisesAscomAfaFitadaptDataLocalEntitySessionExerciseEntity(_collectionExerciseCompletions);
            final SessionWithExercises _result;
            if (_cursor.moveToFirst()) {
              final SessionEntity _tmpSession;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpCardId;
              _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
              final long _tmpDate;
              _tmpDate = _cursor.getLong(_cursorIndexOfDate);
              final boolean _tmpCompleted;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfCompleted);
              _tmpCompleted = _tmp != 0;
              final boolean _tmpPartial;
              final int _tmp_1;
              _tmp_1 = _cursor.getInt(_cursorIndexOfPartial);
              _tmpPartial = _tmp_1 != 0;
              final Integer _tmpActualDurationMin;
              if (_cursor.isNull(_cursorIndexOfActualDurationMin)) {
                _tmpActualDurationMin = null;
              } else {
                _tmpActualDurationMin = _cursor.getInt(_cursorIndexOfActualDurationMin);
              }
              final Integer _tmpPerceivedEffort;
              if (_cursor.isNull(_cursorIndexOfPerceivedEffort)) {
                _tmpPerceivedEffort = null;
              } else {
                _tmpPerceivedEffort = _cursor.getInt(_cursorIndexOfPerceivedEffort);
              }
              final Integer _tmpMood;
              if (_cursor.isNull(_cursorIndexOfMood)) {
                _tmpMood = null;
              } else {
                _tmpMood = _cursor.getInt(_cursorIndexOfMood);
              }
              final Integer _tmpSleepQuality;
              if (_cursor.isNull(_cursorIndexOfSleepQuality)) {
                _tmpSleepQuality = null;
              } else {
                _tmpSleepQuality = _cursor.getInt(_cursorIndexOfSleepQuality);
              }
              final String _tmpNotes;
              if (_cursor.isNull(_cursorIndexOfNotes)) {
                _tmpNotes = null;
              } else {
                _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              _tmpSession = new SessionEntity(_tmpId,_tmpCardId,_tmpDate,_tmpCompleted,_tmpPartial,_tmpActualDurationMin,_tmpPerceivedEffort,_tmpMood,_tmpSleepQuality,_tmpNotes,_tmpCreatedAt);
              final ArrayList<SessionExerciseEntity> _tmpExerciseCompletionsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpExerciseCompletionsCollection = _collectionExerciseCompletions.get(_tmpKey_1);
              _result = new SessionWithExercises(_tmpSession,_tmpExerciseCompletionsCollection);
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
  public Flow<Integer> countCompletedSessions() {
    final String _sql = "SELECT COUNT(*) FROM sessions WHERE completed = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sessions"}, new Callable<Integer>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> countTotalSessions() {
    final String _sql = "SELECT COUNT(*) FROM sessions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sessions"}, new Callable<Integer>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object countCompletedByCard(final long cardId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM sessions WHERE cardId = ? AND completed = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cardId);
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
  public Flow<Integer> totalMinutes() {
    final String _sql = "SELECT COALESCE(SUM(actualDurationMin), 0) FROM sessions WHERE completed = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sessions"}, new Callable<Integer>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getCompletedSessionsSince(final long sinceDate,
      final Continuation<? super List<SessionEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM sessions \n"
            + "        WHERE completed = 1 AND date >= ? \n"
            + "        ORDER BY date ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sinceDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SessionEntity>>() {
      @Override
      @NonNull
      public List<SessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final int _cursorIndexOfPartial = CursorUtil.getColumnIndexOrThrow(_cursor, "partial");
          final int _cursorIndexOfActualDurationMin = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDurationMin");
          final int _cursorIndexOfPerceivedEffort = CursorUtil.getColumnIndexOrThrow(_cursor, "perceivedEffort");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfSleepQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepQuality");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<SessionEntity> _result = new ArrayList<SessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SessionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            final boolean _tmpPartial;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfPartial);
            _tmpPartial = _tmp_1 != 0;
            final Integer _tmpActualDurationMin;
            if (_cursor.isNull(_cursorIndexOfActualDurationMin)) {
              _tmpActualDurationMin = null;
            } else {
              _tmpActualDurationMin = _cursor.getInt(_cursorIndexOfActualDurationMin);
            }
            final Integer _tmpPerceivedEffort;
            if (_cursor.isNull(_cursorIndexOfPerceivedEffort)) {
              _tmpPerceivedEffort = null;
            } else {
              _tmpPerceivedEffort = _cursor.getInt(_cursorIndexOfPerceivedEffort);
            }
            final Integer _tmpMood;
            if (_cursor.isNull(_cursorIndexOfMood)) {
              _tmpMood = null;
            } else {
              _tmpMood = _cursor.getInt(_cursorIndexOfMood);
            }
            final Integer _tmpSleepQuality;
            if (_cursor.isNull(_cursorIndexOfSleepQuality)) {
              _tmpSleepQuality = null;
            } else {
              _tmpSleepQuality = _cursor.getInt(_cursorIndexOfSleepQuality);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SessionEntity(_tmpId,_tmpCardId,_tmpDate,_tmpCompleted,_tmpPartial,_tmpActualDurationMin,_tmpPerceivedEffort,_tmpMood,_tmpSleepQuality,_tmpNotes,_tmpCreatedAt);
            _result.add(_item);
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
  public Object getAllCompletedSessionsOrdered(
      final Continuation<? super List<SessionEntity>> $completion) {
    final String _sql = "SELECT * FROM sessions WHERE completed = 1 ORDER BY date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SessionEntity>>() {
      @Override
      @NonNull
      public List<SessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final int _cursorIndexOfPartial = CursorUtil.getColumnIndexOrThrow(_cursor, "partial");
          final int _cursorIndexOfActualDurationMin = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDurationMin");
          final int _cursorIndexOfPerceivedEffort = CursorUtil.getColumnIndexOrThrow(_cursor, "perceivedEffort");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfSleepQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepQuality");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<SessionEntity> _result = new ArrayList<SessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SessionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            final boolean _tmpPartial;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfPartial);
            _tmpPartial = _tmp_1 != 0;
            final Integer _tmpActualDurationMin;
            if (_cursor.isNull(_cursorIndexOfActualDurationMin)) {
              _tmpActualDurationMin = null;
            } else {
              _tmpActualDurationMin = _cursor.getInt(_cursorIndexOfActualDurationMin);
            }
            final Integer _tmpPerceivedEffort;
            if (_cursor.isNull(_cursorIndexOfPerceivedEffort)) {
              _tmpPerceivedEffort = null;
            } else {
              _tmpPerceivedEffort = _cursor.getInt(_cursorIndexOfPerceivedEffort);
            }
            final Integer _tmpMood;
            if (_cursor.isNull(_cursorIndexOfMood)) {
              _tmpMood = null;
            } else {
              _tmpMood = _cursor.getInt(_cursorIndexOfMood);
            }
            final Integer _tmpSleepQuality;
            if (_cursor.isNull(_cursorIndexOfSleepQuality)) {
              _tmpSleepQuality = null;
            } else {
              _tmpSleepQuality = _cursor.getInt(_cursorIndexOfSleepQuality);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SessionEntity(_tmpId,_tmpCardId,_tmpDate,_tmpCompleted,_tmpPartial,_tmpActualDurationMin,_tmpPerceivedEffort,_tmpMood,_tmpSleepQuality,_tmpNotes,_tmpCreatedAt);
            _result.add(_item);
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
  public Flow<List<SessionEntity>> getSessionsInRange(final long fromDate, final long toDate) {
    final String _sql = "SELECT * FROM sessions WHERE date >= ? AND date <= ? ORDER BY date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, fromDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, toDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sessions"}, new Callable<List<SessionEntity>>() {
      @Override
      @NonNull
      public List<SessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "cardId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final int _cursorIndexOfPartial = CursorUtil.getColumnIndexOrThrow(_cursor, "partial");
          final int _cursorIndexOfActualDurationMin = CursorUtil.getColumnIndexOrThrow(_cursor, "actualDurationMin");
          final int _cursorIndexOfPerceivedEffort = CursorUtil.getColumnIndexOrThrow(_cursor, "perceivedEffort");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfSleepQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepQuality");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<SessionEntity> _result = new ArrayList<SessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SessionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCardId;
            _tmpCardId = _cursor.getLong(_cursorIndexOfCardId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            final boolean _tmpPartial;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfPartial);
            _tmpPartial = _tmp_1 != 0;
            final Integer _tmpActualDurationMin;
            if (_cursor.isNull(_cursorIndexOfActualDurationMin)) {
              _tmpActualDurationMin = null;
            } else {
              _tmpActualDurationMin = _cursor.getInt(_cursorIndexOfActualDurationMin);
            }
            final Integer _tmpPerceivedEffort;
            if (_cursor.isNull(_cursorIndexOfPerceivedEffort)) {
              _tmpPerceivedEffort = null;
            } else {
              _tmpPerceivedEffort = _cursor.getInt(_cursorIndexOfPerceivedEffort);
            }
            final Integer _tmpMood;
            if (_cursor.isNull(_cursorIndexOfMood)) {
              _tmpMood = null;
            } else {
              _tmpMood = _cursor.getInt(_cursorIndexOfMood);
            }
            final Integer _tmpSleepQuality;
            if (_cursor.isNull(_cursorIndexOfSleepQuality)) {
              _tmpSleepQuality = null;
            } else {
              _tmpSleepQuality = _cursor.getInt(_cursorIndexOfSleepQuality);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new SessionEntity(_tmpId,_tmpCardId,_tmpDate,_tmpCompleted,_tmpPartial,_tmpActualDurationMin,_tmpPerceivedEffort,_tmpMood,_tmpSleepQuality,_tmpNotes,_tmpCreatedAt);
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
  public Object hasSessionToday(final long startOfDay, final long endOfDay,
      final Continuation<? super Integer> $completion) {
    final String _sql = "\n"
            + "        SELECT COUNT(*) FROM sessions \n"
            + "        WHERE date >= ? AND date < ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipsessionExercisesAscomAfaFitadaptDataLocalEntitySessionExerciseEntity(
      @NonNull final LongSparseArray<ArrayList<SessionExerciseEntity>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipsessionExercisesAscomAfaFitadaptDataLocalEntitySessionExerciseEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`sessionId`,`cardExerciseId`,`completed` FROM `session_exercises` WHERE `sessionId` IN (");
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
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "sessionId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfSessionId = 1;
      final int _cursorIndexOfCardExerciseId = 2;
      final int _cursorIndexOfCompleted = 3;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<SessionExerciseEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final SessionExerciseEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final long _tmpSessionId;
          _tmpSessionId = _cursor.getLong(_cursorIndexOfSessionId);
          final long _tmpCardExerciseId;
          _tmpCardExerciseId = _cursor.getLong(_cursorIndexOfCardExerciseId);
          final boolean _tmpCompleted;
          final int _tmp;
          _tmp = _cursor.getInt(_cursorIndexOfCompleted);
          _tmpCompleted = _tmp != 0;
          _item_1 = new SessionExerciseEntity(_tmpId,_tmpSessionId,_tmpCardExerciseId,_tmpCompleted);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
