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
window.Steerer = Steerer
