package com.afa.fitadapt.ui.session;

import com.afa.fitadapt.data.repository.ExerciseRepository;
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
public final class SessionViewModel_Factory implements Factory<SessionViewModel> {
  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<TrainingCardRepository> cardRepositoryProvider;

  private final Provider<ExerciseRepository> exerciseRepositoryProvider;

  public SessionViewModel_Factory(Provider<SessionRepository> sessionRepositoryProvider,
      Provider<TrainingCardRepository> cardRepositoryProvider,
      Provider<ExerciseRepository> exerciseRepositoryProvider) {
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.cardRepositoryProvider = cardRepositoryProvider;
    this.exerciseRepositoryProvider = exerciseRepositoryProvider;
  }

  @Override
  public SessionViewModel get() {
    return newInstance(sessionRepositoryProvider.get(), cardRepositoryProvider.get(), exerciseRepositoryProvider.get());
  }

  public static SessionViewModel_Factory create(
      javax.inject.Provider<SessionRepository> sessionRepositoryProvider,
      javax.inject.Provider<TrainingCardRepository> cardRepositoryProvider,
      javax.inject.Provider<ExerciseRepository> exerciseRepositoryProvider) {
    return new SessionViewModel_Factory(Providers.asDaggerProvider(sessionRepositoryProvider), Providers.asDaggerProvider(cardRepositoryProvider), Providers.asDaggerProvider(exerciseRepositoryProvider));
  }

  public static SessionViewModel_Factory create(
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<TrainingCardRepository> cardRepositoryProvider,
      Provider<ExerciseRepository> exerciseRepositoryProvider) {
    return new SessionViewModel_Factory(sessionRepositoryProvider, cardRepositoryProvider, exerciseRepositoryProvider);
  }

  public static SessionViewModel newInstance(SessionRepository sessionRepository,
      TrainingCardRepository cardRepository, ExerciseRepository exerciseRepository) {
    return new SessionViewModel(sessionRepository, cardRepository, exerciseRepository);
  }
}
