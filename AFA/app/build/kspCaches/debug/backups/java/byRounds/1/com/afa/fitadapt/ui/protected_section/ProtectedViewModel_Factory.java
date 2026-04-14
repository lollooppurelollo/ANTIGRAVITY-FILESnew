package com.afa.fitadapt.ui.protected_section;

import com.afa.fitadapt.data.repository.PatientProfileRepository;
import com.afa.fitadapt.security.PasswordManager;
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
public final class ProtectedViewModel_Factory implements Factory<ProtectedViewModel> {
  private final Provider<PasswordManager> passwordManagerProvider;

  private final Provider<PatientProfileRepository> profileRepositoryProvider;

  public ProtectedViewModel_Factory(Provider<PasswordManager> passwordManagerProvider,
      Provider<PatientProfileRepository> profileRepositoryProvider) {
    this.passwordManagerProvider = passwordManagerProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
  }

  @Override
  public ProtectedViewModel get() {
    return newInstance(passwordManagerProvider.get(), profileRepositoryProvider.get());
  }

  public static ProtectedViewModel_Factory create(
      javax.inject.Provider<PasswordManager> passwordManagerProvider,
      javax.inject.Provider<PatientProfileRepository> profileRepositoryProvider) {
    return new ProtectedViewModel_Factory(Providers.asDaggerProvider(passwordManagerProvider), Providers.asDaggerProvider(profileRepositoryProvider));
  }

  public static ProtectedViewModel_Factory create(Provider<PasswordManager> passwordManagerProvider,
      Provider<PatientProfileRepository> profileRepositoryProvider) {
    return new ProtectedViewModel_Factory(passwordManagerProvider, profileRepositoryProvider);
  }

  public static ProtectedViewModel newInstance(PasswordManager passwordManager,
      PatientProfileRepository profileRepository) {
    return new ProtectedViewModel(passwordManager, profileRepository);
  }
}
