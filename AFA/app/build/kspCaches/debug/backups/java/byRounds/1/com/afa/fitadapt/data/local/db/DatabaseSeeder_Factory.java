package com.afa.fitadapt.data.local.db;

import com.afa.fitadapt.data.local.dao.ArticleDao;
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
public final class DatabaseSeeder_Factory implements Factory<DatabaseSeeder> {
  private final Provider<ExerciseDao> exerciseDaoProvider;

  private final Provider<ArticleDao> articleDaoProvider;

  public DatabaseSeeder_Factory(Provider<ExerciseDao> exerciseDaoProvider,
      Provider<ArticleDao> articleDaoProvider) {
    this.exerciseDaoProvider = exerciseDaoProvider;
    this.articleDaoProvider = articleDaoProvider;
  }

  @Override
  public DatabaseSeeder get() {
    return newInstance(exerciseDaoProvider.get(), articleDaoProvider.get());
  }

  public static DatabaseSeeder_Factory create(
      javax.inject.Provider<ExerciseDao> exerciseDaoProvider,
      javax.inject.Provider<ArticleDao> articleDaoProvider) {
    return new DatabaseSeeder_Factory(Providers.asDaggerProvider(exerciseDaoProvider), Providers.asDaggerProvider(articleDaoProvider));
  }

  public static DatabaseSeeder_Factory create(Provider<ExerciseDao> exerciseDaoProvider,
      Provider<ArticleDao> articleDaoProvider) {
    return new DatabaseSeeder_Factory(exerciseDaoProvider, articleDaoProvider);
  }

  public static DatabaseSeeder newInstance(ExerciseDao exerciseDao, ArticleDao articleDao) {
    return new DatabaseSeeder(exerciseDao, articleDao);
  }
}
