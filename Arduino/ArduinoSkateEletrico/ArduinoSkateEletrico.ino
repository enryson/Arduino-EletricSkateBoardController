//Skate
#include <PWMServo.h>
#include <SoftwareSerial.h>
#include <Arduino.h>
//SoftwareSerial mySerial(2, 3);//Carrinho
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

#define ledFarol 03
#define ledfreio 10

void setup() {
  pinMode(ledFarol, OUTPUT);
  pinMode(ledfreio, OUTPUT);
  //myservo.attach(8);//Carrinho
  myservo.attach(9);
  mySerial.begin(9600);
  Serial.begin(9600);
  myservo.write(30);  
  digitalWrite(ledFarol, HIGH);
  digitalWrite(ledfreio, LOW);
}

void loop() {
  
  if (mySerial.available() > 0) {
    int inChar = mySerial.read();    
    if (isDigit(inChar)) {
      inString += (char)inChar;
    }    
    if (inChar == 'n') {
      int x = inString.toInt();
      digitalWrite(ledfreio, LOW);
      if (x <= 180){        
        myservo.write(x);
        if(x > 90){
          digitalWrite(ledfreio, HIGH);
          }      
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
    if (inChar == 'L') {
      digitalWrite(ledFarol, HIGH);
    }
    if (inChar == 'O') {
      digitalWrite(ledFarol, LOW);
    }
  }  
}

static void getv() {
  float v = (analogRead(5) * vPow) / 1023.0;
    float v2 = v / (r2 / (r1 + r2));    
    mySerial.println("{");
    mySerial.println(v2);
    mySerial.println("v");
}


