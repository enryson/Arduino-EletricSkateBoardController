#include <SoftwareSerial.h>
/*  Attiny85 Pin Diagram
    []RESET___[]VCC
    [A3]D3______[A1]D2
    [A2]D4______[]D1(PWM)
    []GND_____[]D0(PWM)
*/

#define LED       4
#define JOYSTIC   3
#define BUTON     0

//Serial BluetoothHC-05
#define RX    2
#define TX    1

SoftwareSerial mySerial(RX , TX);

String servo = "n";   //String ASCII "n"
int escVal = 0;

void setup() { 
  pinMode(LED, OUTPUT);
  pinMode(BUTON, INPUT); 
  mySerial.begin(9600);
  blinks();
}

void loop() {  
  escVal = analogRead(JOYSTIC);
  escVal = map(escVal, 0, 1023, 160, 19);
  int es = (escVal);

  mySerial.print(es+servo);
  delay(200);
}
//Function LED Blink Animation
static void blinks(){
  for (int i=0; i<=3; i++){
    analogWrite(LED, LOW);
    delay(10);
    analogWrite(LED, HIGH);
    delay(50);    
  }
  analogWrite(LED, 255);
}
