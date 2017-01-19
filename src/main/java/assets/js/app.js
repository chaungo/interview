var app = angular.module('App', ['ngAnimate', 'ngMaterial', 'ngResource', 'ngMessages', 'ngCookies']);


app.run(function ($rootScope, $resource, $location, $cookies, $mdToast) {
    /////////////////////////////////////////////////////////////
    //console.log($location.absUrl());

    if ($location.absUrl().indexOf("configuration") > -1) {
        $rootScope.configPage = true;
    } else {
        $rootScope.configPage = false;
    }

    if ($location.absUrl().indexOf("release") > -1) {
        $rootScope.pageName = "Release";
    } else {
        $rootScope.pageName = "Home";
    }

    $rootScope.userfullname = "there";
    $rootScope.getMetricRs = $resource('/getMetrics', {}, {
        query: {
            method: 'post',
            isArray: true
        }
    });

    $rootScope.getReleaseRs = $resource('/getReleaseList', {}, {
        query: {
            method: 'post',
            isArray: true
        }
    });

    $rootScope.debugAjaxAngular = function (data) {
        if (data == null) {
            $mdToast.show($mdToast.simple().textContent(
                'Ajax response error: NULL').hideDelay(5000));
            console.log(data);
            return true;
        } else if (data["type"] == "error") {
            $mdToast.show($mdToast.simple().textContent(
                "Ajax response error: " + data["data"]).hideDelay(5000));
            console.log(data);
            return true;
        }

        return false;
    }
    $rootScope.isDisabled = false;
    $rootScope.tableErrorHandling = function (res) {
        if (res.type == "error") {
            if (res.errorCode == "COOKIES_EXPIRED") {
                window.location = "/logout";
            }
            else {
                $mdToast.show($mdToast.simple().textContent(res['data']).hideDelay(5000));
            }

        } else {
            $mdToast.show($mdToast.simple().textContent(res).hideDelay(5000));
        }
        console.log(res);
    }
    //////////////////////////////////////////////////////


});


/////////////////HomePageCtrl////////////////////////////////////////////////////////////////////////////////////////////////////////////////

