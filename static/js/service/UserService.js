(function() {

'use strict';

angular.module('jogLog')
        .factory('UserService', UserService);

UserService.$inject = ['$http'];

function UserService($http) {
    var service = {};

    service.getAll = getAll;
    service.getAllUsers = getAllUsers;
    service.getUser = getUser;
    service.create = create;
    service.update = update;
    service.del = del;
    service.register = register;
    service.getUserByEmail = getUserByEmail;

    service.editingUser = {};

    return service;

    function getAll(page, size, callback) {
        console.log('UserService::getAll()');
        return $http({method: 'GET',url: '/api/users', headers: {page: page, size: size}})
                .then(callback, handleError('Error getting all users'));
    }

    function getAllUsers(page, size, callback) {
        console.log('UserService::getAllUsers()');
        return $http({method: 'GET', url: '/api/users', params: {field: 'role', value: 'USER'}, 
            headers: {page: page, size: size}})
                .then(callback, handleError('Error getting all users'));
    }

    function getUserByEmail(page, size, emailId, callback) {
        console.log('UserService::getUserByEmail()');
        return $http({method: 'GET', url: '/api/users', params: {field: 'email', value: emailId},
           headers: {page: page, size: size}})
           .then(callback, handleError('Error getting user'));
    }

    function getUser(id, callback) {
        console.log('UserService::getUser()');
        return $http({method: 'GET', url: '/api/users/' + id})
                .then(callback, handleError('Error getting user by id'));
    }

    function create(user, callback) {
        console.log('UserService::create()');
        console.log('user :: ' + JSON.stringify(user));
        return $http({method: 'POST', url: '/api/users', 
            headers: {email: user.email, name: user.name, password: user.password, role: user.role}})
                .then(callback, handleError('Error creating user'));
    }

    function update(user, callback) {
        console.log('UserService::update()');
        console.log('user :: ' + JSON.stringify(user));
        return $http({method: 'PUT', url: '/api/users/', 
            headers: {id: user.id, email: user.email, name: user.name, password: user.password, role: user.role}})
                .then(callback, handleError('Error updating user'));
    }

    function del(id, callback) {
        console.log('UserService::del()');
        return $http({method: 'DELETE', url: '/api/users/' + id}).then(callback, handleError('Error deleting user'));
    }

    function register(user, callback) {
        console.log('UserService::register()');
        return $http({method: 'POST', url: '/api/registration', 
            headers: {email: user.email, name: user.name, password: user.password}})
                .then(callback, handleError('Error registering user'));
    }
    
    // private functions

    function handleSuccess(res) {
        console.log('handleSuccess()');
        console.log(JSON.stringify(res.data));
        return res.data;
    }

    function handleError(error) {
        return function () {
            return {success: false, message: error};
        };
    }
}

}());