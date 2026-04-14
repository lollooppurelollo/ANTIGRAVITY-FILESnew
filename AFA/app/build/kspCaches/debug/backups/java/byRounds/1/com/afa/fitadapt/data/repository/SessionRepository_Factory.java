package com.afa.fitadapt.data.repository;

import com.afa.fitadapt.data.local.dao.SessionDao;
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
public final class SessionRepository_Factory implements Factory<SessionRepository> {
  private final Provider<SessionDao> sessionDaoProvider;

  public SessionRepository_Factory(Provider<SessionDao> sessionDaoProvider) {
    this.sessionDaoProvider = sessionDaoProvider;
  }

  @Override
  public SessionRepository get() {
    return newInstance(sessionDaoProvider.get());
  }

  public static SessionRepository_Factory create(
      javax.inject.Provider<SessionDao> sessionDaoProvider) {
    return new SessionRepository_Factory(Providers.asDaggerProvider(sessionDaoProvider));
  }

  public static SessionRepository_Factory create(Provider<SessionDao> sessionDaoProvider) {
    return new SessionRepository_Factory(sessionDaoProvider);
  }

  public static SessionRepository newInstance(SessionDao sessionDao) {
    return new SessionRepository(sessionDao);
  }
}
