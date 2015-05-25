var app = angular.module('MediaCenter', []);

app.controller('Controller', ['$scope', '$compile', 
  function($scope, $compile) {
  
    $scope.init = function() {
	  loadMenu();
    };
	
	$scope.clicked = function(entryId, subEntryId) {
	  var xmlHttp = new XMLHttpRequest();
	  xmlHttp.open('GET', 'http://localhost:11011/api?id=' + entryId + '.' + subEntryId, true);
      xmlHttp.send();          
      xmlHttp.onloadend = function() {
        if(xmlHttp.status === 200) {
		  showContent(JSON.parse(xmlHttp.response));
        } else {
          console.log('request failed');
        }
      }
	};
	
	var loadMenu = function() {
	  var xmlHttp = new XMLHttpRequest();
      xmlHttp.open('GET', 'http://localhost:11011/menu', true);
      xmlHttp.send();          
      xmlHttp.onloadend = function() {
        if(xmlHttp.status === 200) {
		  buildMenu(JSON.parse(xmlHttp.response));
        } else {
          console.log('request failed');
        }
      }
	};
	
	var buildMenu = function(response) {
	  var menuDiv = document.getElementById('menu');
      menuDiv.innerHTML = '';

      for(var entryName in response) {
        var entry = response[entryName];
        
        var node = document.createElement('div');
		menuDiv.appendChild(node);
        node.setAttribute('id', 'menuEntry');
		var name = 'AustrianCharts';
		node.setAttribute('ng-click', 'clicked(' + entry.id + ', 0)');
		$compile(node)($scope);
        
		var image = document.createElement('img');
		node.appendChild(image);
        image.setAttribute('src', entry.icon);
        image.setAttribute('width', 30);
        image.setAttribute('height', 30);
        image.setAttribute('alt', '&nbsp;');
        image.setAttribute('style', 'vertical-align: middle;');
        
        var span = document.createElement('span');
		node.appendChild(span);
        span.setAttribute('style', 'vertical-align: middle;');
        span.innerHTML = '&nbsp;&nbsp;' + entryName;
        
		if(entry.subentries === undefined)
		  continue;
		  
		for(var i=0; i<entry.subentries.length; i++) {
		  var subentry = entry.subentries[i];
		  
		  var subnode = document.createElement('div');
		  menuDiv.appendChild(subnode);
          subnode.setAttribute('id', 'menuSubEntry');          
          subnode.setAttribute('ng-click', 'clicked(' + entry.id + ', ' + i + ')');
          subnode.setAttribute('class', 'ng-show');
          $compile(subnode)($scope);

          var span = document.createElement('span');
		  subnode.appendChild(span);
          span.innerHTML = '&nbsp;&nbsp;&#8227;&nbsp;&nbsp;' + subentry.name;
		}
      }
	};
	
	var showContent = function(content) {
	  console.log('CONTENT:', content);
	};
  }
]);