app.controller('HomePageCtrl', function ($rootScope, $scope, $resource, $mdDialog, $mdToast, $cookies) {
    $rootScope.hasInfo = false;
    $rootScope.dashboardGadgetInfo = {};
    $rootScope.sonarStList = [];
    $rootScope.reviewList = [];
    $rootScope.gadgetId = "";
    $rootScope.gadgetType;
    $rootScope.currentDashboard = {};
    $rootScope.colorRed = {
        color: 'red'
    };
    $rootScope.dashboardNameList = [];
    $rootScope.dashboardInfo = {};
    $rootScope.MetricsList = [];
    $rootScope.getting = true;
    $rootScope.gettingRs = true;
    $rootScope.dashboardName = "";
    $rootScope.limit = 80;
    $rootScope.userInfo = {};


    $scope.gadgetHover = function (item) {
        item.lastUpateTime = Math.round(((Date.now() - item.upateTime) / 1000) / 60);
    };


    $rootScope.getDashboardList = function () {
        $rootScope.sonarStList = [];
        $rootScope.reviewList = [];
        $rootScope.greenHopperGadgets = [];

        $resource('/getDashboardList', {
            groups: JSON.stringify($rootScope.userInfo.groups),
            projects: JSON.stringify($rootScope.userInfo.projects)
        }, {
            query: {
                method: 'post',
                isArray: true
            }
        }).query().$promise.then(function (respone) {
            $rootScope.dashboardNameList = respone;
            $rootScope.currentDashboard = respone[0];
            $rootScope.getting = false;
            //console.log(respone);
            if ($rootScope.dashboardNameList.length == 0) {
                $rootScope.pageName = "Home";
            }
        }, function (error) {
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Server error')
                    .hideDelay(5000)
            );
            //console.log(error);
        });
    };


    if (typeof $cookies.getObject("userInfo") == 'undefined') {
        $resource('/getUserInfo').save().$promise.then(function (data) {
            $rootScope.userInfo = data;
            $cookies.put("userInfo", JSON.stringify(data));
            $rootScope.userfullname = $rootScope.userInfo.displayName;
            $rootScope.name = $rootScope.userInfo.name;
            console.log(data);
            $rootScope.getDashboardList();
            $rootScope.getting = false;
        }, function (error) {
            $rootScope.getting = false;
            $rootScope.err = true;
            //console.log(error);
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Unable to connect to Jira server. Please check connection!')
                    .hideDelay(30000)
            );
        });
    } else {
        $rootScope.userInfo = $cookies.getObject("userInfo");
        $rootScope.userfullname = $rootScope.userInfo.displayName;
        $rootScope.name = $rootScope.userInfo.name;
        $rootScope.getDashboardList();
    }


    $rootScope.showGadget = function () {
        if (typeof $rootScope.currentDashboard == "undefined") {
            return;
        }

        $rootScope.sonarStList = [];
        $rootScope.reviewList = [];
        $rootScope.greenHopperGadgets = [];


        $resource('/getDashboardInfo', {
            id: $rootScope.currentDashboard.id
        }).save().$promise.then(function (respone) {
            //console.log(respone);
            $rootScope.dashboardGadgetInfo = respone;
            if (respone.Gadget > 0) {
                $rootScope.getting = true;
                $rootScope.hasInfo = true;

                if (respone.Gadget > 0) {
                    $resource('/showGadgets', {
                        id: $rootScope.currentDashboard.id
                    }).save().$promise.then(function (respone) {
                        //console.log(respone.Err);
                        if (typeof respone.Err != 'undefined') {
                            $resource('/clearSession').save().$promise.then(function () {
                                window.location = "/login#cookiesexpired";
                            });
                        } else {
                            $rootScope.sonarStList = respone.AMSSONARStatisticsGadget;
                            $rootScope.greenHopperGadgets = respone.GreenHopperGadget;
                            $rootScope.reviewList = respone.AMSOverdueReviewsReportGadget;
                            $rootScope.hasInfo = false;
                        }
                        $rootScope.getting = false;
                    }, function (error) {
                        //console.log(error);
                        $rootScope.hasInfo = false;
                        $rootScope.getting = false;
                        $mdToast.show(
                            $mdToast.simple()
                                .textContent('Server error! Please logout then login and try again')
                                .hideDelay(5000)
                        );
                    });
                }

            }

        }, function (error) {
            //console.log(error);
        });
    };


    $scope.$watch('currentDashboard', function () {
        $rootScope.showGadget();
        for (var i = 0; i < $rootScope.dashboardNameList.length; i++) {
            if ($rootScope.currentDashboard == $rootScope.dashboardNameList[i]) {
                $rootScope.pageName = $rootScope.dashboardNameList[i].name;
                break;
            }
        }
    });

    $scope.dashboardListItemClicked = function (DashboardItem) {
        if (!$rootScope.getting) {
            $rootScope.currentDashboard = DashboardItem;
            $rootScope.sonarStList = [];
            $rootScope.reviewList = [];
            $rootScope.showGadget();
        } else {
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Please wait!')
                    .hideDelay(2000)
            );
        }
    };

    $rootScope.dashboardOptionClicked = function (DashboardItem, event) {
        $rootScope.dashboardOptionItem = DashboardItem;
        $mdDialog.show({
            templateUrl: 'assets/html/dashboardOptionDialog.html',
            parent: angular.element(document.body),
            targetEvent: event,
            clickOutsideToClose: true
        })
    };


    var event;
    $rootScope.showAddWidgetDialog = function (ev) {
        event = ev;


        $mdDialog.show({
            controller: AddWidgetDialogController,
            templateUrl: 'assets/html/choseGadget.html',
            parent: angular.element(document.body),
            targetEvent: ev,
            clickOutsideToClose: true
        })
            .then(function (answer) {
                $scope.status = 'You said the information was "' + answer + '".';
            }, function () {
                $scope.status = 'You cancelled the dialog.';
            });
    };


    function AddWidgetDialogController($resource, $scope, $rootScope, $mdDialog, $compile) {
        $rootScope.gadgetId = "";

        $resource('/getGadgetList', {}, {
            query: {
                method: 'post',
                isArray: true
            }
        }).query().$promise.then(function (respone) {
            $scope.gadgetList = respone;
            //console.log(respone);
        }, function (error) {
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Server error')
                    .hideDelay(5000)
            );
            //console.log(error);
        });


        $scope.addGadget = function (item) {
            $mdDialog.cancel();
            //clear cache
            $rootScope.gadgetToEdit = null;
            //console.log(item.name);
            $rootScope.gadgetType = item.type;

            $mdDialog.show({
                templateUrl: item.addnewUIurl,
                parent: angular.element(document.getElementById('html')),
                targetEvent: event,
                clickOutsideToClose: false
            });


        };

        $scope.hide = function () {
            $mdDialog.hide();
        };

        $scope.cancel = function () {
            $mdDialog.cancel();
        };

        $scope.answer = function (answer) {
            $mdDialog.hide(answer);
        };
    }

    $scope.clearCacheGagdget = function (item) {
        $rootScope.getting = true;
        $resource('/clearCacheGadget', {
            GadgetId: item.id
        }).save().$promise.then(function (respone) {
            //console.log(respone);
            $rootScope.showGadget();
            $rootScope.getting = false;
        }, function (error) {
            //console.log(error);
            $rootScope.getting = false;
        });


        //console.log("clearCacheGadget " + item.id);
    };


    ///////////////////////SonarGadget/////////////////////////


    $scope.deleteGagdget = function (item) {
        $rootScope.getting = true;
        $resource('/deleteGadget', {
            GadgetId: item.id
        }).save().$promise.then(function (respone) {
            //console.log(respone);
            $rootScope.showGadget();
            $rootScope.getting = false;
        }, function (error) {
            //console.log(error);
            $rootScope.getting = false;
        });


        //console.log("Delete " + item.id);
    };

    $scope.editSonarGagdget = function (item) {
        //console.log("Edit " + item.id);
        $rootScope.gadgetId = item.id;

        for (var i = 0; i < $rootScope.sonarStList.length; i++) {
            if ($rootScope.sonarStList[i].id == item.id) {
                $rootScope.sonarGadgettoEdit = $rootScope.sonarStList[i];
            }
        }

        $mdDialog.show({
            templateUrl: "assets/html/addNewSonarGadget.html",
            parent: angular.element(document.getElementById('html')),
            targetEvent: event,
            clickOutsideToClose: false
        });
    };


    $rootScope.check = function (val) {
        return val >= 0;
    };

    $scope.editGreenhopperGadget = function (item, event) {
        $rootScope.gadgetToEdit = item;
        $mdDialog.show({
            templateUrl: item.addnewUIurl,
            parent: angular.element(document.getElementById('html')),
            targetEvent: event,
            clickOutsideToClose: false
        });
    }

    $scope.deleteGreenhopperGadget = function (item) {
        var gadgetId = item.id;
        $.ajax({
            url: "/gadget/delete",
            dataType: "json",
            data: {
                id: gadgetId
            },
            success: function (result) {
                if (result.type == SUCCESS) {
                    $rootScope.showGadget();
                } else {
                    $mdToast.show(
                        $mdToast.simple()
                            .textContent(result.data)
                            .hideDelay(5000)
                    );
                }
            },
            error: function (error) {
                $mdToast.show(
                    $mdToast.simple()
                        .textContent(error)
                        .hideDelay(5000)
                );
            }
        });
    }
});

