'use strict';

var jogLog = angular.module('jogLog', ['ngRoute', 'ngCookies', 'angularUtils.directives.dirPagination']);

jogLog.run(function ($rootScope) {
    $rootScope.globals = {loggedIn: false};
});

var isLoggedIn = function ($location, $rootScope, $q) {
    var deferred = $q.defer();

    if ( $rootScope.globals.loggedIn) {
        deferred.resolve();
    } else {
        deferred.reject();
        $location.url('/');
    }
    return deferred.promise;
};

var isAdminOrUser = function ($location, $rootScope, $q) {
    var deferred = $q.defer();

    if ( $rootScope.globals.loggedIn && 
        ($rootScope.globals.currentUser.role === 'ROLE_USER' ||
        $rootScope.globals.currentUser.role === 'ROLE_ADMIN')) {
        deferred.resolve();
    } else {
        deferred.reject();
        $location.url('/');
    }
    return deferred.promise;
};

var isAdminOrManager = function ($location, $rootScope, $q) {
    var deferred = $q.defer();

    if ( $rootScope.globals.loggedIn && 
        ($rootScope.globals.currentUser.role === 'ROLE_ADMIN' ||
        $rootScope.globals.currentUser.role === 'ROLE_MANAGER')) {
        deferred.resolve();
    } else {
        deferred.reject();
        $location.url('/');
    }
    return deferred.promise;
};

var isUser = function ($location, $rootScope, $q) {
    var deferred = $q.defer();

    if ( $rootScope.globals.loggedIn && 
        $rootScope.globals.currentUser.role === 'ROLE_USER') {
        deferred.resolve();
    } else {
        deferred.reject();
        $location.url('/');
    }
    return deferred.promise;
};

// configure our routes
jogLog.config(function ($routeProvider) {
    $routeProvider
        // route for the login page
        .when('/', {
            templateUrl: '/views/login.html',
            controller: 'LoginController'
        })

        // route for the register page
        .when('/register', {
            templateUrl: '/views/register.html',
            controller: 'RegisterController'
        })

        // route for the entries page
        .when('/entries', {
            templateUrl: '/views/entries.html',
            controller: 'EntryController',
            resolve: {loggedIn:isAdminOrUser}
        })

        // route for the add entry page
        .when('/addEntry', {
            templateUrl: '/views/addEntry.html',
            controller: 'EntryController',
            resolve: {loggedIn:isAdminOrUser}
        })

        // route for the edit entry page
        .when('/editEntry', {
            templateUrl: '/views/editEntry.html',
            controller: 'EntryController',
            resolve: {loggedIn:isAdminOrUser}
        })

        // route for the users page
        .when('/users', {
            templateUrl: '/views/users.html',
            controller: 'UserController',
            resolve: {loggedIn:isAdminOrManager}
        })

        // route for the add user page
        .when('/addU', {
            templateUrl: '/views/addU.html',
            controller: 'UserController',
            resolve: {loggedIn:isAdminOrManager}
        })

        // route for the edit user page
        .when('/editU', {
            templateUrl: '/views/editU.html',
            controller: 'UserController',
            resolve: {loggedIn:isAdminOrManager}
        })

        // route for the settings page
        .when('/settings', {
            templateUrl: '/views/settings.html',
            controller: 'UserController',
            resolve: {loggedIn:isLoggedIn}
       })

        // route for the logout page
        .when('/logout', {
            templateUrl: '/views/login.html',
            controller: 'LogoutController',
            resolve: {loggedIn:isLoggedIn}
        });
});
