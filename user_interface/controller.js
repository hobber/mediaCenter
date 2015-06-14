var app = angular.module('MediaCenter', []);

app.controller('Controller', ['$scope', '$compile', 
  function($scope, $compile) {
  
	var URL = '';
	var currentMenu = {
	  entryId: 0,
	  subEntryId: 0
	}
  
    $scope.init = function() {
	  try {
        readURL();
	    loadMenu();		
	  } catch(error) {
	    handleError(error);
	  }
    };

	var clicked = function(request) {
	  try {	  
	    var xmlHttp = new XMLHttpRequest();
	    xmlHttp.open('GET', request, true);
        xmlHttp.send();  
        xmlHttp.onloadend = function() {
		  try {
            if(xmlHttp.status === 200) {
  		      showContent(JSON.parse(xmlHttp.response));
            } else {
              console.log('request failed');
            }
          } catch(error) {
	        handleError(error);
	      }
		}
	  } catch(error) {
	    handleError(error);
	  }
	};
	
	$scope.clickedMenu = function(entryId, subEntryId) {
	  currentMenu.entryId = entryId;
	  currentMenu.subEntryId = subEntryId;
	  clicked(URL + 'api?id=' + entryId + '.' + subEntryId);
	};
	
	$scope.clickedElement = function(parameter) {
	  clicked(URL + 'api?id=' + currentMenu.entryId + '.' + currentMenu.subEntryId + '&' + parameter);
	};
	
	var readURL = function() {
	  URL = document.URL.substring(7);
	  var end = URL.indexOf('/');
	  if(end >= 0)
	    URL = URL.substring(0, end);
      URL = 'http://' + URL + '/';
	};
	
	var handleError = function(error) {
	  var xmlHttp = new XMLHttpRequest();
	  xmlHttp.open('POST', URL + 'error?file=' + error.fileName + '&line=' + error.lineNumber, true);
      xmlHttp.send(error.message);  
	  console.error('ERROR: ' + error.fileName + ':' + error.lineNumber + ' - ' + error.message);
	};
	
	//========================================================================= MENU
	
	var loadMenu = function() {
	  var xmlHttp = new XMLHttpRequest();
      xmlHttp.open('GET', URL + 'menu', true);
      xmlHttp.send();     	  
      xmlHttp.onloadend = function() {	  
	    try {
          if(xmlHttp.status === 200) {
		    buildMenu(JSON.parse(xmlHttp.response));
          } else {
            console.log('request failed');
          }
		} catch(error) {
	      handleError(error);
	    }
      }
	};
	
	var buildMenu = function(response) {
	  var menuDiv = document.getElementById('menu');
      menuDiv.innerHTML = '';

	  var firstEntryId;
      for(var entryName in response) {
        var entry = response[entryName];
		
		if(firstEntryId === undefined)
		  firstEntryId = entry.id;
        
        var node = document.createElement('div');
		menuDiv.appendChild(node);
        node.setAttribute('id', 'menuEntry');
		var name = 'AustrianCharts';
		node.setAttribute('ng-click', 'clickedMenu(' + entry.id + ', 0)');
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
          subnode.setAttribute('ng-click', 'clickedMenu(' + entry.id + ', ' + i + ')');
          subnode.setAttribute('class', 'ng-show');
          $compile(subnode)($scope);

          var span = document.createElement('span');
		  subnode.appendChild(span);
          span.innerHTML = '&nbsp;&nbsp;&#8227;&nbsp;&nbsp;' + subentry.name;
		}
      }
	  
	  $scope.clickedMenu(firstEntryId, 0);
	};
	
	//========================================================================= CONTENT
	
	var contentFactories = {};
	var contentWidth = 0;
	
	contentFactories.img = function(parent, definition) {
      var element = document.createElement('img');
      parent.appendChild(element);
      element.setAttribute('id', 'contentItem');
      element.setAttribute('style', 'left: ' + definition.x + 'px; top: ' + definition.y + 'px;');
      element.setAttribute('width', definition.width);
      element.setAttribute('height', definition.height);
      element.setAttribute('src', definition.src);
      element.setAttribute('alt', ' ');
      return element;
    };
	
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
	
	contentFactories.group = function(parent, definition, isLast) {
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

        var element = createElement(groupElement, item);                
        var y = item.y + element.offsetHeight;
        if(y > maxY)
          maxY = y;
      }

      var style = 'height: ' + maxY + 'px;'; 	  
      if(isLast !== true && (definition.options && definition.options.groupBoarder !== false))
        style +='border-bottom: 1px solid #000000; ';
      groupElement.setAttribute('style', style);
      return groupElement;
    };
	
	/**
     * TABLE
	 *  - x: x-offset [int]
	 *  - y: y-offset [int]
	 *  - rows: content [array arrays of content items]
     *  - ?options: 
	 *      - ?fullWidth [true, fals] ... table fill full content width
	 *  - ?widths: defines widths of columns [array of strings]
     */
	contentFactories.table = function(parent, definition) {
      var element = document.createElement('table');
      parent.appendChild(element);

      element.setAttribute('id', 'contentItem');
      element.setAttribute('border', '1');
      element.setAttribute('cellpadding', '0');
      element.setAttribute('cellspacing', '0');
	  if(definition.options && definition.options.fullWidth)
        element.setAttribute('style', 'width: ' + (contentWidth - definition.x) + 'px; left: ' + definition.x + 'px; top: ' + definition.y + 'px; border-collapse: collapse;');
      else
	    element.setAttribute('style', 'left: ' + definition.x + 'px; top: ' + definition.y + 'px; border-collapse: collapse;');

	  if(definition.widths) {
	    for(var i=0; i<definition.widths.length; i++) {
		  var width = document.createElement('col');
		  element.appendChild(width);
		  width.setAttribute('width', definition.widths[i]);
		}
	  }
	  
      for(var i=0; i<definition.rows.length; i++) {
        var row = document.createElement('tr');
        element.appendChild(row);
        row.setAttribute('style', 'position: relative; height: ' + (definition.rowHeight+2) + 'px;');

        for(var j=0; j<definition.columns; j++) {
          var column = document.createElement('td');
          row.appendChild(column);
          var createdItem = createElement(column, definition.rows[i][j]);
          column.setAttribute('style', 'position: relative; width: ' + (createdItem.offsetWidth+2) + 'px;');
        }
      }
      return element;
    };
	
	var createElement = function(parent, definition) {	
      var element = contentFactories[definition.type](parent, definition);
	  if(definition.onClick) {
		element.setAttribute('ng-click', 'clickedElement("' + definition.onClick + '")');
        $compile(element)($scope);
      }
	  return element;
    };
	
	var showContent = function(content) {
	  var options = content.options;

	  var titleDiv = document.getElementById('contentTitle');
	  titleDiv.innerHTML = '';
	  var titlebar = content.titlebar || [];
	  for(var i = 0; i < titlebar.length; i++) {
        var definition = titlebar[i];		
        createElement(titleDiv, definition);
      }
	  contentWidth = titleDiv.offsetWidth - 2 - 15;
	  
	  var contentDiv = document.getElementById('contentBody');
	  contentDiv.innerHTML = '';
	  var items = content.page;
      for(var i = 0; i < items.length; i++) {
        var definition = items[i];
        if(definition.type !== 'group') {
          console.error('ERROR: content can contain only groups instances of ' + definition.type);
          continue;
        }
		
		var isLast = (i === items.length-1);
        createElement(contentDiv, definition);
      }
	};
  }
]);
