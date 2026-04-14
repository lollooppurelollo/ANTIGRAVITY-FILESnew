package com.afa.fitadapt.di;

import com.afa.fitadapt.data.local.dao.SessionDao;
import com.afa.fitadapt.data.local.db.AfaDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DatabaseModule_ProvideSessionDaoFactory implements Factory<SessionDao> {
  private final Provider<AfaDatabase> dbProvider;

  public DatabaseModule_ProvideSessionDaoFactory(Provider<AfaDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SessionDao get() {
    return provideSessionDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideSessionDaoFactory create(
      javax.inject.Provider<AfaDatabase> dbProvider) {
    return new DatabaseModule_ProvideSessionDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideSessionDaoFactory create(Provider<AfaDatabase> dbProvider) {
    return new DatabaseModule_ProvideSessionDaoFactory(dbProvider);
  }

  public static SessionDao provideSessionDao(AfaDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSessionDao(db));
  }
}
