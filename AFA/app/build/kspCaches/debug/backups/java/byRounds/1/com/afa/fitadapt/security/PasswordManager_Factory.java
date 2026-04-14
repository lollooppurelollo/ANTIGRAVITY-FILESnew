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
public final class PasswordManager_Factory implements Factory<PasswordManager> {
  private final Provider<Context> contextProvider;

  public PasswordManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PasswordManager get() {
    return newInstance(contextProvider.get());
  }

  public static PasswordManager_Factory create(javax.inject.Provider<Context> contextProvider) {
    return new PasswordManager_Factory(Providers.asDaggerProvider(contextProvider));
  }

  public static PasswordManager_Factory create(Provider<Context> contextProvider) {
    return new PasswordManager_Factory(contextProvider);
  }

  public static PasswordManager newInstance(Context context) {
    return new PasswordManager(context);
  }
}
