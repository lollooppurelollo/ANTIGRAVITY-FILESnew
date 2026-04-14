package com.afa.fitadapt.data.repository;

import com.afa.fitadapt.data.local.dao.GoalDao;
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
public final class ProgressRepository_Factory implements Factory<ProgressRepository> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<GoalDao> goalDaoProvider;

  private final Provider<DiaryRepository> diaryRepositoryProvider;

  public ProgressRepository_Factory(Provider<SessionRepository> sessionRepositoryProvider,
      Provider<GoalDao> goalDaoProvider, Provider<DiaryRepository> diaryRepositoryProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.goalDaoProvider = goalDaoProvider;
    this.diaryRepositoryProvider = diaryRepositoryProvider;
  }

  @Override
  public ProgressRepository get() {
    return newInstance(sessionRepositoryProvider.get(), goalDaoProvider.get(), diaryRepositoryProvider.get());
  }

  public static ProgressRepository_Factory create(
      javax.inject.Provider<SessionRepository> sessionRepositoryProvider,
      javax.inject.Provider<GoalDao> goalDaoProvider,
      javax.inject.Provider<DiaryRepository> diaryRepositoryProvider) {
    return new ProgressRepository_Factory(Providers.asDaggerProvider(sessionRepositoryProvider), Providers.asDaggerProvider(goalDaoProvider), Providers.asDaggerProvider(diaryRepositoryProvider));
  }

  public static ProgressRepository_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider, Provider<GoalDao> goalDaoProvider,
      Provider<DiaryRepository> diaryRepositoryProvider) {
    return new ProgressRepository_Factory(sessionRepositoryProvider, goalDaoProvider, diaryRepositoryProvider);
  }

  public static ProgressRepository newInstance(SessionRepository sessionRepository, GoalDao goalDao,
      DiaryRepository diaryRepository) {
    return new ProgressRepository(sessionRepository, goalDao, diaryRepository);
  }
}
