/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 07/04/13
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */

//var myApp = angular.module('itcApp', []);
//
//myApp.directive('editLine', function($compile) {
//    var linkFn;
//    linkFn = function(scope, element, attrs) {
//        var editln, deleteln, cancel, update;
//        var oneBtn = angular.element(element.children()[0]);
//        var twoBtn = angular.element(element.children()[1]);
//
//
//        editln = function() {
//            var tva = $(oneBtn).closest('tr').children('td.tva').text();
//            var qty = $(oneBtn).closest('tr').children('td.qty').text();
//            var unity = $(oneBtn).closest('tr').children('td.unity').text();
//            var prix_ht = $(oneBtn).closest('tr').children('td.prix_ht').text();
//
//            //$(oneBtn).closest('tr').append($compile("<input name='id' type='hidden' value="+scope.line.id+">"))
//            $(oneBtn).closest('tr').children('td.tva').replaceWith($compile(
//                "<td class='tva'><input class='smallInput' type='text' name='tva'  value="+tva+"></td>")(scope));
//            $(oneBtn).closest('tr').children('td.qty').replaceWith($compile(
//                "<td class='qty'><input class='smallInput' type='text' name='qty'  value="+qty+"></td>")(scope));
//            $(oneBtn).closest('tr').children('td.unity').replaceWith($compile(
//                "<td class='unity'><input class='smallInput' type='text' name='unite'  value="+unity+"></td>")(scope));
//            $(oneBtn).closest('tr').children('td.prix_ht').replaceWith($compile(
//                "<td class='prix_ht'><input class='smallInput' type='text' name='prix_ht'  value="+prix_ht+"></td>")(scope));
//            $(oneBtn).closest('tr').children('th.control-buttons').replaceWith($compile(
//                "<th class='control-buttons' edit-line='test'><button type='submit' class='btn' ><i class='icon-ok'></i></button>" +
//                    "<button id='cancel' class='btn cancel' ><i class='icon-remove'></i> </button></th>")(scope));
//
//
//            //alert('edit: '+$(oneBtn).closest('tr').children('td.prix_ht').text());
//        }
//
//        deleteln = function(){
//            alert(twoBtn.attr('class'));
//        }
//
//        cancel = function(){
//            alert('cancel');
//            location.reload();
//        }
//
//        update = function (){
//            alert('update');
//        }
//
//        if (oneBtn.attr('class')=='btn editline'&& twoBtn.attr('class')=='btn deleteline'){
//            $(oneBtn).on('click', editln);
//            $(twoBtn).on('click', deleteln);
//        }
//        else {
//            //$(oneBtn).on('click', update);
//            $(twoBtn).on('click', cancel);
//        }
//
//    }
//
//    return {
//        restrict: 'A',
//        link: linkFn
//    }
//});

// directives that represents an order
var order = angular.module("orderModule",[]);

order.directive('orderHeader', function($http){
    return{
        restrict: 'A',
        link: function(scope, element, attrs){
            //look for header directive attrs changes
//          attrs.$observe('orderHeader', function(value){
//             if(value){
//                 $http.get('/order?id='+value).success(function(data){
//                     scope.order = data.ord;
//                     scope.order.customer = data.customer;
//                     scope.order.lines = data.lines;
//                 });
//             }
//          });
        },
        template: '<div class="span12 order-summary">' +
            '<table id="order_totals" class="order">' +
            '<tr><td class="ref">Ref.</td><td>{{order.ref}}</td></tr>' +
            '<tr><td class="client">Client:</td><td>{{order.customer.nom}}</td></tr>' +
            '<tr><td class="date">Date:</td><td>{{order.order_date}}</td></tr>' +
            '<tr><td class="qty">Qty:</td><td id="total_qty">{{total_kg}} KG {{total_piece}} Piece</td></tr>' +
            '<tr><td class="total_ht">Total HT:</td><td>{{order.total_ht}}</td></tr>' +
            '<tr><td class="total_ttc">Total TTC:</td><td>{{order.total_ttc}}</td></tr>' +
            '</table></div>',
        replace: true
    }

});

