package com.afa.fitadapt.di;

import com.afa.fitadapt.data.local.dao.ArticleDao;
import com.afa.fitadapt.data.local.db.AfaDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideArticleDaoFactory implements Factory<ArticleDao> {
  private final Provider<AfaDatabase> dbProvider;

  public DatabaseModule_ProvideArticleDaoFactory(Provider<AfaDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ArticleDao get() {
    return provideArticleDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideArticleDaoFactory create(
      javax.inject.Provider<AfaDatabase> dbProvider) {
    return new DatabaseModule_ProvideArticleDaoFactory(Providers.asDaggerProvider(dbProvider));
  }

  public static DatabaseModule_ProvideArticleDaoFactory create(Provider<AfaDatabase> dbProvider) {
    return new DatabaseModule_ProvideArticleDaoFactory(dbProvider);
  }

  public static ArticleDao provideArticleDao(AfaDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideArticleDao(db));
  }
}
