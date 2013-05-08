// Generated by CoffeeScript 1.3.3
(function() {
  var Canvas;

  Canvas = (function() {

    function Canvas(id) {
      this.el = document.getElementById(id);
      this.width = parseInt(this.el.getAttribute("width"));
      this.height = parseInt(this.el.getAttribute("height"));
      this.context = this.el.getContext("2d");
      this.clearColor = "white";
    }

    Canvas.prototype.drawRect = function(x, y, width, height, color) {
      this.context.fillStyle = color;
      return this.context.fillRect(x, y, width, height);
    };

    Canvas.prototype.drawTriangle = function(_arg, _arg1, _arg2, color) {
      var x1, x2, x3, y1, y2, y3;
      x1 = _arg[0], y1 = _arg[1];
      x2 = _arg1[0], y2 = _arg1[1];
      x3 = _arg2[0], y3 = _arg2[1];
      this.context.beginPath();
      this.context.moveTo(x1, y1);
      this.context.lineTo(x2, y2);
      this.context.lineTo(x3, y3);
      this.context.lineTo(x1, y1);
      this.context.fillStyle = color;
      return this.context.fill();
    };

    Canvas.prototype.outlineCircle = function(x, y, radius, color) {
      this.context.beginPath();
      this.context.arc(x, y, radius, 0, 2 * Math.PI, false);
      this.context.strokeStyle = color;
      return this.context.stroke();
    };

    Canvas.prototype.clear = function() {
      return this.drawRect(0, 0, this.width, this.height, this.clearColor);
    };

    return Canvas;

  })();

  window.Canvas = Canvas;

}).call(this);
