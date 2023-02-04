firebase.auth().onAuthStateChanged(function (user) {
  if (user) {
      // loads data
  } else {
    window.location.href = "login.html"; 
      
  }
});


var keyuselast;
var numberlast;
var dbUsersReviewsImages = firebase.database().ref('images_reviews');

dbUsersReviewsImages.orderByChild("time").limitToFirst(10).once('value', function(snapshot) {
  snapshot.forEach(function(childSnapshot) {
    var childKey = childSnapshot.key;
    var childData = childSnapshot.val();
      designItems(childData,childKey);
  });
});

function designItems(values,childKey){
  var date = new Date(values.time*-1);
      document.getElementById("tbodys_images").innerHTML+=`<tr>
      <td>
      <a href="userImages.html?iduser=${values.userID}"><button style="margin-top:10px;" class="btn btn-primary"><i class="fa fa-edit"></i></button></a>
      
      </td>
      <td><img src="${values.image}" class="user-image img-responsive" style="width:84px;height:84px;margin: 0;"/>
      <td>${date.toLocaleString()}</td>
      <td>${values.userID}</td>
      <td>${values.type}</td>

      </td>
    </tr>`    
  
    
    keyuselast= childKey;
    numberlast= values.time;
  
}

function onClickNextImages(){

  
  document.getElementById("tbodys_images").innerHTML=``;  

  var next = dbUsersReviewsImages.orderByChild("time").limitToFirst(10).startAt(numberlast,keyuselast);

    next.once('value', function(snapshot) {
      snapshot.forEach(function(childSnapshot) {
        var childKey = childSnapshot.key;
        var childData = childSnapshot.val();
     
          designItems(childData,childKey);
      });
    });

}

function onclickSearchImagesByuserID(){

  var seartext = document.getElementById("textSearchImagesByuserID").value;
    if(seartext==""){
      alert("Search text is empty!!!!");
    }else{
     document.getElementById("tbodys_images").innerHTML="";

      dbUsersReviewsImages.orderByChild("userID").equalTo(seartext).once('value', function(snapshot) {
        snapshot.forEach(function(childSnapshot) {

            var childKey = childSnapshot.key;
           var childData = childSnapshot.val();
      
            designItems(childData,childKey);
          });
        });
    }
  
}

function onClickAscending(){
  document.getElementById("tbodys_images").innerHTML="";

  dbUsersReviewsImages.orderByChild("time").limitToFirst(50).once('value', function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      var childKey = childSnapshot.key;
      var childData = childSnapshot.val();
        designItems(childData,childKey);
    });
  });
  
}

function onClickDescending(){
  document.getElementById("tbodys_images").innerHTML="";

  dbUsersReviewsImages.orderByChild("time").limitToLast(50).once('value', function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      var childKey = childSnapshot.key;
      var childData = childSnapshot.val();
        designItems(childData,childKey);
    });
  });
  
}

