settings = {
	drawBoundingSphere: true
}

canvas	= new Canvas("aa")
game	= new Game(canvas, settings)
entity	= new Entity(game, canvas.width/2, canvas.height/2)

game.entities.push(entity)
game.run()

#
#	Pausing crap
#
pauseButton = document.getElementById('pause')

togglePause = ->
	game.togglePause()
	pauseButton.innerText = if game.isPaused then "unpause" else "pause"

pauseButton.onclick = togglePause

document.body.onkeypress = (e) ->
	togglePause() if e.which is 13
