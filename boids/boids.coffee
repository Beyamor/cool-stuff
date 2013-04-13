# Vector operations
vector =\
	add: (vs...) ->
		result = []

		for dimension in [0...vs[0].length]
			sum = 0
			for v in vs
				sum += v[dimension]
			result.push v

		return result

	distanceSquared: (v1, v2) ->
		difference = vector.subtract(v1, v2)
		return difference.reduce((sum, x) -> sum + x*x)


	subtract: (v1, v2) ->
		result = []

		for dimension in [0...v1.length]
			result.push v1[dimension] - v2[dimension]

		return result

	scale: (v, scale) ->
		v.map (x) -> x*scale


class Canvas
	constructor: (id) ->
		@el		= document.getElementById(id)
		@width		= parseInt @el.getAttribute("width")
		@height		= parseInt @el.getAttribute("height")
		@context	= @el.getContext("2d")
		@clearColor	= "black"

	drawRect: (x, y, width, height, color) ->
		@context.fillStyle = color
		@context.fillRect(x, y, width, height)

	clear: ->
		@drawRect(0, 0, @width, @height, @clearColor)

# Using the pseudocode from http://www.vergenet.net/~conrad/boids/pseudocode.html
class Boid
	constructor: (pos) ->
		@position	= pos
		@velocity	= [5 + Math.random() * 5, 5 + Math.random() * 5]

	update: ->
		inverseFlockSize = 1.0 / (@flock.length - 1)

		c1 = [0, 0]
		v2 = [0, 0]
		v3 = [0, 0]

		# Everything we calculate over the flock
		for boid in @flock when boid isnt this
			# Rule 1
			c1 = vector.add(c1, boid.position)

			# Rule 2
			if vector.distanceSquared(@position, boid.position) < 250
				v2 = vector.subtract(v2, vector.subtract(position.position, @position))

			# Rule 3
			v3 = vector.add(v3, boid.velocity)

		# Rule 1 continued
		c1 = vector.scale(v1, inverseFlockSize)
		difference = vector.subtract(c1, @position)
		v1 = vector.scale(difference, 0.1)

		# Rule 3 continued
		v3 = vector.scaleBy(v3, inverseFlockSize)
		v3 = vector.scaleBy(vector.subtract(v3, @velocity), 0.125)

		@velocity	= vector.add(@velocity, v1, v2, v3)
		@position	= vector.add(@position, @velocity)

canvas = new Canvas("boids")
canvas.clearColor = "#202638"
canvas.clear()
