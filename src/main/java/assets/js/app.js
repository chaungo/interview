var app = angular.module('App', ['ngAnimate', 'ngMaterial', 'ngResource', 'ngMessages', 'ngCookies']);


app.run(function ($rootScope, $resource, $location, $cookies) {
    /////////////////////////////////////////////////////////////
    //console.log($location.absUrl());

    if ($location.absUrl().indexOf("configuration") > -1) {
        $rootScope.configPage = true;
    }else {
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


    $rootScope.getDashboardList = function () {
        $rootScope.reviewList = [];
        $rootScope.sonarStList = [];
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
            //console.log(data);
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


    $scope.showAddNewDialog = function (ev) {
        $mdDialog.show({
                controller: DialogController,
                templateUrl: 'assets/html/addNewDashboard.html',
                parent: angular.element(document.html),
                targetEvent: ev,
                clickOutsideToClose: true
            })
            .then(function (answer) {}, function () {});
    };


    function DialogController($scope, $mdDialog) {
        $scope.cancel = function () {
            $mdDialog.cancel();
        };
    }


    $rootScope.showGadget = function () {
        if (typeof $rootScope.currentDashboard == "undefined") {
            return;
        }

        $rootScope.sonarStList = [];
        $rootScope.reviewList = [];

        $resource('/getDashboardInfo', {
            id: $rootScope.currentDashboard.id
        }).save().$promise.then(function (respone) {
            //console.log(respone);
            $rootScope.dashboardGadgetInfo = respone;
            if (respone.Gadget > 0) {
                $rootScope.getting = true;
                $rootScope.hasInfo = true;

                if (respone.SonarGadget > 0) {
                    $resource('/showGadgets', {
                        id: $rootScope.currentDashboard.id
                    }).save().$promise.then(function (respone) {
                        //console.log(respone);
                        $rootScope.sonarStList = respone.AMSSONARStatisticsGadget;
                        $rootScope.greenHopperGadgets = respone.GreenHopperGadget;
                        $rootScope.reviewList = respone.AMSOverdueReviewsReportGadget;
                        $rootScope.hasInfo = false;
                        $rootScope.getting = false;
                    }, function (error) {
                        //console.log(error);
                        $rootScope.hasInfo = false;
                        $rootScope.getting = false;
                        $mdToast.show(
                            $mdToast.simple()
                            .textContent('Server error')
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


});

/////////////////HeaderCtrl////////////////////////////////////////////////////////////////////////////////////////////////////////////////

app.controller('HeaderCtrl', function ($rootScope, $scope, $resource, $mdDialog, $mdToast, $cookies) {

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

});

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

app.controller('AddNewSonarGadgetCtrl', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.choseReleasePage = true;
    $scope.choseIAPage = false;
    $scope.choseMetricPage = false;
    $scope.chosePeriodPage = false;
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

    $scope.periodList = [];
    $scope.period = "";
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

        //
        //console.log($scope.selectedMetricKeys);
        if ($scope.selectedMetricKeys.indexOf("new_coverage") != -1) {
            $scope.IALoading = true;
            $resource('/getPeriodList', {}, {
                query: {
                    method: 'post',
                    isArray: true
                }
            }).query().$promise.then(function (respone) {
                //console.log(respone);
                $scope.periodList = respone;
                $scope.period = $scope.periodList[0].key;
                $scope.chosePeriodPage = true;
                $scope.choseMetricPage = false;
                $scope.IALoading = false;
            }, function (error) {
                //console.log(error);
                $mdToast.show(
                    $mdToast.simple()
                    .textContent('Error! Can not get Period List. Please check connection!')
                    .hideDelay(10000)
                );
            });
        } else {
            finish();
        }


    };


    $scope.chosePeriodBack = function () {
        $scope.choseMetricPage = true;
        $scope.chosePeriodPage = false;
    };


    $scope.chosePeriodNext = function () {
        $scope.gettingRs = true;
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
                    Metrics: Metrics,
                    Period: $scope.period
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
                    Metrics: Metrics,
                    Period: $scope.period
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
        $scope.period = $rootScope.sonarGadgettoEdit.period;
    }


});

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

app.controller('ConfigCtrl', function ($rootScope, $scope, $mdDialog, $mdToast, $location, $resource) {
    $rootScope.pageName = "Configuration";
    $scope.MetricList = [];
    $scope.ReleaseList = [];
    $scope.newMetricName = "";
    $scope.newMetricKey = "";

    $scope.newReleaseName = "";
    $scope.newReleaseUrl = "";

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

    $scope.getReleaseList = function () {
        $rootScope.getReleaseRs.query().$promise.then(function (data) {
            $scope.ReleaseList = data;
        }, function (error) {
            console.log(error);
        });
    };


    $scope.getMetricList = function () {
        $rootScope.getMetricRs.query().$promise.then(function (data) {
            $scope.MetricList = data;
            console.log(data);
        }, function (error) {
            console.log(error);
        });
    };

    $scope.getReleaseList();
    $scope.getMetricList();

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


    /////////////////METRIC
    $scope.addNewMetric = function (ev) {
        if ($scope.newMetricName.length == 0 || $scope.newMetricKey.length == 0) {
            $mdDialog.show(
                $mdDialog.alert()
                .title('Oop!')
                .textContent('Metric name and metric key can not be empty')
                .ok('Got it!')
                .targetEvent(ev)
            );
        } else {
            for (var i = 0; i < $scope.MetricList.length; i++) {
                if ($scope.newMetricName == $scope.MetricList[i].name) {
                    $mdDialog.show(
                        $mdDialog.alert()
                        .title('Oop!')
                        .textContent('Metric name ' + '"' + $scope.newMetricName + '"' + " is already exists")
                        .ok('Got it!')
                        .targetEvent(ev)
                    );
                    return;
                }

                if ($scope.newMetricKey == $scope.MetricList[i].key) {
                    $mdDialog.show(
                        $mdDialog.alert()
                        .title('Oop!')
                        .textContent('Metric key ' + '"' + $scope.newMetricKey + '"' + " is already exists")
                        .ok('Got it!')
                        .targetEvent(ev)
                    );
                    return;
                }
            }

            $scope.MetricList.push({
                'name': $scope.newMetricName,
                'key': $scope.newMetricKey
            });
            $resource('/addNewMetric', {
                name: $scope.newMetricName,
                key: $scope.newMetricKey
            }).save().$promise.then(function (data) {}, function (error) {
                console.log(error);
            });
        }
    };

    $scope.deleteMetric = function (metric, ev) {
        //noinspection JSUnresolvedFunction
        var confirm = $mdDialog.confirm()
            .title('Would you like to delete ' + '"' + metric.name + '"' + " metric ?")
            .textContent('Be careful! You can not revert')
            .clickOutsideToClose(true)
            .targetEvent(ev)
            .ok('Do it!')
            .cancel('Cancel');

        $mdDialog.show(confirm).then(function () {
            var index = $scope.MetricList.indexOf(metric);
            if (index > -1) {
                $scope.MetricList.splice(index, 1);
                $resource('/deleteMetric', {
                    key: metric.key
                }).save().$promise.then(function (data) {
                    console.log(data)
                }, function (error) {
                    console.log(error);
                });
            }
        }, function () {
            //do something
        });
    };

    $scope.editMetric = function (metric, ev) {
        if (typeof metric.name == 'undefined' || typeof metric.key == 'undefined') {

            $mdDialog.show($mdDialog.alert().title('Oop!')
                .textContent('Metric name and metric key can not be empty')
                .ok('Got it!')
                .targetEvent(ev)).then(function () {
                $scope.getMetricList();
            });

        } else {
            var a = 0;
            var b = 0;
            for (var i = 0; i < $scope.MetricList.length; i++) {
                if (metric.name == $scope.MetricList[i].name) {
                    a++;
                    if (a == 2) {
                        $mdDialog.show(
                            $mdDialog.alert()
                            .title('Oop!')
                            .textContent('Metric name ' + '"' + metric.name + '"' + " is already exists")
                            .ok('Got it!')
                            .targetEvent(ev)
                        ).then(function () {
                            $scope.getMetricList();
                        });
                        return;
                    }
                }

                if (metric.key == $scope.MetricList[i].key) {
                    b++;
                    if (b == 2) {
                        $mdDialog.show(
                            $mdDialog.alert()
                            .title('Oop!')
                            .textContent('Metric key ' + '"' + metric.key + '"' + " is already exists")
                            .ok('Got it!')
                            .targetEvent(ev)
                        ).then(function () {
                            $scope.getMetricList();
                        });
                        return;
                    }
                }
            }

            $resource('/updateMetric', {
                id: metric.id,
                name: metric.name,
                key: metric.key
            }).save().$promise.then(function (data) {
                console.log(data)
            }, function (error) {
                console.log(error);
            });
        }
    };


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
            }).save().$promise.then(function (data) {}, function (error) {
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
        }, function () {});
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
    $scope.Loading = true;

    $scope.CruProjectList = [];
    $scope.projectId = "";
    $scope.IAItems = [];
    $scope.IAItemNames = [];
    $scope.selectedIA = [];


    $scope.cancel = function () {
        $mdDialog.cancel();
    };


    $resource('/getCruProjectList', {}, {
        query: {
            method: 'post',
            isArray: true
        }
    }).query().$promise.then(function (respone) {
        //console.log(respone);
        $scope.CruProjectList = respone;
        $scope.Loading = false;
    }, function (error) {
        //console.log(error);
        $mdToast.show(
            $mdToast.simple()
            .textContent('Error! Can not get Cru Project List. Please check connection!')
            .hideDelay(10000)
        );
    });


    $scope.choseProjectNext = function () {
        if ($scope.projectId == "") {
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
                    Project: $scope.projectId
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
    $scope.toggleView = function () {
        $scope.showView = !$scope.showView;
    }
    $scope.init = function (item) {
        drawEpicTable($scope.dataTable, item);
    }

    $scope.onProjectReleaseProductChanged = function (item) {

    }

    $scope.editGreenhopperGadget = function (item) {

    }

    $scope.deleteGreenhopperGadget = function (item) {

    }
});


app.controller('StoryController', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.dataTable = {
        "ajax": null,
        "loading": false
    };
    $scope.showView = true;
    $scope.init = function (item) {
        drawUsTable($scope.dataTable, item);
    }
    $scope.toggleView = function () {
        $scope.showView = !$scope.showView;
    }


    $scope.editGreenhopperGadget = function (item) {

    }

    $scope.deleteGreenhopperGadget = function (item) {

    }
});

app.controller('CycleController', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.dataTable = null;
    $scope.showView = true;
    $scope.init = function (item) {
        drawCycleTable($scope.dataTable, item);
    }
    $scope.toggleView = function () {
        $scope.showView = !$scope.showView;
    }

    $scope.editGreenhopperGadget = function (item) {

    }

    $scope.deleteGreenhopperGadget = function (item) {

    }
});

app.controller('AssigneeController', function ($scope, $rootScope, $window, $mdDialog, $mdToast, $location, $resource) {
    $scope.dataTable = {
        "ajax": null,
        "loading": false
    };
    $scope.showView = true;
    $scope.init = function (item) {
        drawAssigneeTable($scope.dataTable, item);
    }

    $scope.toggleView = function () {
        console.log($scope.showView);
        $scope.showView = !$scope.showView;
    }

    $scope.editGreenhopperGadget = function (item) {

    }

    $scope.deleteGreenhopperGadget = function (item) {

    }
});