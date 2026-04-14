package com.afa.fitadapt.notification.workers;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.afa.fitadapt.data.local.datastore.UserPreferences;
import com.afa.fitadapt.data.repository.SessionRepository;
import com.afa.fitadapt.notification.NotificationHelper;
import dagger.internal.DaggerGenerated;
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
public final class ReminderWorker_Factory {
  private final Provider<UserPreferences> userPreferencesProvider;

  private final Provider<SessionRepository> sessionRepositoryProvider;

  private final Provider<NotificationHelper> notificationHelperProvider;

  public ReminderWorker_Factory(Provider<UserPreferences> userPreferencesProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    this.userPreferencesProvider = userPreferencesProvider;
    this.sessionRepositoryProvider = sessionRepositoryProvider;
    this.notificationHelperProvider = notificationHelperProvider;
  }

  public ReminderWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, userPreferencesProvider.get(), sessionRepositoryProvider.get(), notificationHelperProvider.get());
  }

  public static ReminderWorker_Factory create(
      javax.inject.Provider<UserPreferences> userPreferencesProvider,
      javax.inject.Provider<SessionRepository> sessionRepositoryProvider,
      javax.inject.Provider<NotificationHelper> notificationHelperProvider) {
    return new ReminderWorker_Factory(Providers.asDaggerProvider(userPreferencesProvider), Providers.asDaggerProvider(sessionRepositoryProvider), Providers.asDaggerProvider(notificationHelperProvider));
  }

  public static ReminderWorker_Factory create(Provider<UserPreferences> userPreferencesProvider,
      Provider<SessionRepository> sessionRepositoryProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    return new ReminderWorker_Factory(userPreferencesProvider, sessionRepositoryProvider, notificationHelperProvider);
  }

  public static ReminderWorker newInstance(Context context, WorkerParameters workerParams,
      UserPreferences userPreferences, SessionRepository sessionRepository,
      NotificationHelper notificationHelper) {
    return new ReminderWorker(context, workerParams, userPreferences, sessionRepository, notificationHelper);
  }
}
