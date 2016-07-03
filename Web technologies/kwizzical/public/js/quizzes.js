// Controller for the list of quizzes
"use strict";

angular.module('kwizecalApp', [])
  .controller('qlCtrl', qlCtrl);

function qlCtrl($scope, $http) {

  function loadQuizzes(data) {
    if(data.length == 0) {
      $scope.msg = "There are no Kwizzes to take at this time. Please make one";
    }
    else { $scope.msg = "Select a Kwiz from the list below:"; }
    $scope.quizlist = data;
  }

  function showError(reason) {
    console.log(reason);
  }

  //fetch JSON data from server
  $http
    .get('/quizlist')
    .success(loadQuizzes)
    .error(showError)

}
