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
import com.afa.fitadapt.data.local.entity.PatientProfileEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PatientProfileDao_Impl implements PatientProfileDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PatientProfileEntity> __insertionAdapterOfPatientProfileEntity;

  private final EntityDeletionOrUpdateAdapter<PatientProfileEntity> __updateAdapterOfPatientProfileEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePatientCode;

  private final SharedSQLiteStatement __preparedStmtOfSetAppInitialized;

  private final SharedSQLiteStatement __preparedStmtOfSetBiometricsEnabled;

  private final SharedSQLiteStatement __preparedStmtOfSetProtectedSectionConfigured;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLastAccess;

  public PatientProfileDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPatientProfileEntity = new EntityInsertionAdapter<PatientProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `patient_profile` (`id`,`patientCode`,`createdAt`,`appInitialized`,`biometricsEnabled`,`protectedSectionConfigured`,`lastAccessAt`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PatientProfileEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPatientCode());
        statement.bindLong(3, entity.getCreatedAt());
        final int _tmp = entity.getAppInitialized() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final int _tmp_1 = entity.getBiometricsEnabled() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        final int _tmp_2 = entity.getProtectedSectionConfigured() ? 1 : 0;
        statement.bindLong(6, _tmp_2);
        if (entity.getLastAccessAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLastAccessAt());
        }
      }
    };
    this.__updateAdapterOfPatientProfileEntity = new EntityDeletionOrUpdateAdapter<PatientProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `patient_profile` SET `id` = ?,`patientCode` = ?,`createdAt` = ?,`appInitialized` = ?,`biometricsEnabled` = ?,`protectedSectionConfigured` = ?,`lastAccessAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PatientProfileEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPatientCode());
        statement.bindLong(3, entity.getCreatedAt());
        final int _tmp = entity.getAppInitialized() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final int _tmp_1 = entity.getBiometricsEnabled() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        final int _tmp_2 = entity.getProtectedSectionConfigured() ? 1 : 0;
        statement.bindLong(6, _tmp_2);
        if (entity.getLastAccessAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLastAccessAt());
        }
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfUpdatePatientCode = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE patient_profile SET patientCode = ? WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfSetAppInitialized = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE patient_profile SET appInitialized = 1 WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfSetBiometricsEnabled = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE patient_profile SET biometricsEnabled = ? WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfSetProtectedSectionConfigured = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE patient_profile SET protectedSectionConfigured = 1 WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLastAccess = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE patient_profile SET lastAccessAt = ? WHERE id = 1";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrReplace(final PatientProfileEntity profile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPatientProfileEntity.insert(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final PatientProfileEntity profile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPatientProfileEntity.handle(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePatientCode(final String code, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePatientCode.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, code);
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
          __preparedStmtOfUpdatePatientCode.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setAppInitialized(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetAppInitialized.acquire();
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
          __preparedStmtOfSetAppInitialized.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setBiometricsEnabled(final boolean enabled,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetBiometricsEnabled.acquire();
        int _argIndex = 1;
        final int _tmp = enabled ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
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
          __preparedStmtOfSetBiometricsEnabled.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setProtectedSectionConfigured(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetProtectedSectionConfigured.acquire();
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
          __preparedStmtOfSetProtectedSectionConfigured.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLastAccess(final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLastAccess.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfUpdateLastAccess.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<PatientProfileEntity> getProfile() {
    final String _sql = "SELECT * FROM patient_profile WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"patient_profile"}, new Callable<PatientProfileEntity>() {
      @Override
      @Nullable
      public PatientProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientCode = CursorUtil.getColumnIndexOrThrow(_cursor, "patientCode");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfAppInitialized = CursorUtil.getColumnIndexOrThrow(_cursor, "appInitialized");
          final int _cursorIndexOfBiometricsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "biometricsEnabled");
          final int _cursorIndexOfProtectedSectionConfigured = CursorUtil.getColumnIndexOrThrow(_cursor, "protectedSectionConfigured");
          final int _cursorIndexOfLastAccessAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastAccessAt");
          final PatientProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPatientCode;
            _tmpPatientCode = _cursor.getString(_cursorIndexOfPatientCode);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpAppInitialized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAppInitialized);
            _tmpAppInitialized = _tmp != 0;
            final boolean _tmpBiometricsEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfBiometricsEnabled);
            _tmpBiometricsEnabled = _tmp_1 != 0;
            final boolean _tmpProtectedSectionConfigured;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfProtectedSectionConfigured);
            _tmpProtectedSectionConfigured = _tmp_2 != 0;
            final Long _tmpLastAccessAt;
            if (_cursor.isNull(_cursorIndexOfLastAccessAt)) {
              _tmpLastAccessAt = null;
            } else {
              _tmpLastAccessAt = _cursor.getLong(_cursorIndexOfLastAccessAt);
            }
            _result = new PatientProfileEntity(_tmpId,_tmpPatientCode,_tmpCreatedAt,_tmpAppInitialized,_tmpBiometricsEnabled,_tmpProtectedSectionConfigured,_tmpLastAccessAt);
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
  public Object getProfileSync(final Continuation<? super PatientProfileEntity> $completion) {
    final String _sql = "SELECT * FROM patient_profile WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PatientProfileEntity>() {
      @Override
      @Nullable
      public PatientProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientCode = CursorUtil.getColumnIndexOrThrow(_cursor, "patientCode");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfAppInitialized = CursorUtil.getColumnIndexOrThrow(_cursor, "appInitialized");
          final int _cursorIndexOfBiometricsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "biometricsEnabled");
          final int _cursorIndexOfProtectedSectionConfigured = CursorUtil.getColumnIndexOrThrow(_cursor, "protectedSectionConfigured");
          final int _cursorIndexOfLastAccessAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastAccessAt");
          final PatientProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPatientCode;
            _tmpPatientCode = _cursor.getString(_cursorIndexOfPatientCode);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final boolean _tmpAppInitialized;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAppInitialized);
            _tmpAppInitialized = _tmp != 0;
            final boolean _tmpBiometricsEnabled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfBiometricsEnabled);
            _tmpBiometricsEnabled = _tmp_1 != 0;
            final boolean _tmpProtectedSectionConfigured;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfProtectedSectionConfigured);
            _tmpProtectedSectionConfigured = _tmp_2 != 0;
            final Long _tmpLastAccessAt;
            if (_cursor.isNull(_cursorIndexOfLastAccessAt)) {
              _tmpLastAccessAt = null;
            } else {
              _tmpLastAccessAt = _cursor.getLong(_cursorIndexOfLastAccessAt);
            }
            _result = new PatientProfileEntity(_tmpId,_tmpPatientCode,_tmpCreatedAt,_tmpAppInitialized,_tmpBiometricsEnabled,_tmpProtectedSectionConfigured,_tmpLastAccessAt);
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
