var app = angular.module('MediaCenter', []);

app.controller('MenuController', ['$scope', '$rootScope', '$compile', 
  function($scope, $rootScope, $compile) {

    var menu = [];

    var prepareMenu = function() {      
      var groupCounter = 0;
      var elementCounter = 0;

      for(var i = 0; i < menu.length; i++) {
        var entry = menu[i];    

        if(entry.type === "menuEntry") {
          entry.group = groupCounter;
          entry.entry = elementCounter;
          groupCounter++;
        } 
        else if(entry.type === "menuSubEntry") {
          if(i === 0) {
            console.log('ERROR: first menu item must have type menuEntry');
            return false;  
          }
          entry.group = groupCounter - 1;
          entry.entry = elementCounter;
          elementCounter++;
        }
        else {
          console.log('ERROR: unsupported menu item', entry);
          return false;
        }        
      }
      return true;
    }

    var showMenu = function(index) {
      if(index >= menu.length) {
        console.log('ERROR: invalid index', index);
        return;
      }

      var selectedMenuItem = menu[0];
      for(var i = 0; i < menu.length; i++) {
        if(menu[i].type === 'menuSubEntry' && menu[i].entry === index) {
          selectedMenuItem = menu[i];
          break;
        }
      }

      var menuDiv = document.getElementById("menu");
      menuDiv.innerHTML = '';

      for(var i = 0; i < menu.length; i++) {
        var entry = menu[i];    

        if(entry.type === "menuEntry") {
 
          var node = document.createElement("div");
          node.setAttribute("id", "menuEntry");
          node.setAttribute("ng-click", "clicked('" + entry.entry + "')");
          $compile(node)($scope);

          var image = document.createElement("img");
          image.setAttribute("src", entry.icon);
          image.setAttribute("width", 30);
          image.setAttribute("height", 30);
          image.setAttribute("alt", "&nbsp;");
          image.setAttribute("style", "vertical-align: middle;");
          node.appendChild(image);

          var span = document.createElement("span");
          span.setAttribute("style", "vertical-align: middle;");
          span.innerHTML = "&nbsp;&nbsp;" + entry.name;
          node.appendChild(span);
          menuDiv.appendChild(node);
        } 
        else if(entry.type === "menuSubEntry") {
          if(i === 0) {
            console.log('ERROR: first menu item must have type menuEntry');
            return;
          }

          if(entry.group !== selectedMenuItem.group)
            continue;

          var node = document.createElement("div");
          if(entry.entry === selectedMenuItem.entry) {
            node.setAttribute("id", "menuSubEntrySelected");
          }
          else {
            node.setAttribute("id", "menuSubEntry");
          }
          node.setAttribute("ng-click", "clicked('" + entry.entry + "')");
          $compile(node)($scope);

          var span = document.createElement("span");
          span.innerHTML = "&nbsp;&nbsp;&#8227;&nbsp;&nbsp;" + entry.name;
          node.appendChild(span);
          menuDiv.appendChild(node);
        }
      }
    };

    $scope.load = function() {
      var xmlHttp = new XMLHttpRequest();
      xmlHttp.open("GET", 'http://localhost:11111/api?menu', true);
//    POST:  xmlHttp.send('was los?');   
      xmlHttp.send();          
      xmlHttp.onloadend = function() {
        if(xmlHttp.status === 200) {
          menu = JSON.parse(xmlHttp.response).entries;
          if(prepareMenu() === true)
            showMenu(0);
        } else {
          console.log('request failed');
        }
      }
    };

    $scope.clicked = function(index) {
      $rootScope.$broadcast('showContent', menu[index].id);  
      showMenu(parseInt(index));
    };
  }
]);


