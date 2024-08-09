# Remove calling log methods
-assumenosideeffects class java.lang.Throwable {
    public *** printStackTrace();
    public *** printStackTrace(...);
}

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** d(...);
    public static *** e(...);
}

#works for kotlin
-assumenosideeffects public class timber.log.Timber$Forest {
    public *** d(...);
    public *** v(...);
    public *** i(...);
    public *** w(...);
    public *** e(...);
    public *** wtf(...);
}

#works for java
-assumenosideeffects class timber.log.Timber {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** e(...);
    public static *** w(...);
}
