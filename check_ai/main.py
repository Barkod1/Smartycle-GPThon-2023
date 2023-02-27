import cv2
import firebase_admin
from firebase_admin import credentials, auth, db
import time
from matplotlib import pyplot as plt

# Sign in a user with email and password
email = "a@gmail.com"
password = "123456"
cred = credentials.Certificate("json certificate")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'database_url'
})

try:
    user = auth.get_user_by_email(email=email)
except auth.UserNotFoundError:
    user = auth.create_user(email=email, password=password)

# Load the Haar Cascade classifier
stop_data = cv2.CascadeClassifier('data.xml')

# Open a video capture device (use 0 for the default webcam)


# Initialize the Firebase Admin SDK

# Get a reference to the database root


while True:
    time.sleep(1)
    try:
        ref = db.reference('/7624/picture')
        data_str = ref.get()

        # Convert the data to an integer
        data_int = int(data_str)
        # Capture a frame from the video
        cap = cv2.VideoCapture(0)
        time.sleep(1)
        ret, frame = cap.read()
        time.sleep(1)

        if ret:
            # Convert the frame to grayscale
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

            # Apply the Haar Cascade classifier to detect stop signs
            found = stop_data.detectMultiScale(gray, minSize=(20, 20))
            for (x, y, w, h) in found:
                cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)

            # Display the resulting frame
            cv2.imshow('frame', frame)
            time.sleep(1)
            if len(found) > 0:
                print("eyes found")
                ref = db.reference(str('/7624'))
                ref.update({'/check': 123})
                ref.child('picture').delete()

            # Exit if the 'q' key is pressed
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break

        # Release the video capture device and close all windows
        cap.release()
        cv2.destroyAllWindows()

    except Exception as e:
        print(e)