order.directive('orderLines',function($http,calculateTotalQtyService,$compile){

    return {
        restrict: 'A',
        link: function(scope,element, attrs){

            scope.editLine = function(line,order_id){
                alert ('Edit'+line.id);
                scope.editMode = true;


            }
            scope.deleteLine = function(idx){
                var line_to_delete = scope.order.lines[idx]
                $http.post('/delete/line?id='+line_to_delete.id + '&order_id='+scope.order.id).success(function(data){
                      scope.order.lines.splice(idx,1);
                      scope.order = data.ord;
                      scope.order.customer = data.customer;
                      scope.order.lines = data.lines;
                      var res = calculateTotalQtyService.set(data.lines);
                      scope.total_kg = res[0];
                      scope.total_piece = res[1];
                });

            }

            scope.cancelAction = function(){
                alert ('cancel');
                   scope.editMode = false;
            }
//             $scope.$watch('order.lines',function(oldValue,newValue){
//                 if ( newValue === oldValue ) {
//
//                     return;
//
//                 }
//                // $scope.order.lines=newValue;
//            });


        },
        templateUrl:'/assets/fragments/order/order_lines.html',
        replace: true
    }

});

order.directive('orderControls',function($http,$location){
   return {
       restrict: 'A',
       link: function($scope){
           $scope.modifyAction = function(order){
               alert ('modify'+order.id);
               $http.get('/order/modify?id='+order.id).success(function(data){
                   $scope.order = data.ord;
                   $scope.order.customer = data.customer;
                   $scope.order.lines = data.lines;
               });
           }
           $scope.sendAction = function(order){
               alert ('send'+order.id);
               $http.get('/order/send?id='+order.id).success(function(data){
                     alert("Commande "+order.ref+" envoyée")
               });
           }
           $scope.deleteAction = function (order){
               $http.post('/order/delete?id='+order.id).success(function(data){
                   window.location='/showorders';
               });
           }
           $scope.validateAction = function(order){
               $http.get('/order/validate?id='+order.id).success(function(data){
                   $scope.order = data.ord;
                   $scope.order.customer = data.customer;
                   $scope.order.lines = data.lines;
               });
           }

       },
       templateUrl: '/assets/fragments/order/order_controls.html',
       replace: true
   }
});

order.directive('autoComplete',function($http){
    return {
        restrict: 'A',
        link: function(scope,element,attr,ctrl){
           element.autocomplete({
              source : function(req,resp){
                  $http.get('/searchproducts/?term='+scope.request+'&customer_id='+scope.order.customer.id).success( function(data){
                      var suggestions = [];
                      $.each(data, function(index, val) {
                          var obj = {};
                          //obj.label = val.ref+"-"+val.label+"-"+val.price;
                          obj.label = val._2+"-"+val._3+"-"+val._4;
                          obj.id = val._1;
                          suggestions.push(obj);
                      });
                      resp(suggestions);
                  });
              },
              select: function(event, ui){
                  scope.$apply(function() {
                      scope.product_id = ui.item.id;
                  });
              }


           });
        },
        template: ''
    }

});



order.directive('editable', function(){

    return {
        restrict : 'E',
        replace : true,
        templateUrl: "/assets/fragments/order/editable.html",
//        scope : {
//            label : '@',
//            text : '='
//        },
        link : function(scope, element, attrs){

            // editMode is disable by default
           // scope.editMode = false;
            attrs.$observe('editable',function(){

            });


            // find the input elemnt of this directive ...
            var input = element.find('input');
            // and listen for blur event
//            input.bind('blur', function(){
//                // since blur event occured ouside the angular execution context
//                // we need to call scope.$apply to tell angularjs about the changes
//                scope.$apply(function(){
//                    // the change is to disable the editMode
//                    scope.editMode = false;
//                });
//
//            });

        }
    }

});

//service style, service to calculate total qty
order.service('calculateTotalQtyService', function() {
       return {
           set  : function (lines){
        var total_kg =0;
        var total_piece = 0;
        for(var i = 0; i<lines.length; i++){
            if(lines[i].unity == "piece")
                total_piece+=lines[i].qty ;
            if(lines[i].unity == "kg")
                total_kg+=lines[i].qty ;
        }

        return [total_kg,total_piece];
    }
}
});



//angular.module('itcApp',['filters']);
//angular.module('filters', []).filter('catalogueSearch',function(){
// return function(product,query){
//   for (var key in product){
//       if(key.index)
//   }
// }
//});

