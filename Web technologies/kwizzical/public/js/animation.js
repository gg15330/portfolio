// Script to animate the Kwizecal logo
"use strict";

angular.module('kwizecalApp', [])
  .controller('animationCtrl', animationCtrl)

// animates svg logo according to method here https://jakearchibald.com/2013/animated-line-drawing-svg/
function animationCtrl($scope){
  // Wait for the content to be loaded
  $scope.$on('$includeContentLoaded',function () {
    // for each path in the SVG, get the path by id and the path length
    for (var i = 0; i < 9; i++) {
       var path = document.querySelector('#i' + i);
       var length = path.getTotalLength();
       path.style.transition = path.style.WebkitTransition = path.style.MozTransition = 'none';
       //Convert the path into a dashed path
       path.style.strokeDasharray = length + ' ' + length;
       //Offset the dashes which changes their position
       path.style.strokeDashoffset = length ;
       path.getBoundingClientRect();
       //Animate the offset back to 0 using a transition effect, appears as if being drawn
       path.style.transition = path.style.WebkitTransition = path.style.MozTransition = 'stroke-dashoffset 3s ease-in-out';
       path.style.strokeDashoffset = '0';
    }
  });
}
