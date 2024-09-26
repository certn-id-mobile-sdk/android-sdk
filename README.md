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

In order to integrate CertnIDSDK into your project, the first step is to include Certn, Innovatrics maven repositories and Google repository to your top level build.gradle file.

build.gradle
```kotlin
allprojects {
    repositories {
        maven {
            url = "https://maven.pkg.github.com/certn-id-mobile-sdk/android-sdk"
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
    implementation "certn-id-mobile-sdk:android-sdk:$certnIdVersion" //lattest version 0.6.3
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

## Initialization
Before using any of the components, you need to initialize CertnIDSDK with the license and specific CertnIDLibraryType's objects.

InitializeCertnIDSdkUseCase class in the sample project shows how to initialize CertnIDSDK with libraries. CertnIDMobileSdk.initialize() method should be called on background thread.

Keep in mind that if you try to use any component without initialization, it will throw an exception.

To read a raw file in Android and ensure that the reading operation is performed on an IO thread, you can use Kotlin coroutines.
```kotlin
suspend operator fun invoke() = withContext(Dispatchers.IO) {
    val configuration = createCertnIDSdkConfiguration(context)
    certnIDMobileSdk.initialize(configuration)
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

#### `libraries`

CertnIDSDK uses these libraries:
- **DocumentLibraryType** - Enables document scanning capabilities.
- **BaceBalancedLibraryType** - Enables face recognition with balanced performance and accuracy.
- **FaceFastLibraryType** - Enables faster face recognition with potentially lower accuracy compared to `BaceBalancedLibraryType`.
- **NfcLibraryType** - Enables NFC functionality for reading NFC-enabled documents.

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
## Document Auto Capture

### Camera permission

A fragment will check the camera permission (Manifest.permission.CAMERA) right before the camera is started. If the camera permission is granted the fragment will start the camera. If the camera permission is not granted the fragment will use Android API - ActivityResultContracts.RequestPermission to request the camera permission. Android OS will present the system dialog to the user of the app. If the user explicitly denies the permission at this point, onCertnIDNoCameraPermission() callback is called. Implement this callback in order to navigate the user further in your app workflow.

### Orientation Change

In order to handle the orientation change in multi-window mode correctly, configure the activity in your AndroidManifest.xml file as follows:

```kotlin
<activity
    android:name=".MyActivity"
    android:configChanges="screenSize|smallestScreenSize|screenLayout|orientation" />
```

### Sample implementation

CertnIDDocumentAutoCaptureFragment provides the main functionality. It is embedded into the application as fragment from Android Support Library. Also it is abstract fragment and therefore must be subclassed and overrided its abstract methods. Its required runtime interaction is provided by public methods, for example certnIDStart().

```kotlin
class BasicDocumentAutoCaptureFragment : CertnIDDocumentAutoCaptureFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCertnIDSdkViewModel()
    }

    //…
}
```

```kotlin
private fun setupCertnIDSdkViewModel() {
    collectStateFlow {
        launch {
            certnIDSdkViewModel.state.collect { state ->
                if (state.isInitialized) {
                    certnIDStart()
                }
                //...
            }
        }
    }
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

The fragment with instructions for obtaining quality document images suitable for further processing.

In order to configure the behaviour of CertnIDDocumentAutoCaptureFragment, use CertnIDDocumentConfiguration (see Fragment Configuration).

To use the fragment, create a subclass of CertnIDDocumentAutoCaptureFragment and override appropriate callbacks.

To start the document auto capture process call the certnIDStart() method. You can start the process any time.

In case you want to handle detection data, implement onCertnIDProcessed() callback. This callback is called with each processed camera frame. When the document auto capture process finishes successfully, the result will be returned via the onCertnIDCaptured() callback.

In case you want to force the capture event, call the certnIDRequestCapture() method. The most recent image will be returned via the onCertnIDCaptured() callback asynchronously.

Call certnIDStart() method again in case you need to start over the document auto capture process (e.g. you want to capture both sides of the document, one after another). You can also call certnIDStart() method to stop and start over ongoing process as well.

In case you want to stop the document auto capture process prematurely, call the certnIDStopAsync() method. The callback in the method argument indicates that the processing is over.

### CertnIDDocumentConfiguration

```kotlin
class CertnIDDocumentConfiguration(
    // Specify camera facing. Default is `BackType`.
    certnIDCameraFacing: CertnIDCameraFacing = CertnIDCameraFacing.BackType,
    // Specify camera preview scale type. Default is `FitType`.
    certnIDCameraPreviewScaleType: CertnIDCameraPreviewScaleType = CertnIDCameraPreviewScaleType.FitType,
    // Specify validation mode. Default is `StandardMode`.
    certnIDValidationMode: CertnIDValidationMode = CertnIDValidationMode.StandardMode,
    // Customize thresholds for the quality attributes of the document auto capture result.
    certnIDQualityAttributeThresholds: CertnIDQualityAttributeThresholds = CertnIDQualityAttributeThresholds(),
    // Specify MRZ validation. Default is `NoneType`.
    certnIDMrzValidation: CertnIDMrzValidation = CertnIDMrzValidation.NoneType,
    // Specify the type of placeholder used in document auto-capture UI. Default is `CornersOnlyType`.
    certnIDPlaceholderType: CertnIDPlaceholderType = CertnIDPlaceholderType.CornersOnlyType
) {
    //…
}
```

### Quality Attributes of the Output Image

You may adjust quality requirements for the output image. To perform this, you can use pre-defined instances - CertnIDQualityAttributeThresholds - from CertnIDQualityAttributeThresholdPresets with recommended thresholds and pass it to CertnIDDocumentConfiguration by setting the certnIDQualityAttributeThresholds. You can also create your own instance of CertnIDQualityAttributeThresholds from scratch or based on pre-defined instances according to your needs.

Possible ways how to create CertnIDQualityAttributeThresholds:

- The standard preset

```kotlin
val standard = CertnIDQualityAttributeThresholdPresets.standard

// Modified thresholds based on the standard preset
val modified = standard.copy(
    minConfidence = 0.8,
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

### Customization of UI components

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

## Face Capture - FaceBalancedLibraryType / FaceFastLibraryType

### Camera permission

A fragment will check the camera permission (Manifest.permission.CAMERA) right before the camera is started. If the camera permission is granted the fragment will start the camera. If the camera permission is not granted the fragment will use Android API - ActivityResultContracts.RequestPermission to request the camera permission. Android OS will present the system dialog to the user of the app. If the user explicitly denies the permission at this point, onCertnIDNoCameraPermission() callback is called. Implement this callback in order to navigate the user further in your app workflow.

### Orientation Change

In order to handle the orientation change in multi-window mode correctly, configure the activity in your AndroidManifest.xml file as follows:

```kotlin
<activity
    android:name=".MyActivity"
    android:configChanges="screenSize|smallestScreenSize|screenLayout|orientation" />
```

### Sample implementation

CertnIDFaceAutoCaptureFragment provides the main functionality. It is embedded into the application as fragment from Android Support Library. Also it is abstract fragment and therefore must be subclassed and overrided its abstract methods. Its required runtime interaction is provided by public methods, for example certnIDStart().

```kotlin
class BasicFaceAutoCaptureFragment : CertnIDFaceAutoCaptureFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCertnIDSdkViewModel()
    }

    //…
}
```

```kotlin
private fun setupCertnIDSdkViewModel() {
    collectStateFlow {
        launch {
            certnIDSdkViewModel.state.collect { state ->
                if (state.isInitialized) {
                    certnIDStart()
                }
                //...
            }
        }
    }
}
```

The CertnIDFaceAutoCaptureFragment requires a configuration. To provide configuration data, you should override the provideCertnIDFaceConfiguration() method in your subclass implementation. This method should return an instance of the CertnIDFaceConfiguration data class with the desired parameters.

```kotlin
class BasicFaceAutoCaptureFragment : CertnIDFaceAutoCaptureFragment() {

    override fun provideCertnIDFaceConfiguration() = CertnIDFaceConfiguration(
        certnIDCameraFacing = CertnIDCameraFacing.FrontType,
        certnIDCameraPreviewScaleType = CertnIDCameraPreviewScaleType.FitType,
        //...
    )

    //…
}
```

The fragment with instructions for obtaining quality face images suitable for further processing. 

In order to configure the behaviour of CertnIDFaceAutoCaptureFragment, use CertnIDFaceConfiguration (see Fragment Configuration).

To use the fragment, create a subclass of CertnIDFaceAutoCaptureFragment and override appropriate callbacks.

To start the face auto capture process check whether face library is initialized. If face library is initialized, you can call the certnIDStart() method.

In case you want to handle detection data, implement onCertnIDProcessed() callback. This callback is called with each processed camera frame. When the face auto capture process finishes successfully, the result will be returned via the onCertnIDCaptured() callback.

In case you want to force the capture event, call the certnIDRequestCapture() method. The most recent image will be returned via the onCertnIDCaptured() callback asynchronously.

Call certnIDStart() method again in case you need to start over the face auto capture process. You can also call certnIDStart() method to stop and start over ongoing process as well.

In case you want to stop the face auto capture process prematurely, call the certnIDStopAsync() method. The callback in the method argument indicates that the processing is over.

### CertnIDFaceConfiguration


```kotlin
class CertnIDFaceConfiguration(
    // Specify camera facing. Default is `FrontType`.
    certnIDCameraFacing: CertnIDCameraFacing = CertnIDCameraFacing.FrontType,
    // Specify camera preview scale type. Default is `FitType`.
    certnIDCameraPreviewScaleType: CertnIDCameraPreviewScaleType = CertnIDCameraPreviewScaleType.FitType,
    // Configure thresholds for the quality attributes of the face auto capture result.
    // If the threshold property is omitted, default corresponding quality attribute will be validated.
    certnIDQualityAttributeThresholds: CertnIDQualityAttributeThresholds = CertnIDQualityAttributeThresholds(),
    certnIDFaceDetectionQuery: CertnIDFaceDetectionQuery
) {
    //…
}
```
### CertnIDQualityAttributeThresholds

```kotlin
data class CertnIDQualityAttributeThresholds(
    // Minimal valid confidence of the detected face.
    // Value 0.0 represents low confidence.
    // Vaue 1.0 represents high confidence.
    val minConfidence: Double? = null,
    // Maximal pitch angle of the device.
    // Valid closed interval of angle values is from 0.0 to 90.0 degrees.
    val maxDevicePitchAngle: Float? = null,
    // Valid interval of size of the detected face.
    // Value 0.0 represents small size.
    // Vaue 1.0 represents large size.
    val sizeInterval: CertnIDIntervalDouble? = null,
    // Valid interval of rotation of the pitch of the detected face.
    // Value -90.0 represents down rotated pitch of the detected face.
    // Vaue 90.0 represents up rotated pitch of the detected face.
    val pitchAngleInterval: CertnIDIntervalFloat? = null,
    // Valid interval of rotation of the yaw of the detected face.
    // Value -90.0 represents left rotated yaw of the detected face.
    // Vaue 90.0 represents right rotated yaw of the detected face.
    val yawAngleInterval: CertnIDIntervalFloat? = null,
    // Minimal valid sharpness of the detected face.
    // Value 0.0 represents blurry face.
    // Vaue 1.0 represents sharp face.
    val minSharpness: Double? = null,
    // Valid interval of brightness of the detected face.
    // Value 0.0 represents low brightness.
    // Vaue 1.0 represents high brightness.
    val brightnessInterval: CertnIDIntervalDouble? = null,
    // Valid interval of contrast of the detected face.
    // Value 0.0 represents low contrast.
    // Vaue 1.0 represents high contrast.
    val contrastInterval: CertnIDIntervalDouble? = null,
    // Minimal valid unique intensity levels of the detected face.
    // Value 0.0 represents low unique intensity levels.
    // Vaue 1.0 represents high unique intensity levels.
    val minUniqueIntensityLevels: Double? = null,
    val minBackgroundUniformity: Double? = null,
    // Maximal valid glasses presence score of the detected face.
    // Value 0.0 represents face with no glasses.
    // Vaue 1.0 represents face with glasses.
    val maxGlassesPresenceScore: Double? = null,
    // Maximal valid mask presence score of the detected face.
    // Value 0.0 represents face with no mask.
    // Vaue 1.0 represents face with mask.
    val maxMaskPresenceScore: Double? = null,
    // Minimal valid mouth status score of the detected face.
    // Value 0.0 represents face with mouth opened.
    // Vaue 1.0 represents face with mouth closed.
    val minMouthStatusScore: Double? = null,
    // Minimal valid eyes status score of the detected face.
    // Value 0.0 represents face with eyes closed.
    // Vaue 1.0 represents face with eyes opened.
    val minEyesStatusScore: Double? = null,
    // Maximal valid shadow of the detected face.
    // Value 0.0 represents face without shadows.
    // Vaue 1.0 represents face with shadows.
    val maxShadow: Double? = null
) {
    //…
}
```

### Quality Attributes of the Output Image

You may adjust quality requirements for the output image. To perform this, you can use pre-defined instances - CertnIDQualityAttributeThresholds - from CertnIDQualityAttributeThresholdPresets with recommended thresholds and pass it to CertnIDDocumentConfiguration by setting the certnIDQualityAttributeThresholds. You can also create your own instance of CertnIDQualityAttributeThresholds from scratch or based on pre-defined instances according to your needs.

Possible ways how to create CertnIDQualityAttributeThresholds:

- The standard preset

```kotlin
val standard = CertnIDQualityAttributeThresholdPresets.standard

// Modified thresholds based on the standard preset
val modified = standard.copy(
    minConfidence = 0.8,
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

- **standard** - the resulting image suitable for onboarding or matching. Thresholds for standard image output quality:

  - minConfidence value is 0.15

  - maxDevicePitchAngle value is 30.0

  - sizeInterval value is [0.16, 0.20]

  - minSharpness value is 0.2832


### Customization of UI components

***Strings***

You can override the string resources in your application and provide alternative strings for supported languages using the standard Android localization mechanism.

```kotlin
<string name="certn_id_face_auto_capture_instruction_background_nonuniform">Plain background required</string>
<string name="certn_id_face_auto_capture_instruction_candidate_selection">Stay still&#8230;</string>
<string name="certn_id_face_auto_capture_instruction_device_pitch_too_high">Hold your phone at eye level</string>
<string name="certn_id_face_auto_capture_instruction_expression_neutral_too_high">Smile :)</string>
<string name="certn_id_face_auto_capture_instruction_expression_neutral_too_low">Keep neutral expression</string>
<string name="certn_id_face_auto_capture_instruction_eyes_too_closed">Open your eyes</string>
<string name="certn_id_face_auto_capture_instruction_face_out_of_bounds">Center your face</string>
<string name="certn_id_face_auto_capture_instruction_face_not_detected">Position your face into the circle</string>
<string name="certn_id_face_auto_capture_instruction_size_too_large">Move back</string>
<string name="certn_id_face_auto_capture_instruction_size_too_small">Move closer</string>
<string name="certn_id_face_auto_capture_instruction_glasses_present">Remove glasses</string>
<string name="certn_id_face_auto_capture_instruction_brightness_too_high">Turn towards light</string>
<string name="certn_id_face_auto_capture_instruction_brightness_too_low">Turn towards light</string>
<string name="certn_id_face_auto_capture_instruction_contrast_too_high">Turn towards light</string>
<string name="certn_id_face_auto_capture_instruction_contrast_too_low">Turn towards light</string>
<string name="certn_id_face_auto_capture_instruction_shadow_too_high">Turn towards light</string>
<string name="certn_id_face_auto_capture_instruction_sharpness_too_low">Turn towards light</string>
<string name="certn_id_face_auto_capture_instruction_unique_intensity_levels_too_low">Turn towards light</string>
<string name="certn_id_face_auto_capture_instruction_mask_present">Remove mask</string>
<string name="certn_id_face_auto_capture_instruction_mouth_too_open">Close your mouth</string>
<string name="certn_id_face_auto_capture_instruction_pitch_too_high">Lower your chin</string>
<string name="certn_id_face_auto_capture_instruction_pitch_too_low">Lift your chin</string>
<string name="certn_id_face_auto_capture_instruction_yaw_too_left">Look right</string>
<string name="certn_id_face_auto_capture_instruction_yaw_too_right">Look left</string>
```

***Colors***

You may customize the colors used by FaceBalancedLibraryType in your application. To use custom colors, override the specific color.

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

## NFC Reading

UI NFC Travel Document Reader is a ready-to-use component for incorporating NFC reading capabilities into your application. It includes a set of UI elements to guide the user through the NFC reading process.

This component is based on a Fragment from AndroidX. In order to use it, an abstract CertnIDNfcTravelDocumentReaderFragment must be subclassed.

The CertnIDNfcTravelDocumentReaderFragment requires a configuration. To provide configuration data, you should override the provideCertnIDTravelConfiguration() method in your subclass implementation. This method should return an instance of the CertnIDTravelConfiguration class with the desired parameters.

Override the onCertnIDSucceeded method to handle successful NFC document reading. Similarly, override the onCertnIDFailed method to manage reading failures. The reading process of the NFC Travel Document is tied to the fragment’s lifecycle. To cancel the reading process, simply transition the fragment to the destroyed state, for example, by navigating to another fragment.

### Sample implementation


```kotlin
class DefaultNfcTravelDocumentReaderFragment : CertnIDNfcTravelDocumentReaderFragment() {

    private val nfcReadingViewModel: NfcReadingViewModel by activityViewModels()

    override fun onCertnIDSucceeded(certnIDTravelDocument: CertnIDTravelDocument) {
        nfcReadingViewModel.setTravelDocument(certnIDTravelDocument)
    }

    override fun onCertnIDFailed(exception: Exception) {
        nfcReadingViewModel.setError(exception)
    }

    override fun provideCertnIDTravelConfiguration(): CertnIDTravelConfiguration {
        return nfcReadingViewModel.state.value?.certnIDTravelConfiguration!!
    }
}
```

```kotlin
class BasicFaceAutoCaptureFragment : CertnIDFaceAutoCaptureFragment() {

collectStateFlow {
    launch {
        faceViewModel.nfcKeyResponse.collect { nfcKeyResponse ->
            nfcKeyResponse?.data?.apply {
                if (documentNumber != null && dateOfExpiry != null && dateOfBirth != null) {
                    nfcReadingViewModel.setupConfiguration(
                        CertnIDNfcKey(documentNumber, dateOfExpiry, dateOfBirth)
                    )
                }
            }
        }   
    }
    //...
}
```

```kotlin
fun setupConfiguration(certnIDNfcKey: CertnIDNfcKey) {
    viewModelScope.launch {
        val certnIDTravelConfiguration = CertnIDTravelConfiguration(
            certnIDNfcKey = certnIDNfcKey,
            authorityCertificatesFilePath = resolveAuthorityCertificatesFileUseCase().path,
        )
        mutableState.value =
            state.value!!.copy(certnIDTravelConfiguration = certnIDTravelConfiguration)
    }
}
```

### CertnIDTravelConfiguration

`CertnIDTravelConfiguration` has two parameters. `CertnIDNfcKey` and path to `PEM` file as string. `PEM` format file is used to store certificates of Country Signing Certificate Authority (CSCA).

```kotlin
class CertnIDTravelConfiguration(
    certnIDNfcKey: CertnIDNfcKey,
    authorityCertificatesFilePath: String? = ""
) {
  //...
}
```

### CertnIDNfcKey

`CertnIDNfcKey` is the access key to NFC data:

```kotlin
class CertnIDNfcKey(documentNumber: String, dateOfExpiry: String, dateOfBirth: String) {
    //...
}
```

### CertnIDTravelDocument

`CertnIDTravelDocument` is an result object of NFC scanning process. It can be encoded and sended to the server for processing. It has no public available parameters.


**UI of the fragment can be customized**

***Strings***

You can override the string resources and provide alternative strings for supported languages using the standard Android localization mechanism.


```kotlin
<string name="certn_id_nfc_travel_document_reader_searching_title">Searching for NFC chip…</string>
<string name="certn_id_nfc_travel_document_reader_searching_subhead">Slide your phone gently on the surface of the document until we find the right position.</string>
<string name="certn_id_nfc_travel_document_reader_reading_title">Hold still</string>
<string name="certn_id_nfc_travel_document_reader_reading_subhead">Please keep both device and document still.</string>
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

For detailed documentation and support, visit the [TrustmaticMobileSDK Documentation](https://demo.trustmatic.io/documentation/) or contact support at [support@trustmatic.com](mailto:support@trustmatic.com).

## License

CertnIDMobileSDK is available under a commercial license. For more information, please refer to the official website or contact the sales team.
