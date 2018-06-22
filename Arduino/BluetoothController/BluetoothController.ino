//Skate
#include <PWMServo.h>
#include <SoftwareSerial.h>
#include <Arduino.h>
//Setuo Ports
SoftwareSerial mySerial(6, 5); 
PWMServo myservo;  

///Resistors
float vPow = 5;
float r1 = 47000;
float r2 = 10000;

////////////
String inString = "";
int voltcheck = 0;

///LEDS
#define ledFront 03
#define ledBreak 10

void setup() {
  //Leds Lights
  pinMode(ledFront, OUTPUT);
  pinMode(ledBreak, OUTPUT);
  /////////////
  Serial.begin(9600);
  mySerial.begin(9600);
  myservo.attach(9);
  myservo.write(30);
  ///////////// 
  digitalWrite(ledFront, HIGH);
  digitalWrite(ledBreak, LOW);
}

void loop() {
  //ReadBlutooth data
  if (mySerial.available() > 0) {
    int inChar = mySerial.read();
         //Serial.println("olá");
    if (isDigit(inChar)) {
      inString += (char)inChar;
      
    }
    //Recilver pulse to the motor    
    if (inChar == 'n') {
      int x = inString.toInt();
      digitalWrite(ledBreak, LOW); //LedON
      //Make motor run
      if (x <= 180){    
           Serial.println(x);
        myservo.write(x);
        if(x > 90){
          digitalWrite(ledBreak, HIGH); //LedOff
          }
        }      
      inString = "";
    }
    //Send Voltage to the APP
    if (inChar == 'm') {
      if (voltcheck > 10){
      getv();
      voltcheck = 0;
      }
      else {
        voltcheck = voltcheck + 1;
      }
    }
    //Control Front Led
    if (inChar == 'L') {
      digitalWrite(ledFront, HIGH);
    }
    if (inChar == 'O') {
      digitalWrite(ledFront, LOW);
    }
  }  
}
//Get Voltage
static void getv() {
  float v = (analogRead(5) * vPow) / 1023.0;
    float v2 = v / (r2 / (r1 + r2));
    mySerial.println("{");
    mySerial.println(v2);
    mySerial.println("v");
}