/////////////////HeaderCtrl////////////////////////////////////////////////////////////////////////////////////////////////////////////////

app.controller('HeaderCtrl', function ($rootScope, $scope, $resource, $mdDialog, $mdToast, $cookies) {

    function DialogController($scope, $mdDialog) {
        $scope.cancel = function () {
            $mdDialog.cancel();
        };
    }

    $scope.showAddNewDialog = function (ev) {
        $mdDialog.show({
            controller: DialogController,
            templateUrl: 'assets/html/addNewDashboard.html',
            parent: angular.element(document.html),
            targetEvent: ev,
            clickOutsideToClose: true
        })
            .then(function (answer) {
            }, function () {
            });
    };
});

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

app.controller('LoginCtrl', function ($rootScope, $scope, $mdDialog, $mdToast, $location, $cookies) {
    $cookies.remove("userInfo");

    $rootScope.pageName = "Login";
    $scope.wronginfo = false;
    $scope.connectionfailed = false;
    var url = $location.absUrl();

    if (url.indexOf("incorrectinfo") > -1) {
        $scope.wronginfo = true;
    }

    if (url.indexOf("connectionfailed") > -1) {
        $scope.connectionfailed = true;
    }

    if (url.indexOf("cookiesexpired") > -1) {
        $scope.cookiesexpired = true;
    }

});

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

