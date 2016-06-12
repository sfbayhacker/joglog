(function () {

    'use strict';

    jogLog.controller(
            'LayoutController', LayoutController);

    LayoutController.$inject = ['$scope', 'LayoutService'];

    function LayoutController($scope, LayoutService) {
        $scope.isAdmin = LayoutService.isAdmin;
        $scope.isManager = LayoutService.isManager;
        $scope.isUser = LayoutService.isUser;
    }

}());