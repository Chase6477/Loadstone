# Loadstone -- An App for finding your path in a new way

### About the App

This App provides a compass that only shows the direction of the destination instead of the exact path.

## Selection screen
<img width="270" height="600" alt="selection" src="https://github.com/user-attachments/assets/1ca87dc5-f6dd-4ca1-bb22-6911fecaf715" />

- your location is indicated by a marker
- drag and zoom the map to your desired destination, indicated by the dot
- press ok to choose this location

## Compass screen

<img width="270" height="600" alt="compass" src="https://github.com/user-attachments/assets/517f579e-d211-4815-92e0-dddc9d399b14" />

- you might have to wait for a few seconds until your current location is set
- as soon as the "GPS location" text updates, you are ready to go
- follow the red side of the needle
- remaining distance in metres is shown under the Current location
- press the "Map view" button to show a map instead of the compass

The magnetic sensor is crucial for displaying the correct direction on your display, so ensure it's calibrated correctly by moving your smartphone in a figur-8-motion. The "Compass accuracy" will show the estimated current precision, but it is not always correct.
  
If you are still unsure about the correctness of the compass rotate your smartphone flat around it self and test if it always points in the globally same direction.

## Map screen

<img width="270" height="600" alt="map" src="https://github.com/user-attachments/assets/a35cb2c1-cd1e-4d24-8f98-5c695f32f2de" />


- same movement options like the selection map
- destination location is indicated by the red marker
- press the "Back to compass" button to switch back to the compass screen

## Settings screen

<img width="270" height="2400" alt="settings" src="https://github.com/user-attachments/assets/8d2ce120-3121-47b0-bb3d-bf28f8be70bb" />

- press the Cogwheel button to acces the settings, press the return button or navigation key to go back to the last opened screen

##### Median smoothing

- smoothes the motion when the smartphone is rotated, by only rotating the needle by the median of the last 21[Buffer size] values

- *Buffer size*: Integer  -- Size of the stored rotations to calculate the median. higer values lead to higher delay but smoother rotations
- *Angle*: Float          -- The Minimum angle to activate the median smoothing (affects calm smoothing)

##### Calm smoothing

- smoothes the needles motion by keeping the angle for 500ms[Time in milliseconds] the same. It will only stay its direction if the smartphone does not rotate too much / the [Angle] value is overstepped

- *Time in milliseconds*: Float -- The time the rotation stays the same until it is forced to update

##### Map

- *Maximal saved map data in MB*: Integer -- The maximum saved map data on your phone in Megabyte, only needed for offline map

## To Do

- adding a Geo Coding API for finding your destination via text input
- releasing APKs
- ADVERTISEMENT $$ <- hehe sure

## Specifications

- Android 11+ Smartphones and wearableOS

  *tested on android 11 and 15 - pretty unusuable for watches*

- magnetic sensor and acceleration sensor for device orientation

- this app is available in the english and german language

- there are some text-spacing issues on smaller devices

## Other more or less unimportant stuff

- I do only check for location permissions when starting the App, sooo better don't remove them on runtime

- there would be a way to implement the google maps API, but I wanted to use the OSMDroid alternative. You could still write your own imlementation for Google, I used Interfaces (for my first time wooooowwww)

- this was my first App I made for Android, so It's pretty messy and inefficient etc.

- all the locations aren't real btw, I sadly don't live in San Francisco, but I've bin there once!
