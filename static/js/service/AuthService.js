(function () {

    'use strict';

    jogLog.factory('AuthService', AuthService);
    AuthService.$inject = ['$http', '$cookieStore', '$rootScope'];

    function AuthService($http, $cookieStore, $rootScope) {
        var service = {};
        service.login = login;
        service.setCredentials = setCredentials;
        service.clearCredentials = clearCredentials;

        return service;

        function login(email, password, callback) {
            console.log("AuthService::login()");

            $http({method: 'POST', url: '/auth', headers: {email: email, password: password}})
                    .success(function (response) {
                        callback(response);
                    });
        }

        function setCredentials(id, email, role, token) {
            console.log("AuthService::setCredentials()");
            console.log("authdata :: " + token);
            console.log("role :: " + role);
            $rootScope.globals = {
                currentUser: {
                    id: id,
                    email: email,
                    role: role,
                    authdata: token
                },
                loggedIn: true
            };
            $http.defaults.headers.common['Authorization'] = 'Bearer ' + token; // jshint ignore:line
            $cookieStore.put('jogLog.globals', $rootScope.globals);
        }

        function clearCredentials() {
            console.log("AuthService::setCredentials()");
            $rootScope.globals = {loggedIn: false};
            $cookieStore.remove('globals');
            $http.defaults.headers.common.Authorization = 'Bearer';
        }
    }

}());