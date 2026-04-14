package com.afa.fitadapt.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.afa.fitadapt.data.local.entity.ExerciseEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
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
public final class ExerciseDao_Impl implements ExerciseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ExerciseEntity> __insertionAdapterOfExerciseEntity;

  private final EntityDeletionOrUpdateAdapter<ExerciseEntity> __updateAdapterOfExerciseEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeactivate;

  private final SharedSQLiteStatement __preparedStmtOfActivate;

  public ExerciseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfExerciseEntity = new EntityInsertionAdapter<ExerciseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `exercises` (`id`,`name`,`category`,`description`,`videoUri`,`defaultDurationSec`,`defaultRepetitions`,`defaultIntensity`,`notes`,`isActive`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExerciseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getCategory());
        statement.bindString(4, entity.getDescription());
        if (entity.getVideoUri() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getVideoUri());
        }
        if (entity.getDefaultDurationSec() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getDefaultDurationSec());
        }
        if (entity.getDefaultRepetitions() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getDefaultRepetitions());
        }
        statement.bindString(8, entity.getDefaultIntensity());
        if (entity.getNotes() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getNotes());
        }
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(10, _tmp);
        statement.bindLong(11, entity.getCreatedAt());
      }
    };
    this.__updateAdapterOfExerciseEntity = new EntityDeletionOrUpdateAdapter<ExerciseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `exercises` SET `id` = ?,`name` = ?,`category` = ?,`description` = ?,`videoUri` = ?,`defaultDurationSec` = ?,`defaultRepetitions` = ?,`defaultIntensity` = ?,`notes` = ?,`isActive` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExerciseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getCategory());
        statement.bindString(4, entity.getDescription());
        if (entity.getVideoUri() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getVideoUri());
        }
        if (entity.getDefaultDurationSec() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getDefaultDurationSec());
        }
        if (entity.getDefaultRepetitions() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getDefaultRepetitions());
        }
        statement.bindString(8, entity.getDefaultIntensity());
        if (entity.getNotes() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getNotes());
        }
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(10, _tmp);
        statement.bindLong(11, entity.getCreatedAt());
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeactivate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE exercises SET isActive = 0 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfActivate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE exercises SET isActive = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ExerciseEntity exercise,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfExerciseEntity.insertAndReturnId(exercise);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<ExerciseEntity> exercises,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfExerciseEntity.insert(exercises);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ExerciseEntity exercise,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfExerciseEntity.handle(exercise);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deactivate(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivate.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfDeactivate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object activate(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActivate.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfActivate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ExerciseEntity>> getAllActive() {
    final String _sql = "SELECT * FROM exercises WHERE isActive = 1 ORDER BY category, name";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"exercises"}, new Callable<List<ExerciseEntity>>() {
      @Override
      @NonNull
      public List<ExerciseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfVideoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "videoUri");
          final int _cursorIndexOfDefaultDurationSec = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultDurationSec");
          final int _cursorIndexOfDefaultRepetitions = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultRepetitions");
          final int _cursorIndexOfDefaultIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultIntensity");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ExerciseEntity> _result = new ArrayList<ExerciseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExerciseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpVideoUri;
            if (_cursor.isNull(_cursorIndexOfVideoUri)) {
              _tmpVideoUri = null;
            } else {
              _tmpVideoUri = _cursor.getString(_cursorIndexOfVideoUri);
            }
            final Integer _tmpDefaultDurationSec;
            if (_cursor.isNull(_cursorIndexOfDefaultDurationSec)) {
              _tmpDefaultDurationSec = null;
            } else {
              _tmpDefaultDurationSec = _cursor.getInt(_cursorIndexOfDefaultDurationSec);
            }
            final Integer _tmpDefaultRepetitions;
            if (_cursor.isNull(_cursorIndexOfDefaultRepetitions)) {
              _tmpDefaultRepetitions = null;
            } else {
              _tmpDefaultRepetitions = _cursor.getInt(_cursorIndexOfDefaultRepetitions);
            }
            final String _tmpDefaultIntensity;
            _tmpDefaultIntensity = _cursor.getString(_cursorIndexOfDefaultIntensity);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ExerciseEntity(_tmpId,_tmpName,_tmpCategory,_tmpDescription,_tmpVideoUri,_tmpDefaultDurationSec,_tmpDefaultRepetitions,_tmpDefaultIntensity,_tmpNotes,_tmpIsActive,_tmpCreatedAt);
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
  public Flow<List<ExerciseEntity>> getByCategory(final String category) {
    final String _sql = "SELECT * FROM exercises WHERE category = ? AND isActive = 1 ORDER BY name";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"exercises"}, new Callable<List<ExerciseEntity>>() {
      @Override
      @NonNull
      public List<ExerciseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfVideoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "videoUri");
          final int _cursorIndexOfDefaultDurationSec = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultDurationSec");
          final int _cursorIndexOfDefaultRepetitions = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultRepetitions");
          final int _cursorIndexOfDefaultIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultIntensity");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ExerciseEntity> _result = new ArrayList<ExerciseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExerciseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpVideoUri;
            if (_cursor.isNull(_cursorIndexOfVideoUri)) {
              _tmpVideoUri = null;
            } else {
              _tmpVideoUri = _cursor.getString(_cursorIndexOfVideoUri);
            }
            final Integer _tmpDefaultDurationSec;
            if (_cursor.isNull(_cursorIndexOfDefaultDurationSec)) {
              _tmpDefaultDurationSec = null;
            } else {
              _tmpDefaultDurationSec = _cursor.getInt(_cursorIndexOfDefaultDurationSec);
            }
            final Integer _tmpDefaultRepetitions;
            if (_cursor.isNull(_cursorIndexOfDefaultRepetitions)) {
              _tmpDefaultRepetitions = null;
            } else {
              _tmpDefaultRepetitions = _cursor.getInt(_cursorIndexOfDefaultRepetitions);
            }
            final String _tmpDefaultIntensity;
            _tmpDefaultIntensity = _cursor.getString(_cursorIndexOfDefaultIntensity);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ExerciseEntity(_tmpId,_tmpName,_tmpCategory,_tmpDescription,_tmpVideoUri,_tmpDefaultDurationSec,_tmpDefaultRepetitions,_tmpDefaultIntensity,_tmpNotes,_tmpIsActive,_tmpCreatedAt);
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
  public Object getById(final long id, final Continuation<? super ExerciseEntity> $completion) {
    final String _sql = "SELECT * FROM exercises WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ExerciseEntity>() {
      @Override
      @Nullable
      public ExerciseEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfVideoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "videoUri");
          final int _cursorIndexOfDefaultDurationSec = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultDurationSec");
          final int _cursorIndexOfDefaultRepetitions = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultRepetitions");
          final int _cursorIndexOfDefaultIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultIntensity");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ExerciseEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpVideoUri;
            if (_cursor.isNull(_cursorIndexOfVideoUri)) {
              _tmpVideoUri = null;
            } else {
              _tmpVideoUri = _cursor.getString(_cursorIndexOfVideoUri);
            }
            final Integer _tmpDefaultDurationSec;
            if (_cursor.isNull(_cursorIndexOfDefaultDurationSec)) {
              _tmpDefaultDurationSec = null;
            } else {
              _tmpDefaultDurationSec = _cursor.getInt(_cursorIndexOfDefaultDurationSec);
            }
            final Integer _tmpDefaultRepetitions;
            if (_cursor.isNull(_cursorIndexOfDefaultRepetitions)) {
              _tmpDefaultRepetitions = null;
            } else {
              _tmpDefaultRepetitions = _cursor.getInt(_cursorIndexOfDefaultRepetitions);
            }
            final String _tmpDefaultIntensity;
            _tmpDefaultIntensity = _cursor.getString(_cursorIndexOfDefaultIntensity);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new ExerciseEntity(_tmpId,_tmpName,_tmpCategory,_tmpDescription,_tmpVideoUri,_tmpDefaultDurationSec,_tmpDefaultRepetitions,_tmpDefaultIntensity,_tmpNotes,_tmpIsActive,_tmpCreatedAt);
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
  public Flow<List<ExerciseEntity>> searchByName(final String query) {
    final String _sql = "SELECT * FROM exercises WHERE name LIKE '%' || ? || '%' AND isActive = 1 ORDER BY name";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"exercises"}, new Callable<List<ExerciseEntity>>() {
      @Override
      @NonNull
      public List<ExerciseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfVideoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "videoUri");
          final int _cursorIndexOfDefaultDurationSec = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultDurationSec");
          final int _cursorIndexOfDefaultRepetitions = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultRepetitions");
          final int _cursorIndexOfDefaultIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultIntensity");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ExerciseEntity> _result = new ArrayList<ExerciseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExerciseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpVideoUri;
            if (_cursor.isNull(_cursorIndexOfVideoUri)) {
              _tmpVideoUri = null;
            } else {
              _tmpVideoUri = _cursor.getString(_cursorIndexOfVideoUri);
            }
            final Integer _tmpDefaultDurationSec;
            if (_cursor.isNull(_cursorIndexOfDefaultDurationSec)) {
              _tmpDefaultDurationSec = null;
            } else {
              _tmpDefaultDurationSec = _cursor.getInt(_cursorIndexOfDefaultDurationSec);
            }
            final Integer _tmpDefaultRepetitions;
            if (_cursor.isNull(_cursorIndexOfDefaultRepetitions)) {
              _tmpDefaultRepetitions = null;
            } else {
              _tmpDefaultRepetitions = _cursor.getInt(_cursorIndexOfDefaultRepetitions);
            }
            final String _tmpDefaultIntensity;
            _tmpDefaultIntensity = _cursor.getString(_cursorIndexOfDefaultIntensity);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ExerciseEntity(_tmpId,_tmpName,_tmpCategory,_tmpDescription,_tmpVideoUri,_tmpDefaultDurationSec,_tmpDefaultRepetitions,_tmpDefaultIntensity,_tmpNotes,_tmpIsActive,_tmpCreatedAt);
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
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM exercises";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
  public Object countActive(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM exercises WHERE isActive = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
  public Flow<List<String>> getActiveCategories() {
    final String _sql = "SELECT DISTINCT category FROM exercises WHERE isActive = 1 ORDER BY category";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"exercises"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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
}
