firebase.auth().onAuthStateChanged(function (user) {
  if (user) {
      // loads data
  } else {
    window.location.href = "login.html"; 
      
  }
});

// chat
var rootRefMovies = firebase.database().ref('chat');

//get count chat
rootRefMovies.on("value", function(snapshot) {
  document.getElementById('chatstext').innerHTML=snapshot.numChildren();
})
//



//Spam
var rootRefSeasons = firebase.database().ref('reports');

//get count Spam
rootRefSeasons.child('spam').on("value", function(snapshot) {
  document.getElementById('spamtext').innerHTML=snapshot.numChildren();
})

//ip
var rootRefCast = firebase.database().ref('reports').child('inappropriate_photo');

//get count iptext
rootRefCast.on("value", function(snapshot) {
  document.getElementById('iptext').innerHTML=snapshot.numChildren();
})

//Users
var rootRefUsers = firebase.database().ref('users');

//get count Users
rootRefUsers.on("value", function(snapshot) {
  document.getElementById('userstext').innerHTML=snapshot.numChildren();
})

//images_reviews
var rootRefSliders = firebase.database().ref('images_reviews');

//get count images_reviews
rootRefSliders.on("value", function(snapshot) {
  document.getElementById('images_reviewstext').innerHTML=snapshot.numChildren();
})

//images_reviews
var rootRefMessages = firebase.database().ref('messages_reviews');

//get count images_reviews
rootRefMessages.on("value", function(snapshot) {
  document.getElementById('messagestext').innerHTML=snapshot.numChildren();
})

