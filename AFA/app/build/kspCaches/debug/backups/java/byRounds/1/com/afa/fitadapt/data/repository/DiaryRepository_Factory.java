package com.afa.fitadapt.data.repository;

import com.afa.fitadapt.data.local.dao.DiaryDao;
import com.afa.fitadapt.data.local.dao.ScaleEntryDao;
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
public final class DiaryRepository_Factory implements Factory<DiaryRepository> {
  private final Provider<DiaryDao> diaryDaoProvider;

  private final Provider<ScaleEntryDao> scaleEntryDaoProvider;

  public DiaryRepository_Factory(Provider<DiaryDao> diaryDaoProvider,
      Provider<ScaleEntryDao> scaleEntryDaoProvider) {
    this.diaryDaoProvider = diaryDaoProvider;
    this.scaleEntryDaoProvider = scaleEntryDaoProvider;
  }

  @Override
  public DiaryRepository get() {
    return newInstance(diaryDaoProvider.get(), scaleEntryDaoProvider.get());
  }

  public static DiaryRepository_Factory create(javax.inject.Provider<DiaryDao> diaryDaoProvider,
      javax.inject.Provider<ScaleEntryDao> scaleEntryDaoProvider) {
    return new DiaryRepository_Factory(Providers.asDaggerProvider(diaryDaoProvider), Providers.asDaggerProvider(scaleEntryDaoProvider));
  }

  public static DiaryRepository_Factory create(Provider<DiaryDao> diaryDaoProvider,
      Provider<ScaleEntryDao> scaleEntryDaoProvider) {
    return new DiaryRepository_Factory(diaryDaoProvider, scaleEntryDaoProvider);
  }

  public static DiaryRepository newInstance(DiaryDao diaryDao, ScaleEntryDao scaleEntryDao) {
    return new DiaryRepository(diaryDao, scaleEntryDao);
  }
}
