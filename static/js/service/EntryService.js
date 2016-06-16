(function() {

'use strict';

angular.module('jogLog')
        .factory('EntryService', EntryService);

EntryService.$inject = ['$http', '$rootScope'];

function EntryService($http, $rootScope) {
    var service = {};

    service.getAll = getAll;
    service.getAllEntries = getAllEntries;
    service.getEntries = getEntries;
    service.getEntry = getEntry;
    service.create = create;
    service.update = update;
    service.del = del;
    service.getWeeklySummary = getWeeklySummary;

    service.editingEntry = {};
    service.entryUserForAdd = -1;
    service.entryUserNameForAdd = '';

    return service;

    function getAll(page, size, callback) {
        console.log('EntryService::getAll()');
        return $http({method: 'GET',url: '/api/entries', headers: {page: page, size: size}})
                .then(callback, handleError('Error getting all entries'));
    }

    function getAllEntries(page, size, callback) {
        console.log('EntryService::getAllEntries()');
        return $http({method: 'GET', url: '/api/entries', 
            headers: {user: $rootScope.globals.currentUser.id, page: page, size: size}})
                .then(callback, handleError('Error getting all entries'));
    }

    function getEntries(fromDate, toDate, page, size, callback) {
        console.log('EntryService::getEntries()');
        
        if (fromDate === null || toDate === null) {
            return $http({method: 'GET', url: '/api/entries', 
                headers: {user: $rootScope.globals.currentUser.id, page: page, size: size}})
                        .then(callback, handleError('Error getting filtered entries'));
        } else {
            return $http({method: 'GET', url: '/api/entries', 
                headers: {user: $rootScope.globals.currentUser.id, page: page, size: size, 
                    fromDate: fromDate, toDate: toDate}})
                        .then(callback, handleError('Error getting filtered entries'));
        }
    }

    function getEntry(id, callback) {
        console.log('EntryService::getEntry()');
        return $http({method: 'GET', url: '/api/entries/' + id})
                .then(callback, handleError('Error getting entries by id'));
    }

    function getWeeklySummary(weekStartDate, callback) {
        console.log('EntryService::getWeeklySummary()');
        return $http({method: 'GET', url: '/api/entries/summary',
                params: {user: $rootScope.globals.currentUser.id, weekStartDate: weekStartDate}})
                .then(callback, handleError('Error getting weekly summary'));
    }

    function create(entry, callback) {
        console.log('EntryService::create()');
        console.log('entry :: ' + JSON.stringify(entry));
        return $http({method: 'POST', url: '/api/entries', 
            headers: {entryDate: entry.date, time: entry.time, distance: entry.distance, user: entry.user}})
                .then(callback, handleError('Error creating entry'));
    }

    function update(entry, callback) {
        console.log('EntryService::update()');
        console.log('entry :: ' + JSON.stringify(entry));
        return $http({method: 'PUT', url: '/api/entries/', 
            headers: {id: entry.id, entryDate: entry.date, time: entry.time, distance: entry.distance}})
                .then(callback, handleError('Error updating entry'));
    }

    function del(id, callback) {
        console.log('EntryService::del()');
        return $http({method: 'DELETE', url: '/api/entries/' + id})
                .then(callback, handleError('Error deleting entry'));
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