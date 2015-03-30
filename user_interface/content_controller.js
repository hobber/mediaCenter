app.controller('ContentController', ['$scope','$rootScope', '$compile', 
  function($scope, $rootScope, $compile) {

    var contentDiv;
    var contentLinks = [];
    var contentStack = [];

    $scope.init = function() {
      contentDiv = document.getElementById('content');
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

    var showErrorPage = function() {
      contentDiv.innerHTML = 'content not found	';
    };

    var addMenuBar = function() {
      var menuBar = document.createElement('div');
      menuBar.setAttribute('id', 'contentMenu');
      contentDiv.appendChild(menuBar);

      var back = document.createElement('img');
      back.setAttribute('id', 'contentItem');
      back.setAttribute('style', 'right: 250px; top: 0px;');
      back.setAttribute('width', 38);
      back.setAttribute('height', 38);
      back.setAttribute('src', 'content/back.png');
      back.setAttribute('alt', '&nbsp');
      back.setAttribute('ng-click', 'back()');
      $compile(back)($scope);
      menuBar.appendChild(back);
    };

    var showContent = function(content) {
      if(content === undefined) {
        showErrorPage();
        return;
      }

      contentStack.push(content);
      contentDiv.innerHTML = '';
      contentLinks = [];
      
      addMenuBar();
      for(var i = 0; i < content.length; i++) {
        var groupElement = contentFactories.group(content[i]);
        contentDiv.appendChild(groupElement);
      }
    };

    $rootScope.$on('showContent', function(event, id) {
      var xmlHttp = new XMLHttpRequest();
      xmlHttp.open('GET', 'http://localhost:11111/api?content=' + id, true); 
      xmlHttp.send();          
      xmlHttp.onloadend = function() {
        if(xmlHttp.status === 200)
          showContent(JSON.parse(xmlHttp.response).content);
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
  }
]);
