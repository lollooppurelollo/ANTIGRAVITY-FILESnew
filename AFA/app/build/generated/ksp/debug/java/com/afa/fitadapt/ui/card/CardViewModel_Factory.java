package com.afa.fitadapt.ui.card;

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
public final class CardViewModel_Factory implements Factory<CardViewModel> {
  private final Provider<TrainingCardRepository> cardRepositoryProvider;

  private final Provider<ExerciseRepository> exerciseRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  public CardViewModel_Factory(Provider<TrainingCardRepository> cardRepositoryProvider,
      Provider<ExerciseRepository> exerciseRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    this.cardRepositoryProvider = cardRepositoryProvider;
    this.exerciseRepositoryProvider = exerciseRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public CardViewModel get() {
    return newInstance(cardRepositoryProvider.get(), exerciseRepositoryProvider.get(), sessionRepositoryProvider.get());
  }

  public static CardViewModel_Factory create(
      javax.inject.Provider<TrainingCardRepository> cardRepositoryProvider,
      javax.inject.Provider<ExerciseRepository> exerciseRepositoryProvider,
      javax.inject.Provider<SessionRepository> sessionRepositoryProvider) {
    return new CardViewModel_Factory(Providers.asDaggerProvider(cardRepositoryProvider), Providers.asDaggerProvider(exerciseRepositoryProvider), Providers.asDaggerProvider(sessionRepositoryProvider));
  }

  public static CardViewModel_Factory create(
      Provider<TrainingCardRepository> cardRepositoryProvider,
      Provider<ExerciseRepository> exerciseRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new CardViewModel_Factory(cardRepositoryProvider, exerciseRepositoryProvider, sessionRepositoryProvider);
  }

  public static CardViewModel newInstance(TrainingCardRepository cardRepository,
      ExerciseRepository exerciseRepository, SessionRepository sessionRepository) {
    return new CardViewModel(cardRepository, exerciseRepository, sessionRepository);
  }
}
