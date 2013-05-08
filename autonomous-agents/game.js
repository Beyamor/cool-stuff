// Generated by CoffeeScript 1.3.3
(function() {
  var Entity, Game;

  Entity = (function() {

    function Entity(game, initialX, initialY) {
      this.game = game;
      this.pos = new Vec2(initialX, initialY);
      this.vel = new Vec2;
      this.invMass = 50;
      this.maxSpeed = 150;
      this.maxTurnRate = 1;
      this.steerer = new Steerer(game, this, 20);
      this.heading = 0;
      this.radius = 32;
    }

    Entity.prototype.update = function(timeDelta) {
      var acceleration;
      acceleration = this.steerer.force().scaleBy(this.invMass);
      this.vel = this.vel.plus(acceleration.scaleBy(timeDelta)).clamp(this.maxSpeed);
      this.pos = this.pos.plus(this.vel.scaleBy(timeDelta));
      if (this.vel.lengthSquared() > 0.0000001) {
        return this.heading = this.vel.direction();
      }
    };

    Entity.prototype.draw = function(canvas) {
      var headLength, tailAngle, tailLength, x, y;
      x = this.pos.x;
      y = this.pos.y;
      if (this.game.settings.drawBoundingSphere) {
        canvas.outlineCircle(x, y, this.radius, "grey");
      }
      headLength = this.radius;
      tailLength = this.radius - 5;
      tailAngle = 2.2;
      return canvas.drawTriangle([x + Math.cos(this.heading) * headLength, y + Math.sin(this.heading) * headLength], [x + Math.cos(this.heading - tailAngle) * tailLength, y + Math.sin(this.heading - tailAngle) * tailLength], [x + Math.cos(this.heading + tailAngle) * tailLength, y + Math.sin(this.heading + tailAngle) * tailLength], "black");
    };

    return Entity;

  })();

  window.Entity = Entity;

  Game = (function() {

    function Game(canvas, settings) {
      var _this = this;
      this.canvas = canvas;
      this.settings = settings;
      this.isPaused = false;
      this.entities = [];
      this.mousePos = new Vec2;
      this.canvas.el.addEventListener('mousemove', function(e) {
        var rect;
        rect = _this.canvas.el.getBoundingClientRect();
        return _this.mousePos = new Vec2(e.clientX - rect.left, e.clientY - rect.top);
      });
    }

    Game.prototype.update = function(timeDelta) {
      var entity, _i, _len, _ref, _results;
      _ref = this.entities;
      _results = [];
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        entity = _ref[_i];
        _results.push(entity.update(timeDelta));
      }
      return _results;
    };

    Game.prototype.draw = function() {
      var entity, _i, _len, _ref, _results;
      this.canvas.clear();
      _ref = this.entities;
      _results = [];
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        entity = _ref[_i];
        _results.push(entity.draw(this.canvas));
      }
      return _results;
    };

    Game.prototype.run = function() {
      var currentTime,
        _this = this;
      currentTime = new Date().getTime() / 1000;
      return setInterval(function() {
        var previousTime, timeDelta;
        previousTime = currentTime;
        currentTime = new Date().getTime() / 1000;
        timeDelta = currentTime - previousTime;
        if (!_this.isPaused) {
          _this.update(timeDelta);
        }
        return _this.draw();
      }, 16);
    };

    Game.prototype.togglePause = function() {
      return this.isPaused = !this.isPaused;
    };

    return Game;

  })();

  window.Game = Game;

}).call(this);
