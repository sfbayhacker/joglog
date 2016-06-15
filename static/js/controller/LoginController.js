(function () {

    'use strict';

    jogLog.controller(
            'LoginController', LoginController);

    LoginController.$inject = ['$location', 'AuthService', '$scope', 'HomePageService'];

    function LoginController($location, AuthService, $scope, HomePageService) {

        $scope.login = login;

        (function initController() {
            // reset login status
            AuthService.clearCredentials();
        })();

        function login() {
            console.log("LoginController::login()");
            AuthService.login(email.value, password.value, function (response) {
                console.log("authService.login() response :: " + response);
                if (response) {
                    AuthService.setCredentials(response.user.id, email.value, 
                        response.user.role.id, response.token);
                    
                    var path = HomePageService.getHomePath();
                    console.log('path :: ' + path);
                    $location.path(path);
                } else {
                    console.log('Error: ' + response.message);
                }
            });
        }

    }

}());
