## WhatsApp Clone Android

A WhatsApp like application for Android with various WhatsApp features
such as sending texts, media and voice recording messages. Some features
will come at a later stage.

## About the application

The application heavily relies on
[Firebase](https://firebase.google.com/) for it's seamless integration
with android, fast development and free usage. Most of it's products
like [Authentication](https://firebase.google.com/products/auth),
[Cloud Firestore](https://firebase.google.com/products/firestore),
[Cloud Storage](https://firebase.google.com/products/storage) and
[Cloud Functions](https://firebase.google.com/products/functions) make
it very easy to mimic WhatsApp's features without so much hassle.

### How to use/test the application

Since the application relies on
[Firebase](https://firebase.google.com/), we need to add it to our app
and also create a new project on the
[Firebase Console](https://console.firebase.google.com/).

#### Adding Firebase to the app

**Step 1:** Create a Firebase project

Before you can add Firebase to your Android app, you need to create a
Firebase project to connect to your Android app.

1. In the [Firebase Console](https://console.firebase.google.com/),
   click Add project, then select or enter a Project name.
2. (Optional) If you are creating a new project, you can edit the
   Project ID.
3. Click Continue.
4. Set up Google Analytics for the project, which enables us to have an
   optimal experience using
   [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging)
5. Click Create project

Visit
[Creating a Firebase project](https://firebase.google.com/docs/android/setup?authuser=0#create-firebase-project)
for any clarifications.

**Step 2:** Register the app with Firebase

After you have a Firebase project, you can add your Android app to it.

1. Go to the Firebase console
   [Firebase Console](https://console.firebase.google.com/).
2. In the center of the project overview page, click the Android icon to
   launch the setup workflow.
3. Enter your app's package name in the Android package name field.

Make sure to enter the package name that your app is actually using. The
package name value is case-sensitive, and it cannot be changed for this
Firebase Android app after it's registered with your Firebase project.

4. Enter other app information: App nickname and Debug signing
   certificate SHA-1.

Debug signing certificate SHA-1: A
[SHA-1 hash](https://developers.google.com/android/guides/client-auth)
is required by Firebase Authentication (when using
[phone number sign in](https://firebase.google.com/docs/auth/android/phone-auth))
and
[Firebase Dynamic Links](https://firebase.google.com/docs/dynamic-links)
of which we are both using.

5. Click Register app.

**Step 3:** Add a Firebase configuration file

1. Add the Firebase Android configuration file to your app:

* Click Download google-services.json to obtain your Firebase Android
  config file (google-services.json).
* Move your config file into the module (app-level) directory of your
  app.

2. Sync the app gradle files to ensure that all dependencies have been
   downloaded.

**Step 4:** Enable Phone Number sign-in for Firebase project

Since WhatsApp uses phone numbers for authentication, we must first
enable the Phone Number sign-in method for your Firebase project:

1. In the [Firebase Console](https://console.firebase.google.com/), open
   the Authentication section.
2. On the Sign-in Method page, enable the Phone sign-in method, also
   enable the Email/Password for future usage.

#### Deploying Cloud Functions to Firebase project

These
[Firebase Cloud Functions](https://github.com/Lmakgae/WApp-Clone-Google-Cloud-Functions)
for the application need to be deployed first before running the
application. Check the repository to see a great detailed explanation on
how each function work.

See
[How to deploy functions](https://firebase.google.com/docs/functions/manage-functions)
for further info on how to deploy them to Firebase.

### How it works

#### 1. Authentication

Since WhatsApp uses phone numbers for authentication, I used Firebase
Authentication to mimic the sign-in process and flow where the phone
number is entered and followed by an SMS to verify the number. After
signing in, you are given a screen to set your name and profile picture.

[![Authentication 1][Authentication-1]] [![Authentication
2][Authentication-2]] [![Authentication 3][Authentication-3]]
[![Authentication 4][Authentication-4]]

#### 2. Sending Text, Media and Voice Recording Messages

##### Sending Messages

WhatsApp stores the messages we send in the server for a period of time
until the recipient actually receives or downloads the message in their
phone, then once the message has been delivered, it gets deleted from
the server and only stored in the recipient's device. I replicated that
flow with Firebase's
[Cloud Firestore database](https://firebase.google.com/docs/firestore)
and with the help of
[Cloud Functions](https://firebase.google.com/docs/functions) and
[Cloud Messaging](https://firebase.google.com/docs/cloud-messaging).

##### The process of sending messages

When a user sends a message:

1. The message data is written to the Cloud Firestore Database where
   messages between users are stored.
2. A cloud function triggers on writes to the Cloud Firestore database
   path where messages between users are stored.
3. The function composes two message to send via
   [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging)
   to the user's and recipient's registered devices. One message is to
   let the user that sent the message that the message was received by
   the server and the other message is to let the recipient know that
   they have a new message to download from the server. See
   [the code](https://github.com/Lmakgae/WApp-Clone-Google-Cloud-Functions)
   for the Cloud Functions for more info.
4. When the recipient receives the message via
   [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging),
   the device initiates a process to download the message data from the
   database and deletes the message from the database as well.
5. A cloud function triggers on deletes to the Cloud Firestore database
   path where message between users are stored.
6. The function composes a message to send via Cloud Messaging to the
   user that sent the message to let them know that the recipient
   actually received the message and updates the UI accordingly.

<br>

[![Sending Messages 1][Sending-Messages-1]] [![Sending Messages
2][Sending-Messages-2]]

<br>

##### More Screenshots

[![Screenshot 1][screenshot-1]] [![Screenshot 2][screenshot-2]]
[![Screenshot 3][screenshot-3]]



## Acknowledgements
* [Varun John's Audio-Recording-Animation](https://github.com/varunjohn/Audio-Recording-Animation)
* [Dushyanth's Barcode Scanner](https://github.com/dm77/barcodescanner)
* [Henning Dodenhof's Circle Image View](https://github.com/hdodenhof/CircleImageView)

## Contributing

Contributions, issues and feature requests are welcome.<br />

## License

Copyright © 2019 [Lehlogonolo Makagae](https://github.com/lmakgae). <br />
This project is [MIT](https://github.com/lmakage/espacio-dios-website/blob/master/LICENSE) licensed.


<!-- SCREENSHOTS AND GIFS  -->
[Authentication-1]: screenshots/authentication_1.jpg
[Authentication-2]: screenshots/authentication_2.jpg
[Authentication-3]: screenshots/authentication_3.jpg
[Authentication-4]: screenshots/authentication_4.jpg
[Sending-Messages-1]: screenshots/sending_messages_1.gif
[Sending-Messages-2]: screenshots/sending_messages_2.jpg
[Screenshot-1]: screenshots/screenshot_1.jpg
[Screenshot-2]: screenshots/screenshot_2.jpg
[Screenshot-3]: screenshots/screenshot_3.jpg



