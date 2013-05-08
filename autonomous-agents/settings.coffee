window.addSettingsPanel = (settings) ->
	$settings = $('#settings')

	$drawBoundingSphere = $('<input type="checkbox">')
	$drawBoundingSphere.change ->
		settings.drawBoundingSphere = $(this).is(':checked')
	$settings.append($drawBoundingSphere).append('Draw bounding sphere<br/>')

	$invMass = $('<input type="range" min="-1" max="2" step="0.25" value="1">')
	$invMass.change ->
		settings.forEntity.invMass = Math.pow(10, $(this).val())
	$settings.append('Inverse mass: ').append($invMass).append('<br/>')

	$maxSpeed = $('<input type="range" min="50" max="400" step="50" value="150">')
	$maxSpeed.change ->
		settings.forEntity.maxSpeed = $(this).val()
	$settings.append('Max speed: ').append($maxSpeed).append('<br/>')

	$maxForce = $('<input type="range" min="10" max="150" step="10" value="50">')
	$maxForce.change ->
		settings.forSteering.maxForce = $(this).val()
	$settings.append('Max force: ').append($maxForce).append('<br/>')

	$seeker = $('<select></select>')
	$seeker.append("<option value\"#{option}\">#{option}</option>") for option in ['Seek', 'Arrive']
	$seeker.change ->
		settings.steerer =\
			switch $(this).val()
				when 'Seek' then new Seeker settings
				when 'Arrive' then new Arriver settings
	$settings.append('Steering: ').append($seeker).append('<br/>')
