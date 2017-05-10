#include <Servo.h>
#include <SoftwareSerial.h>
#include <Arduino.h>

//SoftwareSerial mySerial(2, 3);//Carrinho
SoftwareSerial mySerial(6, 5); 
Servo myservo;  

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
  //myservo.attach(8);//Carrinho
  myservo.attach(9);
  mySerial.begin(9600);
  digitalWrite(13, LOW);
  Serial.begin(9600);
  myservo.write(30);
}

void loop() {
  
  if (mySerial.available() > 0) {
    int inChar = mySerial.read();
    
    if (isDigit(inChar)) {
      //Serial.print("inChar");
      //Serial.println(inChar);
      
      
      inString += (char)inChar;
      
      //Serial.print("inChar ");
      //Serial.println(inChar);
      
      //Serial.print("inString");
      //Serial.println(inString); 
    }
    
    if (inChar == 'n') {
      //Serial.println(inChar);
      //Serial.println(inString);
      
      int x = inString.toInt();
       
      if (x <= 180){
        //Serial.print("PULSO DO MOTOR = ");
        //Serial.println(x);
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
      startTime = millis();
    }
  }
  
}

static void getv() {
  float v = (analogRead(5) * vPow) / 1023.0;
    float v2 = v / (r2 / (r1 + r2));
    mySerial.print(v2);
    mySerial.println("v");
}


