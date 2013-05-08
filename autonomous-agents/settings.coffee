window.addSettingsPanel = (settings) ->
	$settings = $('#settings')

	$drawBoundingSphere = $('<input type="checkbox">')
	$drawBoundingSphere.change ->
		settings.drawBoundingSphere = $(this).is(':checked')
	$settings.append($drawBoundingSphere).append('Draw bounding sphere<br/>')

	$invMass = $('<input type="range" min="-1" max="2" step="0.25" value="1">')
	$invMass.change ->
		settings.entity.invMass = Math.pow(10, $(this).val())
	$settings.append('Inverse mass: ').append($invMass).append('<br/>')
