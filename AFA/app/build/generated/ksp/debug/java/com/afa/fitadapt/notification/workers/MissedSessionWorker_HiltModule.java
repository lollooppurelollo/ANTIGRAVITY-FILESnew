package com.afa.fitadapt.notification.workers;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = MissedSessionWorker.class
)
public interface MissedSessionWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.afa.fitadapt.notification.workers.MissedSessionWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      MissedSessionWorker_AssistedFactory factory);
}
