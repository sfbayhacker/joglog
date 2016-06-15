(function () {

    'use strict';

    jogLog.controller(
            'UserController', UserController);

    UserController.$inject = ['$location', '$scope', '$rootScope', '$window', 'UserService', 'EntryService', 'LayoutService'];

    function UserController($location, $scope, $rootScope, $window, UserService, EntryService, LayoutService) {

        $scope.getAllUsers = getAllUsers;
        $scope.editUser = editUser;
        $scope.deleteUser = deleteUser;
        $scope.createUser = createUser;
        $scope.showNewUserForm = showNewUserForm;
        $scope.showEditUserForm = showEditUserForm;
        $scope.showNewEntryFormForUser = showNewEntryFormForUser;
        $scope.checkUnique = checkUnique
        $scope.checkUniqueEdit = checkUniqueEdit
        $scope.isEmailTaken = isEmailTaken
        $scope.isNotCurrentUser = isNotCurrentUser;
        $scope.emailError = false
        $scope.oldEmail = UserService.oldEmail;

        $scope.isAdmin = LayoutService.isAdmin;
        $scope.isManager = LayoutService.isManager;
        $scope.editingUser = UserService.editingUser;
        $scope.loggedInUser = $rootScope.globals.currentUser.id;

        (function() {
            if ($scope.isManager() || $scope.isAdmin()) {
                getAllUsers();
            }
        }());
        
        function checkUnique() {
            $scope.emailError = false
            UserService.getUserByEmail(0, 0, email.value, function (response) {
                console.log('anon func :: ' + JSON.stringify(response.data));
                if (response.data[0] != null) {
                    $scope.emailError = true
                    $location.path('/addU');
                }
            });
        }
        function checkUniqueEdit() {
            $scope.emailError = false
            if (email.value !== $scope.oldEmail) {
                UserService.getUserByEmail(0, 0, email.value, function (response) {
                console.log('anon func :: ' + JSON.stringify(response.data));
                if (response.data[0] != null) {
                    $scope.emailError = true
                    $location.path('/editU');
                }
            });
            }
        }
        
        function isEmailTaken() {
            return $scope.emailError
        }

        function getAllUsers() {
            console.log("UserController::getAllUsers()");

            if(LayoutService.isAdmin()) {
                UserService.getAll(0, 0, function (response) {
                    $scope.users = response.data[0]["content"];
                });
            } else {
                UserService.getAllUsers(0, 0, function (response) {
                    $scope.users = response.data;
                });
            }
        }

        function showNewUserForm() {
            console.log('UserController::showNewUserForm()');
            
            $location.path('/addU');
        }

        function showEditUserForm(id) {
            console.log('UserController::showEditUserForm()');
            
            UserService.getUser(id, function (response) {
                console.log('anon func :: ' + JSON.stringify(response.data));
                UserService.editingUser = response.data;
                UserService.oldEmail = UserService.editingUser.email;
                $location.path('/editU');
            });
        }

        function createUser() {
            console.log('UserController::createUser()');
            var role = 'ROLE_USER'
            if (typeof srole !== 'undefined') {
                role =  srole.options[srole.selectedIndex].value;
            }
            var user = {email: email.value, password: password.value, name: uname.value, role: role};
            
            UserService.create(user, function (response) {
                console.log('anon func :: ' + JSON.stringify(response));

                $location.path('/users');
            });
        }
        
        function editUser() {
            console.log('UserController::editUser()');
            
            console.log('user id :: ' + uid.value);
            var role = 'ROLE_USER'
            if (typeof srole !== 'undefined') {
                role =  srole.options[srole.selectedIndex].value;
            }
            var user = {id: uid.value, email: email.value, password: password.value, name: uname.value, role: role};
            
            UserService.update(user, function (response) {
                console.log('anon func :: ' + JSON.stringify(response));

                $location.path('/users');
            });
        }
        
        function deleteUser(id) {
            console.log('UserController::deleteUser()');
            
            var deleteUser = $window.confirm('Are you sure you want to delete?');

            if (deleteUser) {
                UserService.del(id, function (response) {
                    getAllUsers();
                });
            }
        }
        
        function showNewEntryFormForUser(userId, userName) {
            console.log('UserController::showNewEntryForm()');
            
            EntryService.entryUserForAdd = userId;
            EntryService.entryUserNameForAdd = userName;
            $location.path('/addEntry');
        }
        
        function isNotCurrentUser(userId) {
            return (userId !== $rootScope.globals.currentUser.id);
        } 
    }
}());
