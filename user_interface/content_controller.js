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

    contentFactories.group = function(group) {
      var groupElement = document.createElement('div');
      groupElement.setAttribute('id', 'contentContainer');
      groupElement.setAttribute('style', group.style);
      for(var j = 0; j < group.items.length; j++) {
        var definition = group.items[j];
        if(contentFactories[definition.type] === undefined) {
          console.error('ERROR: unsupported content type ' + definition.type);
          continue;
        }
        var element = contentFactories[definition.type](definition);
        groupElement.appendChild(element);
      }

      var link = contentLinks.length;
      contentLinks.push(group);
      groupElement.setAttribute('ng-click', 'clicked(' + link + ')');
      $compile(groupElement)($scope);         

      return groupElement;
    };

    contentFactories.img = function(definition) {
      var element = document.createElement('img');
      element.setAttribute('id', 'contentItem');
      element.setAttribute('style', 'left: ' + definition.x + 'px; top: ' + definition.y + 'px;');
      element.setAttribute('width', definition.width);
      element.setAttribute('height', definition.height);
      element.setAttribute('src', definition.src);
      element.setAttribute('alt', '&nbsp');
      return element;
    };

    contentFactories.text = function(definition) {
      var element = document.createElement('span');
      element.setAttribute('id', 'contentItem');
      if(definition.style === undefined)
        element.setAttribute('style', 'left: ' + definition.x + 'px; top: ' + definition.y + 'px;');
      else
        element.setAttribute('style', 'left: ' + definition.x + 'px; top: ' + definition.y + 'px;' + definition.style);
      element.innerHTML = definition.text;
      return element;
    };

    contentFactories.backButton = function(definition) {
      var element = document.createElement('img');
      element.setAttribute('id', 'contentItem');
      element.setAttribute('style', 'right: ' + definition.x + 'px; top: 0px;');
      element.setAttribute('width', 38);
      element.setAttribute('height', 38);
      element.setAttribute('src', 'content/back.png');
      element.setAttribute('alt', '&nbsp');
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

      for(var i = 0; i < content.length; i++) {
        var groupElement = contentFactories.group(content[i]);
        contentDiv.appendChild(groupElement);
      }
    };

    $rootScope.$on('showContent', function(event, id) {
      var xmlHttp = new XMLHttpRequest();
      xmlHttp.open('GET', 'http://localhost:11011/api?content=' + id, true); 
      xmlHttp.send();          
      xmlHttp.onloadend = function() {
        if(xmlHttp.status === 200) {
          var response = JSON.parse(xmlHttp.response); 
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
      showContent(contentLinks[index].subgroup);
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

      var xmlHttp = new XMLHttpRequest();
      xmlHttp.open('GET', 'http://localhost:11011/api?context=' + context + '&search=' + $scope.searchTerm, true); 
      xmlHttp.send();          
      xmlHttp.onloadend = function() {
        if(xmlHttp.status === 200) {
          var response = JSON.parse(xmlHttp.response); 
          showContent(response.content);
        }
        else
          console.log('request failed:', xmlHttp);
      }
    };  
  }
]);
