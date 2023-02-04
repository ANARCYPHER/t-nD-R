firebase.auth().onAuthStateChanged(function (user) {
    if (user) {
        // loads data
    } else {
      window.location.href = "login.html"; 
        
    }
  });
  var keyuselast;
  var numberlast;
  
  var dbinphoto = firebase.database().ref("reports").child("inappropriate_photo").orderByChild("time").limitToFirst(10);

  dbinphoto.once('value', function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      var childKey = childSnapshot.key;
      var childData = childSnapshot.val();
         designItemsinphoto(childData,childKey);
    });
  });

function onClickNextinphoto(){

    document.getElementById("tbodys_inphoto").innerHTML=``;  
 
    var next = dbinphoto.startAt(numberlast,keyuselast);

    next.once('value', function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
          var childKey = childSnapshot.key;
          var childData = childSnapshot.val();
       
          designItemsinphoto(childData,childKey);
        });
      });
  
}

function onClickLast10inphoto(){
    document.getElementById("tbodys_inphoto").innerHTML=``;  
    dbinphoto.once('value', function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
          var childKey = childSnapshot.key;
          var childData = childSnapshot.val();
             designItemsinphoto(childData,childKey);
        });
      });
}

function designItemsinphoto(values,idinphoto){

  var date = new Date(values.time*1000*-1);

  document.getElementById("tbodys_inphoto").innerHTML+=`<tr>
  <td>
  <button style="margin-top:10px;" class="btn btn-danger" onclick="onclickDeleteinphoto('${idinphoto}');return false;"><i class="fa fa-trash-alt"></i></button>
  <a href="editUser.html?iduser=${values.reportUserID}"><button style="margin-top:10px;" class="btn btn-primary"><i class="fa fa-user"></i></button></a>
  
  </td>
  <td>${values.userID}</td>
  <td>${values.reportUserID}</td>
  <td>${date.toLocaleString()}</td>

  </td>
  </tr>`    

  keyuselast= idinphoto;
  numberlast= values.time;

}

function onclickSearchinphoto(){

    var seartext = document.getElementById("textSearchinphoto").value;
    if(seartext==""){
      alert("Search text is empty!!!!");
    }else{
     document.getElementById("tbodys_inphoto").innerHTML="";

     firebase.database().ref("reports").child("inappropriate_photo").orderByChild("reportUserID").equalTo(seartext).once('value', function(snapshot) {
        snapshot.forEach(function(childSnapshot) {

            var childKey = childSnapshot.key;
           var childData = childSnapshot.val();
      
           designItemsinphoto(childData,childKey);
          });
        });
    }

}


function onclickDeleteinphoto(idinphoto){

    firebase.database().ref("reports").child("inappropriate_photo").child(idinphoto).remove()
        .then(function() {
        alert("Remove succeeded.");
        location.reload();
        })
        .catch(function(error) {
        alert("Remove failed: " + error.message);
        });

}
