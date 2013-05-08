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
$pauseButton = $('#pause')

togglePause = ->
	game.togglePause()
	$pauseButton.text(if game.isPaused then "unpause" else "pause")

$pauseButton.click togglePause

$('body').keypress (e) ->
	togglePause() if e.which is 13
