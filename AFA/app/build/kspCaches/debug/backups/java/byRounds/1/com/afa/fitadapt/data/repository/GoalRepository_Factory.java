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
public final class GoalRepository_Factory implements Factory<GoalRepository> {
  private final Provider<GoalDao> goalDaoProvider;

  public GoalRepository_Factory(Provider<GoalDao> goalDaoProvider) {
    this.goalDaoProvider = goalDaoProvider;
  }

  @Override
  public GoalRepository get() {
    return newInstance(goalDaoProvider.get());
  }

  public static GoalRepository_Factory create(javax.inject.Provider<GoalDao> goalDaoProvider) {
    return new GoalRepository_Factory(Providers.asDaggerProvider(goalDaoProvider));
  }

  public static GoalRepository_Factory create(Provider<GoalDao> goalDaoProvider) {
    return new GoalRepository_Factory(goalDaoProvider);
  }

  public static GoalRepository newInstance(GoalDao goalDao) {
    return new GoalRepository(goalDao);
  }
}
