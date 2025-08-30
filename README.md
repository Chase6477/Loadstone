# Loadstone -- An App for finding your path in a new way

### About the App

This App provides a compass that only shows the direction of the destination instead of the exact path.

## Selection screen
<img width="270" height="600" alt="selection" src="https://github.com/user-attachments/assets/24141644-629c-4c8c-ba5a-00b2c11e5f6e" />

- your location is indicated by a marker
- drag and zoom the map to your desired destination, indicated by the dot
- press ok to choose this location

## Compass screen

<img width="270" height="600" alt="compass" src="https://github.com/user-attachments/assets/f4f6dd25-14be-4f6b-9b97-a3aeaf10c576" />

- you might have to wait for a few seconds until your current location is set
- as soon as the "GPS location" text updates, you are ready to go
- follow the red side of the needle
- Remaining distance in metres is shown under the Current location

The magnetic sensor is crucial for displaying the correct direction on your display, so ensure it's calibrated correctly by moving your smartphone in a figur-8-motion. The "Compass accuracy" will show the estimated current precision, but it is not always correct.
  
If you are still unsure about the correctness of the compass rotate your smartphone flat around it self and test if it always points in the globally same direction.

## Settings screen

<img width="270" height="2400" alt="Screenshot_20250830_225811" src="https://github.com/user-attachments/assets/0063cd0e-d9dd-49be-a860-e66b97af42cb" />

- Press the Cogwheel button to acces the settings, press the return button or navigation key to go back to the last opened screen

##### Median smoothing

- smoothes the motion when the smartphone is rotated, by only rotating the needle by the median of the last 21[Buffer size] values

- *Buffer size*: Integer  -- Size of the stored rotations to calculate the median. higer values lead to higher delay but smoother rotations
- *Angle*: Float          -- The Minimum angle to activate the median smoothing (affects calm smoothing)

##### Calm smoothing

- smoothes the needles motion by keeping the angle for 500ms[Time in milliseconds] the same. It will only stay its direction if the smartphone does not rotate too much / the [Angle] value is overstepped

- *Time in milliseconds*: -- Float: The time the rotation stays the same until it is forced to update

## To Do

- adding a Geo Coding API for finding your destination via text input
- data management for OSMDroid - It is currently saving everything you have seen on the map
- switching to a map from the compass screen - nice to have when you are really near your destination
- adding some loading indicators when switching to compass screen, to indicate the current location getting loadad
- adding some animations when switching screens
- releasing APKs
- ADVERTISEMENT $$ <- hehe sure

## Specifications

- Android 11+ Smartphones and wearableOS

  *tested on android 15 only - pretty unusuable for watches*

- magnetic sensor and acceleration sensor for device orientation

- this app is available in the english and german language

## Other more or less unimportant stuff

- I do only check for location permissions when starting the App, sooo better don't remove them on runtime

- There would be a way to implement the google maps API, but I wanted to use the OSMDroid alternative. You could still write your own imlementation for Google, I used Interfaces (for my first time wooooowwww)

- This was my first App I made for Android, so It's pretty messy and inefficient etc.

- All the locations aren't real btw, I sadly don't live in San Francisco, but I've bin there once!
