package com.afa.fitadapt.data.repository;

import com.afa.fitadapt.data.local.dao.TrainingCardDao;
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
public final class TrainingCardRepository_Factory implements Factory<TrainingCardRepository> {
  private final Provider<TrainingCardDao> trainingCardDaoProvider;

  public TrainingCardRepository_Factory(Provider<TrainingCardDao> trainingCardDaoProvider) {
    this.trainingCardDaoProvider = trainingCardDaoProvider;
  }

  @Override
  public TrainingCardRepository get() {
    return newInstance(trainingCardDaoProvider.get());
  }

  public static TrainingCardRepository_Factory create(
      javax.inject.Provider<TrainingCardDao> trainingCardDaoProvider) {
    return new TrainingCardRepository_Factory(Providers.asDaggerProvider(trainingCardDaoProvider));
  }

  public static TrainingCardRepository_Factory create(
      Provider<TrainingCardDao> trainingCardDaoProvider) {
    return new TrainingCardRepository_Factory(trainingCardDaoProvider);
  }

  public static TrainingCardRepository newInstance(TrainingCardDao trainingCardDao) {
    return new TrainingCardRepository(trainingCardDao);
  }
}
