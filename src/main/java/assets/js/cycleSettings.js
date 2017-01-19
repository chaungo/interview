var SUCCESS = "SUCCESS";
function getGreenHopperProjectList(callBack) {
    $.get("/listproject", function (data) {
        callBack(data);
    });
}
function getGreenHopperProduct(callBack) {
    $.get("/product/getall", function (data) {
        callBack(data);
    });
}
function getGreenHopperRelease(callBack) {
    $.get("/listRelease", function (data) {
        callBack(data);
    });
}

function verifyValue(arrayArgument) {
    var verify = true;
    $.each(arrayArgument, function (index, value) {
        verify &= (value != null && value != "");
    });
    return verify;
}

app.controller('CycleSettingController', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.gadgetId = null;
    $scope.greenHopperProjectList = [];
    $scope.greenHopperProduct = [];
    $scope.greenHopperRelease = [];
    $scope.selectedProduct = null;
    $scope.selectedProject = null;
    $scope.selectedRelease = null;
    $scope.greenHopperCycleLink = [];
    $scope.selectAllCycle = true;
    $scope.cancel = function () {
        $mdDialog.cancel();
    }
    $scope.productPage = "configuration";
    $scope.init = function () {
        var item;
        var callBack = function (result) {
            if (result.type == null) {
                $scope.greenHopperProjectList = result;
                $scope.$apply();
            } else {
                console.log(result);
                showError(result.data);
            }
        }
        var callBackProduct = function (result) {
            if (result.type == SUCCESS) {
                $scope.greenHopperProduct = result.data;
                $scope.$apply();
            } else {
                console.log(result);
                showError(result.data);
            }

        }
        var callBackRelease = function (result) {
            if (result.type == SUCCESS) {
                $scope.greenHopperRelease = result.data;
                $scope.$apply();
            } else {
                console.log(result);
                showError(result.data);
            }
        }

        getGreenHopperProjectList(callBack);
        getGreenHopperRelease(callBackRelease);
        getGreenHopperProduct(callBackProduct);

        item = $rootScope.gadgetToEdit;
        if (item != null) {
            if (item.type == "TEST_CYCLE_TEST_EXECUTION") {
                $scope.gadgetId = item.id;
                $scope.selectedProject = item.projectName;
                $scope.selectedRelease = item.release;
                $scope.selectedProduct = item.products[0];
                $scope.selectAllCycle = item.selectAllCycle;
                $scope.selectedCycleLink = item.cycles;
                $scope.selectedMetric = item.metrics;

            }
            $rootScope.gadgetToEdit = null;
        }
    }

    $scope.onCheckAllCycle = function () {

        var cycleCycle = $("#cycleCycle");
        var cycleCheckAllCycle = $("#cycleCheckAllCycle").prop('checked');
        if (cycleCheckAllCycle) {
            cycleCycle.css("display", "none");
        } else {
            $scope.onProjectReleaseProductChanged();
            cycleCycle.css("display", "");
        }
    }

    $scope.onProjectReleaseProductChanged = function () {
    }


    $scope.isDisabled = false;

    $scope.saveGadget = function () {
        var cycleProjectVal = $("#cycleProject").val();
        var cycleProductVal = $("#cycleProduct").val();
        var cycleReleaseVal = $("#cycleRelease").val();
        var metricsVal = $("#cycleMetricMultiSelect").val();
        var isNotEmpty = true;
        var cycleCheckAllCycleVal = true
        isNotEmpty &= (metricsVal != null && metricsVal.length > 0);
        if (cycleCheckAllCycleVal) {
            isNotEmpty &= verifyValue([cycleProjectVal, cycleProductVal, cycleReleaseVal]);
        } else {
            isNotEmpty &= (cycleCycle != null && cycleCycle.length > 0);
        }

        if (isNotEmpty && $rootScope.currentDashboard != null) {
            var object = {};
            object['id'] = $scope.gadgetId;
            var dashboardId = $rootScope.currentDashboard.id;
            object['dashboardId'] = dashboardId;
            object['release'] = cycleReleaseVal;
            object['projectName'] = cycleProjectVal;
            object['products'] = [cycleProductVal];
            object['metrics'] = metricsVal;
            if (cycleCheckAllCycleVal) {
                object['selectAllCycle'] = true;
            }
            var jsonObj = JSON.stringify(object);
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
                    showError(result.data);
                } else {

                    $scope.cancel();
                    $rootScope.showGadget();
                }
            }

            saveGadgetSettings('TEST_CYCLE_TEST_EXECUTION', jsonObj, callback);
        } else {
            showError("Need to select settings");
        }

    }

    function loadCycle(callback) {
        $.ajax({
            url: "/cycleExisting",
            method: "GET",
            dataType: "json",
            success: function (result) {
                callback(result);
            },
            error: function (error) {
                console.log(error);
                showError(error);
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
});