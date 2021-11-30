Currently CI/CD is fully working for Mobile 2.

**Prerequisites**:

Java
Ensure Backend is running
Change url in BackendConnections.java 

**Steps for installation, building application and deploying application**
1. Clone repository 
2. Navigate to the root of PTTMobile2
3. Run the following command to build and deploy an APK to the device:

Command for Mac or Linux:
./gradlew installDebug

Command for Windows:
gradlew installDebug

4. To run the tests, please run the following command at the root of the Mobile2 repository.

Command for Mac or Linux:
./gradlew test

Command for Windows:
gradlew test

**To Deploy to Emulator**
1. Use Android Studio to create an emulator
2. Run the command 
emulator -avd **emulator_name** 

where emulator_name is the name of the avd created 


