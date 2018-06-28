
/*  Attiny85 Pin Diagram
    []RESET___[]VCC
    [A3]D3______[A1]D2
    [A2]D4______[]D1(PWM)
    []GND_____[]D0(PWM)
*/
#define LED       0
#define JOYSTIC   3
#define BUTON     4
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
  blinks(3);
}

void loop() {  
  escVal = analogRead(JOYSTIC);
  escVal = map(escVal, 0, 1023, 170, 19);
  if (escVal < 120 && escVal > 100){
    escVal=escVal++;
    mySerial.print(escVal+servo); 
  }
  mySerial.print(escVal+servo);    
  delay(80);
}
static void blinks(int n){ //Function LED Blink Animation
  for (int i=0; i<=n; i++){
    analogWrite(LED, LOW);
    delay(100);
    analogWrite(LED, 255);
    delay(100);   }
  analogWrite(LED, 255);
}
