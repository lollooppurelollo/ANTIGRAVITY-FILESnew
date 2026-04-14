package com.afa.fitadapt.data.repository;

import com.afa.fitadapt.data.local.dao.ExerciseDao;
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
public final class ExerciseRepository_Factory implements Factory<ExerciseRepository> {
  private final Provider<ExerciseDao> exerciseDaoProvider;

  public ExerciseRepository_Factory(Provider<ExerciseDao> exerciseDaoProvider) {
    this.exerciseDaoProvider = exerciseDaoProvider;
  }

  @Override
  public ExerciseRepository get() {
    return newInstance(exerciseDaoProvider.get());
  }

  public static ExerciseRepository_Factory create(
      javax.inject.Provider<ExerciseDao> exerciseDaoProvider) {
    return new ExerciseRepository_Factory(Providers.asDaggerProvider(exerciseDaoProvider));
  }

  public static ExerciseRepository_Factory create(Provider<ExerciseDao> exerciseDaoProvider) {
    return new ExerciseRepository_Factory(exerciseDaoProvider);
  }

  public static ExerciseRepository newInstance(ExerciseDao exerciseDao) {
    return new ExerciseRepository(exerciseDao);
  }
}
