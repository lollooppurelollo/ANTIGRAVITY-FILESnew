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
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.afa.fitadapt.data.local.entity.ScaleEntryEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
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
public final class ScaleEntryDao_Impl implements ScaleEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ScaleEntryEntity> __insertionAdapterOfScaleEntryEntity;

  private final EntityDeletionOrUpdateAdapter<ScaleEntryEntity> __updateAdapterOfScaleEntryEntity;

  public ScaleEntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfScaleEntryEntity = new EntityInsertionAdapter<ScaleEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `scale_entries` (`id`,`date`,`asthenia`,`osteoarticularPain`,`restDyspnea`,`exertionDyspnea`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScaleEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDate());
        if (entity.getAsthenia() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getAsthenia());
        }
        if (entity.getOsteoarticularPain() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getOsteoarticularPain());
        }
        if (entity.getRestDyspnea() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getRestDyspnea());
        }
        if (entity.getExertionDyspnea() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getExertionDyspnea());
        }
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
    this.__updateAdapterOfScaleEntryEntity = new EntityDeletionOrUpdateAdapter<ScaleEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `scale_entries` SET `id` = ?,`date` = ?,`asthenia` = ?,`osteoarticularPain` = ?,`restDyspnea` = ?,`exertionDyspnea` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScaleEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDate());
        if (entity.getAsthenia() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getAsthenia());
        }
        if (entity.getOsteoarticularPain() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getOsteoarticularPain());
        }
        if (entity.getRestDyspnea() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getRestDyspnea());
        }
        if (entity.getExertionDyspnea() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getExertionDyspnea());
        }
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final ScaleEntryEntity entry, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfScaleEntryEntity.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ScaleEntryEntity entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfScaleEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ScaleEntryEntity>> getAll() {
    final String _sql = "SELECT * FROM scale_entries ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"scale_entries"}, new Callable<List<ScaleEntryEntity>>() {
      @Override
      @NonNull
      public List<ScaleEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfAsthenia = CursorUtil.getColumnIndexOrThrow(_cursor, "asthenia");
          final int _cursorIndexOfOsteoarticularPain = CursorUtil.getColumnIndexOrThrow(_cursor, "osteoarticularPain");
          final int _cursorIndexOfRestDyspnea = CursorUtil.getColumnIndexOrThrow(_cursor, "restDyspnea");
          final int _cursorIndexOfExertionDyspnea = CursorUtil.getColumnIndexOrThrow(_cursor, "exertionDyspnea");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ScaleEntryEntity> _result = new ArrayList<ScaleEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScaleEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final Integer _tmpAsthenia;
            if (_cursor.isNull(_cursorIndexOfAsthenia)) {
              _tmpAsthenia = null;
            } else {
              _tmpAsthenia = _cursor.getInt(_cursorIndexOfAsthenia);
            }
            final Integer _tmpOsteoarticularPain;
            if (_cursor.isNull(_cursorIndexOfOsteoarticularPain)) {
              _tmpOsteoarticularPain = null;
            } else {
              _tmpOsteoarticularPain = _cursor.getInt(_cursorIndexOfOsteoarticularPain);
            }
            final Integer _tmpRestDyspnea;
            if (_cursor.isNull(_cursorIndexOfRestDyspnea)) {
              _tmpRestDyspnea = null;
            } else {
              _tmpRestDyspnea = _cursor.getInt(_cursorIndexOfRestDyspnea);
            }
            final Integer _tmpExertionDyspnea;
            if (_cursor.isNull(_cursorIndexOfExertionDyspnea)) {
              _tmpExertionDyspnea = null;
            } else {
              _tmpExertionDyspnea = _cursor.getInt(_cursorIndexOfExertionDyspnea);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ScaleEntryEntity(_tmpId,_tmpDate,_tmpAsthenia,_tmpOsteoarticularPain,_tmpRestDyspnea,_tmpExertionDyspnea,_tmpCreatedAt);
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
  public Object getById(final long id, final Continuation<? super ScaleEntryEntity> $completion) {
    final String _sql = "SELECT * FROM scale_entries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ScaleEntryEntity>() {
      @Override
      @Nullable
      public ScaleEntryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfAsthenia = CursorUtil.getColumnIndexOrThrow(_cursor, "asthenia");
          final int _cursorIndexOfOsteoarticularPain = CursorUtil.getColumnIndexOrThrow(_cursor, "osteoarticularPain");
          final int _cursorIndexOfRestDyspnea = CursorUtil.getColumnIndexOrThrow(_cursor, "restDyspnea");
          final int _cursorIndexOfExertionDyspnea = CursorUtil.getColumnIndexOrThrow(_cursor, "exertionDyspnea");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ScaleEntryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final Integer _tmpAsthenia;
            if (_cursor.isNull(_cursorIndexOfAsthenia)) {
              _tmpAsthenia = null;
            } else {
              _tmpAsthenia = _cursor.getInt(_cursorIndexOfAsthenia);
            }
            final Integer _tmpOsteoarticularPain;
            if (_cursor.isNull(_cursorIndexOfOsteoarticularPain)) {
              _tmpOsteoarticularPain = null;
            } else {
              _tmpOsteoarticularPain = _cursor.getInt(_cursorIndexOfOsteoarticularPain);
            }
            final Integer _tmpRestDyspnea;
            if (_cursor.isNull(_cursorIndexOfRestDyspnea)) {
              _tmpRestDyspnea = null;
            } else {
              _tmpRestDyspnea = _cursor.getInt(_cursorIndexOfRestDyspnea);
            }
            final Integer _tmpExertionDyspnea;
            if (_cursor.isNull(_cursorIndexOfExertionDyspnea)) {
              _tmpExertionDyspnea = null;
            } else {
              _tmpExertionDyspnea = _cursor.getInt(_cursorIndexOfExertionDyspnea);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new ScaleEntryEntity(_tmpId,_tmpDate,_tmpAsthenia,_tmpOsteoarticularPain,_tmpRestDyspnea,_tmpExertionDyspnea,_tmpCreatedAt);
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
  public Object getLatest(final Continuation<? super ScaleEntryEntity> $completion) {
    final String _sql = "SELECT * FROM scale_entries ORDER BY date DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ScaleEntryEntity>() {
      @Override
      @Nullable
      public ScaleEntryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfAsthenia = CursorUtil.getColumnIndexOrThrow(_cursor, "asthenia");
          final int _cursorIndexOfOsteoarticularPain = CursorUtil.getColumnIndexOrThrow(_cursor, "osteoarticularPain");
          final int _cursorIndexOfRestDyspnea = CursorUtil.getColumnIndexOrThrow(_cursor, "restDyspnea");
          final int _cursorIndexOfExertionDyspnea = CursorUtil.getColumnIndexOrThrow(_cursor, "exertionDyspnea");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ScaleEntryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final Integer _tmpAsthenia;
            if (_cursor.isNull(_cursorIndexOfAsthenia)) {
              _tmpAsthenia = null;
            } else {
              _tmpAsthenia = _cursor.getInt(_cursorIndexOfAsthenia);
            }
            final Integer _tmpOsteoarticularPain;
            if (_cursor.isNull(_cursorIndexOfOsteoarticularPain)) {
              _tmpOsteoarticularPain = null;
            } else {
              _tmpOsteoarticularPain = _cursor.getInt(_cursorIndexOfOsteoarticularPain);
            }
            final Integer _tmpRestDyspnea;
            if (_cursor.isNull(_cursorIndexOfRestDyspnea)) {
              _tmpRestDyspnea = null;
            } else {
              _tmpRestDyspnea = _cursor.getInt(_cursorIndexOfRestDyspnea);
            }
            final Integer _tmpExertionDyspnea;
            if (_cursor.isNull(_cursorIndexOfExertionDyspnea)) {
              _tmpExertionDyspnea = null;
            } else {
              _tmpExertionDyspnea = _cursor.getInt(_cursorIndexOfExertionDyspnea);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new ScaleEntryEntity(_tmpId,_tmpDate,_tmpAsthenia,_tmpOsteoarticularPain,_tmpRestDyspnea,_tmpExertionDyspnea,_tmpCreatedAt);
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
  public Flow<List<ScaleEntryEntity>> getEntriesInRange(final long fromDate, final long toDate) {
    final String _sql = "SELECT * FROM scale_entries WHERE date >= ? AND date <= ? ORDER BY date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, fromDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, toDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"scale_entries"}, new Callable<List<ScaleEntryEntity>>() {
      @Override
      @NonNull
      public List<ScaleEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfAsthenia = CursorUtil.getColumnIndexOrThrow(_cursor, "asthenia");
          final int _cursorIndexOfOsteoarticularPain = CursorUtil.getColumnIndexOrThrow(_cursor, "osteoarticularPain");
          final int _cursorIndexOfRestDyspnea = CursorUtil.getColumnIndexOrThrow(_cursor, "restDyspnea");
          final int _cursorIndexOfExertionDyspnea = CursorUtil.getColumnIndexOrThrow(_cursor, "exertionDyspnea");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ScaleEntryEntity> _result = new ArrayList<ScaleEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScaleEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final Integer _tmpAsthenia;
            if (_cursor.isNull(_cursorIndexOfAsthenia)) {
              _tmpAsthenia = null;
            } else {
              _tmpAsthenia = _cursor.getInt(_cursorIndexOfAsthenia);
            }
            final Integer _tmpOsteoarticularPain;
            if (_cursor.isNull(_cursorIndexOfOsteoarticularPain)) {
              _tmpOsteoarticularPain = null;
            } else {
              _tmpOsteoarticularPain = _cursor.getInt(_cursorIndexOfOsteoarticularPain);
            }
            final Integer _tmpRestDyspnea;
            if (_cursor.isNull(_cursorIndexOfRestDyspnea)) {
              _tmpRestDyspnea = null;
            } else {
              _tmpRestDyspnea = _cursor.getInt(_cursorIndexOfRestDyspnea);
            }
            final Integer _tmpExertionDyspnea;
            if (_cursor.isNull(_cursorIndexOfExertionDyspnea)) {
              _tmpExertionDyspnea = null;
            } else {
              _tmpExertionDyspnea = _cursor.getInt(_cursorIndexOfExertionDyspnea);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ScaleEntryEntity(_tmpId,_tmpDate,_tmpAsthenia,_tmpOsteoarticularPain,_tmpRestDyspnea,_tmpExertionDyspnea,_tmpCreatedAt);
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
  public Flow<List<ScaleEntryEntity>> getLatestEntries(final int limit) {
    final String _sql = "SELECT * FROM scale_entries ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"scale_entries"}, new Callable<List<ScaleEntryEntity>>() {
      @Override
      @NonNull
      public List<ScaleEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfAsthenia = CursorUtil.getColumnIndexOrThrow(_cursor, "asthenia");
          final int _cursorIndexOfOsteoarticularPain = CursorUtil.getColumnIndexOrThrow(_cursor, "osteoarticularPain");
          final int _cursorIndexOfRestDyspnea = CursorUtil.getColumnIndexOrThrow(_cursor, "restDyspnea");
          final int _cursorIndexOfExertionDyspnea = CursorUtil.getColumnIndexOrThrow(_cursor, "exertionDyspnea");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ScaleEntryEntity> _result = new ArrayList<ScaleEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScaleEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final Integer _tmpAsthenia;
            if (_cursor.isNull(_cursorIndexOfAsthenia)) {
              _tmpAsthenia = null;
            } else {
              _tmpAsthenia = _cursor.getInt(_cursorIndexOfAsthenia);
            }
            final Integer _tmpOsteoarticularPain;
            if (_cursor.isNull(_cursorIndexOfOsteoarticularPain)) {
              _tmpOsteoarticularPain = null;
            } else {
              _tmpOsteoarticularPain = _cursor.getInt(_cursorIndexOfOsteoarticularPain);
            }
            final Integer _tmpRestDyspnea;
            if (_cursor.isNull(_cursorIndexOfRestDyspnea)) {
              _tmpRestDyspnea = null;
            } else {
              _tmpRestDyspnea = _cursor.getInt(_cursorIndexOfRestDyspnea);
            }
            final Integer _tmpExertionDyspnea;
            if (_cursor.isNull(_cursorIndexOfExertionDyspnea)) {
              _tmpExertionDyspnea = null;
            } else {
              _tmpExertionDyspnea = _cursor.getInt(_cursorIndexOfExertionDyspnea);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ScaleEntryEntity(_tmpId,_tmpDate,_tmpAsthenia,_tmpOsteoarticularPain,_tmpRestDyspnea,_tmpExertionDyspnea,_tmpCreatedAt);
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
    final String _sql = "SELECT COUNT(*) FROM scale_entries";
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
  public Object averageAstheniaSince(final long sinceDate,
      final Continuation<? super Float> $completion) {
    final String _sql = "SELECT AVG(asthenia) FROM scale_entries WHERE date >= ? AND asthenia IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sinceDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
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
  public Object averagePainSince(final long sinceDate,
      final Continuation<? super Float> $completion) {
    final String _sql = "SELECT AVG(osteoarticularPain) FROM scale_entries WHERE date >= ? AND osteoarticularPain IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sinceDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
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
  public Object averageRestDyspneaSince(final long sinceDate,
      final Continuation<? super Float> $completion) {
    final String _sql = "SELECT AVG(restDyspnea) FROM scale_entries WHERE date >= ? AND restDyspnea IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sinceDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
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
  public Object averageExertionDyspneaSince(final long sinceDate,
      final Continuation<? super Float> $completion) {
    final String _sql = "SELECT AVG(exertionDyspnea) FROM scale_entries WHERE date >= ? AND exertionDyspnea IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, sinceDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
