(function() {
    'use strict';

    jogLog.controller(
            'RegisterController', RegisterController);

    RegisterController.$inject = ['$location', '$scope', 'UserService', 'AuthService', 'HomePageService'];

    function RegisterController($location, $scope, UserService, AuthService, HomePageService) {
        $scope.register = register;
        
        function register() {
            console.log("register()");
            var user = {email: email.value, name: uname.value, password: password.value};
            
            UserService.register(user, function (response) {
                console.log('anon func :: ' + JSON.stringify(response));
                user = response.data;
                console.log('user :: ' + user);
                AuthService.setCredentials(user.id, user.email, user.password, user.role.id);

                $location.path('#/entries');
            });

        }
        
        function checkEmail() {
            console.log("checkEmail()");
            var emailId = email.value;
            
            UserService.getUserByEmail(emailId, function (response) {
                console.log('anon func :: ' + JSON.stringify(response));
                user = response.data[0];
                if (user != null) {
                    console.log("ERROR: email is already taken")
                }
            });

        }
    }
    
}());