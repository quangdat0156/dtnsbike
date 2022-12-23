var app = angular.module("google-chart-sample", ["googlechart"]);
app.controller("Report1ChartCtrl", function ($scope) {
    $scope.myChartObject = {};

    $scope.myChartObject.type = "ColumnChart";

    $scope.onions = [
        { v: "Onions" },
        { v: 3 },
    ];

    $scope.myChartObject.data = {
        "cols": [
            { id: "t", label: "Topping", type: "string" },
            { id: "s", label: "Slices", type: "number" }
        ], "rows": [
            {
                c: [
                    { v: "Mushrooms" },
                    { v: 3 },
                ]
            },
            { c: $scope.onions },
            {
                c: [
                    { v: "Olives" },
                    { v: 31 }
                ]
            },
            {
                c: [
                    { v: "Zucchini" },
                    { v: 1 },
                ]
            },
            {
                c: [
                    { v: "Pepperoni" },
                    { v: 2 },
                ]
            }
        ]
    };

    $scope.myChartObject.options = {
        'title': 'How Much Pizza I Ate Last Night'
    };
});

// REport 2
app.controller("Report2ChartCtrl", function ($scope) {
    $scope.myChartObject = {};

    $scope.myChartObject.type = "PieChart";

    $scope.onions = [
        { v: "Onions" },
        { v: 3 },
    ];

    $scope.myChartObject.data = {
        "cols": [
            { id: "t", label: "Topping", type: "string" },
            { id: "s", label: "Slices", type: "number" }
        ], "rows": [
            {
                c: [
                    { v: "Mushrooms" },
                    { v: 3 },
                ]
            },
            { c: $scope.onions },
            {
                c: [
                    { v: "Olives" },
                    { v: 31 }
                ]
            },
            {
                c: [
                    { v: "Zucchini" },
                    { v: 1 },
                ]
            },
            {
                c: [
                    { v: "Pepperoni" },
                    { v: 2 },
                ]
            }
        ]
    };

    $scope.myChartObject.options = {
        'title': 'How Much Pizza I Ate Last Night'
    };
});

// Report 0
app.controller("Report0ChartCtrl", function ($scope) {
    $scope.myChartObject = {};

    $scope.myChartObject.type = "BarChart";

    $scope.onions = [
        { v: "Onions" },
        { v: 3 },
    ];

    $scope.myChartObject.data = {
        "cols": [
            { id: "t", label: "Topping", type: "string" },
            { id: "s", label: "Slices", type: "number" }
        ], "rows": [
            {
                c: [
                    { v: "Mushrooms" },
                    { v: 3 },
                ]
            },
            { c: $scope.onions },
            {
                c: [
                    { v: "Olives" },
                    { v: 31 }
                ]
            },
            {
                c: [
                    { v: "Zucchini" },
                    { v: 1 },
                ]
            },
            {
                c: [
                    { v: "Pepperoni" },
                    { v: 2 },
                ]
            }
        ]
    };

    $scope.myChartObject.options = {
        'title': 'How Much Pizza I Ate Last Night'
    };
});