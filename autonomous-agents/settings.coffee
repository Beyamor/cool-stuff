window.addSettingsPanel = (settings) ->
	$settings = $('#settings')

	$drawBoundingSphere = $('<input type="checkbox">')
	$drawBoundingSphere.change ->
		settings.drawBoundingSphere = $(this).is(':checked')
	$settings.append($drawBoundingSphere).append('Draw bounding sphere')
