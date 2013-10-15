$(function() {

	var	$app = $('#app'),
		$submit,
		$input,
		$receivedMessage;

	disableSubmit = function() {
		$submit.prop('disabled', ($input.val().length == 0));
	};

	write = function($el, message, callback) {

		var	pos = 0,
			interval,
			writeChar;

		writeChar = function() {

			var	read = message.charAt(pos),
				write;

			switch (read) {

				case "\n":
					write = "<br/>";
					break;

				case "\r":
					write = "";
					break;

				case " ":
					write = "&nbsp;";
					break;

				case "<":
					write = "&lt;";
					break;

				case ">":
					write = "&gt;";
					break;

				case "&":
					write = "&amp;";
					break;

				default:
					write = read;
			}

			$el.html($el.html() + write);
			++pos;

			$el.scrollTop($el[0].scrollHeight);
		};

		function nextStep() {

			clearInterval(interval);
			if (pos < message.length) {

				writeChar();
				interval = setInterval(nextStep, 50);
			}

			else {
				if (callback) callback();
			}
		};
		interval = setInterval(nextStep, 50);
	};

	receiveMessage = function(message) {

		$receivedMessage.html('');
		write($receivedMessage, message, function() {

			$input.val('').fadeIn();
			$submit.fadeIn();
		});
	};

	$receivedMessage = $('<div>').attr('id', 'received-message');
	$app.append($receivedMessage);

	$input = $('<textarea>').attr('id', 'input');
	$input.keypress(disableSubmit).keydown(disableSubmit).keyup(disableSubmit).change(disableSubmit);
	$app.append($input);

	$submit = $('<button type="button">').attr('id', 'submit').text('send').prop('disabled', true);
	$submit.click(function() {
		var message = $input.val();

		$submit.prop('disabled', true).fadeOut();
		$input.fadeOut();

		$.ajax({
			url: "/projects/message-in-a-bottle/submit",
			data: {
				message: message
			},
			success: receiveMessage,
			failure: function(response) {
				$('body').text('error: ' + response );
			}
		});
	});
	$app.append($submit);
});
