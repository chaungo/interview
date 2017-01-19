app.controller('UserStorySettingsController', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.gadgetId = null;
    $scope.usProject = null;
    $scope.usProjectOptions = [];
    $scope.usRelease = null;
    $scope.usReleaseOptions = [];
    $scope.usProduct = null;
    $scope.usProductOptions = null;
    $scope.showEpic = true;
    $scope.showStory = true;
    $scope.usEpicAvailable = [];
    $scope.usEpic = [];
    $scope.usEpicAvailableOptions = [];
    $scope.usEpicOptions = [];
    $scope.usMultiSelect = [];
    $scope.usMultiSelectOptions = [];
    $scope.showEpicLoader = false;
    $scope.showStoryLoader = false;
    $scope.productPage = "configuration";

    $scope.onProjectReleaseProductChanged = function () {
        if ($scope.checkSettings()) {
            $scope.getEpicsAngular(null);
        }

    }

    $scope.onAddEpic = function () {
        angular.forEach($scope.usEpicAvailable, function (item) {
            var index = $scope.usEpicAvailableOptions.indexOf(item);
            if (index != -1) {
                $scope.usEpicAvailableOptions.splice(index, 1);
                $scope.usEpicOptions.push(item);
            }
        });
        $scope.reloadStoryList(null);
    }

    $scope.onRemoveEpic = function () {
        angular.forEach($scope.usEpic, function (item) {
            var index = $scope.usEpicOptions.indexOf(item);
            if (index != -1) {
                $scope.usEpicOptions.splice(index, 1);
                $scope.usEpicAvailableOptions.push(item);
            }
        });
        $scope.reloadStoryList(null);
    }

    $scope.onAddAllEpic = function () {
        angular.forEach($scope.usEpicAvailableOptions, function (item) {
            $scope.usEpicOptions.push(item);
        });
        $scope.usEpicAvailableOptions.length = 0;
        $scope.reloadStoryList(null);
    }

    $scope.onRemoveAllEpic = function () {
        angular.forEach($scope.usEpicOptions, function (item) {
            $scope.usEpicAvailableOptions.push(item);
        });
        $scope.usEpicOptions.length = 0;
        $scope.usMultiSelectOptions.length = 0;
    }

    $scope.onUpdate = function () {
        if (!$scope.isUpdateReady()) {
            return;
        }
        var object = {};
        object["id"] = $scope.gadgetId;
        object["dashboardId"] = $rootScope.currentDashboard.id;
        object["projectName"] = $scope.usProject;
        object["release"] = $scope.usRelease;
        object["products"] = [$scope.usProduct];
        object["metrics"] = $scope.usMetricMultiSelect;
        if ($scope.showEpic === false) {
            object["epic"] = $scope.usEpicOptions;
        }
        else {
            object["selectAllEpic"] = true;
        }

        if ($scope.showStory === false) {
            object["stories"] = $scope.usMultiSelect;
        }
        else {
            object["selectAllStory"] = true;
        }

        var callback = function (result) {
            if (result.type == "SUCCESS") {
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Gadget updated succesfully')
                        .hideDelay(5000)
                );
            }
            if (result.type != SUCCESS) {
                console.log(result);
                $rootScope.debugAjaxAngular(result);
            } else {
                $mdDialog.cancel();
                $rootScope.showGadget();
            }
        }

        saveGadgetSettings("STORY_TEST_EXECUTION", JSON.stringify(object), callback);
    }

    $scope.reloadStoryList = function (callback) {
        if ($scope.usEpicOptions == undefined || $scope.usEpicOptions == null || $scope.usEpicOptions.length == 0) {
            return;
        }
        else {
            $.ajax({
                url: GET_STORY_URI,
                data: {
                    epics: JSON.stringify($scope.usEpicOptions)
                },
                beforeSend: function () {
                    $scope.showStoryLoader = true;
                },
                error: function (res) {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent('Server error')
                            .hideDelay(5000)
                    );
                    $scope.showStoryLoader = false;
                    $scope.$apply();
                },
                success: function (res) {
                    if ($rootScope.debugAjaxAngular(res)) {
                        return;
                    }

                    else {
                        $scope.usMultiSelectOptions.length = 0;
                        angular.forEach(res, function (item) {
                            angular.forEach(item, function (item1) {
                                $scope.usMultiSelectOptions.push(item1);
                            });

                        });
                    }
                    $scope.showStoryLoader = false;
                    if (callback != null) {
                        callback();
                    }
                    $scope.$apply();
                }
            });
        }
    }

    $scope.checkSettings = function () {
        if ($scope.usProject != null && $scope.usRelease != null && $scope.usProduct != null) {
            if ($scope.usProject != "" && $scope.usRelease != "" && $scope.usProduct != "") {
                return true;
            }
        }
        return false;
    }

    $scope.getEpicsAngular = function (callback) {
        $.ajax({
            url: GET_EPIC_URI,
            data: {
                project: $scope.usProject,
                release: $scope.usRelease,
                product: $scope.usProduct
            },
            beforeSend: function () {
                $scope.showEpicLoader = true;
            },
            error: function (res) {
                $mdToast.show($mdToast.simple().textContent('Server error')
                    .hideDelay(5000));
                $scope.showEpicLoader = false;
                $scope.$apply();
            },
            success: function (res) {
                if ($rootScope.debugAjaxAngular(res)) {
                    return;
                }

                else {
                    console.log(res);
                    $scope.usEpicAvailableOptions = res;
                    $scope.usEpicOptions.length = 0;
                }
                $scope.showEpicLoader = false;
                if (callback != null) {
                    callback();
                }
                $scope.$apply();
            }
        });
    }

    $scope.init = function () {
        $scope.getProjects();
        $scope.getRelease();
        $scope.getProducts();
        var item = $rootScope.gadgetToEdit;

        if (item != null) {
            var reloadStoryHandler = function () {
                $scope.usMultiSelect = item.stories;
            }
            var getEpicHandler = function () {
                angular.forEach(item.epic, function (e) {
                    var index = $scope.usEpicAvailableOptions.indexOf(e);
                    if (index != -1) {
                        $scope.usEpicAvailableOptions.splice(index, 1);
                    }
                });
                if (!item.selectAllEpic) {
                    $scope.usEpicOptions = item.epic;
                }
                $scope.reloadStoryList(reloadStoryHandler);
            }

            if (item.type == "STORY_TEST_EXECUTION") {
                $scope.gadgetId = item.id;
                $scope.usProject = item.projectName;
                $scope.usRelease = item.release;
                $scope.usProduct = item.products[0];
                $scope.getEpicsAngular(getEpicHandler);
                $scope.showEpic = item.selectAllEpic;
                $scope.showStory = item.selectAllStory;
                $scope.usMetricMultiSelect = item.metrics;
            }
            $rootScope.gadgetToEdit = null;
        }
    }

    $scope.getProjects = function () {
        $resource(GET_PROJECTS_URI, {
            query: {
                method: 'GET',
                isArray: true
            }
        }).query().$promise.then(function (res) {
            if ($rootScope.debugAjaxAngular(res)) {
                return;
            }
            else {
                $scope.usProjectOptions = res;
                $scope.usProject = "FNMS 557x";
            }

        }, function (error) {
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Server error')
                    .hideDelay(5000)
            );
        });
    }

    $scope.getRelease = function () {
        $.ajax({
            url: GET_RELEASE_URI,
            beforeSend: function () {

            },
            error: function (res) {
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Server error')
                        .hideDelay(5000)
                );
            },
            success: function (res) {
                if ($rootScope.debugAjaxAngular(res)) {
                    return;
                }
                else {
                    $scope.usReleaseOptions = res['data'];
                }
            }
        });
    }

    $scope.getProducts = function () {
        $.ajax({
            url: GET_PRODUCT_URI,
            beforeSend: function () {

            },
            error: function (res) {
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Server error')
                        .hideDelay(5000)
                );
            },
            success: function (res) {
                if ($rootScope.debugAjaxAngular(res)) {
                    return;
                }
                else {
                    $scope.usProductOptions = res['data'];
                }
            }
        });
    }

    function showError(message) {
        $mdToast.show(
            $mdToast.simple()
                .textContent(message)
                .hideDelay(5000)
        );
    }

    $scope.isUpdateReady = function () {
        if ($scope.usProject == null) {
            showError("No project selected");
            return false;
        }
        else if ($scope.usRelease == null) {
            showError("No release selected");
            return false;
        }
        else if ($scope.usProduct == null) {
            showError("No product selected");
            return false;
        }
        else if ($scope.usEpicOptions == null && $scope.showEpic == false) {
            showError("No epic selected");
            return false;
        }
        else if ($scope.usMultiSelect == null && $scope.showStory == false) {
            showError("No user story selected");
            return false;
        }
        else if ($scope.usMetricMultiSelect == null) {
            showError("No metric selected");
            return false;
        }
        return true;
    }

    $scope.cancel = function () {
        $mdDialog.cancel();
    }
});