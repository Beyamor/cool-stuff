class Entity
	constructor: (@game, initialX, initialY) ->
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
window.Entity = Entity

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

	draw: ->
		@canvas.clear()
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
window.Game = Game
