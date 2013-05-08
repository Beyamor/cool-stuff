programSettings = {
	drawBoundingSphere: true
}

class Vec2
	constructor: (@x=0, @y=0) ->

	clone: ->
		new Vec2 @x, @y

	plus: (other) ->
		new Vec2 @x + other.x, @y + other.y

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
	force: ->
		return new Vec2(1, 0)

class Entity
	constructor: (initialX, initialY) ->
		@pos		= new Vec2 initialX, initialY
		@vel 		= new Vec2
		@invMass	= 1
		@maxSpeed	= 10
		@maxForce	= 10
		@maxTurnRate	= 1
		@steerer	= new Steerer
		@heading	= 0
		@radius		= 32

	update: (timeDelta) ->
		acceleration	= @steerer.force().scaleBy(@invMass)
		@vel		= @vel.plus(acceleration.scaleBy(timeDelta)).clamp(@maxSpeed)
		@pos		= @pos.plus(@vel)

		if (@vel.lengthSquared() > 0.0001)
			@heading	= @vel.direction()

	draw: (canvas) ->
		x = @pos.x
		y = @pos.y

		if programSettings.drawBoundingSphere
			canvas.outlineCircle x, y, @radius, "grey"

		headLength	= @radius
		tailLength	= @radius
		tailAngle	= 2.2

		canvas.drawTriangle(
			[x + Math.cos(@heading) * headLength,			y - Math.sin(@heading) * headLength		],
			[x + Math.cos(@heading - tailAngle) * tailLength,	y - Math.sin(@heading - tailAngle) * tailLength	],
			[x + Math.cos(@heading + tailAngle) * tailLength,	y - Math.sin(@heading + tailAngle)* tailLength	],
			"black"
		)

canvas = new Canvas "aa"
entity = new Entity(canvas.width/2, canvas.height/2)

currentTime = new Date().getTime() / 1000
setInterval(->

	previousTime	= currentTime
	currentTime	= new Date().getTime() / 1000
	timeDelta	= currentTime - previousTime

	entity.update(timeDelta)

	canvas.clear()
	entity.draw(canvas)
, 16)
