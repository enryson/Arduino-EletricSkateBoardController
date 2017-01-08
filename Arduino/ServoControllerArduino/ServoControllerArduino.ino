#include <PWMServo.h>
#include <SoftwareSerial.h>
#include <Arduino.h>

SoftwareSerial mySerial(6, 5); 

PWMServo myservo;  

String inString = "";

unsigned long startTime;
unsigned long otherTime;

int prev = 150;

float vPow = 5;
float r1 = 47000;
float r2 = 10000;

int voltcheck = 0;

double b = 85; //Braking
double n = 90; //Neutral
double m = 120; //Accelerate

void setup() {
  pinMode(13, OUTPUT);
  pinMode(10, OUTPUT);
  myservo.attach(9);
  mySerial.begin(9600);
  digitalWrite(13, LOW);
  digitalWrite(10, HIGH);
}

void loop() {
  if (mySerial.available() > 0) {
    int inChar = mySerial.read();
    if (isDigit(inChar)) {
      inString += (char)inChar; 
    }
    if (inChar == 'n') {
      int x = inString.toInt();
      if (x <= 180){
        digitalWrite(10, LOW);
        myservo.write(x); 
      }
      inString = "";
    }
    if (inChar == 'm') {
      if (voltcheck > 10){
      getv();
      voltcheck = 0;
      }
      else {
        voltcheck = voltcheck + 1;
      }
      digitalWrite(13, HIGH);
      digitalWrite(10, HIGH);
      //Serial.println("ALIVE");
      startTime = millis();
    }
  }
  if ((millis() - startTime) > 210) {
    digitalWrite(13, HIGH);
    digitalWrite(10, HIGH);
    myservo.write(90); 
    delay(30);
  }
}

static void getv() {
  float v = (analogRead(5) * vPow) / 1023.0;
    float v2 = v / (r2 / (r1 + r2));
    mySerial.print(v2);
    mySerial.println("v");
}


