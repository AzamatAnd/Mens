plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("org.jetbrains.kotlin.plugin.compose")
	id("com.google.devtools.ksp")
}

android {
	namespace = "com.example.cycletracker"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.example.cycletracker"
		minSdk = 24
		targetSdk = 34
		versionCode = 1
		versionName = "1.0.0"

		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
			// Optional signing via gradle.properties if provided
			signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
		}
		debug {
			isMinifyEnabled = false
		}
	}

	signingConfigs {
		create("release") {
			val storeFileProp = project.findProperty("RELEASE_STORE_FILE") as String?
			val storePasswordProp = project.findProperty("RELEASE_STORE_PASSWORD") as String?
			val keyAliasProp = project.findProperty("RELEASE_KEY_ALIAS") as String?
			val keyPasswordProp = project.findProperty("RELEASE_KEY_PASSWORD") as String?
			if (!storeFileProp.isNullOrBlank() && !storePasswordProp.isNullOrBlank() && !keyAliasProp.isNullOrBlank() && !keyPasswordProp.isNullOrBlank()) {
				storeFile = file(storeFileProp)
				storePassword = storePasswordProp
				keyAlias = keyAliasProp
				keyPassword = keyPasswordProp
			}
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
		isCoreLibraryDesugaringEnabled = true
	}

	kotlin {
		jvmToolchain(17)
	}

	buildFeatures {
		compose = true
	}

	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.14"
	}

	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

ksp {
	arg("room.schemaLocation", "${projectDir}/schemas")
	arg("room.generateKotlin", "true")
}

dependencies {
	implementation(platform("androidx.compose:compose-bom:2024.09.02"))
	implementation("androidx.core:core-ktx:1.13.1")
	implementation("androidx.activity:activity-compose:1.9.2")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
	implementation("androidx.navigation:navigation-compose:2.8.1")

	implementation("androidx.compose.material3:material3")
	implementation("androidx.compose.material:material-icons-extended")
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-tooling-preview")
	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")
	androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.02"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")

	implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
	implementation("com.google.accompanist:accompanist-permissions:0.36.0")

	implementation("androidx.datastore:datastore-preferences:1.1.1")

	implementation("androidx.room:room-runtime:2.6.1")
	ksp("androidx.room:room-compiler:2.6.1")
	implementation("androidx.room:room-ktx:2.6.1")

	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")

	// Glance App Widget
	implementation("androidx.glance:glance-appwidget:1.1.0")

	// JSON (org.json is available), CSV we do manually

	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.2.1")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
