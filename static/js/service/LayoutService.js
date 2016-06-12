(function () {

    'use strict';

    jogLog.factory('LayoutService', LayoutService);
    LayoutService.$inject = ['$rootScope'];

    function LayoutService($rootScope) {
        var service = {};
        service.isAdmin = isAdmin;
        service.isManager = isManager;
        service.isUser = isUser;
        
        return service;
        
        function isAdmin() {
            console.log("LayoutService::isAdmin()");
            
            if ($rootScope.globals.loggedIn) {
                if ($rootScope.globals.currentUser.role === 'ADMIN') {
                    console.log('returning true');
                    return true;
                }
            }
            
            return false;
        }

        function isManager() {
            console.log("LayoutService::isManager()");
            if ($rootScope.globals.loggedIn) {
                if ($rootScope.globals.currentUser.role === 'MANAGER') {
                    console.log('returning true');
                    return true;
                }
            }
        
            return false;
        }

        function isUser() {
            console.log("LayoutService::isUser()");
            if ($rootScope.globals.loggedIn) {
                if ($rootScope.globals.currentUser.role === 'USER') {
                    console.log('returning true');
                    return true;
                }
            }
            
            return false;
        }
    }

}());