app.controller('AddNewSonarGadgetCtrl', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.choseReleasePage = true;
    $scope.choseIAPage = false;
    $scope.choseMetricPage = false;
    $scope.IALoading = false;

    $scope.releaseName = "";
    $scope.releaseUrl = "";
    $scope.IAItems = [];
    $scope.IAItemNames = [];
    $scope.selectedIA = [];
    $scope.selectedMetric = [];
    $scope.period = "";


    $scope.cancel = function () {
        $mdDialog.cancel();
    };
    $rootScope.getReleaseRs.query().$promise.then(function (data) {
        $scope.ReleaseList = data;
        //console.log(data);
        $scope.releaseName = $scope.ReleaseList[0].name;
    }, function (error) {
        //console.log(error);
        $mdToast.show(
            $mdToast.simple()
                .textContent('Error! Can not get Release List. Please check connection!')
                .hideDelay(10000)
        );
    });


    $scope.choseReleaseNext = function () {

        for (var i = 0; i < $scope.ReleaseList.length; i++) {
            if ($scope.ReleaseList[i].name == $scope.releaseName) {
                $scope.releaseUrl = $scope.ReleaseList[i].url;
                break;
            }
        }

        $scope.IALoading = true;
        $resource('/getIAComponents', {
            data: {
                url: $scope.releaseUrl
            }
        }, {
            query: {
                method: 'post',
                isArray: true
            }
        }).query().$promise.then(function (respone) {
            //console.log(respone);
            $scope.IAItems = respone;
            for (var i = 0; i < respone.length; i++) {
                $scope.IAItemNames.push(respone[i].name);
            }
            $scope.selectedIA.push(respone[0].name);
            $scope.choseReleasePage = false;
            $scope.choseIAPage = true;
            $scope.IALoading = false;
        }, function (error) {
            //console.log(error);
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Error! Can not get IA Component. Please check connection!')
                    .hideDelay(10000)
            );
        });
    };


    $scope.toggleIA = function (item) {

        var idx = $scope.selectedIA.indexOf(item);
        if (idx > -1) {
            $scope.selectedIA.splice(idx, 1);
        } else {
            $scope.selectedIA.push(item);
        }

        //console.log($scope.selectedIA);
    };

    $scope.existsIA = function (item) {
        return $scope.selectedIA.indexOf(item) > -1;
    };

    $scope.isIAIndeterminate = function () {
        return ($scope.selectedIA.length !== 0 &&
        $scope.selectedIA.length !== $scope.IAItemNames.length);
    };

    $scope.isIAChecked = function () {
        return $scope.selectedIA.length === $scope.IAItemNames.length;
    };

    $scope.toggleAllIA = function () {
        if ($scope.selectedIA.length === $scope.IAItemNames.length) {
            $scope.selectedIA = [];
        } else if ($scope.selectedIA.length === 0 || $scope.selectedIA.length > 0) {
            $scope.selectedIA = $scope.IAItemNames.slice(0);
        }
    };

    $scope.choseIANext = function () {
        $scope.IALoading = true;
        $scope.components = [];
        $scope.IALoading = false;
        $scope.choseMetricPage = true;
        $scope.choseIAPage = false;

        //console.log($scope.selectedIA);


    };


    $scope.choseIABack = function () {
        $scope.choseReleasePage = true;
        $scope.choseIAPage = false;
    };


    /////////////////////////////////////////////////////////////////////////////////////////

    $scope.itemsNameMetric = [];
    $scope.MetricItems = [];
    $rootScope.getMetricRs.query().$promise.then(function (data) {
        //console.log(data);
        $scope.MetricItems = data;
        for (var i = 0; i < data.length; i++) {
            $scope.itemsNameMetric.push(data[i].name);
        }
        $scope.selectedMetric.push($scope.MetricItems[0].name);
    }, function (error) {
        //console.log(error);
        $mdToast.show(
            $mdToast.simple()
                .textContent('Error! Can not get Metric List. Please check connection!')
                .hideDelay(10000)
        );
    });


    $scope.toggleMetric = function (item) {
        var idx = $scope.selectedMetric.indexOf(item);
        if (idx > -1) {
            $scope.selectedMetric.splice(idx, 1);
        } else {
            $scope.selectedMetric.push(item);
        }
    };

    $scope.existsMetric = function (item) {
        return $scope.selectedMetric.indexOf(item) > -1;
    };

    $scope.isMetricIndeterminate = function () {
        return ($scope.selectedMetric.length !== 0 &&
        $scope.selectedMetric.length !== $scope.itemsNameMetric.length);
    };

    $scope.isMetricChecked = function () {
        return $scope.selectedMetric.length === $scope.itemsNameMetric.length;
    };

    $scope.toggleAllMetric = function () {
        if ($scope.selectedMetric.length === $scope.itemsNameMetric.length) {
            $scope.selectedMetric = [];
        } else if ($scope.selectedMetric.length === 0 || $scope.selectedMetric.length > 0) {
            $scope.selectedMetric = $scope.itemsNameMetric.slice(0);
        }
    };

    /////////////////////////////////////////////////////////////////////////////////////////


    $scope.choseMetricBack = function () {
        $scope.choseMetricPage = false;
        $scope.choseIAPage = true;
    };


    $scope.choseMetricNext = function () {
        //console.log("choseMetricNext");
        if ($scope.selectedMetric.length == 0) {
            $scope.selectedMetric.push($scope.MetricItems[0].name);
        }
        $scope.selectedMetricKeys = [];

        for (var i = 0; i < $scope.selectedMetric.length; i++) {
            for (var j = 0; j < $scope.MetricItems.length; j++) {
                if ($scope.selectedMetric[i] == $scope.MetricItems[j].name) {
                    $scope.selectedMetricKeys.push($scope.MetricItems[j].key);
                }
            }
        }


        finish();


    };

    function finish() {
        var IANames = "";
        for (var i = 0; i < $scope.selectedIA.length; i++) {
            if (i < $scope.selectedIA.length - 1) {
                IANames = IANames + $scope.selectedIA[i] + ",";
            } else {
                IANames = IANames + $scope.selectedIA[i];
            }
        }

        var Metrics = "";
        for (var i = 0; i < $scope.selectedMetricKeys.length; i++) {
            if (i < $scope.selectedMetricKeys.length - 1) {
                Metrics = Metrics + $scope.selectedMetricKeys[i] + ",";
            } else {
                Metrics = Metrics + $scope.selectedMetricKeys[i];
            }
        }


        if ($rootScope.gadgetId != "") {

            var data = {
                DashboardId: $rootScope.currentDashboard.id,
                DashboardGadgetId: $rootScope.gadgetId,
                Data: {
                    Release: $scope.releaseName,
                    IANames: IANames,
                    Metrics: Metrics
                }
            };


            //console.log(data);
            $resource('/updateGadget', {
                data: data
            }, {
                query: {
                    method: 'post',
                    isArray: false
                }
            }).query().$promise.then(function (data) {
                //console.log(data);
                $mdDialog.cancel();
                $rootScope.showGadget();
            }, function (error) {
                //console.log(error);
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Error! Can not update gadget. ')
                        .hideDelay(10000)
                );
            });
        } else {

            var data = {
                DashboardId: $rootScope.currentDashboard.id,
                GadgetType: $rootScope.gadgetType,
                Data: {
                    Release: $scope.releaseName,
                    IANames: IANames,
                    Metrics: Metrics
                }
            };


            //console.log(data);
            $resource('/addNewGadget', {
                data: data
            }, {
                query: {
                    method: 'post',
                    isArray: false
                }
            }).query().$promise.then(function (data) {
                //console.log(data);
                $mdDialog.cancel();
                $rootScope.showGadget();
            }, function (error) {
                //console.log(error);
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Error! Can not get add new gadget')
                        .hideDelay(10000)
                );
            });

        }


    }


    if ($rootScope.gadgetId != "") {
        $scope.releaseName = $rootScope.sonarGadgettoEdit.release;
        for (var i = 0; i < $rootScope.sonarGadgettoEdit.RsIAArray.length; i++) {
            $scope.selectedIA.push($rootScope.sonarGadgettoEdit.RsIAArray[i].name);
        }
        for (var i = 0; i < $rootScope.sonarGadgettoEdit.metricList.length; i++) {
            $scope.selectedMetric.push($rootScope.sonarGadgettoEdit.metricList[i].name);
        }
    }


});

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

