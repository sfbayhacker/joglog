(function () {

    'use strict';

    jogLog.controller(
            'EntryController', EntryController);

    EntryController.$inject = ['$location', '$scope', '$rootScope', '$window', 'EntryService', 'UserService', 'LayoutService'];

    function EntryController($location, $scope, $rootScope, $window, EntryService, UserService, LayoutService) {

        $scope.getAllEntries = getAllEntries;
        $scope.editEntry = editEntry;
        $scope.deleteEntry = deleteEntry;
        $scope.createEntry = createEntry;
        $scope.showNewEntryForm = showNewEntryForm;
        $scope.showEditEntryForm = showEditEntryForm;
        $scope.filterEntries = filterEntries;
        $scope.getWeeklySummary = getWeeklySummary;
        
        $scope.isAdmin = LayoutService.isAdmin;
        $scope.isUser = LayoutService.isUser;
        $scope.editingEntry = EntryService.editingEntry;
        $scope.entryUserForAdd = EntryService.entryUserForAdd;
        $scope.entryUserNameForAdd = EntryService.entryUserNameForAdd;
        
        (function() {
            getAllEntries();
            if ($scope.isUser()) {
                //getCalorieCount();
            }
        }());

        function getAllEntries() {
            console.log("EntryController::getAllEntries()");

            EntryService.getAllEntries(0, 0, function (response) {
                if (LayoutService.isAdmin()) {
                    $scope.entries = response.data[0]["content"];
                } else {
                    $scope.entries = response.data;
                }
            });
        }

        function filterEntries() {
            console.log("EntryController::filterEntries()");
            
            if (fromDate.value === '' && toDate.value === '') {
                $window.alert('Please enter criteria to filter.');
                return;
            }
            
            if ( 
                    (fromDate.value === '' && toDate.value !== '') ||
                    (fromDate.value !== '' && toDate.value === '')
                ) {
                $window.alert('Both from date and to date must be selected.');
                return;
            }
            
            var fd=null,td=null;
            
            if ( fromDate.value !== '' && toDate.value !== '' ) {
                fd = fromDate.value;
                td = toDate.value;
            }
            
            EntryService.getEntries(fd, td, 0, 0, function (response) {
                $scope.entries = response.data;
            });
        }

        function showNewEntryForm() {
            console.log('EntryController::showNewEntryForm()');
            
            $location.path('/addEntry');
        }

        function showEditEntryForm(id) {
            console.log('EntryController::showEditEntryForm()');
            
            EntryService.getEntry(id, function (response) {
                console.log('anon func :: ' + JSON.stringify(response.data));
                EntryService.editingEntry = response.data;
                $location.path('/editEntry');
            });
        }

        function createEntry() {
            console.log('EntryController::createEntry()');
            
            var user;
            
            if ($scope.isAdmin()) {
                user = userId.value;
            } else {
                console.log($rootScope.globals.currentUser);
                user = $rootScope.globals.currentUser.id;
            }
            
            console.log('user :: ' + user);
            
            var entry = {date: date.value, time: time.value, 
                distance: distance.value, user: user};
            
            EntryService.create(entry, function (response) {
                console.log('anon func :: ' + JSON.stringify(response));

                $location.path('/entries');
            });
        }
        
        function editEntry() {
            console.log('EntryController::editEntry()');
            
            console.log('entry id :: ' + eid.value);
            
            var entry = {id: eid.value, date: date.value, 
                time: time.value, distance: distance.value};
            
            EntryService.update(entry, function (response) {
                console.log('anon func :: ' + JSON.stringify(response));

                $location.path('/entries');
            });
        }
        
        function deleteEntry(id) {
            console.log('EntryController::deleteEntry()');
            
            var deleteEntry = $window.confirm('Are you sure you want to delete?');

            if (deleteEntry) {
                EntryService.del(id, function (response) {
                    getAllEntries();
                });
            }
        }
        
        function getWeeklySummary() {
            console.log("EntryController::getWeeklySummary()");
            console.log(weekStartDate.value);
            EntryService.getWeeklySummary(weekStartDate.value, function (response) {
                console.log(response.data);
                $scope.summary = response.data;
            });
        }
    }
}());
