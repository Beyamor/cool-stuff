programSettings = {
	drawBoundingSphere: true
}

class Vec2
	constructor: (@x=0, @y=0) ->

	clone: ->
		new Vec2 @x, @y

	plus: (other) ->
		new Vec2 @x + other.x, @y + other.y

	minus: (other) ->
		new Vec2 @x - other.x, @y - other.y

	lengthSquared: ->
		@x*@x + @y*@y

	length: ->
		Math.sqrt(@lengthSquared())

	direction: ->
		Math.atan2(@y, @x)

	scaleBy: (scale) ->
		new Vec2 @x*scale, @y*scale

	clamp: (maxLength) ->
		if @lengthSquared() <= maxLength*maxLength
			@clone()
		else
			@scaleBy(maxLength / @length())

	isZero: ->
		@x == 0 && @y == 0

	normal: ->
		@scaleBy(1 / @length())

class Canvas
	constructor: (id) ->
		@el		= document.getElementById(id)
		@width		= parseInt @el.getAttribute("width")
		@height		= parseInt @el.getAttribute("height")
		@context	= @el.getContext("2d")
		@clearColor	= "white"

	drawRect: (x, y, width, height, color) ->
		@context.fillStyle = color
		@context.fillRect(x, y, width, height)

	drawTriangle: ([x1, y1], [x2, y2], [x3, y3], color) ->
		@context.beginPath()
		@context.moveTo(x1, y1)
		@context.lineTo(x2, y2)
		@context.lineTo(x3, y3)
		@context.lineTo(x1, y1)
		@context.fillStyle = color
		@context.fill()

	outlineCircle: (x, y, radius, color) ->
		@context.beginPath()
		@context.arc(x, y, radius, 0, 2 * Math.PI, false)
		@context.strokeStyle = color
		@context.stroke()

	clear: ->
		@drawRect(0, 0, @width, @height, @clearColor)

class Steerer
	constructor: (@game, @entity, @maxForce) ->
		@isOn = {
			seek:	false
			arrive:	true
		}

	force: ->
		force		= new Vec2
		targetPos	= @game.mousePos
		toTarget	= targetPos.minus(@entity.pos)

		# Seek
		if @isOn["seek"]
			desiredVelocity	= toTarget.normal().scaleBy(@entity.maxSpeed)
			force		= desiredVelocity.minus(@entity.vel)

		# Arrive
		if @isOn["arrive"]
			distance	= toTarget.length()

			if distance > 0
				speed		= Math.min(@entity.maxSpeed, distance)
				desiredVelocity	= toTarget.normal().scaleBy(speed)
				force		= desiredVelocity.minus(@entity.vel)

		return force.clamp(@maxForce)

class Entity
	constructor: (game, initialX, initialY) ->
		@pos		= new Vec2 initialX, initialY
		@vel 		= new Vec2
		@invMass	= 50
		@maxSpeed	= 150
		@maxTurnRate	= 1
		@steerer	= new Steerer game, this, 20
		@heading	= 0
		@radius		= 32

	update: (timeDelta) ->
		acceleration	= @steerer.force().scaleBy(@invMass)
		@vel		= @vel.plus(acceleration.scaleBy(timeDelta)).clamp(@maxSpeed)
		@pos		= @pos.plus(@vel.scaleBy(timeDelta))

		if @vel.lengthSquared() > 0.0000001
			@heading = @vel.direction()

	draw: (canvas) ->
		x = @pos.x
		y = @pos.y

		if programSettings.drawBoundingSphere
			canvas.outlineCircle x, y, @radius, "grey"

		headLength	= @radius
		tailLength	= @radius - 5
		tailAngle	= 2.2

		canvas.drawTriangle(
			[x + Math.cos(@heading) * headLength,			y + Math.sin(@heading) * headLength		],
			[x + Math.cos(@heading - tailAngle) * tailLength,	y + Math.sin(@heading - tailAngle) * tailLength	],
			[x + Math.cos(@heading + tailAngle) * tailLength,	y + Math.sin(@heading + tailAngle)* tailLength	],
			"black"
		)

class Game
	constructor: (@canvas) ->
		@isPaused = false
		@entities = []

		@mousePos = new Vec2
		@canvas.el.addEventListener 'mousemove', (e) =>
			rect		= @canvas.el.getBoundingClientRect()
			@mousePos	= new Vec2(e.clientX - rect.left, e.clientY - rect.top)

	update: (timeDelta) ->
		entity.update(timeDelta) for entity in @entities

	draw: ->
		canvas.clear()
		entity.draw(@canvas) for entity in @entities

	run: ->
		currentTime = new Date().getTime() / 1000
		setInterval(=>
			previousTime	= currentTime
			currentTime	= new Date().getTime() / 1000
			timeDelta	= currentTime - previousTime

			return if @isPaused

			@update(timeDelta)
			@draw()
		, 16)

	togglePause: ->
		@isPaused = !@isPaused

canvas	= new Canvas("aa")
game	= new Game(canvas)
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
