package com.afa.fitadapt.data.repository;

import com.afa.fitadapt.data.local.dao.ArticleDao;
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
public final class ArticleRepository_Factory implements Factory<ArticleRepository> {
  private final Provider<ArticleDao> articleDaoProvider;

  public ArticleRepository_Factory(Provider<ArticleDao> articleDaoProvider) {
    this.articleDaoProvider = articleDaoProvider;
  }

  @Override
  public ArticleRepository get() {
    return newInstance(articleDaoProvider.get());
  }

  public static ArticleRepository_Factory create(
      javax.inject.Provider<ArticleDao> articleDaoProvider) {
    return new ArticleRepository_Factory(Providers.asDaggerProvider(articleDaoProvider));
  }

  public static ArticleRepository_Factory create(Provider<ArticleDao> articleDaoProvider) {
    return new ArticleRepository_Factory(articleDaoProvider);
  }

  public static ArticleRepository newInstance(ArticleDao articleDao) {
    return new ArticleRepository(articleDao);
  }
}
