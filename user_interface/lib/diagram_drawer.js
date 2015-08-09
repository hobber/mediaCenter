function drawDiagram(data) {
  function drawLineChart(data) {
    if(data.data === undefined) {
      console.error('no data to draw a diagram');
      return;
    }
    if(data.yAxis === undefined) {
      console.error('data contains no yAxis');
      return;
    }
  
    //configuration
    var offsetX = 100, offsetY = 30;
    var width = 600, height = 400;
    var endPaddingX = 20, endPaddingY = 10;
    
    var i, j, dataSet, value, pos;
    
    var canvas = document.createElement('canvas');
    canvas.width  = width;
    canvas.height = height;
    canvas.style.border = '1px solid';
    document.body.appendChild(canvas);
    var diagram = canvas.getContext('2d');        
  
    //analyse y-axis
    var yAxisZero = height - offsetY, yAxisScale = 1.0, yAxisValues = [];
    var minValueY, maxValueY;
    if(data.yAxis.values !== undefined) {
      if(data.yAxis.values.length < 2) {
        console.error('yAxis.values must contain at least 2 values');
        return;
      }
      for(i = 0; i < data.yAxis.values.length; i++) {
        value = data.yAxis.values[i];
        if(minValueY === undefined || value < minValueY)
          minValueY = value;
        if(maxValueY === undefined || value > maxValueY)
          maxValueY = value;
        yAxisValues.push(value);
      }
      yAxisScale = (height - offsetY - endPaddingY) / (maxValueY - Math.min(0, minValueY));
      if(minValueY < 0)
        yAxisZero += minValueY * yAxisScale;            
    }
    if(minValueY === undefined || maxValueY === undefined) {
      console.error('data contains no valid values for yAxis');
      return;
    }
    
    //analyse x-axis
    var xAxisZero = offsetX, xAxisScale = 1.0, xAxisValues = [];
    var minValueX, maxValueX;
    if(data.xAxis !== undefined && data.xAxis.values !== undefined) {
      if(data.xAxis.values.length < 2) {
        console.error('xAxis.values must contain at least 2 values');
        return;
      }
      for(i = 0; i < data.xAxis.values.length; i++) {
        value = data.xAxis.values[i];
        if(minValueX === undefined || value < minValueX)
          minValueX = value;
        if(maxValueX === undefined || value > maxValueX)
          maxValueX = value;
        xAxisValues.push(value);
      }
    }
    else {    
      minValueX = 0;        
      for(dataSet in data.data) {
        dataSet = data.data[dataSet];
        if(dataSet === undefined || dataSet.values === undefined)
            continue;
        if(maxValueX === undefined || dataSet.values.length - 1 > maxValueX)
          maxValueX = dataSet.values.length - 1;
      }
      for(i = 0; i <= maxValueX; i++)
        xAxisValues.push(i);
    }
    if(maxValueX - minValueX < 2) {
      console.error('datasets must consist at least of 2 data values');
      return;
    }
    xAxisScale = (width - offsetX - endPaddingX) / (maxValueX - Math.min(0, minValueX));
    if(minValueX < 0)
      xAxisZero -= minValueX * xAxisScale;
  
    //draw y-axis
    diagram.beginPath();
    diagram.moveTo(xAxisZero, height - offsetY);
    diagram.lineTo(xAxisZero, 0);
    diagram.lineTo(xAxisZero - 3, 6);
    diagram.moveTo(xAxisZero + 3, 6);
    diagram.lineTo(xAxisZero, 0);
    diagram.stroke();
    for(i = 0; i < yAxisValues.length; i++) {
      value = yAxisValues[i];
      pos = yAxisZero - value * yAxisScale;
      diagram.beginPath();
      diagram.moveTo(xAxisZero - 5, pos);
      diagram.lineTo(xAxisZero, pos);
      diagram.stroke();
      diagram.fillText(value, xAxisZero - 50, pos);
    }        
  
    //draw x-axis
    diagram.beginPath();
    diagram.moveTo(offsetX, yAxisZero);
    diagram.lineTo(width, yAxisZero);
    diagram.lineTo(width - 6, yAxisZero - 3);
    diagram.moveTo(width - 6, yAxisZero + 3);
    diagram.lineTo(width, yAxisZero);
    diagram.stroke();
    for(i = 0; i < xAxisValues.length; i++) {
      value = xAxisValues[i];
      pos = xAxisZero + value * xAxisScale;
      diagram.beginPath();
      diagram.moveTo(pos, yAxisZero + 5);
      diagram.lineTo(pos, yAxisZero);
      diagram.stroke();
      diagram.fillText(value, pos, yAxisZero + 25);
    }    
    
    //draw diagram        
    for(dataSet in data.data) {
      dataSet = data.data[dataSet];
      diagram.strokeStyle = dataSet.style.color;
      value = dataSet.values[0];
      diagram.moveTo(xAxisZero + xAxisValues[0] * xAxisScale, yAxisZero - value * yAxisScale);
      for(i = 1; i < dataSet.values.length; i++) {
        value = dataSet.values[i];
        diagram.lineTo(xAxisZero + xAxisValues[i] * xAxisScale, yAxisZero - value * yAxisScale);
      }
      diagram.stroke();
    } 
  }

  if(data.type === 'Line-Chart')
    drawLineChart(data);
  else
    console.error('unsupported chart type ' + data.type);
}