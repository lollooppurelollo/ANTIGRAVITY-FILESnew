package com.afa.fitadapt.di;

import com.afa.fitadapt.data.local.dao.TrainingCardDao;
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
public final class DatabaseModule_ProvideTrainingCardDaoFactory implements Factory<TrainingCardDao> {
  private final Provider<AfaDatabase> dbProvider;

  public DatabaseModule_ProvideTrainingCardDaoFactory(Provider<AfaDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TrainingCardDao get() {
    return provideTrainingCardDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideTrainingCardDaoFactory create(
      javax.inject.Provider<AfaDatabase> dbProvider) {
    return new DatabaseModule_ProvideTrainingCardDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideTrainingCardDaoFactory create(
      Provider<AfaDatabase> dbProvider) {
    return new DatabaseModule_ProvideTrainingCardDaoFactory(dbProvider);
  }

  public static TrainingCardDao provideTrainingCardDao(AfaDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTrainingCardDao(db));
  }
}
