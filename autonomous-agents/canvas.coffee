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
window.Canvas = Canvas
