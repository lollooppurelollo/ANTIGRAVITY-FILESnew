package com.afa.fitadapt.security;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class CryptoManager_Factory implements Factory<CryptoManager> {
  private final Provider<KeystoreManager> keystoreManagerProvider;

  private final Provider<Context> contextProvider;

  public CryptoManager_Factory(Provider<KeystoreManager> keystoreManagerProvider,
      Provider<Context> contextProvider) {
    this.keystoreManagerProvider = keystoreManagerProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public CryptoManager get() {
    return newInstance(keystoreManagerProvider.get(), contextProvider.get());
  }

  public static CryptoManager_Factory create(
      javax.inject.Provider<KeystoreManager> keystoreManagerProvider,
      javax.inject.Provider<Context> contextProvider) {
    return new CryptoManager_Factory(Providers.asDaggerProvider(keystoreManagerProvider), Providers.asDaggerProvider(contextProvider));
  }

  public static CryptoManager_Factory create(Provider<KeystoreManager> keystoreManagerProvider,
      Provider<Context> contextProvider) {
    return new CryptoManager_Factory(keystoreManagerProvider, contextProvider);
  }

  public static CryptoManager newInstance(KeystoreManager keystoreManager, Context context) {
    return new CryptoManager(keystoreManager, context);
  }
}
