(function() {
    'use strict';

    jogLog.controller(
            'LogoutController', LogoutController);

    LogoutController.$inject = ['AuthService'];

    function LogoutController(AuthService) {
        (function initController() {
            // reset login status
            AuthService.clearCredentials();
        })();
    }
    
}());