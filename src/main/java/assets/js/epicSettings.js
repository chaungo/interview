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

function saveGadgetSettings(gadgetType, settings, callback) {
    $.ajax({
        url: "/gadget/save",
        method: 'POST',
        data: {
            type: gadgetType,
            data: settings
        },
        success: function (data) {
            callback(data);
        },
        error: function (xhr, textStatus, error) {
            showError(error);
        }
    });
}

app.controller('EpicSettingController', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.gadgetId = null;
    $scope.greenHopperProjectList = [];
    $scope.greenHopperProduct = [];
    $scope.greenHopperRelease = [];
    $scope.selectedProduct = null;
    $scope.selectedProject = null;
    $scope.selectedRelease = null;
    $scope.hideEpic = true;
    $scope.showEpicLoader = false;
    $scope.selectedEpicLink = null;
    $scope.selectedMetric = null;
    $scope.cancel = function () {
        $mdDialog.cancel();
    }
    $scope.productPage = "configuration";

    $scope.init = function () {
        var item = $rootScope.gadgetToEdit;
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

        if (item != null) {
            if (item.type == "EPIC_US_TEST_EXECUTION") {
                $scope.gadgetId = item.id;
                $scope.selectedProject = item.projectName;
                $scope.selectedRelease = item.release;
                $scope.selectedProduct = item.products[0];
                $scope.hideEpic = item.selectAll;
                $scope.onProjectReleaseProductChanged();
                $scope.selectedEpicLink = item.epic;
                $scope.selectedMetric = item.metrics;
            }
            $rootScope.gadgetToEdit = null;
        }
    }

    $scope.onCheckAllEpic = function () {
        $scope.onProjectReleaseProductChanged();
    }

    $scope.onProjectReleaseProductChanged = function () {
        var epicProjectVal = $scope.selectedProject;
        var epicProductVal = $scope.selectedProduct;
        var epicReleaseVal = $scope.selectedRelease;
        var isNotEmpty = verifyValue([epicProjectVal, epicProductVal, epicReleaseVal]);


        if (isNotEmpty) {
            var requestData = {
                project: epicProjectVal,
                release: epicReleaseVal,
                products: JSON.stringify([epicProductVal])
            }

            var callback = function (result) {
                if (result.type == null) {
                    $scope.greenHopperEpicLink = result;
                    $scope.$apply();
                } else {
                    showError(result.data);
                }
            }
            // load Epic Link
            loadEpicLink(requestData, callback);

        }
    }

    $scope.saveGadget = function () {
        var epicProjectVal = $("#epicProject").val();
        var epicProductVal = $("#epicProduct").val();
        var epicReleaseVal = $("#epicRelease").val();
        var metricsVal = $("#epicMetricMultiSelect").val();
        var epicLink = $("#epicLinkSelection").val();
        var isNotEmpty;
        var epicCheckAllVal = $("#epicCheckAll").prop('checked');
        if (epicCheckAllVal) {
            isNotEmpty = verifyValue([epicProjectVal, epicProductVal, epicReleaseVal]);
            isNotEmpty &= (metricsVal != null);
        } else {
            isNotEmpty = verifyValue([epicProjectVal, epicProductVal, epicReleaseVal]);
            isNotEmpty &= (epicLink != null && epicLink.length > 0);
            isNotEmpty &= (metricsVal != null);
        }

        if (isNotEmpty && $rootScope.currentDashboard != null) {
            var object = {};
            // object['id'] = TEST_EPIC_ID;
            var dashboardId = $rootScope.currentDashboard.id;
            object["id"] = $scope.gadgetId;
            object['dashboardId'] = dashboardId;
            object['release'] = epicReleaseVal;
            object['projectName'] = epicProjectVal;
            object['products'] = [epicProductVal];
            object['metrics'] = metricsVal;
            if (epicCheckAllVal) {
                object['selectAll'] = true;
            } else {
                object['epic'] = epicLink;
            }
            if ($rootScope.gadgetToEdit != null) {
                object['id'] = $rootScope.gadgetToEdit.id;
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

            saveGadgetSettings('EPIC_US_TEST_EXECUTION', jsonObj, callback);
        } else {
            showError("Need to select settings");
        }

    }

    function loadEpicLink(requestData, callback) {

        $.ajax({
            url: "/getEpicLinks",
            method: "GET",
            dataType: "json",
            data: requestData,
            beforeSend: function () {
                $scope.showEpicLoader = true;
            },
            success: function (result) {
                callback(result);
                $scope.showEpicLoader = false;
                $scope.$apply();
            },
            error: function (error) {
                console.log(error);
                $scope.showEpicLoader = false;
                $scope.$apply();
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