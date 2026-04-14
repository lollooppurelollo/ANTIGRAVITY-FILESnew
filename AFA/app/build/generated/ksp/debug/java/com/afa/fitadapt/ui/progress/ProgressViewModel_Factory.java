package com.afa.fitadapt.ui.progress;

import com.afa.fitadapt.data.repository.ProgressRepository;
import com.afa.fitadapt.data.repository.SessionRepository;
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
public final class ProgressViewModel_Factory implements Factory<ProgressViewModel> {
  private final Provider<ProgressRepository> progressRepositoryProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  public ProgressViewModel_Factory(Provider<ProgressRepository> progressRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    this.progressRepositoryProvider = progressRepositoryProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
  }

  @Override
  public ProgressViewModel get() {
    return newInstance(progressRepositoryProvider.get(), sessionRepositoryProvider.get());
  }

  public static ProgressViewModel_Factory create(
      javax.inject.Provider<ProgressRepository> progressRepositoryProvider,
      javax.inject.Provider<SessionRepository> sessionRepositoryProvider) {
    return new ProgressViewModel_Factory(Providers.asDaggerProvider(progressRepositoryProvider), Providers.asDaggerProvider(sessionRepositoryProvider));
  }

  public static ProgressViewModel_Factory create(
      Provider<ProgressRepository> progressRepositoryProvider,
      Provider<SessionRepository> sessionRepositoryProvider) {
    return new ProgressViewModel_Factory(progressRepositoryProvider, sessionRepositoryProvider);
  }

  public static ProgressViewModel newInstance(ProgressRepository progressRepository,
      SessionRepository sessionRepository) {
    return new ProgressViewModel(progressRepository, sessionRepository);
  }
}
