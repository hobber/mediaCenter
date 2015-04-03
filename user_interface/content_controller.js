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

    contentFactories.img = function(definition) {
      var element = document.createElement('img');
      element.setAttribute('id', 'contentItem');
      element.setAttribute('style', 'left: ' + definition.x + 'px; top: ' + definition.y + 'px;');
      element.setAttribute('width', definition.width);
      element.setAttribute('height', definition.height);
      element.setAttribute('src', definition.src);
      element.setAttribute('alt', ' ');
      return element;
    };

    contentFactories.text = function(definition) {
      var element = document.createElement('span');
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

    contentFactories.backButton = function(definition) {
      var element = document.createElement('img');
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

    contentFactories.searchField = function(definition) {
      var element = document.createElement('input');
      element.setAttribute('type', 'text');
      element.setAttribute('id', 'searchBox');
      element.setAttribute('style', 'right: ' + definition.x + 'px; top: 2px;');
      element.setAttribute('placeholder', 'Search...');
      element.setAttribute('ng-keypress', 'search(\'' + definition.context + '\', $event)');    
      element.setAttribute('ng-model', 'searchTerm');   
      $compile(element)($scope);
      return element;
    };

    var createGroup = function(group, isLastGroup) {
      var groupElement = document.createElement('div');
      var elements = [];

      groupElement.setAttribute('id', 'contentContainer');      
      for(var j = 0; j < group.items.length; j++) {
        var definition = group.items[j];
        if(contentFactories[definition.type] === undefined) {
          console.error('ERROR: unsupported content type ' + definition.type);
          continue;
        }
        var element = contentFactories[definition.type](definition);
        elements.push({element: element, y: definition.y});
        groupElement.appendChild(element);
      }

      var link = contentLinks.length;
      contentLinks.push(group);
      groupElement.setAttribute('ng-click', 'clicked(' + link + ')');
      $compile(groupElement)($scope); 

      contentDiv.appendChild(groupElement);

      // calculation of dimensions of group must be done after adding to contentDiv
      var maxY = 0;
      for(var j=0; j<elements.length; j++) {
        var y = elements[j].y + elements[j].element.offsetHeight;
        if(y > maxY)
          maxY = y;
        console.log('HEIGHT:', elements[j].element.offsetWidth, elements[j].element.offsetHeight, elements[j].y);        
      }
      if(isLastGroup)
        groupElement.setAttribute('style', 'height: ' + maxY + 'px;');
      else
        groupElement.setAttribute('style', 'height: ' + maxY + 'px; border-bottom: 1px solid #000000; ');
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
        menuBar.appendChild(contentFactories[element.type](element));
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

      for(var i = 0; i < content.length; i++)
        createGroup(content[i], i === content.length-1);      
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
