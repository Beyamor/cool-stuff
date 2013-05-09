#
#	This is all super gross,
#	but I'm never going to have to maintain it so whatever
#
window.addSettingsPanel = (settings) ->
	$settings = $('#settings')

	$drawBoundingSphere = $('<input type="checkbox">')
	$drawBoundingSphere.change ->
		settings.drawBoundingSphere = $(this).is(':checked')
	$settings.append($drawBoundingSphere).append('Draw bounding sphere<br/>')

	$invMass = $('<input type="range" min="-1" max="2" step="0.25" value="1">')
	$invMass.change ->
		settings.forEntity.invMass = Math.pow(10, $(this).val())
	$settings.append('Inverse mass: 0.1').append($invMass).append('100<br/>')

	$maxSpeed = $('<input type="range" min="50" max="400" step="50" value="150">')
	$maxSpeed.change ->
		settings.forEntity.maxSpeed = $(this).val()
	$settings.append('Max speed: 50').append($maxSpeed).append('400<br/>')

	$maxForce = $('<input type="range" min="10" max="150" step="10" value="50">')
	$maxForce.change ->
		settings.forSteering.maxForce = $(this).val()
	$settings.append('Max force: 10').append($maxForce).append('150<br/>')

	$seekerSettings = $('<div id="seeker-settings"></div>')

	$seeker = $('<select></select>')
	$seeker.append("<option value\"#{option}\">#{option}</option>") for option in ['Seek', 'Arrive', 'Wander']
	$seeker.change ->
		$seekerSettings.empty()
		switch $(this).val()
			when 'Seek'
				settings.steerer = new Seeker(settings)

			when 'Arrive'
				settings.steerer = new Arriver(settings)

				$deccelerationControl = $('<input type="range" value="0" min="-1" max="1" step="0.2">')
				$deccelerationControl.change ->
					settings.forSteering.decceleration = Math.pow(10, $(this).val())
				$seekerSettings.append('Deceleration coefficient: 0.1').append($deccelerationControl).append('10<br/>')
				settings.forSteering.decceleration = 1

			when 'Wander'
				settings.steerer = new Wanderer(settings)

				$radiusControl = $('<input type="range" min="5" max="50" step="5" value="25">')
				$radiusControl.change ->
					settings.forSteering.wanderRadius = $(this).val()
				$seekerSettings.append('Wander radius: 5').append($radiusControl).append('50<br/>')
				settings.forSteering.wanderRadius = 25

				$distanceControl = $('<input type="range" min="5" max="100" step="5" value="50">')
				$distanceControl.change ->
					settings.forSteering.wanderDistance = $(this).val()
				$seekerSettings.append('Wander distance: 5' ).append($distanceControl).append('100<br/>')
				settings.forSteering.wanderDistance = 50

				$jitterControl = $('<input type="range" min="0.1" max="1.2" step="0.1" value="0.2">')
				$jitterControl.change ->
					seetings.forSteering.jitter = $(this).val()
				$seekerSettings.append('Jitter: 0.1' ).append($jitterControl).append('1.2<br/>')
				settings.forSteering.jitter = 0.2

	$settings.append('Steering: ').append($seeker).append('<br/>').append($seekerSettings)
