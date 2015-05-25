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
	
	//========================================================================= MENU
	
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
	
	//========================================================================= CONTENT
	
	var contentFactories = {};
	
	contentFactories.text = function(parent, definition) {
      var element = document.createElement('span');
      parent.appendChild(element);
      element.setAttribute('id', 'contentItem');
      if(definition.style === undefined)
        element.setAttribute('style', 'left: ' + definition.x + 'px; top: ' + definition.y + 'px;');
      else
        element.setAttribute('style', 'left: ' + definition.x + 'px; top: ' + definition.y + 'px;' + definition.style);
      if(definition.url === undefined)
        element.innerHTML = definition.text;
      else {
        var text = document.createElement('span');
        element.appendChild(text);
        text.innerHTML = definition.text;
        var url = document.createElement('a');
        element.appendChild(url);
        url.setAttribute('href', definition.url);
        url.innerHTML = definition.url;
      }
      return element;
    };
	
	contentFactories.group = function(parent, definition, options, isLast) {
      var groupElement = document.createElement('div');
      parent.appendChild(groupElement);
      groupElement.setAttribute('id', 'contentContainer');      

      var maxY = 0;
      for(var j = 0; j < definition.items.length; j++) {
        var item = definition.items[j];
        if(contentFactories[item.type] === undefined) {
          console.error('ERROR: unsupported content type ' + item.type);
          continue;
        }

        var element = createElement(groupElement, item, options);                
        var y = item.y + element.offsetHeight;
        if(y > maxY)
          maxY = y;
      }

      var style = 'height: ' + maxY + 'px;';      
      if(isLast !== true && (options === undefined || options.groupBoarder !== false))
        style +='border-bottom: 1px solid #000000; ';
      groupElement.setAttribute('style', style);
      return groupElement;
    };
	
	var createElement = function(parent, definition, options, parameter) {
      return contentFactories[definition.type](parent, definition, options, parameter);  
    };
	
	var showContent = function(content) {
	  console.log('CONTENT:', content);
	  
	  var contentDiv = document.getElementById('contentBody');
	  contentDiv.innerHTML = '';
	  
	  var options = content.options;
	  var items = content.page;
      for(var i = 0; i < items.length; i++) {
        var definition = items[i];
        if(definition.type !== 'group') {
          console.error('ERROR: content can contain only groups instances of ' + definition.type);
          continue;
        }
		
		var isLast = (i === items.length-1);
        contentFactories.group(contentDiv, definition, options, isLast);
      }
	};
  }
]);
