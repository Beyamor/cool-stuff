class Entity
	constructor: (initialX, initialY) ->
		@pos		= new Vec2 initialX, initialY
		@radius		= 32

	update: (timeDelta) ->
		"Override in subclass"

	draw: (canvas) ->
		"Override in subclass"

class Vehicle extends Entity
	constructor: (@target, @bounds, initialX, initialY, @settings) ->
		super(initialX, initialY)

		@vel 		= new Vec2
		@heading	= 0

	update: (timeDelta) ->
		acceleration	= @settings.steerer.force(this, @target.pos)
					.clamp(@settings.forSteering.maxForce)
					.scaleBy(@settings.forEntity.invMass)
		@vel		= @vel.plus(acceleration.scaleBy(timeDelta)).clamp(@settings.forEntity.maxSpeed)
		@pos		= @pos.plus(@vel.scaleBy(timeDelta))

		if @vel.lengthSquared() > 0.0000001
			@heading = @vel.direction()

		# wrap
		if @pos.x + @radius < 0 then @pos.x = @bounds.width
		if @pos.y + @radius < 0 then @pos.y = @bounds.height
		if @pos.x - @radius > @bounds.width then @pos.x = 0
		if @pos.y - @radius > @bounds.height then @pos.y = 0

	draw: (canvas) ->
		x = @pos.x
		y = @pos.y

		if @settings.drawBoundingSphere
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

class Marker extends Entity
	constructor: (@game, initialX, initialY) ->
		super(initialX, initialY)
		@radius = 16

		mousePos = (e) =>
			rect = @game.canvas.el.getBoundingClientRect()
			return new Vec2(e.clientX - rect.left, e.clientY - rect.top)

		$(@game.canvas.el).mousedown((e) =>
			@isBeingDragged = true if mousePos(e).minus(@pos).length() <= @radius*@radius
		).mouseup((e) =>
			@isBeingDragged = false
		)

	update: (timeDelta) ->
		@pos = @game.mousePos.clone() if @isBeingDragged

	draw: (canvas) ->
		canvas.outlineCircle @pos.x, @pos.y, @radius, "#1BA8E0"
window.Marker = Marker

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

			@update(timeDelta) unless @isPaused
			@draw()
		, 16)

	togglePause: ->
		@isPaused = !@isPaused
window.Game = Game
