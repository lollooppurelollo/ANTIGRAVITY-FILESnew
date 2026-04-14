package com.afa.fitadapt.ui.auth;

import com.afa.fitadapt.data.local.datastore.UserPreferences;
import com.afa.fitadapt.data.local.db.DatabaseSeeder;
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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<UserPreferences> userPreferencesProvider;

  private final Provider<PasswordManager> passwordManagerProvider;

  private final Provider<PatientProfileRepository> profileRepositoryProvider;

  private final Provider<DatabaseSeeder> databaseSeederProvider;

  public AuthViewModel_Factory(Provider<UserPreferences> userPreferencesProvider,
      Provider<PasswordManager> passwordManagerProvider,
      Provider<PatientProfileRepository> profileRepositoryProvider,
      Provider<DatabaseSeeder> databaseSeederProvider) {
    this.userPreferencesProvider = userPreferencesProvider;
    this.passwordManagerProvider = passwordManagerProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.databaseSeederProvider = databaseSeederProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(userPreferencesProvider.get(), passwordManagerProvider.get(), profileRepositoryProvider.get(), databaseSeederProvider.get());
  }

  public static AuthViewModel_Factory create(
      javax.inject.Provider<UserPreferences> userPreferencesProvider,
      javax.inject.Provider<PasswordManager> passwordManagerProvider,
      javax.inject.Provider<PatientProfileRepository> profileRepositoryProvider,
      javax.inject.Provider<DatabaseSeeder> databaseSeederProvider) {
    return new AuthViewModel_Factory(Providers.asDaggerProvider(userPreferencesProvider), Providers.asDaggerProvider(passwordManagerProvider), Providers.asDaggerProvider(profileRepositoryProvider), Providers.asDaggerProvider(databaseSeederProvider));
  }

  public static AuthViewModel_Factory create(Provider<UserPreferences> userPreferencesProvider,
      Provider<PasswordManager> passwordManagerProvider,
      Provider<PatientProfileRepository> profileRepositoryProvider,
      Provider<DatabaseSeeder> databaseSeederProvider) {
    return new AuthViewModel_Factory(userPreferencesProvider, passwordManagerProvider, profileRepositoryProvider, databaseSeederProvider);
  }

  public static AuthViewModel newInstance(UserPreferences userPreferences,
      PasswordManager passwordManager, PatientProfileRepository profileRepository,
      DatabaseSeeder databaseSeeder) {
    return new AuthViewModel(userPreferences, passwordManager, profileRepository, databaseSeeder);
  }
}
