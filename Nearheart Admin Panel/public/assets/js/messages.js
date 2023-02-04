
firebase.auth().onAuthStateChanged(function (user) {
    if (user) {
        // loads data
    } else {
      window.location.href = "login.html"; 
        
    }
  });


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


// get current url and idgenre
var params = getUrlParams(window.location.href);
var idfrom = params.idfrom;
var idto = params.idto;

var dbMessages = firebase.database().ref('messages/'+params.idfrom+'/'+params.idto);
var dbusers = firebase.database().ref('users');

dbMessages.orderByChild("msgTimeAgo").once('value', function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      var childKey = childSnapshot.key;
      var childData = childSnapshot.val();
        designItems(childData,childKey);
    });
  });
  
  function designItems(values,childKey){

    if(values.from==params.idfrom){
      
      dbusers.child(params.idfrom).once('value', function(snapshot) {
          var childDatauser = snapshot.val();

          document.getElementById("chatbox").innerHTML+=`
          <li class="left clearfix">
          <span class="chat-img pull-left">
              <img class="user-image img-responsive" style="width:64px;height:64px;margin: 0;" src="${childDatauser.photoProfile}" alt="User" class="img-circle">
          </span>
          <div class="chat-body">                                        
                  <strong>${childDatauser.username}</strong>
                  <small class="pull-right text-muted">
                      <i class="fa fa-clock-o fa-fw"></i>${time_ago(values.msgTimeAgo)}
                  </small>                                      
              <p>
              ${values.message}
              </p>
          </div>
      </li>`    
      });

    
    }
    if(values.from==params.idto){

         
      dbusers.child(params.idto).once('value', function(snapshot) {
        var childDatauser = snapshot.val();

        document.getElementById("chatbox").innerHTML+=`
        <li class="right clearfix">
                                    <span class="chat-img pull-right">
                                    <img class="user-image img-responsive" style="width:64px;height:64px;margin: 0;" src="${childDatauser.photoProfile}" alt="User" class="img-circle">
                                    </span>
                                    <div class="chat-body clearfix">
                                      
                                            <small class=" text-muted">
                                            <i class="fa fa-clock-o fa-fw"></i>${time_ago(values.msgTimeAgo)}
                                            </small>
                                            <strong class="pull-right">${childDatauser.username}</strong>
                                       
                                        <p>
                                        ${values.message}
                                        </p>
                                    </div>
                                </li>`    
    });

    }
    
  }
  
  function time_ago(time) {

    switch (typeof time) {
      case 'number':
        break;
      case 'string':
        time = +new Date(time);
        break;
      case 'object':
        if (time.constructor === Date) time = time.getTime();
        break;
      default:
        time = +new Date();
    }
    var time_formats = [
      [60, 'seconds', 1], // 60
      [120, '1 minute ago', '1 minute from now'], // 60*2
      [3600, 'minutes', 60], // 60*60, 60
      [7200, '1 hour ago', '1 hour from now'], // 60*60*2
      [86400, 'hours', 3600], // 60*60*24, 60*60
      [172800, 'Yesterday', 'Tomorrow'], // 60*60*24*2
      [604800, 'days', 86400], // 60*60*24*7, 60*60*24
      [1209600, 'Last week', 'Next week'], // 60*60*24*7*4*2
      [2419200, 'weeks', 604800], // 60*60*24*7*4, 60*60*24*7
      [4838400, 'Last month', 'Next month'], // 60*60*24*7*4*2
      [29030400, 'months', 2419200], // 60*60*24*7*4*12, 60*60*24*7*4
      [58060800, 'Last year', 'Next year'], // 60*60*24*7*4*12*2
      [2903040000, 'years', 29030400], // 60*60*24*7*4*12*100, 60*60*24*7*4*12
      [5806080000, 'Last century', 'Next century'], // 60*60*24*7*4*12*100*2
      [58060800000, 'centuries', 2903040000] // 60*60*24*7*4*12*100*20, 60*60*24*7*4*12*100
    ];
    var seconds = (+new Date() - time) / 1000,
      token = 'ago',
      list_choice = 1;
  
    if (seconds == 0) {
      return 'Just now'
    }
    if (seconds < 0) {
      seconds = Math.abs(seconds);
      token = 'from now';
      list_choice = 2;
    }
    var i = 0,
      format;
    while (format = time_formats[i++])
      if (seconds < format[0]) {
        if (typeof format[2] == 'string')
          return format[list_choice];
        else
          return Math.floor(seconds / format[2]) + ' ' + format[1] + ' ' + token;
      }
    return time;
  }
  
  
