
var dataMax = 590;
var data = {
  'Focus': {
	'Blackberry': 8,
	'Windows': 10,
	'Android': 182,
	'iOS': 261,
    'Web': 370
  }
};

var graph = document.querySelector('#graphs');
var resultbars = document.createElement('ul');
resultbars.setAttribute('class', 'resultbars');
graph.appendChild(resultbars);
for (valueName in data['Focus']) {
  var value = data['Focus'][valueName];
  var item = document.createElement('li');
  resultbars.appendChild(item);
    
  var caption = document.createElement('span');
  caption.setAttribute('class', 'os');
  caption.innerHTML = valueName;
  item.appendChild(caption);
    
  var bar = document.createElement('div');
  bar.setAttribute('class', 'progress');  
  item.appendChild(bar);  
  var fill = document.createElement('span');
  bar.appendChild(fill);
  fill.style.width = (value * bar.offsetWidth / dataMax) + 'px';
    
  var label = document.createElement('span');
  label.setAttribute('class', 'value');
  label.innerHTML = value;
  item.appendChild(label);
}

