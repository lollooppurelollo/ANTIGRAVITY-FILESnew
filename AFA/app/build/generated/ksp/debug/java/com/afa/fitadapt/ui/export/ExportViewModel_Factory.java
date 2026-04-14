package com.afa.fitadapt.ui.export;

import com.afa.fitadapt.data.repository.DiaryRepository;
import com.afa.fitadapt.data.repository.ExportRepository;
import com.afa.fitadapt.data.repository.GoalRepository;
import com.afa.fitadapt.data.repository.PatientProfileRepository;
import com.afa.fitadapt.data.repository.SessionRepository;
import com.afa.fitadapt.data.repository.TrainingCardRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class ExportViewModel_Factory implements Factory<ExportViewModel> {
  private final Provider<ExportRepository> exportRepositoryProvider;

  private final Provider<PatientProfileRepository> profileRepositoryProvider;

  private final Provider<TrainingCardRepository> cardRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<DiaryRepository> diaryRepositoryProvider;

  private final Provider<GoalRepository> goalRepositoryProvider;

  public ExportViewModel_Factory(Provider<ExportRepository> exportRepositoryProvider,
      Provider<PatientProfileRepository> profileRepositoryProvider,
      Provider<TrainingCardRepository> cardRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<GoalRepository> goalRepositoryProvider) {
    this.exportRepositoryProvider = exportRepositoryProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.cardRepositoryProvider = cardRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.diaryRepositoryProvider = diaryRepositoryProvider;
    this.goalRepositoryProvider = goalRepositoryProvider;
  }

  @Override
  public ExportViewModel get() {
    return newInstance(exportRepositoryProvider.get(), profileRepositoryProvider.get(), cardRepositoryProvider.get(), sessionRepositoryProvider.get(), diaryRepositoryProvider.get(), goalRepositoryProvider.get());
  }

  public static ExportViewModel_Factory create(
      javax.inject.Provider<ExportRepository> exportRepositoryProvider,
      javax.inject.Provider<PatientProfileRepository> profileRepositoryProvider,
      javax.inject.Provider<TrainingCardRepository> cardRepositoryProvider,
      javax.inject.Provider<SessionRepository> sessionRepositoryProvider,
      javax.inject.Provider<DiaryRepository> diaryRepositoryProvider,
      javax.inject.Provider<GoalRepository> goalRepositoryProvider) {
    return new ExportViewModel_Factory(Providers.asDaggerProvider(exportRepositoryProvider), Providers.asDaggerProvider(profileRepositoryProvider), Providers.asDaggerProvider(cardRepositoryProvider), Providers.asDaggerProvider(sessionRepositoryProvider), Providers.asDaggerProvider(diaryRepositoryProvider), Providers.asDaggerProvider(goalRepositoryProvider));
  }

  public static ExportViewModel_Factory create(Provider<ExportRepository> exportRepositoryProvider,
      Provider<PatientProfileRepository> profileRepositoryProvider,
      Provider<TrainingCardRepository> cardRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<GoalRepository> goalRepositoryProvider) {
    return new ExportViewModel_Factory(exportRepositoryProvider, profileRepositoryProvider, cardRepositoryProvider, sessionRepositoryProvider, diaryRepositoryProvider, goalRepositoryProvider);
  }

  public static ExportViewModel newInstance(ExportRepository exportRepository,
      PatientProfileRepository profileRepository, TrainingCardRepository cardRepository,
      SessionRepository sessionRepository, DiaryRepository diaryRepository,
      GoalRepository goalRepository) {
    return new ExportViewModel(exportRepository, profileRepository, cardRepository, sessionRepository, diaryRepository, goalRepository);
  }
}
