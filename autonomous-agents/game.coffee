class Vehicle
	constructor: (@game, @settings, initialX, initialY) ->
		@pos		= new Vec2 initialX, initialY
		@vel 		= new Vec2
		@heading	= 0
		@radius		= 32

	update: (timeDelta) ->
		acceleration	= @settings.steerer.force(this, @game.mousePos).scaleBy(@settings.forEntity.invMass)
		@vel		= @vel.plus(acceleration.scaleBy(timeDelta)).clamp(@settings.forEntity.maxSpeed)
		@pos		= @pos.plus(@vel.scaleBy(timeDelta))

		if @vel.lengthSquared() > 0.0000001
			@heading = @vel.direction()

	draw: (canvas) ->
		x = @pos.x
		y = @pos.y

		if @game.settings.drawBoundingSphere
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
window.Vehicle = Vehicle

class Game
	constructor: (@canvas, @settings) ->
		@isPaused = false
		@entities = []

		@mousePos = new Vec2
		@canvas.el.addEventListener 'mousemove', (e) =>
			rect		= @canvas.el.getBoundingClientRect()
			@mousePos	= new Vec2(e.clientX - rect.left, e.clientY - rect.top)

	update: (timeDelta) ->
		entity.update(timeDelta) for entity in @entities

		for entity in @entities
			if entity.pos.x + entity.radius < 0 then entity.pos.x = @canvas.width
			if entity.pos.y + entity.radius < 0 then entity.pos.y = @canvas.height
			if entity.pos.x - entity.radius > @canvas.width then entity.pos.x = 0
			if entity.pos.y - entity.radius > @canvas.height then entity.pos.y = 0

	draw: ->
		@canvas.clear()
		entity.draw(@canvas) for entity in @entities

	run: ->
		currentTime = new Date().getTime() / 1000
		setInterval(=>
			previousTime	= currentTime
			currentTime	= new Date().getTime() / 1000
			timeDelta	= currentTime - previousTime

			@update(timeDelta) unless @isPaused
			@draw()
		, 16)

	togglePause: ->
		@isPaused = !@isPaused
window.Game = Game
