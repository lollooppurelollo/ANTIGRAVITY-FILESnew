package com.afa.fitadapt.ui.home;

import com.afa.fitadapt.data.repository.ArticleRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<PatientProfileRepository> profileRepositoryProvider;

  private final Provider<TrainingCardRepository> cardRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<ArticleRepository> articleRepositoryProvider;

  private final Provider<GoalRepository> goalRepositoryProvider;

  public HomeViewModel_Factory(Provider<PatientProfileRepository> profileRepositoryProvider,
      Provider<TrainingCardRepository> cardRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<ArticleRepository> articleRepositoryProvider,
      Provider<GoalRepository> goalRepositoryProvider) {
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.cardRepositoryProvider = cardRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.articleRepositoryProvider = articleRepositoryProvider;
    this.goalRepositoryProvider = goalRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(profileRepositoryProvider.get(), cardRepositoryProvider.get(), sessionRepositoryProvider.get(), articleRepositoryProvider.get(), goalRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(
      javax.inject.Provider<PatientProfileRepository> profileRepositoryProvider,
      javax.inject.Provider<TrainingCardRepository> cardRepositoryProvider,
      javax.inject.Provider<SessionRepository> sessionRepositoryProvider,
      javax.inject.Provider<ArticleRepository> articleRepositoryProvider,
      javax.inject.Provider<GoalRepository> goalRepositoryProvider) {
    return new HomeViewModel_Factory(Providers.asDaggerProvider(profileRepositoryProvider), Providers.asDaggerProvider(cardRepositoryProvider), Providers.asDaggerProvider(sessionRepositoryProvider), Providers.asDaggerProvider(articleRepositoryProvider), Providers.asDaggerProvider(goalRepositoryProvider));
  }

  public static HomeViewModel_Factory create(
      Provider<PatientProfileRepository> profileRepositoryProvider,
      Provider<TrainingCardRepository> cardRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<ArticleRepository> articleRepositoryProvider,
      Provider<GoalRepository> goalRepositoryProvider) {
    return new HomeViewModel_Factory(profileRepositoryProvider, cardRepositoryProvider, sessionRepositoryProvider, articleRepositoryProvider, goalRepositoryProvider);
  }

  public static HomeViewModel newInstance(PatientProfileRepository profileRepository,
      TrainingCardRepository cardRepository, SessionRepository sessionRepository,
      ArticleRepository articleRepository, GoalRepository goalRepository) {
    return new HomeViewModel(profileRepository, cardRepository, sessionRepository, articleRepository, goalRepository);
  }
}
