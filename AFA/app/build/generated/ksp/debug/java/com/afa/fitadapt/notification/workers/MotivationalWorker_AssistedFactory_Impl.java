package com.afa.fitadapt.notification.workers;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MotivationalWorker_AssistedFactory_Impl implements MotivationalWorker_AssistedFactory {
  private final MotivationalWorker_Factory delegateFactory;

  MotivationalWorker_AssistedFactory_Impl(MotivationalWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public MotivationalWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<MotivationalWorker_AssistedFactory> create(
      MotivationalWorker_Factory delegateFactory) {
    return InstanceFactory.create(new MotivationalWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<MotivationalWorker_AssistedFactory> createFactoryProvider(
      MotivationalWorker_Factory delegateFactory) {
    return InstanceFactory.create(new MotivationalWorker_AssistedFactory_Impl(delegateFactory));
  }
}
