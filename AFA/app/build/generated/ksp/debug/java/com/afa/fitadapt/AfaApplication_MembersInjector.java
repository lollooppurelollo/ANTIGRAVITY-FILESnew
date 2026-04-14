package com.afa.fitadapt;

import androidx.hilt.work.HiltWorkerFactory;
import com.afa.fitadapt.notification.WorkScheduler;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class AfaApplication_MembersInjector implements MembersInjector<AfaApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  private final Provider<WorkScheduler> workSchedulerProvider;

  public AfaApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<WorkScheduler> workSchedulerProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
    this.workSchedulerProvider = workSchedulerProvider;
  }

  public static MembersInjector<AfaApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<WorkScheduler> workSchedulerProvider) {
    return new AfaApplication_MembersInjector(workerFactoryProvider, workSchedulerProvider);
  }

  public static MembersInjector<AfaApplication> create(
      javax.inject.Provider<HiltWorkerFactory> workerFactoryProvider,
      javax.inject.Provider<WorkScheduler> workSchedulerProvider) {
    return new AfaApplication_MembersInjector(Providers.asDaggerProvider(workerFactoryProvider), Providers.asDaggerProvider(workSchedulerProvider));
  }

  @Override
  public void injectMembers(AfaApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
    injectWorkScheduler(instance, workSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.afa.fitadapt.AfaApplication.workerFactory")
  public static void injectWorkerFactory(AfaApplication instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }

  @InjectedFieldSignature("com.afa.fitadapt.AfaApplication.workScheduler")
  public static void injectWorkScheduler(AfaApplication instance, WorkScheduler workScheduler) {
    instance.workScheduler = workScheduler;
  }
}
