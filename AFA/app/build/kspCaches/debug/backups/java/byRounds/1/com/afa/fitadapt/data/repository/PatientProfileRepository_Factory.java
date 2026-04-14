package com.afa.fitadapt.data.repository;

import com.afa.fitadapt.data.local.dao.PatientProfileDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class PatientProfileRepository_Factory implements Factory<PatientProfileRepository> {
  private final Provider<PatientProfileDao> profileDaoProvider;

  public PatientProfileRepository_Factory(Provider<PatientProfileDao> profileDaoProvider) {
    this.profileDaoProvider = profileDaoProvider;
  }

  @Override
  public PatientProfileRepository get() {
    return newInstance(profileDaoProvider.get());
  }

  public static PatientProfileRepository_Factory create(
      javax.inject.Provider<PatientProfileDao> profileDaoProvider) {
    return new PatientProfileRepository_Factory(Providers.asDaggerProvider(profileDaoProvider));
  }

  public static PatientProfileRepository_Factory create(
      Provider<PatientProfileDao> profileDaoProvider) {
    return new PatientProfileRepository_Factory(profileDaoProvider);
  }

  public static PatientProfileRepository newInstance(PatientProfileDao profileDao) {
    return new PatientProfileRepository(profileDao);
  }
}
