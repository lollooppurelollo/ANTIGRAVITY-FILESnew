package com.afa.fitadapt.di;

import com.afa.fitadapt.data.local.dao.ExportLogDao;
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
public final class DatabaseModule_ProvideExportLogDaoFactory implements Factory<ExportLogDao> {
  private final Provider<AfaDatabase> dbProvider;

  public DatabaseModule_ProvideExportLogDaoFactory(Provider<AfaDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ExportLogDao get() {
    return provideExportLogDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideExportLogDaoFactory create(
      javax.inject.Provider<AfaDatabase> dbProvider) {
    return new DatabaseModule_ProvideExportLogDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideExportLogDaoFactory create(Provider<AfaDatabase> dbProvider) {
    return new DatabaseModule_ProvideExportLogDaoFactory(dbProvider);
  }

  public static ExportLogDao provideExportLogDao(AfaDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideExportLogDao(db));
  }
}
