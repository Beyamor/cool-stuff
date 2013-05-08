// Generated by CoffeeScript 1.3.3
(function() {
  var Canvas, Entity, Steerer, Vec2, canvas, entity, programSettings;

  programSettings = {
    drawBoundingSphere: true
  };

  Vec2 = (function() {

    function Vec2(x, y) {
      this.x = x != null ? x : 0;
      this.y = y != null ? y : 0;
    }

    return Vec2;

  })();

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

  Steerer = (function() {

    function Steerer() {}

    Steerer.prototype.force = function() {
      return new Vec2(1, 0);
    };

    return Steerer;

  })();

  Entity = (function() {

    function Entity(initialX, initialY) {
      this.pos = new Vec2(initialX, initialY);
      this.vel = new Vec2;
      this.invMass = 1;
      this.maxSpeed = 10;
      this.maxForce = 10;
      this.maxTurnRate = 1;
      this.steerer = new Steerer;
      this.heading = 0;
      this.radius = 32;
    }

    Entity.prototype.update = function(timeDelta) {
      var acceleration;
      acceleration = this.steerer.force().scaleBy(this.invMass);
      this.vel = this.vel.plus(acceleration.scaleBy(timeDelta)).clamp(this.maxSpeed);
      this.pos = this.pos.plus(this.vel);
      if (this.vel.lengthSquared() > 0.0001) {
        return this.heading = this.vel.direction();
      }
    };

    Entity.prototype.draw = function(canvas) {
      var headLength, tailAngle, tailLength, x, y;
      x = this.pos.x;
      y = this.pos.y;
      if (programSettings.drawBoundingSphere) {
        canvas.outlineCircle(x, y, this.radius, "grey");
      }
      headLength = this.radius;
      tailLength = this.radius;
      tailAngle = 2.2;
      return canvas.drawTriangle([x + Math.cos(this.heading) * headLength, y - Math.sin(this.heading) * headLength], [x + Math.cos(this.heading - tailAngle) * tailLength, y - Math.sin(this.heading - tailAngle) * tailLength], [x + Math.cos(this.heading + tailAngle) * tailLength, y - Math.sin(this.heading + tailAngle) * tailLength], "black");
    };

    return Entity;

  })();

  canvas = new Canvas("aa");

  entity = new Entity(canvas.width / 2, canvas.height / 2);

  setInterval(function() {
    canvas.clear();
    return entity.draw(canvas);
  }, 16);

}).call(this);
