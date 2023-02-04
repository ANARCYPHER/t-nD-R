firebase.auth().onAuthStateChanged(function (user) {
    if (user) {
        // loads data
    } else {
      window.location.href = "login.html"; 
        
    }
  });
  var keyuselast;
  var timelast;
  
  var dbUsers = firebase.database().ref('users');

  var dbChats = firebase.database().ref('messages_reviews')
          .orderByChild("time")
          .limitToFirst(15);

   var dbChatsSearch = firebase.database().ref('messages_reviews')
          .orderByChild("from");
  
        
  
  dbChats.once('value', function(snapshot) {
      snapshot.forEach(function(childSnapshot) {
        var childKey = childSnapshot.key;
        var childData = childSnapshot.val();
        
          designItemsChats(childData,childKey);
      });
    });
  
  function designItemsChats(values,idChats){
  
  var date = new Date(values.time*-1);
  
  dbUsers.child(values.from).once('value', function(snapshotfrom) {
      var childDataFrom = snapshotfrom.val();

      dbUsers.child(values.to).once('value', function(snapshotto) {
        
        var childDataTo = snapshotto.val();
       
        document.getElementById("tbodys_messages_reviews").innerHTML+=`<tr>
        <td>
        <a href="editUser.html?iduser=${values.from}"><button style="margin-top:10px;" class="btn btn-success"><i class="fa fa-user"></i> From</button></a>
        <a href="editUser.html?iduser=${values.to}"><button style="margin-top:10px;" class="btn btn-primary"><i class="fa fa-user"></i> To</button></a>
        <a href="messages.html?idfrom=${values.from}&idto=${values.to}"><button style="margin-top:10px;" class="btn btn-primary"><i class="fa fa-comments"></i> </button></a>
        
        </td>
        <td>${childDataFrom.username}</td>
        <td>${childDataTo.username}</td>
        <td>${values.message}</td>
        <td>${values.type}</td>
        <td>${date.toLocaleString()}</td>
        </td>
        </tr>`    
  
    });

  });

   
  
    
    keyuselast= idChats;
    timelast= values.time;
  
  }
  
  function onclickSearchmessages_reviews(){
  
      var seartext = document.getElementById("textSearchUser").value;
      if(seartext==""){
        alert("Search text is empty!!!!");
      }else{
       document.getElementById("tbodys_messages_reviews").innerHTML="";
  
        dbChatsSearch.equalTo(seartext).once("value", function(snapshot) {
            snapshot.forEach(function(childSnapshot) {
                    
                var childKey = childSnapshot.key;
                var childData = childSnapshot.val();
                
                    designItemsChats(childData,childKey);
                    });

          });
      }
    
  }
  
  function onclickNext15messages_reviews(){
  
    
       document.getElementById("tbodys_messages_reviews").innerHTML=``;  
   
       var next = firebase.database().ref('messages_reviews')
         .orderByChild("time").limitToFirst(15).startAt(timelast,keyuselast);
  
         next.once('value', function(snapshot) {
           snapshot.forEach(function(childSnapshot) {
             var childKey = childSnapshot.key;
             var childData = childSnapshot.val();
          
               designItemsChats(childData,childKey);
           });
         });
  
  }
  
  
  function onclickLastest50messages_reviews(){
  
    document.getElementById("tbodys_messages_reviews").innerHTML=``;  
  
    var next50 = firebase.database().ref('messages_reviews')
      .orderByChild("time").limitToFirst(50);
  
      next50.once('value', function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
          var childKey = childSnapshot.key;
          var childData = childSnapshot.val();
  
          designItemsChats(childData,childKey);
        });
      });
  
  }
  
  function onclickFirst50messages_reviews(){
  
    document.getElementById("tbodys_messages_reviews").innerHTML=``;  
  
    var first50 = firebase.database().ref('messages_reviews')
      .orderByChild("time").limitToLast(50);
  
      first50.once('value', function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
          var childKey = childSnapshot.key;
          var childData = childSnapshot.val();
  
          designItemsChats(childData,childKey);
        });
      });
  
  }
  
  
 
  
  
  
  
  
  