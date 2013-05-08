settings = {
	drawBoundingSphere: false,
	forEntity: {
		invMass: 10,
		maxSpeed: 150
	},
	forSteering: {
		maxForce: 50
	}
}

canvas	= new Canvas("aa")
game	= new Game(canvas, settings)
entity	= new Entity(game, settings, canvas.width/2, canvas.height/2)

settings.steerer = new Seeker settings

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

#
#	Settings
#
addSettingsPanel(settings)
