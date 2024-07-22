# ``CertnIDSDK``

CertnIDSDK provides components for document capture, digital onboarding process, NFC reading and related functionalities which are easy to integrate into an Android application.

## Key Features
- **Identity Verification:** Uses advanced algorithms to verify user identities through biometric recognition and document analysis.
- **Document Capture and Validation:** Supports capturing high-quality images of identity documents and validates them against various security features.
- **Liveness Detection:** Ensures that the user is physically present during the onboarding process through liveness detection techniques.
- **Regulatory Compliance:** Helps businesses stay compliant with KYC (Know Your Customer) and AML (Anti-Money Laundering) regulations.
- **User-Friendly Interface:** Provides a seamless and intuitive user experience with customizable UI elements.
- **Data Security:** Ensures the highest level of data security with end-to-end encryption.

## Requirements
- Minimum Android API level 21
- Minimum Kotlin Gradle plugin version 1.6.0 (if used)

## Distribution

**Maven Repository**
CertnIDSDK is distributed as an Android library (.aar package) stored in the Certn maven repository.

In order to integrate CertnIDSDK into your project, the first step is to include the Certn maven repository and Google repository to your top level build.gradle file.

build.gradle
```kotlin
allprojects {
    repositories {
        maven {
            url 'https://maven.pkg.github.com/certn-id-mobile-sdk/android-sdk-private'
            credentials {
                username "${certn_id_user}"
                password "${certn_id_password}"
            }
        }
    }
}
```
Then, specify the dependency on CertnIDSDK library in the module’s build.gradle file. Dependencies of this library will be downloaded alongside the library.

build.gradle
```kotlin
dependencies {
    //…
    implementation "certn-id-mobile-sdk:android-sdk-private:$certnIdVersion"
    //…
}
```
In order to optimize application size, we also recommend adding the following excludes to your application’s **build.gradle** file.

build.gradle
```kotlin
android {
    //…
    packagingOptions {
        exclude("**/libjnidispatch.a")
        exclude("**/jnidispatch.dll")
        exclude("**/libjnidispatch.jnilib")
        exclude("**/*.proto")
    }
}
```

## Supported Architectures

CertnIDSDK provides binaries for these architectures:

- armeabi-v7a
- arm64-v8a
- x86
- x86_64

If your target application format is APK and not Android App Bundle, and the APK splits are not specified, the generated APK file will contain binaries for all available architectures. Therefore we recommend to use APK splits. For example, to generate arm64-v8a APK, add the following section into your module **build.gradle**:

build.gradle
```kotlin
splits {
    abi {
        enable true
        reset()
        include 'arm64-v8a'
        universalApk false
    }
}
```
If you do not specify this section, the resulting application can become too large in size.

## Licensing
In order to use CertnIDSDK in other apps, it must be licensed. The license can be compiled into the application as it is bound to the application ID specified in build.gradle:

build.gradle
```kotlin
defaultConfig {
    applicationId "co.certn.mobile"
    //…
}
```
In order to obtain the license, please contact your Certn’ representative specifying the application ID. If the application uses build flavors with different application IDs, each flavor must contain a separate license. Put the license file into the raw resource folder.

To read a raw file in Android and ensure that the reading operation is performed on an IO thread, you can use Kotlin coroutines.
```kotlin
suspend operator fun invoke() = withContext(Dispatchers.IO) {
    val configuration = createCertnIDSdkConfiguration(context)
    //…
}

private fun createCertnIDSdkConfiguration(context: Context) = CertnIDSdkConfiguration(
    context = context,
    licenseBytes = readLicenseBytes(context.resources),
    libraries = listOf(
        CertnIDLibraryType.DocumentLibraryType,
        CertnIDLibraryType.FaceBalancedLibraryType,
        CertnIDLibraryType.NfcLibraryType
    )
)

private fun readLicenseBytes(resources: Resources) =
    resources.openRawResource(R.raw.certn_id_license).use(InputStream::readBytes)
```

## Usage
CertnIDSDK uses these libraries:
- **DocumentLibraryType**
- **BaceBalancedLibraryType**
- **FaceFastLibraryType**
- **NfcLibraryType**

Only one of BaceBalancedLibraryType and FaceFastLibraryType can be used at the same time.

```kotlin
private fun createCertnIDSdkConfiguration(context: Context) = CertnIDSdkConfiguration(
    context = context,
    licenseBytes = readLicenseBytes(context.resources),
    libraries = listOf(
        CertnIDLibraryType.DocumentLibraryType,
        CertnIDLibraryType.FaceBalancedLibraryType, //or FaceFastLibraryType
        CertnIDLibraryType.NfcLibraryType
    )
)
```

## Permissions
CertnIDSDK declares the following permission in **AndroidManifest.xml**:

AndroidManifest.xml
```kotlin
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.NFC" />
<uses-permission android:name="android.permission.INTERNET" />
```

## Support and Documentation
For detailed documentation and support, visit the ...
