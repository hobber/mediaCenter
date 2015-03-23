app.controller('ContentController', ['$scope','$rootScope', '$compile', 
  function($scope, $rootScope, $compile) {

    var content = [];

    var showContent = function() {
      var menuDiv = document.getElementById("content");
      //console.log('show:', content);
      menuDiv.innerHTML = "show " + content;
    };

    $rootScope.$on('showContent', function(event, id) {
      var xmlHttp = new XMLHttpRequest();
      xmlHttp.open("GET", 'http://localhost:11111/api?content=' + id, true); 
      xmlHttp.send();          
      xmlHttp.onloadend = function() {
        if(xmlHttp.status === 200) {
          content = JSON.parse(xmlHttp.response).entries; 
          showContent();
        } else {
          console.log('request failed');
        }
      }
    });   
  }
]);
