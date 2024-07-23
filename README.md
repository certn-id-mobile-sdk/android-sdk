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
## UI Components - DocumentLibraryType

**Fragment Configuration**

CertnIDDocumentAutoCaptureFragment provides the main functionality. It is abstract fragment and therefore must be subclassed and overrided its abstract methods. Its required runtime interaction is provided by public methods, for example certnIDStart().

```kotlin
class BasicDocumentAutoCaptureFragment : CertnIDDocumentAutoCaptureFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        certnIDStart()
    }

    //…
}
```

The CertnIDDocumentAutoCaptureFragment requires a configuration. To provide configuration data, you should override the provideCertnIDDocumentConfiguration() method in your subclass implementation. This method should return an instance of the CertnIDDocumentConfiguration data class with the desired parameters.

```kotlin
class BasicDocumentAutoCaptureFragment : CertnIDDocumentAutoCaptureFragment() {

    override fun provideCertnIDDocumentConfiguration() = CertnIDDocumentConfiguration(
        certnIDCameraFacing = CertnIDCameraFacing.BackType,
        certnIDCameraPreviewScaleType = CertnIDCameraPreviewScaleType.FitType,
        //…
    )

    //…
}
```

**Camera permission**

A fragment will check the camera permission (Manifest.permission.CAMERA) right before the camera is started. If the camera permission is granted the fragment will start the camera. If the camera permission is not granted the fragment will use Android API - ActivityResultContracts.RequestPermission to request the camera permission. Android OS will present the system dialog to the user of the app. If the user explicitly denies the permission at this point, onCertnIDNoCameraPermission() callback is called. Implement this callback in order to navigate the user further in your app workflow.

**Orientation Change**

In order to handle the orientation change in multi-window mode correctly, configure the activity in your AndroidManifest.xml file as follows:

```kotlin
<activity
    android:name=".MyActivity"
    android:configChanges="screenSize|smallestScreenSize|screenLayout|orientation" />
```

**Document Auto Capture**

The fragment with instructions for obtaining quality document images suitable for further processing.

In order to configure the behaviour of CertnIDDocumentAutoCaptureFragment, use CertnIDDocumentConfiguration (see Fragment Configuration).

To use the fragment, create a subclass of CertnIDDocumentAutoCaptureFragment and override appropriate callbacks.

To start the document auto capture process call the certnIDStart() method. You can start the process any time.

In case you want to handle detection data, implement onCertnIDProcessed() callback. This callback is called with each processed camera frame. When the document auto capture process finishes successfully, the result will be returned via the onCertnIDCaptured() callback.

In case you want to force the capture event, call the certnIDRequestCapture() method. The most recent image will be returned via the onCertnIDCaptured() callback asynchronously.

Call certnIDStart() method again in case you need to start over the document auto capture process (e.g. you want to capture both sides of the document, one after another). You can also call certnIDStart() method to stop and start over ongoing process as well.

In case you want to stop the document auto capture process prematurely, call the certnIDStopAsync() method. The callback in the method argument indicates that the processing is over.

***Quality Attributes of the Output Image***

You may adjust quality requirements for the output image. To perform this, you can use pre-defined instances - CertnIDQualityAttributeThresholds - from CertnIDQualityAttributeThresholdPresets with recommended thresholds and pass it to CertnIDDocumentConfiguration by setting the certnIDQualityAttributeThresholds. You can also create your own instance of CertnIDQualityAttributeThresholds from scratch or based on pre-defined instances according to your needs.

Possible ways how to create CertnIDQualityAttributeThresholds:

- The standard preset

```kotlin
val standard = CertnIDQualityAttributeThresholdPresets.standard

// Modified thresholds based on the standard preset
val modified = standard.copy(
    minConfidence = 0.8
    minSharpness = null
)
```

- Custom thresholds

```kotlin
val custom = CertnIDQualityAttributeThresholds(
    minConfidence = 0.8,
    minSharpness = null
)
```

Available presets (pre-defined instances with thresholds) in CertnIDQualityAttributeThresholdPresets:

- **standard** - the resulting image suitable for evaluation on Digital Identity Service. Thresholds for standard image output quality:

  - minConfidence value is 0.9

  - minSize value is 0.43

  - minSharpness value is 0.65

  - brightnessInterval value is [0.25, 0.9]

  - maxHotspotsScore value is 0.008

**Customization of UI components**

***Strings***

You can override the string resources in your application and provide alternative strings for supported languages using the standard Android localization mechanism.

```kotlin
<string name="certn_id_document_auto_capture_instruction_brightness_too_high">Less light needed</string>
<string name="certn_id_document_auto_capture_instruction_brightness_too_low">More light needed</string>
<string name="certn_id_document_auto_capture_instruction_candidate_selection">Hold still…</string>
<string name="certn_id_document_auto_capture_instruction_document_does_not_fit_placeholder">Center document</string>
<string name="certn_id_document_auto_capture_instruction_document_not_detected">Scan document</string>
<string name="certn_id_document_auto_capture_instruction_document_out_of_bounds">Center document</string>
<string name="certn_id_document_auto_capture_instruction_hotspots_score_too_high">Avoid reflections</string>
<string name="certn_id_document_auto_capture_instruction_mrz_not_present">Scan valid machine readable document</string>
<string name="certn_id_document_auto_capture_instruction_mrz_not_valid">Scan valid machine readable document</string>
<string name="certn_id_document_auto_capture_instruction_sharpness_too_low">More light needed</string>
<string name="certn_id_document_auto_capture_instruction_size_too_small">Move closer</string>
```

***Colors***

You may customize the colors used by DocumentLibraryType in your application. To use custom colors, override the specific color.

```kotlin
<color name="certn_id_detection_layer">#ffffffff</color>
<color name="certn_id_instruction_background">#fff8fbfb</color>
<color name="certn_id_instruction_candidate_selection_background">#ff00bfb2</color>
<color name="certn_id_instruction_candidate_selection_text">#ff131313</color>
<color name="certn_id_instruction_text">#ff131313</color>
<color name="certn_id_placeholder">#ffffffff</color>
<color name="certn_id_placeholder_candidate_selection">#ff00bfb2</color>
<color name="certn_id_placeholder_overlay">#80131313</color>
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
