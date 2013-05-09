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

	rotate: (theta) ->
		cosTheta = Math.cos(theta)
		sinTheta = Math.sin(theta)
		return new Vec2(@x*cosTheta - @y*sinTheta, @x*sinTheta + @y*cosTheta)

	toString: ->
		"(#{Math.floor(@x)}, #{Math.floor(@y)})"

window.Vec2 = Vec2
