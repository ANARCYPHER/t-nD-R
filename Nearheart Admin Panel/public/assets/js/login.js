const auth = firebase.auth();    
// login
const loginForm = document.querySelector('#login-form');
loginForm.addEventListener('submit', (e) => {
  e.preventDefault();
  
  // get user info
  const email = loginForm['textemail'].value;
  const password = loginForm['textpassword'].value;

  // log the user in
  auth.signInWithEmailAndPassword(email, password).then((cred) => {
    //console.log(cred.message);
    // close the signup modal & reset form
    //const modal = document.querySelector('#modal-login');
    //M.Modal.getInstance(modal).close();
    window.location.href = "index.html"; 
  }); 

});

auth.onAuthStateChanged(function (user) {
  if (user) {
      // loads data
      window.location.href = "index.html"; 
  } 
});

