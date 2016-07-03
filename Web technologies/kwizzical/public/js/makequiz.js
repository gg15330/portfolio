// Module in charge of making a quiz
"use strict";

angular.module('kwizecalApp', [])
  .controller('formCtrl', formCtrl)

//controller for quiz form
function formCtrl($scope, $http, $location) {
  $scope.formquiz = {
    "title":"",
    "questions":[]
  };

  $scope.formquestion = {
    "question":"",
    "choices":[{choice:""}, {choice:""}, {choice:""},{choice:""}],
    "answer":0
  };

  // add a question to the quiz JSON object
  $scope.addQuestion = function() {
    if($scope.formquiz.questions.length < 10){
      $scope.formquiz.questions.push($scope.formquestion);
      $scope.clearFields();
    }
    else{
      alert(" Maximum quiz length is " + $scope.formquiz.questions.length + " questions");
    }
  }

  // reset the form fields after adding a question
  $scope.clearFields = function() {
    $scope.formquestion = {
      "question":"",
      "choices":[{choice:""}, {choice:""}, {choice:""},{choice:""}],
      "answer":0
    };
  }

  // submit the quiz JSON object to the server
  $scope.formSubmit = function() {
    var data = $scope.formquiz;
    console.log("Submitting form: " + data);

    $http.post("/postquiz", data)
      .success(function(data, status) {
        $location.path("/congrats");
      })
      .error(function(data, status, headers, config) {
        console.log("Failure :" + JSON.stringify({data : data}));
      });
  }

}
