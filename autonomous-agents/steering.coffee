class Steerer
	constructor: (@settings) ->

	maxSpeed: ->
		@settings.forEntity.maxSpeed

	maxForce: ->
		@settings.forSteering.maxForce

class window.Seeker extends Steerer
	force: (entity, targetPos) ->
		toTarget	= targetPos.minus(entity.pos)
		desiredVelocity	= toTarget.normal().scaleBy(@maxSpeed())
		force		= desiredVelocity.minus(entity.vel)
		return force.clamp(@maxForce())

class window.Arriver extends Steerer
	force: (entity, targetPos) ->
		force		= new Vec2
		toTarget	= targetPos.minus(entity.pos)
		distance	= toTarget.length()

		if distance > 0
			speed		= Math.min(@maxSpeed(), distance)
			desiredVelocity	= toTarget.normal().scaleBy(speed)
			force		= desiredVelocity.minus(entity.vel)

		return force.clamp(@maxForce())
