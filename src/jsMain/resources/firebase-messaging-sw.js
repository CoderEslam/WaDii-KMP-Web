// Firebase Cloud Messaging background handler.
// Runs in its own service-worker scope, so it can't share Kotlin's Constants.kt —
// keep this config in sync with FIREBASE_* in src/jsMain/kotlin/com/wadii/utils/Constants.kt.
// This is the public web config only; never put the admin.json service account here.
importScripts("https://www.gstatic.com/firebasejs/11.6.0/firebase-app-compat.js");
importScripts("https://www.gstatic.com/firebasejs/11.6.0/firebase-messaging-compat.js");

firebase.initializeApp({
    apiKey: "AIzaSyChi41C0aEzzjctisYpqpHvzJKN6uD0v8A",
    authDomain: "wadii-kmp.firebaseapp.com",
    projectId: "wadii-kmp",
    storageBucket: "wadii-kmp.firebasestorage.app",
    messagingSenderId: "1041244088662",
    appId: "1:1041244088662:web:292b831dddb6727ed40ef7",
});

firebase.messaging();
