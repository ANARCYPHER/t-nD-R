firebase.auth().onAuthStateChanged(function (user) {
  if (user) {
      // loads data
  } else {
    window.location.href = "login.html"; 
      
  }
});
var keyuselast;
var numberlast;

var dbUsersSearch = firebase.database().ref('users');
var dbUsers = firebase.database().ref('users')
        .orderByChild("number")
        .limitToFirst(15);

      

dbUsers.once('value', function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      var childKey = childSnapshot.key;
      var childData = childSnapshot.val();
      
        designItemsUsers(childData,childKey);
    });
  });

function designItemsUsers(values,idUsers){

var date = new Date(values.number*-1);
values.number==null
if(values.number!=null && (values.photoProfile=="none" || values.photoProfile==null)){
  document.getElementById("tbodys_users").innerHTML+=`<tr>
    <td>
    <button style="margin-top:10px;" class="btn btn-danger" onclick="onclickDeleteUsers('${idUsers}');return false;"><i class="fa fa-trash-alt"></i></button>
    <a href="editUser.html?iduser=${idUsers}"><button style="margin-top:10px;" class="btn btn-primary"><i class="fa fa-edit"></i></button></a>
    
    </td>
    <td>${date.toLocaleString()}</td>
    <td><img src="https://via.placeholder.com/150" class="user-image img-responsive" style="width:64px;height:64px;margin: 0;"/>
    <td>${values.username}</td>
    <td>${values.gender}</td>
    <td>${values.numberPhone}</td>
    <td>${values.email}</td>
    <td>${values.type_account}</td>
    </td>
  </tr>`    
}else
if(values.number!=null)
{
  document.getElementById("tbodys_users").innerHTML+=`<tr>
  <td>
  <button style="margin-top:10px;" class="btn btn-danger" onclick="onclickDeleteUsers('${idUsers}');return false;"><i class="fa fa-trash-alt"></i></button>
  <a href="editUser.html?iduser=${idUsers}"><button style="margin-top:10px;" class="btn btn-primary"><i class="fa fa-edit"></i></button></a>
  
  </td>
  <td>${date.toLocaleString()}</td>
  <td><img src="${values.photoProfile}" class="user-image img-responsive" style="width:64px;height:64px;margin: 0;"/>
  <td>${values.username}</td>
  <td>${values.gender}</td>
  <td>${values.numberPhone}</td>
  <td>${values.email}</td>
  <td>${values.type_account}</td>
  </td>
</tr>`    
}


  
  keyuselast= idUsers;
  numberlast= values.number;

}

function onclickSearchUsers(){

    var seartext = document.getElementById("textSearchUser").value;
    if(seartext==""){
      alert("Search text is empty!!!!");
    }else{
     document.getElementById("tbodys_users").innerHTML="";

      dbUsersSearch.orderByKey().on("child_added", function(snapshot) {
            
        var childKey = snapshot.key;
        var childData = snapshot.val();
      
          if(seartext==childKey){
            designItemsUsers(childData,childKey);
          }  
        });
    }
  
}

function onclickNext15users(){

  
     document.getElementById("tbodys_users").innerHTML=``;  
 
     var next = firebase.database().ref('users')
       .orderByChild("number").limitToFirst(15).startAt(numberlast,keyuselast);

       next.once('value', function(snapshot) {
         snapshot.forEach(function(childSnapshot) {
           var childKey = childSnapshot.key;
           var childData = childSnapshot.val();
        
             designItemsUsers(childData,childKey);
         });
       });

}


function onclickLastest50users(){

  document.getElementById("tbodys_users").innerHTML=``;  

  var next50 = firebase.database().ref('users')
    .orderByChild("number").limitToFirst(50);

    next50.once('value', function(snapshot) {
      snapshot.forEach(function(childSnapshot) {
        var childKey = childSnapshot.key;
        var childData = childSnapshot.val();

        designItemsUsers(childData,childKey);
      });
    });

}

function onclickFirst50users(){

  document.getElementById("tbodys_users").innerHTML=``;  

  var first50 = firebase.database().ref('users')
    .orderByChild("number").limitToLast(50);

    first50.once('value', function(snapshot) {
      snapshot.forEach(function(childSnapshot) {
        var childKey = childSnapshot.key;
        var childData = childSnapshot.val();

        designItemsUsers(childData,childKey);
      });
    });

}


function onclickDeleteUsers(keyuser){
  dbUsersSearch.child(keyuser).remove()
    .then(function() {
      alert("Remove succeeded.");
      location.reload();
    })
    .catch(function(error) {
      alert("Remove failed: " + error.message);
    });
}






