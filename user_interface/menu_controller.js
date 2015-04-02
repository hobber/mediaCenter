var app = angular.module('MediaCenter', []);

app.controller('MenuController', ['$scope', '$rootScope', '$compile', 
  function($scope, $rootScope, $compile) {

    var menu = [];

    var prepareMenu = function() {      
      var groupCounter = 0;
      var elementCounter = 0;

      for(var i = 0; i < menu.length; i++) {
        var entry = menu[i];    

        if(entry.type === 'menuEntry') {
          elementCounter++;
          entry.group = groupCounter;
          entry.link = elementCounter;
          groupCounter++;
        } 
        else if(entry.type === 'menuSubEntry') {
          if(i === 0) {
            console.log('ERROR: first menu item must have type menuEntry');
            return false;  
          }
          entry.group = groupCounter - 1;
          entry.link = elementCounter;
          elementCounter++;
        }
        else {
          console.log('ERROR: unsupported menu item', entry);
          return false;
        }        
      }
      return true;
    }

    var buildMenu = function() {           
      var isFirstSubEntry = true;
      var menuDiv = document.getElementById('menu');
      menuDiv.innerHTML = '';

      for(var i = 0; i < menu.length; i++) {
        var entry = menu[i];    

        if(entry.type === 'menuEntry') {
 
          var node = document.createElement('div');
          node.setAttribute('id', 'menuEntry');
          node.setAttribute('ng-click', 'clicked(' + entry.link + ')');
          $compile(node)($scope);

          var image = document.createElement('img');
          image.setAttribute('src', entry.icon);
          image.setAttribute('width', 30);
          image.setAttribute('height', 30);
          image.setAttribute('alt', '&nbsp;');
          image.setAttribute('style', 'vertical-align: middle;');
          node.appendChild(image);

          var span = document.createElement('span');
          span.setAttribute('style', 'vertical-align: middle;');
          span.innerHTML = '&nbsp;&nbsp;' + entry.name;
          node.appendChild(span);
          menuDiv.appendChild(node);
        } 
        else if(entry.type === 'menuSubEntry') {
          if(i === 0) {
            console.log('ERROR: first menu item must have type menuEntry');
            return;
          }

          var node = document.createElement('div');
          if(isFirstSubEntry === true) {
            node.setAttribute('id', 'menuSubEntrySelected');
            isFirstSubEntry = false;
          }
          else
            node.setAttribute('id', 'menuSubEntry');
          
          node.setAttribute('ng-click', 'clicked(' + entry.link + ')');
          node.setAttribute('class', entry.group === 0 ? 'ng-show' : 'ng-hide');
          $compile(node)($scope);

          var span = document.createElement('span');
          span.innerHTML = '&nbsp;&nbsp;&#8227;&nbsp;&nbsp;' + entry.name;
          node.appendChild(span);
          menuDiv.appendChild(node);
          entry.element = node;
        }
      }
    };

    $scope.init = function() {
      var xmlHttp = new XMLHttpRequest();
      xmlHttp.open('GET', 'http://localhost:11011/api?menu', true);
//    POST:  xmlHttp.send('was los?');   
      xmlHttp.send();          
      xmlHttp.onloadend = function() {
        if(xmlHttp.status === 200) {
          menu = JSON.parse(xmlHttp.response).entries;
          if(prepareMenu() === true) {
            buildMenu();
            $rootScope.$broadcast('showContent', menu[menu[0].link].id);
          }
        } else {
          console.log('request failed');
        }
      }
    };

    $scope.clicked = function(index) {
      if(index === $scope.lastClicked)
        return;
      $scope.lastClicked = index;

      var visibleGroup = menu[index].group;
      $rootScope.$broadcast('showContent', menu[index].id);      
      for(var i = 0; i < menu.length; i++) {
        var entry = menu[i];      
        if(entry.type !== 'menuSubEntry')
          continue;
 
        var element = entry.element;
        if(i === index)
          element.setAttribute('id', 'menuSubEntrySelected');
        else
          element.setAttribute('id', 'menuSubEntry');
        element.setAttribute('class', entry.group === visibleGroup ? 'ng-show' : 'ng-hide');
        $compile(element)($scope);
      }      
    };
  }
]);


