#include <Stepper.h>
#include <ESP8266WiFi.h>

const int stepsPerRev = 200; // The number of steps in one revolution for the stepper motor being used
const int rpm = 10;          // The speed of the motor in RPM
Stepper stepper(stepsPerRev,  D5, D6, D7, D8); // Object for controlling the stepper motor. The last four numbers are the pins on the microcontroller.

void setup() {
  stepper.setSpeed(rpm);
}

void loop() {
  stepper.step(stepsPerRev);
}
