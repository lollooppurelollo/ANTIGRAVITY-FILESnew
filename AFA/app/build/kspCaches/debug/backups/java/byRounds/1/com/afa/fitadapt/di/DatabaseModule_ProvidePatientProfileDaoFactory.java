package com.afa.fitadapt.di;

import com.afa.fitadapt.data.local.dao.PatientProfileDao;
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
public final class DatabaseModule_ProvidePatientProfileDaoFactory implements Factory<PatientProfileDao> {
  private final Provider<AfaDatabase> dbProvider;

  public DatabaseModule_ProvidePatientProfileDaoFactory(Provider<AfaDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public PatientProfileDao get() {
    return providePatientProfileDao(dbProvider.get());
  }

  public static DatabaseModule_ProvidePatientProfileDaoFactory create(
      javax.inject.Provider<AfaDatabase> dbProvider) {
    return new DatabaseModule_ProvidePatientProfileDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvidePatientProfileDaoFactory create(
      Provider<AfaDatabase> dbProvider) {
    return new DatabaseModule_ProvidePatientProfileDaoFactory(dbProvider);
  }

  public static PatientProfileDao providePatientProfileDao(AfaDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePatientProfileDao(db));
  }
}