app.controller('ConfigCtrl', function ($rootScope, $scope, $mdDialog, $mdToast, $location, $resource, $cookies) {
    $rootScope.pageName = "Configuration";
    $rootScope.userInfo = $cookies.getObject("userInfo");
    $rootScope.userfullname = $rootScope.userInfo.displayName;
    $scope.ReleaseList = [];
    $scope.newReleaseName = "";
    $scope.newReleaseUrl = "";

    $resource('/getPeriodList', {}, {
        query: {
            method: 'post',
            isArray: false
        }
    }).query().$promise.then(function (respone) {
        console.log(respone);
        $scope.periodList = respone.PeriodArray;
        $scope.period = respone.CurrentPeriod;
    }, function (error) {
        //console.log(error);
        $mdToast.show(
            $mdToast.simple()
                .textContent('Error! Can not get Period List. Please check connection!')
                .hideDelay(10000)
        );
    });

    $scope.$watch('period', function () {
        $resource('/setPeriod', {
            period: $scope.period
        }).save().$promise.then(function (respone) {

        }, function (error) {
            console.log(error)
        });
    });


    $rootScope.getReleaseRs = $resource('/getReleaseList', {}, {
        query: {
            method: 'post',
            isArray: true
        }
    });

    $scope.getReleaseList = function () {
        $rootScope.getReleaseRs.query().$promise.then(function (data) {
            $scope.ReleaseList = data;
        }, function (error) {
            console.log(error);
        });
    };

    $scope.getReleaseList();

    $rootScope.getReleaseRs.query().$promise.then(function (data) {
        $scope.ReleaseList = data;
    }, function (error) {
        console.log(error);
    });

    $rootScope.getMetricRs.query().$promise.then(function (data) {
        $scope.MetricList = data;
    }, function (error) {
        console.log(error);
    });

    /////////////////RELEASE

    $scope.addNewRelease = function (ev) {
        if ($scope.newReleaseName.length == 0 || $scope.newReleaseUrl.length == 0) {
            $mdDialog.show(
                $mdDialog.alert()
                    .title('Oop!')
                    .textContent('Release name and release url can not be empty')
                    .ok('Got it!')
                    .targetEvent(ev)
            ).then(function () {
                $scope.getReleaseList();
            });


        } else {
            for (var i = 0; i < $scope.ReleaseList.length; i++) {
                if ($scope.newReleaseName == $scope.ReleaseList[i].name) {
                    $mdDialog.show(
                        $mdDialog.alert()
                            .title('Oop!')
                            .textContent('Release name ' + '"' + $scope.newReleaseName + '"' + " is already exists")
                            .ok('Got it!')
                            .targetEvent(ev)
                    ).then(function () {
                        $scope.getReleaseList();
                    });
                    return;
                }

                if ($scope.newReleaseUrl == $scope.ReleaseList[i].url) {
                    $mdDialog.show(
                        $mdDialog.alert()
                            .title('Oop!')
                            .textContent('Release url ' + '"' + $scope.newReleaseUrl + '"' + " is already exists")
                            .ok('Got it!')
                            .targetEvent(ev)
                    ).then(function () {
                        $scope.getReleaseList();
                    });
                    return;
                }
            }

            $scope.ReleaseList.push({
                'name': $scope.newReleaseName,
                'url': $scope.newReleaseUrl
            });
            $resource('/addNewRelease', {
                name: $scope.newReleaseName,
                url: $scope.newReleaseUrl
            }).save().$promise.then(function (data) {
            }, function (error) {
                console.log(error);
            });
        }
    };

    $scope.deleteRelease = function (release, ev) {
        var confirm = $mdDialog.confirm()
            .title('Would you like to delete ' + " release " + '"' + release.name + '"' + "?")
            .textContent('Be careful! You can not revert')
            .clickOutsideToClose(true)
            .targetEvent(ev)
            .ok('Do it!')
            .cancel('Cancel');

        $mdDialog.show(confirm).then(function () {
            var index = $scope.ReleaseList.indexOf(release);
            if (index > -1) {
                $scope.ReleaseList.splice(index, 1);
                $resource('/deleteRelease', {
                    url: release.url
                }).save().$promise.then(function (data) {
                    console.log(data)
                }, function (error) {
                    console.log(error);
                });
            }
        }, function () {
        });
    };


    $scope.editRelease = function (release, ev) {
        if (typeof release.name == 'undefined' || typeof release.url == 'undefined') {

            $mdDialog.show($mdDialog.alert().title('Oop!')
                .textContent('Release name and release url can not be empty')
                .ok('Got it!')
                .targetEvent(ev)).then(function () {
                $scope.getReleaseList();
            });

        } else {
            var a = 0;
            var b = 0;
            for (var i = 0; i < $scope.ReleaseList.length; i++) {

                if (release.name == $scope.ReleaseList[i].name) {
                    a++;

                    if (a == 2) {
                        $mdDialog.show(
                            $mdDialog.alert()
                                .title('Oop!')
                                .textContent('Release name ' + '"' + release.name + '"' + " is already exists")
                                .ok('Got it!')
                                .targetEvent(ev)
                        ).then(function () {
                            $scope.getReleaseList();
                        });
                        return;
                    }
                }


                if (release.url == $scope.ReleaseList[i].url) {
                    b++;
                    if (b == 2) {
                        $mdDialog.show(
                            $mdDialog.alert()
                                .title('Oop!')
                                .textContent('Release url ' + '"' + release.url + '"' + " is already exists")
                                .ok('Got it!')
                                .targetEvent(ev)
                        ).then(function () {
                            $scope.getReleaseList();
                        });
                        return;
                    }
                }


            }
            $resource('/updateRelease', {
                id: release.id,
                name: release.name,
                url: release.url
            }).save().$promise.then(function (data) {
                console.log(data)
            }, function (error) {
                console.log(error);
            });
        }
    };


});


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


