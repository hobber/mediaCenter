app.controller('ContentController', ['$scope','$rootScope', '$compile', 
  function($scope, $rootScope, $compile) {

    var menuDiv;
    var contentDiv;
    var contentLinks = [];
    var contentStack = [];
    var searchTerm;

    $scope.init = function() {
      menuDiv = document.getElementById('contentHeader');
      contentDiv = document.getElementById('contentBody');
    };

    contentFactories = {};

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

    contentFactories.table = function(parent, definition) {
      var element = document.createElement('table');
      parent.appendChild(element);

      element.setAttribute('id', 'contentItem');
      element.setAttribute('border', '1');
      element.setAttribute('cellpadding', '0');
      element.setAttribute('cellspacing', '0');
      element.setAttribute('style', 'left: ' + definition.x + 'px; top: ' + definition.y + 'px; border-collapse: collapse;');
      
      for(var i=0; i<definition.rows.length; i++) {
        var row = document.createElement('tr');
        element.appendChild(row);
        row.setAttribute('style', 'position: relative; height: ' + (definition.rowHeight+2) + 'px;');

        for(var j=0; j<definition.columns; j++) {
          var column = document.createElement('td');
          row.appendChild(column);
          //column width first must be set on maximum, that no line breaks in texts happen
          column.setAttribute('style', 'position: relative; width: 100%;');         
          var item = definition.rows[i][j];
          var createdItem = contentFactories[item.type](column, item);
          column.setAttribute('style', 'position: relative; width: ' + (createdItem.offsetWidth+2) + 'px;');
        }
      }
      return element;
    };

    contentFactories.backButton = function(parent, definition) {
      var element = document.createElement('img');
      parent.appendChild(element);
      element.setAttribute('id', 'contentItem');
      element.setAttribute('style', 'right: ' + definition.x + 'px; top: 0px;');
      element.setAttribute('width', 38);
      element.setAttribute('height', 38);
      element.setAttribute('src', 'content/back.png');
      element.setAttribute('alt', ' ');
      element.setAttribute('ng-click', 'back()');
      $compile(element)($scope);
      return element;
    };

    contentFactories.searchField = function(parent, definition) {
      var element = document.createElement('input');
      parent.appendChild(element);
      element.setAttribute('type', 'text');
      element.setAttribute('id', 'searchBox');
      element.setAttribute('style', 'right: ' + definition.x + 'px; top: 2px;');
      element.setAttribute('placeholder', 'Search...');
      element.setAttribute('ng-keypress', 'search(\'' + definition.context + '\', $event)');    
      element.setAttribute('ng-model', 'searchTerm');   
      $compile(element)($scope);
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

        var element = contentFactories[item.type](groupElement, item, options);                
        var y = item.y + element.offsetHeight;
        if(y > maxY)
          maxY = y;
      }

      var link = contentLinks.length;
      contentLinks.push(definition);
      groupElement.setAttribute('ng-click', 'clicked(' + link + ')');
      $compile(groupElement)($scope);

      var style = 'height: ' + maxY + 'px;';
      if(definition.subgroup !== undefined)
        style += ' cursor:pointer;';
      if(isLast !== true && (options === undefined || options.groupBoarder !== false))
        style +='border-bottom: 1px solid #000000; ';
      groupElement.setAttribute('style', style);
    };

    var showErrorPage = function() {
      contentDiv.innerHTML = 'content not found	';
    };

    var showMenu = function(menu) {
      menuDiv.innerHTML = '';
      if(menu === undefined)
        return;

      var menuBar = document.createElement('div');
      menuBar.setAttribute('id', 'contentMenu');
      menuDiv.appendChild(menuBar);

      for(var i = 0; i < menu.length; i++) {
        var element = menu[i];
        contentFactories[element.type](menuBar, element);
      }
    }

    var showContent = function(content) {
      if(content === undefined) {
        showErrorPage();
        return;
      }

      contentStack.push(content);
      contentDiv.innerHTML = '';
      contentLinks = [];

      var groupBoarder = true;
      if(options !== undefined && options.groupBoarder === false)
        groupBoarder = false;

      var options = content.options;
      var items = content.items;
      for(var i = 0; i < items.length; i++) {
        var definition = items[i];
        if(definition.type !== 'group') {
          console.error('ERROR: content can contain only groups instances of ' + definition.type);
          continue;
        }
        var isLast = (i === items.length-1);
        contentFactories['group'](contentDiv, definition, options, isLast);
      }
    };

    $rootScope.$on('showContent', function(event, id) {
      var xmlHttp = new XMLHttpRequest();
      xmlHttp.open('GET', 'http://localhost:11011/api?content=' + id, true); 
      xmlHttp.send();          
      xmlHttp.onloadend = function() {
        if(xmlHttp.status === 200) {
          var response = JSON.parse(decode(xmlHttp.response)); 
          contentStack = [];
          showMenu(response.menu);
          showContent(response.content);
        }
        else
          console.log('request failed');
      }
    });

    $scope.clicked = function(index) {
      if(contentLinks[index] === undefined) {
        consonsole.error('ERROR: invalid content index ' + index);
        return;
      }

      var subgroup = contentLinks[index].subgroup;
      if(subgroup === undefined)
        return;

      if(subgroup.type === 'loadOnDemand')
        sendRequest(subgroup.context, subgroup.query);        
      else
        showContent(subgroup);
    };  

    $scope.back = function() {
      if(contentStack.length <= 1) {
        console.error('ERROR: cannot go further back');
        return;
      }
      contentStack.pop();
      var content = contentStack.pop();
      showContent(content);
    };  

    $scope.search = function(context, event) {
      if(event.keyCode !== 13)
        return;
      sendRequest(context, 'search=' + $scope.searchTerm);
    }; 

    var decode = function(response) {
      var result = '';
      for(var i=0; i<response.length; i++) {
        var c = response.charAt(i);
        if(c !== '%')
          result += c;
        else {
          var tmp = response.substr(i, 6);
          if(tmp.match(/%[0-9A-Fa-f]{2}%[0-9A-Fa-f]{2}/) != null) {
            result += decodeURIComponent(tmp);
            i += 5;
          }
          else
            result += c;
        }
      }
      return result;
    };

    var sendRequest = function(context, query) {
      var xmlHttp = new XMLHttpRequest();
      xmlHttp.open('GET', 'http://localhost:11011/api?context=' + context + '&' + query, true); 
      xmlHttp.send();          
      xmlHttp.onloadend = function() {
        if(xmlHttp.status === 200) {
          var response = JSON.parse(decode(xmlHttp.response)); 
          showContent(response.content);
        }
        else
          console.log('request failed:', xmlHttp);
      }
    }
  }
]);
