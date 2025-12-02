plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" // ← agrega esta línea
}

android {
    namespace = "com.example.barlacteo_manuel_caceres"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.barlacteo_manuel_caceres"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
    buildFeatures { compose = true }
    packaging {
        resources {
            pickFirst("META-INF/LICENSE.md")
            pickFirst("META-INF/LICENSE-notice.md")
        }
    }
}

dependencies {
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.text)
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // Activity / Lifecycle / Navigation
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Imágenes
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Networking opcional
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // kotlinx-serialization (esto resuelve 'kotlinx')
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3") // ← agrega esta
    // --- LÓGICA (Unit Tests) ---
    // JUnit 5 (El motor de los tests)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    // Kotest (Para aserciones más legibles: "shouldBe", "shouldNotBe")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")

    // MockK (Para simular dependencias sin usar las reales)
    testImplementation("io.mockk:mockk:1.13.8")

    // Coroutines Test (Para runTest y controlar el tiempo)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // --- UI (Android Tests) ---
    // Nota: Compose suele usar JUnit4 por defecto para UI, pero se puede mezclar.
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.0")

    // MockK para Android (Instrumentado)
    androidTestImplementation("io.mockk:mockk-android:1.13.8")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}


// Configuración para permitir JUnit 5 en pruebas unitarias
tasks.withType<Test> {
    useJUnitPlatform()
}