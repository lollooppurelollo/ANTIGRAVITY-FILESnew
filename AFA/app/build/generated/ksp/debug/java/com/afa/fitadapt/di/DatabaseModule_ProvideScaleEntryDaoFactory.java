package com.afa.fitadapt.di;

import com.afa.fitadapt.data.local.dao.ScaleEntryDao;
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
public final class DatabaseModule_ProvideScaleEntryDaoFactory implements Factory<ScaleEntryDao> {
  private final Provider<AfaDatabase> dbProvider;

  public DatabaseModule_ProvideScaleEntryDaoFactory(Provider<AfaDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ScaleEntryDao get() {
    return provideScaleEntryDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideScaleEntryDaoFactory create(
      javax.inject.Provider<AfaDatabase> dbProvider) {
    return new DatabaseModule_ProvideScaleEntryDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideScaleEntryDaoFactory create(
      Provider<AfaDatabase> dbProvider) {
    return new DatabaseModule_ProvideScaleEntryDaoFactory(dbProvider);
  }

  public static ScaleEntryDao provideScaleEntryDao(AfaDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideScaleEntryDao(db));
  }
}