app.controller('AddNewOverdueReviewReportGadgetCtrl', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {

    $scope.choseProjectPage = true;
    $scope.choseUserPage = false;
    $scope.Loading = false;

    $scope.CruProjectList = [];
    $scope.projectId = "";
    $scope.IAItems = [];
    $scope.IAItemNames = [];
    $scope.selectedIA = [];
    $scope.loginForm = false;


    $scope.loginCru = function (cruusername, crupassword) {
        $scope.Loading = true;
        $resource('/loginCru', {
            username: cruusername,
            password: crupassword
        }).save().$promise.then(function (response) {
            $scope.loginForm = false;
            $scope.getCruProjectList();
            $scope.Loading = false;

        }, function (error) {
            $scope.Loading = false;
            $scope.loginForm = true;
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Error! Please check connection!')
                    .hideDelay(10000)
            );
        });

    };


    $scope.cancel = function () {
        $mdDialog.cancel();
    };

    $scope.getCruProjectList = function () {
        $scope.Loading = true;
        $resource('/getCruProjectList', {}, {
            query: {
                method: 'post',
                isArray: true
            }
        }).query().$promise.then(function (respone) {
            //console.log(respone);
            $scope.CruProjectList = respone;
            if ($scope.CruProjectList.length == 0) {
                // $window.location.href = '/logout';
                $scope.loginForm = true;
            }

            $scope.Loading = false;
        }, function (error) {
            $scope.Loading = false;
            $scope.loginForm = true;
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Error! Can not get Cru Project List. Please login to Crucible!')
                    .hideDelay(10000)
            );
        });
    };

    $scope.getCruProjectList();

    $scope.choseProjectNext = function (projectId) {
        if (projectId == "") {
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Please choose a project')
                    .hideDelay(5000)
            );
        } else {
            var data = {
                DashboardId: $rootScope.currentDashboard.id,
                GadgetType: $rootScope.gadgetType,
                Data: {
                    Project: projectId
                }
            };
            $resource('/addNewGadget', {
                data: data
            }, {
                query: {
                    method: 'post',
                    isArray: false
                }
            }).query().$promise.then(function (data) {
                //console.log(data);
                $mdDialog.cancel();
                $rootScope.showGadget();
            }, function (error) {
                //console.log(error);
                $mdToast.show(
                    $mdToast.simple()
                        .textContent('Error! Can not get add new gadget.')
                        .hideDelay(5000)
                );
            });
        }

    };


});

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

