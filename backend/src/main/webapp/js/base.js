/**
 * @fileoverview
 * Provides methods for the Hello Endpoints sample UI and interaction with the
 * Hello Endpoints API.
 */

/** google global namespace for Google projects. */
var google = google || {};

/** appengine namespace for Google Developer Relations projects. */
google.appengine = google.appengine || {};

/** samples namespace for App Engine sample code. */
google.appengine.samples = google.appengine.samples || {};

/** hello namespace for this sample. */
google.appengine.samples.hello = google.appengine.samples.hello || {};

/**
 * Prints a greeting to the greeting log.
 * param {Object} greeting Greeting to print.
 */
google.appengine.samples.hello.print = function(response) {
  var element = document.createElement('div');
  element.classList.add('row');
  //element.innerHTML = response.content;
  element.innerHTML = response.taskTitle;
  document.querySelector('#outputLog').appendChild(element);
};

/**
 * Gets a numbered greeting via the API.
 * @param {string} id ID of the greeting.
 */
google.appengine.samples.hello.getGreeting = function(id) {
  var requestData = {};
  //requestData.taskId = '5200313652871168';
  requestData.profileID = id;
  //gapi.client.myTaskApi.getChatRoomMessages(requestData).execute(
  gapi.client.myTaskApi.getChatRooms(requestData).execute(
      function(resp) {
        if (!resp.code) {
          //google.appengine.samples.hello.print(resp);
          resp.items = resp.items || [];
          for (var i = 0; i < resp.items.length; i++) {
                      google.appengine.samples.hello.print(resp.items[i]);
          }
        }
      });
};

/**
 * Lists greetings via the API.
 */

google.appengine.samples.hello.listGreeting = function() {
  gapi.client.helloworld.greetings.listGreeting().execute(
      function(resp) {
        if (!resp.code) {
          resp.items = resp.items || [];
          for (var i = 0; i < resp.items.length; i++) {
            google.appengine.samples.hello.print(resp.items[i]);
          }
        }
      });
};

/**
 * Enables the button callbacks in the UI.
 */
google.appengine.samples.hello.enableButtons = function() {
  var getGreeting = document.querySelector('#getGreeting');
  getGreeting.addEventListener('click', function(e) {
    google.appengine.samples.hello.getGreeting(
        document.querySelector('#id').value);
  });

  var listGreeting = document.querySelector('#listGreeting');
  listGreeting.addEventListener('click',
      google.appengine.samples.hello.listGreeting);

};
/**
 * Initializes the application.
 * @param {string} apiRoot Root of the API's path.
 */
google.appengine.samples.hello.init = function(apiRoot) {
  // Loads the OAuth and helloworld APIs asynchronously, and triggers login
  // when they have completed.
  var apisToLoad;
  var callback = function() {
    if (--apisToLoad == 0) {
      google.appengine.samples.hello.enableButtons();
    }
  }

  apisToLoad = 1; // must match number of calls to gapi.client.load()
  gapi.client.load('myTaskApi', 'v1', callback, apiRoot);
  //gapi.client.load('myTaskApi', 'v1', null, 'https://xz-logical-bird-e.appspot.com');
};

