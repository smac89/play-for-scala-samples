$(document).ready (function() {
    var $success = $("#success-alert");
    var $error = $("#error-alert");

    fadeAlert($success);
    fadeAlert($error);

    function fadeAlert($elem) {
        if ($elem.is(':visible')) {
            $elem.fadeTo(5000, 500).slideUp(500, function(){
                $elem.slideUp(500);
            });
        }
    }
});
