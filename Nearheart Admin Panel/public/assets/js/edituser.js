
firebase.auth().onAuthStateChanged(function (user) {
    if (user) {
        // loads data
    } else {
      window.location.href = "login.html"; 
        
    }
  });


// get current url and idgenre
var params = getUrlParams(window.location.href);
var dbUser = firebase.database().ref('users/'+params.iduser);

// function for get id 
function getUrlParams(url) 
{
  var params = {};
  (url + '?').split('?')[1].split('&').forEach(
    function (pair) 
    {
       pair = (pair + '=').split('=').map(decodeURIComponent);
       if (pair[0].length) 
       {
         params[pair[0]] = pair[1];
       }
  });

  return params;
};



getUserInfo();
function getUserInfo(){
  
    dbUser.once('value', function(snapshot) {
      //var childKey = snapshot.key;
      var childData = snapshot.val();
      //console.log(childData.name);
      document.getElementById("textName").value=childData.username;
      document.getElementById("picProfile").src = childData.photoProfile;
      document.getElementById("textAbout").value=childData.about;
      document.getElementById("textGender").value=childData.gender;
      document.getElementById("textAge").value=childData.age;
      document.getElementById("textCountry").value=childData.country;
      document.getElementById("textCity").value=childData.city;
      document.getElementById("textEmail").value=childData.email;
      document.getElementById("textJob").value=childData.work;
      document.getElementById("textPhone").value=childData.numberPhone;
      document.getElementById("textSchool").value=childData.school;
      document.getElementById("textTypeAccount").value=childData.type_account;
      document.getElementById("myInput").value=params.iduser;

      var date = new Date(childData.number*-1);
      document.getElementById("textCreated").value=date.toLocaleString();


    });

    
}

function clickbtnAllimages(){
     window.location='userImages.html?iduser='+params.iduser;
}

function onClickbtnEditUser(){
        dbUser.update({
            username: document.getElementById("textName").value,
            about: document.getElementById("textAbout").value,
            gender: document.getElementById("textGender").value,
            age: document.getElementById("textAge").value,
            birthday: document.getElementById("textBirthday").value,
            email: document.getElementById("textEmail").value,
            job: document.getElementById("textJob").value,
            numberPhone: document.getElementById("textPhone").value,
            school: document.getElementById("textSchool").value
        
        }, function(error) {
            if (error) {
                // The write failed...
                alert("save data failed");
            } else {
                // Data saved successfully!
                alert("Data saved successfully");
                location.reload();
            }
        });
}

//function myFunction() {
  /* Get the text field */
 // var copyText = document.getElementById("myInput");
 // copyToClipboard(copyText);
  /* Select the text field */
 // copyText.select();

  /* Copy the text inside the text field */
  //document.execCommand("copy");

  /* Alert the copied text */
  //alert("Copied the text: " + copyText.value);
//}




function myFunction() {
  var copyText = document.querySelector("#myInput");
  copyText.select();
  document.execCommand("copy");
  // Alert the copied text 
  alert("Copied the text: " + copyText.value);
}