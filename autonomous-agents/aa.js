// Generated by CoffeeScript 1.3.3
(function() {
  var canvas, entity, game, pauseButton, settings, togglePause;

  settings = {
    drawBoundingSphere: true
  };

  canvas = new Canvas("aa");

  game = new Game(canvas, settings);

  entity = new Entity(game, canvas.width / 2, canvas.height / 2);

  game.entities.push(entity);

  game.run();

  pauseButton = document.getElementById('pause');

  togglePause = function() {
    game.togglePause();
    return pauseButton.innerText = game.isPaused ? "unpause" : "pause";
  };

  pauseButton.onclick = togglePause;

  document.body.onkeypress = function(e) {
    if (e.which === 13) {
      return togglePause();
    }
  };

}).call(this);