app.controller('dasboardOptionCtrl', function ($rootScope, $scope, $mdDialog, $mdToast, $location, $resource, $cookies) {
    $scope.share = [];
    $scope.dashboardName = $rootScope.dashboardOptionItem.name;
    $scope.cancel = function () {
        $mdDialog.cancel();
    };

    $scope.groups = $rootScope.userInfo.groups;

    $resource('/getProjectList', {}, {
        query: {
            method: 'post',
            isArray: true
        }
    }).query().$promise.then(function (data) {
        $scope.projects = data;
    }, function (error) {
        //console.log(error);
        $mdToast.show(
            $mdToast.simple()
                .textContent('Error! Can not get project list. Please check connection!')
                .hideDelay(10000)
        );
    });


    $scope.share = $rootScope.dashboardOptionItem.privacy.share;


    $scope.groupItemClicked = function (groupItem) {

        if ($scope.share.indexOf("Ony me") > -1) {
            $scope.share.splice($scope.share.indexOf("Ony me"), 1);
        }

        if ($scope.share.indexOf("Everyone") > -1) {
            $scope.share.splice($scope.share.indexOf("Everyone"), 1);
        }

        if ($scope.share.indexOf(groupItem) < 0) {
            $scope.share.push(groupItem);
        }
        //console.log($scope.share);

    };

    $scope.projectItemClicked = function (projectItem) {

        if ($scope.share.indexOf("Ony me") > -1) {
            $scope.share.splice($scope.share.indexOf("Ony me"), 1);
        }

        if ($scope.share.indexOf("Everyone") > -1) {
            $scope.share.splice($scope.share.indexOf("Everyone"), 1);
        }

        if ($scope.share.indexOf(projectItem) < 0) {
            $scope.share.push(projectItem);
        }
        //console.log($scope.share);
    };

    $scope.everyoneClicked = function () {
        $scope.share = [];
        $scope.share.push("Everyone");
        //console.log($scope.share);
    };


    $scope.privateClicked = function () {
        $scope.share = [];
        $scope.share.push("Ony me");
        //console.log($scope.share);
    };

    $scope.allGroupClicked = function () {
        $scope.share = [];
        $scope.share = $scope.groups;
        //console.log($scope.share);
    };
    $scope.allProjectClicked = function () {
        $scope.share = [];
        $scope.share = $scope.projects;
        //console.log($scope.share);
    };


    $scope.$watch("share.length", function () {
        if ($scope.share.length == 0) {
            $scope.share.push("Ony me");
        }
    });


    $scope.saveOption = function () {
        var status = "other";
        if ($scope.share.indexOf("Ony me") > -1) {
            status = "private";
        } else {
            if ($scope.share.indexOf("Everyone") > -1) {
                status = "public";
            }
        }

        $resource('/updateDashboardOption', {
            id: $rootScope.dashboardOptionItem.id,
            name: $scope.dashboardName,
            privacy: {
                status: status,
                share: $scope.share
            }
        }).save().$promise.then(function (data) {
            $rootScope.getDashboardList();
            //console.log(data);
            $scope.cancel();
        }, function (error) {
            //console.log(error);
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Error! Can not update dashboard option')
                    .hideDelay(5000)
            );
        });
    };


    $scope.deleteDashboard = function () {
        $resource('/deleteDashboard', {
            id: $rootScope.dashboardOptionItem.id
        }).save().$promise.then(function (respone) {
            $rootScope.getDashboardList();
            $scope.cancel();
            //console.log(respone);
        }, function (error) {
            $mdToast.show(
                $mdToast.simple()
                    .textContent('Server error')
                    .hideDelay(5000)
            );
            //console.log(error);
        });
    }


});

