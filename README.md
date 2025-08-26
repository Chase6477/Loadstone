# Loadstone -- An App for finding your path in a new way

### About the App

- Its purpose is to let you spend time enjoying the outdoors, without constantly having to check the map for your next intersection - just knowing the overall direction and distance

**WARNING!**

- This is my first android app I made, so it's probably very unoptimized and has lots of bugs in it

### Selection screen
<img width="238" height="515" alt="scr1" src="https://github.com/user-attachments/assets/aaab2758-65aa-47c8-9e3b-67ec97ff5539" />

- Typed in coordinates MUST follow this format:  
`49.53388529109523, 10.7029669810218`  
*(decimal count doesn’t matter, other formats not yet supported)*
- Well... the "Show distance" switch doesn't do that much yet...

### Compass view
<img width="237" height="515" alt="scr2" src="https://github.com/user-attachments/assets/3a7a65b5-b96a-48cc-a9a6-b0e3004d2b06" />

#### Text

*It might take a while for the GPS data to be recognized, and for the first three texts to be shown*

- Lat, Lon: current position coordinates
- 3rd value: distance in meters
- 4th and 5th values: smoothed and raw device rotation
- Color change indicates the rotation precision:
  
| **Color**   | **Meaning**             |
|---------|-----------------------------|
| Red     | No precision / uncalibrated |
| Orange  | Low precision               |
| Yellow  | Medium precision            |
| Green   | Highest precision           |

- Calibrate the device by moving it in an 8-shaped pattern

#### Compass

- Star-shaped object in the background shows the cardinal directions
- The red side of the needle shows the direction of the destination

#### Buttons

- Enabling the "Median smoothing" button stabilizes the needle orientation by picking the median out of the last 21 rotation values
- Enabling the "Iterational smoothing" button stabilizes the needle orientation by holding the median value for a short time if the rotation of the device doesn't change too much

### Known bugs

- Incorrect coordinate formatting will cause the app to crash
- Sometimes the app crashes when loading GPS data for the first time
- You might get stuck on the permission window when exiting the app too quickly
- Don't remove the GPS permission while you're in the compass view

### What do you need

- Android 11+
  
*Tested on android 11 and 15*

- Magnetic sensor and acceleration sensor for device orientation

