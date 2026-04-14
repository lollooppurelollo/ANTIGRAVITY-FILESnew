package com.afa.fitadapt.ui.articles;

import com.afa.fitadapt.data.repository.ArticleRepository;
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
public final class ArticlesViewModel_Factory implements Factory<ArticlesViewModel> {
  private final Provider<ArticleRepository> articleRepositoryProvider;

  public ArticlesViewModel_Factory(Provider<ArticleRepository> articleRepositoryProvider) {
    this.articleRepositoryProvider = articleRepositoryProvider;
  }

  @Override
  public ArticlesViewModel get() {
    return newInstance(articleRepositoryProvider.get());
  }

  public static ArticlesViewModel_Factory create(
      javax.inject.Provider<ArticleRepository> articleRepositoryProvider) {
    return new ArticlesViewModel_Factory(Providers.asDaggerProvider(articleRepositoryProvider));
  }

  public static ArticlesViewModel_Factory create(
      Provider<ArticleRepository> articleRepositoryProvider) {
    return new ArticlesViewModel_Factory(articleRepositoryProvider);
  }

  public static ArticlesViewModel newInstance(ArticleRepository articleRepository) {
    return new ArticlesViewModel(articleRepository);
  }
}
