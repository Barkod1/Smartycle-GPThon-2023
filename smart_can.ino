#include <Arduino.h>
#if defined(ESP32)
  #include <WiFi.h>
#elif defined(ESP8266)
  #include <ESP8266WiFi.h>
#endif
#include <Firebase_ESP_Client.h>
//Provide the token generation process info.
#include "addons/TokenHelper.h"
//Provide the RTDB payload printing info and other helper functions.
#include "addons/RTDBHelper.h"

// Insert your network credentials
#define WIFI_SSID "<WIFI_CHANGE_THIS>"
#define WIFI_PASSWORD "YOUR_PASSWORD"

// Insert Firebase project API Key
#define API_KEY "WEB_API"

// Insert RTDB URLefine the RTDB URL */
#define DATABASE_URL "DATABASE_URL" 

//Define Firebase Data object
FirebaseData fbdo;

FirebaseAuth auth;
FirebaseConfig config;
String randNum = "/7624";
unsigned long sendDataPrevMillis = 0;
float count = 0;
bool signupOK = false;
float lastCount = 0;
float difference = 0;
void setup(){
  Serial.begin(115200);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED){
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();
auth.user.email = "USER@example.fake";
auth.user.password = "PASS";
  /* Assign the api key (required) */
config.api_key = API_KEY;
/* Assign the RTDB URL (required) */
config.database_url = DATABASE_URL;
  /* Sign in */
  Firebase.begin(&config, &auth);
    Serial.println("ok");
    signupOK = true;

}
void loop(){

  if (Firebase.ready() && signupOK){
    sendDataPrevMillis = millis();
     if (Firebase.RTDB.get(&fbdo, randNum+"/check")){
      if (fbdo.dataTypeEnum() == fb_esp_rtdb_data_type_integer) {
      Serial.println(fbdo.to<int>());
      lastCount = count;
      count = count + 1 + (rand() % 10);
      difference = count - lastCount;
    // Write an Int number on the database path test/int
    if (Firebase.RTDB.setFloat(&fbdo, randNum +"/count", count)){
      Serial.println("PASSED");
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
    }
    else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }
    if (Firebase.RTDB.setFloat(&fbdo, randNum+"/difference", difference)){
      Serial.println("PASSED");
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
    }
    else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }
    // Write an Float number on the database path test/float
    if (Firebase.RTDB.setFloat(&fbdo, randNum+"/lastCount", lastCount)){
      Serial.println("PASSED");
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
    }
    else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }
    Firebase.RTDB.deleteNode(&fbdo, randNum+"/check");
  }
    }
  }
}
