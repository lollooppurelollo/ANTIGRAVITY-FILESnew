package com.afa.fitadapt;

import com.afa.fitadapt.security.BiometricHelper;
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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<BiometricHelper> biometricHelperProvider;

  public MainActivity_MembersInjector(Provider<BiometricHelper> biometricHelperProvider) {
    this.biometricHelperProvider = biometricHelperProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<BiometricHelper> biometricHelperProvider) {
    return new MainActivity_MembersInjector(biometricHelperProvider);
  }

  public static MembersInjector<MainActivity> create(
      javax.inject.Provider<BiometricHelper> biometricHelperProvider) {
    return new MainActivity_MembersInjector(Providers.asDaggerProvider(biometricHelperProvider));
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectBiometricHelper(instance, biometricHelperProvider.get());
  }

  @InjectedFieldSignature("com.afa.fitadapt.MainActivity.biometricHelper")
  public static void injectBiometricHelper(MainActivity instance, BiometricHelper biometricHelper) {
    instance.biometricHelper = biometricHelper;
  }
}
