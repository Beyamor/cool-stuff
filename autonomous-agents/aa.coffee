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
marker	= new Marker(game, canvas.width * 0.25, canvas.height * 0.25)
vehicle	= new Vehicle(marker, canvas, canvas.width/2, canvas.height/2, settings)

settings.steerer = new Seeker settings

game.entities.push(vehicle)
game.entities.push(marker)
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
