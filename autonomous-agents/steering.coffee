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
		return force

class window.Arriver extends Steerer
	force: (entity, targetPos) ->
		force		= new Vec2
		toTarget	= targetPos.minus(entity.pos)
		distance	= toTarget.length()

		if distance > 0
			speed		= Math.min(@maxSpeed(), distance * @settings.forSteering.decceleration)
			desiredVelocity	= toTarget.normal().scaleBy(speed)
			force		= desiredVelocity.minus(entity.vel)

		return force

class window.Wanderer extends Steerer
	constructor: (@settings) ->
		@angle = 0

	force: (entity, targetPos) ->
		wanderRadius	= @settings.forSteering.wanderRadius
		wanderDistance	= @settings.forSteering.wanderDistance
		jitter		= @settings.forSteering.jitter

		@angle += jitter * Math.random() * (if Math.random() < 0.5 then -1 else 1)

		destination	= new Vec2(wanderDistance + Math.cos(@angle) * wanderRadius, Math.sin(@angle) * wanderRadius)
		destination	= destination.rotate(entity.vel.direction()).plus(entity.pos)

		force		= destination.minus(entity.pos)
		return force
