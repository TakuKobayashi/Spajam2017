/*

27.05.2017 Erik Hornberger
 
 */

#include <WiFi.h>
#include <Stepper.h>

/*
 * Initialize the stepper motor. `stepsPerRevolution`
 * is the number of steps the stepper motor has to go
 * through to complete one revolution. It is typically
 * around 200, but might be different depending on the
 * model of the motor you use. Check your motor's 
 * to be sure. The pins can be any 4 GPIO pins.
 */
const int stepsPerRevolution = 200;
Stepper stepper(stepsPerRevolution, 33, 25, 26, 27);

/* 
 *  Specify the SSID and password for the network
 *  the server will be accessed from. The server
 *  is created on port 80, as that is the port 
 *  typically used for HTTP web browsing.
 */
const char* ssid     = "Moppy En Fuego";
const char* password = "hornberger";
WiFiServer server(80);

void setup()
{
    // Open the serial connection and pause to
    // give it time to get ready
    Serial.begin(115200);
    delay(10);

    // Next try to connect to the WiFi network
    Serial.println();
    Serial.println();
    Serial.print("Connecting to ");
    Serial.println(ssid);

    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
    }

    Serial.println("");
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());
 
    server.begin();

    // Initialize the stepper motor speed
    stepper.setSpeed(150);
}

void loop(){
 WiFiClient client = server.available();   // listen for incoming clients

  if (client) {                             // if you get a client,
    Serial.println("new client");           // print a message out the serial port
    String currentLine = "";                // make a String to hold incoming data from the client
    while (client.connected()) {            // loop while the client's connected
      if (client.available()) {             // if there's bytes to read from the client,
        char c = client.read();             // read a byte, then
        Serial.write(c);                    // print it out on the serial monitor.
        if (c == '\n') {                    // If the byte was a newline character

          // and if the current line is blank, you got two newline characters in a row.
          // that's the end of the client HTTP request, so send a response:
          if (currentLine.length() == 0) {
            // HTTP headers always start with a response code (e.g. HTTP/1.1 200 OK)
            // and a content-type so the client knows what's coming, then a blank line:
            client.println("HTTP/1.1 200 OK");
            client.println("Content-type:text/html");
            client.println();

            // the content of the HTTP response follows the header:
            client.print("Tatakimashita!");

            // The HTTP response ends with another blank line:
            client.println();

            // Swing the hammer
            int steps = stepsPerRevolution/4;
            stepper.step(steps);
            stepper.step(-steps);
            
            // break out of the while loop:
            break;
            
          } else {    // If the character was a newline, then clear currentLine:
            currentLine = "";
          }
          
        } else if (c != '\r') {  // if you got anything else but a carriage return character,
          currentLine += c;      // add it to the end of the currentLine
        }
      }
    }
    // close the connection:
    client.stop();
    Serial.println("client disonnected");
  }
}
