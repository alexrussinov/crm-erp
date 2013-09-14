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
//var order = angular.module("itcModule",['calculateTotalQtyService']);

google.setOnLoadCallback(function() {
    angular.bootstrap(document.body, ['itcModule']);
});
google.load('visualization', '1', {packages: ['corechart']});

var main = angular.module("itcModule",[]);

main.directive('orderHeader', function($http){
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

            // we hide alert block by default
            $('.alert').hide();
        },
        templateUrl: '/assets/fragments/order/order_header.html',
        replace: true
    }

});

main.directive('orderLines',function($http,calculateTotalQtyService,$compile){

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

main.directive('orderControls',function($http,$location){
    return {
        restrict: 'A',
        link: function($scope){
            $scope.modifyAction = function(order){
              if($scope.user.admin == 1){
                $http.get('/order/modify?id='+order.id).success(function(data){
                    $scope.order = data.ord;
                    $scope.order.customer = data.customer;
                    $scope.order.lines = data.lines;
                });
              }
                else{
                  if(!$.isEmptyObject($scope.activeorder) && $scope.activeorder.fk_statut == 0){
                      console.log("validate order"+$scope.activeorder.ref);
                      $('#createOrderAlert').modal('toggle');
                  }
                  else{
                      $http.get('/order/modify?id='+order.id).success(function(data){
                          $scope.order = data.ord;
                          $scope.order.customer = data.customer;
                          $scope.order.lines = data.lines;
                          $scope.activeorder=$scope.getUserActiveOrder();
                          console.log("From directive validate action activeorder inside get: "+ $scope.order.fk_statut);
                      });
                      console.log("From directive validate action activeorder outside get: "+ $scope.order.fk_statut);
                  }
              }
            }
            $scope.sendAction = function(order){
                $('#sendOrderAlert').modal('hide');
                $('#sendingOrderProgressBarModal').modal('show');
                $http.get('/order/send?id='+order.id).success(function(data){
                    $scope.order.sent=true;
                    $('#sendingOrderProgressBarModal').modal('hide');
                    $('.alert').show();
                    window.setTimeout(function() { $('.alert').hide(); }, 3000);
                    //alert("Commande "+order.ref+" envoyée")
                });
            }
            $scope.deleteAction = function (order){
                $http.post('/order/delete?id='+order.id).success(function(data){
                    window.location='/showorders';
                });
            }
            $scope.validateAction = function(order){
                $('#validateOrderAlert').modal('hide');
                $http.get('/order/validate?id='+order.id).success(function(data){
                    $scope.order = data.ord;
                    $scope.order.customer = data.customer;
                    $scope.order.lines = data.lines;
                    if($scope.user.admin!=1)
                    $scope.activeorder = $scope.getUserActiveOrder();
                    console.log("From directive validate action order inside get: "+ $scope.order.fk_statut);
                });
                console.log("From directive validate action order outside get: "+ $scope.order.fk_statut);
            }
            $scope.validateAlertAction = function(){
                $('#validateOrderAlert').modal('toggle');
            }
            $scope.sendAlertAction = function(){
                $('#sendOrderAlert').modal('toggle');
            }

        },
        templateUrl: '/assets/fragments/order/order_controls.html',
        replace: true
    }
});

main.directive('autoComplete',function($http){
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



main.directive('editable', function(){

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

main.directive('editableCell',function(){
    return {
        restrict : 'E',
        replace : true,
        templateUrl: "/assets/fragments/editable-cell.html",
        scope : {
//            label : '@',
            value : '='
        },
        link : function(scope, element, attrs,$scope){



            // editMode is disable by default
           // scope.editMode = false;

            // if label attribut is not provide then remove
            // the label element
            if(!attrs.label){
                element.find('label').remove();
            }


            // find the input elemnt of this directive ...
            var input = element.find('input');
            // and listen for blur event
            input.bind('blur', function(){
                // since blur event occured ouside the angular execution context
                // we need to call scope.$apply to tell angularjs about the changes
                scope.$apply(function(){
                    // the change is to disable the editMode
                    scope.editMode = false;
                });

            });

        }
    }
});

main.directive('blockCategories',function(){
    return {
        restrict : 'E',
        link : function($scope,element,attrs){
            attrs.$observe('withId', function(value) {
                $scope.categoryWithId = value;
                console.log('withId=', value);
            });
        },
        templateUrl: '/assets/fragments/main/categories.html',
        replace : true
    }
});

main.directive('blockManufactures',function(){
    return {
        restrict : 'A',
        link : function($scope,element,attr){
            $scope.plus = function(){
                $scope.rest = true;
            }
            $scope.minus = function(){
                $scope.rest = false;
            }
        },
        templateUrl: '/assets/fragments/main/manufactures.html',
        replace: true
    }
});

main.directive('activeOrder',function(){
       return {
           restrict : 'E',
           link : function($scope,element,attr){},
           templateUrl: '/assets/fragments/main/active-order.html',
           replace: true
       }
});

main.directive('addProductModal',function($http){
    return {
        restrict : 'E',
        link : function($scope,element,attr){
            /*insert line to order from catalog if user not admin*/
            $scope.insertProductNotAdmin = function (){

                $scope.insert_product_json =
                {
                    user_id: $scope.activeorder.fk_soc,
                    order_id: $scope.activeorder.id,
                    product_id: $scope.current_product_id,
                    qty: $scope.current_order_product_qty
                };

                $http.post("/addLineJson", $scope.insert_product_json)
                    .success(function(data, status, headers, config) {
                        $('#CatalogAddProductNotAdmin').modal('hide');
                        $scope.getUserActiveOrder();
                    }).error(function(data, status, headers, config) {
                        $scope.status = status;
                        alert('Error'+status)
                    });


            }
        },
        templateUrl: '/assets/fragments/main/add-product-modal.html',
        replace: true
    }
});

main.directive('createOrderModal',function($http){
    return {
        restrict : 'A',
        link : function($scope,element,attr){

        },
        templateUrl: '/assets/fragments/main/new-order-modal.html',
        replace: true
    }
});

main.directive('imagesUpload', function($http){
   return {
       restrict : 'E',
       link : function($scope){
          $scope.selectCat = function (id){
              alert(id);
          }
       },
       templateUrl: '/assets/fragments/imageupload/imageupload.html',
       replace: true
   }
});

main.directive('productFiche',function($http){
    return {
        restrict : 'E',
        link : function(){},
        templateUrl: '/assets/fragments/product/product-fiche.html',
        replace: true
    }
});

main.directive('googleChart',function($http,$timeout){
    return{
        restrict:'EA',
//        scope: {
//            title:    '@title',
//            width:    '@width',
//            height:   '@height',
//            client_id:'@clientid',
//            data:     '=data'
//        },
        link : function($scope, $elm, $attr){

            var data = google.visualization.arrayToDataTable($scope.chartData);
            var chart = new google.visualization.ColumnChart($elm[0]);


            $http.get('orders/totals/permonth?id='+$scope.client_id).success(function(res){
                 data = google.visualization.arrayToDataTable([
                    ['Mois', 'Sales'],
                    ['Janvier',  parseFloat(res[0].toFixed(2))],
                    ['Fevrier',  parseFloat(res[1].toFixed(2))],
                    ['Mars',  parseFloat(res[2].toFixed(2))],
                    ['Avril',  parseFloat(res[3].toFixed(2))],
                    ['Mai',  parseFloat(res[4].toFixed(2))],
                    ['Juin',  parseFloat(res[5].toFixed(2))],
                    ['Juillet', parseFloat(res[6].toFixed(2))],
                    ['Aout',  parseFloat(res[7].toFixed(2))],
                    ['Septembre', parseFloat(res[8].toFixed(2))],
                    ['Octobre', parseFloat(res[9].toFixed(2))],
                    ['Novembre', parseFloat(res[10].toFixed(2))],
                    ['Decembre', parseFloat(res[11].toFixed(2))]
                ]);
            draw();
            });

            $scope.$watch('data', function() {
                draw();
            }, true); // true is for deep object equality checking
            $scope.$watch('title', function() {
                draw();
            });
            $scope.$watch('width', function() {
                draw();
            });
            $scope.$watch('height', function() {
                draw();
            });


                function draw(){
                var options = {
                    'title': $scope.chartTitle,
                    'width': $scope.chartWidth,
                    'height':$scope.chartHeight
                };
                chart.draw(data, options);

            }


        }
    }
});

main.directive('customerFiche',function($http){
    return{
        restrict : 'E',
        link : function($scope){
           $scope.edit = function(){
               $scope.edit2Mode.customerInfo = true;
           }
           $scope.cancel = function(){
               $scope.edit2Mode.customerInfo = false;
           }

           $scope.submit = function(){
               $http({
                   url :'/company/update',
                   method : 'POST',
                   data : $scope.company,
                   headers: {'Content-Type': 'application/json'}
               }).success(function(data){
                       $scope.company = data;
                       $scope.edit2Mode.customerInfo = false;
                   }).error(function(data){
                       alert(data);
                   });
           }

//           $scope.editDiscount = function(){
//               $scope.edit2Mode.customerDiscount = true;
//               $scope.editMode = true;
//           }
//
//           $scope.createDiscount = function(){
//
//           }
        },
        templateUrl: '/assets/fragments/company/customer-fiche.html',
        replace: true
    }

});

main.directive('listCustomers',function($http){
    return{
        restrict : 'E',
        link : function($scope){

        },
        templateUrl: '/assets/fragments/company/list-customers.html',
        replace: true
    }

});


main.directive('editableDiscount',function(){
    return {
        restrict : 'E',
        replace : true,
        templateUrl: "/assets/fragments/company/editable-discount.html",

        link : function($scope, element, attrs){

        }
    }
});

////service style, service to calculate total qty
main.service('calculateTotalQtyService', function() {
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

main.service('getCustomersService',function($http){
    return {
        set : function(){
            var result ={};
            $http.get('/getcustomers').success(function(data){
                for(var i=0; i< data.length; i++){
                    result[data[i].id]=data[i];
                }
            });
            return result;
        }
    }
});

// TODO Progress indication while sending an order, order sent notification
// TODO Pop up notification when sending an order that after no deletion or modification possible

//angular.module('itcApp',['filters']);
//angular.module('filters', []).filter('catalogueSearch',function(){
// return function(product,query){
//   for (var key in product){
//       if(key.index)
//   }
// }
//});

main.filter('marqueFilter',function(){
    return function(items,query){
        var filtered =[];
        if(query.length === 0)
        return items;
        else{
        angular.forEach(items,function(item){
           for(var i=0; i< query.length; i++){
              if(item.manufacture.indexOf(query[i]) !== -1)
              filtered.push(item);
           }
        });

        return filtered;
        }
    }
});