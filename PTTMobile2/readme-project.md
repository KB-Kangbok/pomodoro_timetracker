Installation Instructions for PTTMobile2

1. To build and deploy our app, you must plug in an Android device.
(Please make sure developer mode is turned on in Settings)

2. Navigate to the root of `PTTMobile2`

3. Run the following command to build and deploy an APK to the device:
* `./gradlew installDebug` or `./gradlew build`

4. For DevOps Team: To change the IP of Backend Connection, navigate to `.../BackendConnections.java` and change the `baseUrl` on line 28.

5. To run the tests, please run the following command at the root of the Mobile2 repository.
* `./gradlew test`

This should install and deploy the application on the device.
*Note: These steps should also work on an emulator, but the emulator needs to be up and running from Android Studio.*
