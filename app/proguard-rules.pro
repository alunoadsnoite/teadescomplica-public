# ProGuard rules for TEA Descomplica
# Note: isMinifyEnabled is false, so these rules are not applied.
# Add proguard rules here if minification is enabled in the future.

# Keep Application class
-keep class br.com.teadescomplica.TeaDescomplicaApplication {
    *;
}

# Keep models
-keep class br.com.teadescomplica.domain.model.** {
    *;
}

# Keep repository interfaces
-keep interface br.com.teadescomplica.domain.repository.** {
    *;
}
