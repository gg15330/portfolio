//Controller that deals with taking a quiz wrapped in a JSON object
"use strict";

angular.module('kwizecalApp', [])
  .controller('quizCtrl', quizCtrl);

function quizCtrl($scope, $http, $routeParams) {

  function loadQuiz(data) {
    checkJSON(data);
    $scope.quiz = data;
  }

  function showError(reason) {
    console.log(reason);
  }

  //initialises quiz variables
  $scope.init = function() {
    $scope.i = 0;
    $scope.score = 0;
    $scope.msg = "Current score: ";
    $scope.btnmsg = "Next";
    $scope.running = true;
    $scope.choicemade = false;
  }

  //checks answer and updates score, ends quiz if last question is reached
  $scope.update = function(choice) {
    if(!$scope.choicemade) {
      if(choice == $scope.quiz.questions[$scope.i].answer) { $scope.score++; }
      if($scope.i >= $scope.quiz.questions.length - 1) {
        $scope.running = false;
        $scope.msg = "Final score: ";
        $scope.btnmsg = "Restart";
      }
      $scope.choicemade = true;
    }
  }

  //moves on to next question -
  //if end of quiz, resets quiz
  $scope.next = function() {
    if($scope.running) {
      $scope.i++;
      $scope.choicemade = false;
    }
    else { $scope.init(); }
  }

  // send a request to the server for a quiz
  var id = $routeParams.id;
  console.log("ID: " + id);
  console.log("Client sending request for quiz: " + id);
  var url = 'getquiz/' + id;

  //fetch JSON data for quiz
  $http
   .get(url)
   .success(loadQuiz)
   .error(showError)

  //call to init displays start of quiz
  $scope.init();
}

//check JSON data is in the correct format(4 choices per question etc.)
function checkJSON(data) {
  for(var i = 0; i < data.length; i++) {
    if(data[i].choices.length != 4) {
      console.log("Error: question " +  i + " has incorrect number of choices.");
    }
  }
}
