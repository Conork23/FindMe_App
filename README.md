# Find Me
Find Me is an Android application designed to help a user find his/her device if it has been lost or stolen. The application keeps a record of the devices location and stores it in Firebase’s Real-Time Database. This location is then retrievable by logging into another device or by sending an SMS to the devices that contains a keyword set by the user. The device will then respond with its current location. This allows the user to retrieve the device's location regardless of internet connection.

## Set Up

In /Users/Conor/Desktop/FindMe_App/app/src/main/res/values/strings.xml replace the '[Google Maps API key]' with an actual Maps API Key.


The google-services.json file located /Users/Conor/Desktop/FindMe_App/app/google-services.json needs to be replaced with a vaild one. This can be done by creating a Firebase project and downloading the generated file.
