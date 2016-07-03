// Main angular app for Kwizical website
"use strict";

var kwizecalApp = angular
  .module('kwizecalApp', ['ngRoute'])
  .config(routing)
  .directive("header", header)
  .directive("footer", footer)

//provides routing for each page template
function routing($routeProvider) {
  $routeProvider
  .when('/', {
    templateUrl : 'public/pages/home.html'
  })

  .when('/takequiz/:id', {
    templateUrl : 'public/pages/takequiz.html',
    controller : 'quizCtrl'
  })

  .when('/quizzes', {
    templateUrl : 'public/pages/quizzes.html',
    controller : 'qlCtrl'
  })

  .when('/makequiz', {
    templateUrl : 'public/pages/makequiz.html',
    controller : 'formCtrl'
  })

  .when('/congrats', {
    templateUrl : 'public/pages/congrats.html'
  })

  .when('/about', {
    templateUrl : 'public/pages/about.html'
  });
}

// load header.html
function header() {
  return {
    templateUrl : 'public/pages/header.html',
    scope: true,
    transclude : false
  };
}

// load footer.html
function footer() {
  return {
    templateUrl : 'public/pages/footer.html',
    scope : true,
    transclude : false
  };
}
