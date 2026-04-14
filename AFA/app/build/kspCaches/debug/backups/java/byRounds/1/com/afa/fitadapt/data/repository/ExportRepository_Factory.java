package com.afa.fitadapt.data.repository;

import android.content.Context;
import com.afa.fitadapt.data.local.dao.ExportLogDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ExportRepository_Factory implements Factory<ExportRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<ExportLogDao> exportLogDaoProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<DiaryRepository> diaryRepositoryProvider;

  private final Provider<PatientProfileRepository> patientProfileRepositoryProvider;

  private final Provider<GoalRepository> goalRepositoryProvider;

  public ExportRepository_Factory(Provider<Context> contextProvider,
      Provider<ExportLogDao> exportLogDaoProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<PatientProfileRepository> patientProfileRepositoryProvider,
      Provider<GoalRepository> goalRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.exportLogDaoProvider = exportLogDaoProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.diaryRepositoryProvider = diaryRepositoryProvider;
    this.patientProfileRepositoryProvider = patientProfileRepositoryProvider;
    this.goalRepositoryProvider = goalRepositoryProvider;
  }

  @Override
  public ExportRepository get() {
    return newInstance(contextProvider.get(), exportLogDaoProvider.get(), sessionRepositoryProvider.get(), diaryRepositoryProvider.get(), patientProfileRepositoryProvider.get(), goalRepositoryProvider.get());
  }

  public static ExportRepository_Factory create(javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<ExportLogDao> exportLogDaoProvider,
      javax.inject.Provider<SessionRepository> sessionRepositoryProvider,
      javax.inject.Provider<DiaryRepository> diaryRepositoryProvider,
      javax.inject.Provider<PatientProfileRepository> patientProfileRepositoryProvider,
      javax.inject.Provider<GoalRepository> goalRepositoryProvider) {
    return new ExportRepository_Factory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(exportLogDaoProvider), Providers.asDaggerProvider(sessionRepositoryProvider), Providers.asDaggerProvider(diaryRepositoryProvider), Providers.asDaggerProvider(patientProfileRepositoryProvider), Providers.asDaggerProvider(goalRepositoryProvider));
  }

  public static ExportRepository_Factory create(Provider<Context> contextProvider,
      Provider<ExportLogDao> exportLogDaoProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<PatientProfileRepository> patientProfileRepositoryProvider,
      Provider<GoalRepository> goalRepositoryProvider) {
    return new ExportRepository_Factory(contextProvider, exportLogDaoProvider, sessionRepositoryProvider, diaryRepositoryProvider, patientProfileRepositoryProvider, goalRepositoryProvider);
  }

  public static ExportRepository newInstance(Context context, ExportLogDao exportLogDao,
      SessionRepository sessionRepository, DiaryRepository diaryRepository,
      PatientProfileRepository patientProfileRepository, GoalRepository goalRepository) {
    return new ExportRepository(context, exportLogDao, sessionRepository, diaryRepository, patientProfileRepository, goalRepository);
  }
}
