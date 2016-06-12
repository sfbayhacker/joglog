(function () {

    'use strict';

    jogLog.factory('HomePageService', HomePageService);
    HomePageService.$inject = ['$rootScope'];

    function HomePageService($rootScope) {
        var service = {};
        service.getHomePath = getHomePath;
        
        return service;
        
        function getHomePath() {
            console.log('getHomePath()');
            if ($rootScope.globals.loggedIn) {
                var role = $rootScope.globals.currentUser.role;
                if (role==='ADMIN' || role==='USER') {
                    console.log('returning entries');
                    return '/entries';
                } else if(role==='MANAGER') {
                    console.log('returning users');
                    return '/users';
                }
            }
            
            return '/error';
        }
    }

}());