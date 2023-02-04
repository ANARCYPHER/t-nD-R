firebase.auth().onAuthStateChanged(function (user) {
    if (user) {
        // loads data
    } else {
      window.location.href = "login.html"; 
        
    }
  });
  var keyuselast;
  var numberlast;
  
  var dbSpams = firebase.database().ref("reports").child("spam").orderByChild("time").limitToFirst(10);

  dbSpams.once('value', function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      var childKey = childSnapshot.key;
      var childData = childSnapshot.val();
         designItemsSpams(childData,childKey);
    });
  });

function onClickNextSpams(){

    document.getElementById("tbodys_Spams").innerHTML=``;  
 
    var next = dbSpams.startAt(numberlast,keyuselast);

    next.once('value', function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
          var childKey = childSnapshot.key;
          var childData = childSnapshot.val();
       
          designItemsSpams(childData,childKey);
        });
      });
  
}

function onClickLast10Spams(){
    document.getElementById("tbodys_Spams").innerHTML=``;  
    dbSpams.once('value', function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
          var childKey = childSnapshot.key;
          var childData = childSnapshot.val();
             designItemsSpams(childData,childKey);
        });
      });
}

function designItemsSpams(values,idSpams){

  var date = new Date(values.time*-1);

  document.getElementById("tbodys_Spams").innerHTML+=`<tr>
  <td>
  <button style="margin-top:10px;" class="btn btn-danger" onclick="onclickDeleteSpams('${idSpams}');return false;"><i class="fa fa-trash-alt"></i></button>
  <a href="editUser.html?iduser=${values.reportUserID}"><button style="margin-top:10px;" class="btn btn-primary"><i class="fa fa-user"></i></button></a>
  
  </td>
  <td>${values.userID}</td>
  <td>${values.reportUserID}</td>
  <td>${date.toLocaleString()}</td>

  </td>
  </tr>`    

  keyuselast= idSpams;
  numberlast= values.time;

}

function onclickSearchSpams(){

    var seartext = document.getElementById("textSearchSpams").value;
    if(seartext==""){
      alert("Search text is empty!!!!");
    }else{
     document.getElementById("tbodys_Spams").innerHTML="";

     firebase.database().ref("reports").child("spam").orderByChild("reportUserID").equalTo(seartext).once('value', function(snapshot) {
        snapshot.forEach(function(childSnapshot) {

            var childKey = childSnapshot.key;
           var childData = childSnapshot.val();
      
           designItemsSpams(childData,childKey);
          });
        });
    }

}


function onclickDeleteSpams(idSpams){

    firebase.database().ref("reports").child("spam").child(idSpams).remove()
        .then(function() {
        alert("Remove succeeded.");
        location.reload();
        })
        .catch(function(error) {
        alert("Remove failed: " + error.message);
        });

}