app.controller('EpicController', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.dataTable = null;
    $scope.showView = true;
    $scope.titleAdditionalInfo = null;
    $scope.isClearingCache = false;
    $scope.toggleView = function () {
        $scope.showView = !$scope.showView;
    }
    $scope.init = function (item) {
        var titleHandler = function (number) {
            $scope.titleAdditionalInfo = "- " + number + " table";
            $rootScope.$apply();
        }
        var dataTableCallback = function (table) {
            $scope.dataTable = table;
        }
        var clearCacheCallback = function () {
            $scope.isClearingCache = false;
        }
        drawEpicTable($scope.dataTable, item, $rootScope.tableErrorHandling, titleHandler, dataTableCallback, clearCacheCallback);
    }

    $scope.clearCacheGreenhopperGagdget = function (item) {
        if (!$scope.isClearingCache) {
            $.ajax({
                url: "/clearCache",
                data: {
                    id: item.id
                },
                beforeSend: function () {
                    $scope.isClearingCache = true;
                },
                success: function (res) {
                    if ($rootScope.debugAjaxAngular(res)) {
                        $scope.isClearingCache = false;
                        return;
                    }
                    else {
                        $scope.init(item);
                    }
                }
            });
        }
    }

});


app.controller('StoryController', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.dataTable = {
        "ajax": null,
        "loading": false
    };
    $scope.titleAdditionalInfo = null;
    $scope.showView = true;
    $scope.isClearingCache = false;
    $scope.init = function (item) {
        var titleHandler = function (number) {
            $scope.titleAdditionalInfo = "- " + number + " table(s)";
            $rootScope.$apply();
        }
        var dataTableCallback = function (table) {
            $scope.dataTable = table;

        }
        var clearCacheCallback = function () {
            $scope.isClearingCache = false;
        }
        drawUsTable($scope.dataTable, item, $rootScope.tableErrorHandling, titleHandler, dataTableCallback, clearCacheCallback);
    }
    $scope.toggleView = function () {
        $scope.showView = !$scope.showView;
    }

    $scope.clearCacheGreenhopperGagdget = function (item) {
        if (!$scope.isClearingCache) {
            $.ajax({
                url: "/clearCache",
                data: {
                    id: item.id
                },
                beforeSend: function () {
                    $scope.isClearingCache = true;
                },
                success: function (res) {
                    if ($rootScope.debugAjaxAngular(res)) {
                        $scope.isClearingCache = false;
                        return;
                    }
                    else {
                        $scope.init(item);
                    }
                }
            });
        }
    }

});

app.controller('CycleController', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.dataTable = null;
    $scope.showView = true;
    $scope.titleAdditionalInfo = null;
    $scope.isClearingCache = false;
    $scope.init = function (item) {
        var titleHandler = function (number) {
            $scope.titleAdditionalInfo = "- " + number + " table";
            $rootScope.$apply();
        }
        var dataTableCallback = function (table) {
            $scope.dataTable = table;
        }
        var clearCacheCallback = function () {
            $scope.isClearingCache = false;
        }
        drawCycleTable($scope.dataTable, item, $rootScope.tableErrorHandling, titleHandler, dataTableCallback, clearCacheCallback);
    }
    $scope.toggleView = function () {
        $scope.showView = !$scope.showView;
    }

    $scope.clearCacheGreenhopperGagdget = function (item) {
        if (!$scope.isClearingCache) {
            $.ajax({
                url: "/clearCache",
                data: {
                    id: item.id
                },
                beforeSend: function () {
                    $scope.isClearingCache = true;
                },
                success: function (res) {
                    if ($rootScope.debugAjaxAngular(res)) {
                        $scope.isClearingCache = false;
                        return;
                    }
                    else {
                        $scope.init(item);
                    }
                }
            });
        }
    }

});

app.controller('AssigneeController', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.dataTable = {
        "ajax": null,
        "loading": false
    };
    $scope.titleAdditionalInfo = null;
    $scope.showView = true;
    $scope.isClearingCache = false;

    $scope.init = function (item) {
        var titleHandler = function (index) {
            $scope.titleAdditionalInfo = "- " + index + " table(s)";
            $rootScope.$apply();
        }
        var dataTableCallback = function (table) {
            $scope.dataTable = table;
        }
        var clearCacheCallback = function () {
            $scope.isClearingCache = false;
        }
        drawAssigneeTable($scope.dataTable, item, $rootScope.tableErrorHandling, titleHandler, dataTableCallback, clearCacheCallback);
    }

    $scope.toggleView = function () {
        $scope.showView = !$scope.showView;
    }

    $scope.clearCacheGreenhopperGagdget = function (item) {
        if (!$scope.isClearingCache) {
            $.ajax({
                url: "/clearCache",
                data: {
                    id: item.id
                },
                beforeSend: function () {
                    $scope.isClearingCache = true;
                },
                success: function (res) {
                    if ($rootScope.debugAjaxAngular(res)) {
                        $scope.isClearingCache = false;
                        return;
                    }
                    else {
                        $scope.init(item);
                    }
                }
            });
        }
    }

});