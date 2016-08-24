# thyN
This is the codebase for thyNeighbr. An android app that brings neighborhood together. This is a platform that enables people to post and exchange messages with others who have subscribed to the platform.

## Section 1: Android
The important features are-  
com.thyn.broadcast - Client side code that connects to Google Cloud Messaging platform.  
com.thyn.collection - Model speaking to the local cache.  
Android assets
## Section 2: Backend API - Google App Engine
The backend API is deployed in Google Cloud Endpoints which connects to the Google App Engine Datastore. We are also using the Google Cloud Messaging feature with which users can subscribe to messages once registered with the platform.

