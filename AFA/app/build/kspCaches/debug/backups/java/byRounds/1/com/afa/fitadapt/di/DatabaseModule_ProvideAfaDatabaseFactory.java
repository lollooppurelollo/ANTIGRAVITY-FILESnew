package com.afa.fitadapt.di;

import android.content.Context;
import com.afa.fitadapt.data.local.db.AfaDatabase;
import com.afa.fitadapt.security.CryptoManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvideAfaDatabaseFactory implements Factory<AfaDatabase> {
  private final Provider<Context> contextProvider;

  private final Provider<CryptoManager> cryptoManagerProvider;

  public DatabaseModule_ProvideAfaDatabaseFactory(Provider<Context> contextProvider,
      Provider<CryptoManager> cryptoManagerProvider) {
    this.contextProvider = contextProvider;
    this.cryptoManagerProvider = cryptoManagerProvider;
  }

  @Override
  public AfaDatabase get() {
    return provideAfaDatabase(contextProvider.get(), cryptoManagerProvider.get());
  }

  public static DatabaseModule_ProvideAfaDatabaseFactory create(
      javax.inject.Provider<Context> contextProvider,
      javax.inject.Provider<CryptoManager> cryptoManagerProvider) {
    return new DatabaseModule_ProvideAfaDatabaseFactory(Providers.asDaggerProvider(contextProvider), Providers.asDaggerProvider(cryptoManagerProvider));
  }

  public static DatabaseModule_ProvideAfaDatabaseFactory create(Provider<Context> contextProvider,
      Provider<CryptoManager> cryptoManagerProvider) {
    return new DatabaseModule_ProvideAfaDatabaseFactory(contextProvider, cryptoManagerProvider);
  }

  public static AfaDatabase provideAfaDatabase(Context context, CryptoManager cryptoManager) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAfaDatabase(context, cryptoManager));
  }
}
