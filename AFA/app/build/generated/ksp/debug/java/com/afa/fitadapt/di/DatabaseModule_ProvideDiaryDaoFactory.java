package com.afa.fitadapt.di;

import com.afa.fitadapt.data.local.dao.DiaryDao;
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
public final class DatabaseModule_ProvideDiaryDaoFactory implements Factory<DiaryDao> {
  private final Provider<AfaDatabase> dbProvider;

  public DatabaseModule_ProvideDiaryDaoFactory(Provider<AfaDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public DiaryDao get() {
    return provideDiaryDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideDiaryDaoFactory create(
      javax.inject.Provider<AfaDatabase> dbProvider) {
    return new DatabaseModule_ProvideDiaryDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideDiaryDaoFactory create(Provider<AfaDatabase> dbProvider) {
    return new DatabaseModule_ProvideDiaryDaoFactory(dbProvider);
  }

  public static DiaryDao provideDiaryDao(AfaDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDiaryDao(db));
  }
}
