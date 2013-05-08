class Steerer
	constructor: (@game, @settings, @entity) ->
		@isOn = {
			seek:	false
			arrive:	true
		}

	force: ->
		force		= new Vec2
		targetPos	= @game.mousePos
		toTarget	= targetPos.minus(@entity.pos)
		maxSpeed	= @settings.forEntity.maxSpeed
		maxForce	= @settings.forSteering.maxForce

		# Seek
		if @isOn["seek"]
			desiredVelocity	= toTarget.normal().scaleBy(maxSpeed)
			force		= desiredVelocity.minus(@entity.vel)

		# Arrive
		if @isOn["arrive"]
			distance	= toTarget.length()

			if distance > 0
				speed		= Math.min(maxSpeed, distance)
				desiredVelocity	= toTarget.normal().scaleBy(speed)
				force		= desiredVelocity.minus(@entity.vel)

		return force.clamp(maxForce)
window.Steerer = Steerer
