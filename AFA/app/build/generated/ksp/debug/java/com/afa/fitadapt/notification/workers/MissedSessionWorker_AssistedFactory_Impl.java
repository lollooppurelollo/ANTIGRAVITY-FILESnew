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
public final class MissedSessionWorker_AssistedFactory_Impl implements MissedSessionWorker_AssistedFactory {
  private final MissedSessionWorker_Factory delegateFactory;

  MissedSessionWorker_AssistedFactory_Impl(MissedSessionWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public MissedSessionWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<MissedSessionWorker_AssistedFactory> create(
      MissedSessionWorker_Factory delegateFactory) {
    return InstanceFactory.create(new MissedSessionWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<MissedSessionWorker_AssistedFactory> createFactoryProvider(
      MissedSessionWorker_Factory delegateFactory) {
    return InstanceFactory.create(new MissedSessionWorker_AssistedFactory_Impl(delegateFactory));
  }
}
