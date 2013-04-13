# Vector operations
vector = {
	add: (vs...) ->
		result = []

		for dimension in [0...vs[0].length]
			sum = 0
			for v in vs
				sum += v[dimension]
			result.push sum

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
		return v.map((x) -> x*scale)

	direction: (v) ->
		return Math.atan2(v[1], v[0])
}

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

	drawTriangle: ([x1, y1], [x2, y2], [x3, y3], color) ->
		@context.beginPath()
		@context.moveTo(x1, y1)
		@context.lineTo(x2, y2)
		@context.lineTo(x3, y3)
		@context.lineTo(x1, y1)
		@context.fillStyle = color
		@context.fill()

	clear: ->
		@drawRect(0, 0, @width, @height, @clearColor)

random = -> Math.random()
random.posOrNeg = -> random() < 0.5
random.inRange = (min, max) -> min + random() * (max - min)

# Using the pseudocode from http://www.vergenet.net/~conrad/boids/pseudocode.html
class Boid
	constructor: (@flock, @position, @color) ->
		vx		= random.inRange(5, 10) * random.posOrNeg()
		vy		= random.inRange(5, 10) * random.posOrNeg()
		@velocity	= [vx, vy]

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

	draw: (canvas) ->
		[x, y] = @position

		direction	= vector.direction(@velocity)
		tailDirection1	= direction + Math.PI * 0.75
		tailDirection2	= direction - Math.PI * 0.75

		p1 = vector.add(@position, [Math.cos(direction) * 10, Math.sin(direction) * 10])
		p2 = vector.add(@position, [Math.cos(tailDirection1) * 5, Math.sin(tailDirection1) * 5])
		p3 = vector.add(@position, [Math.cos(tailDirection2) * 5, Math.sin(tailDirection2) * 5])

		canvas.drawTriangle(p1, p2, p3, @color)

canvas = new Canvas("boids")
canvas.clearColor = "#202638"
canvas.clear()

boidColors = ["#61E6E8", "#E8C061", "#F2C2E0"]

flock = []
for i in [0..Math.floor(20 + 20 * Math.random())]
	pos	= [Math.random() * canvas.width, Math.random() * canvas.height]
	color	= boidColors[i % boidColors.length]
	flock.push new Boid(flock, pos, color)

for boid in flock
	boid.draw canvas
