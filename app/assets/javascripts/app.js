/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 07/04/13
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
var myApp = angular.module('itcApp', []);

myApp.directive('editLine', function($compile) {
    var linkFn;
    linkFn = function(scope, element, attrs) {
        var editln, deleteln, cancel, update;
        var oneBtn = angular.element(element.children()[0]);
        var twoBtn = angular.element(element.children()[1]);


        editln = function() {
            var tva = $(oneBtn).closest('tr').children('td.tva').text();
            var qty = $(oneBtn).closest('tr').children('td.qty').text();
            var unity = $(oneBtn).closest('tr').children('td.unity').text();
            var prix_ht = $(oneBtn).closest('tr').children('td.prix_ht').text();

            //$(oneBtn).closest('tr').append($compile("<input name='id' type='hidden' value="+scope.line.id+">"))
            $(oneBtn).closest('tr').children('td.tva').replaceWith($compile(
                "<td class='tva'><input class='smallInput' type='text' name='tva'  value="+tva+"></td>")(scope));
            $(oneBtn).closest('tr').children('td.qty').replaceWith($compile(
                "<td class='qty'><input class='smallInput' type='text' name='qty'  value="+qty+"></td>")(scope));
            $(oneBtn).closest('tr').children('td.unity').replaceWith($compile(
                "<td class='unity'><input class='smallInput' type='text' name='unite'  value="+unity+"></td>")(scope));
            $(oneBtn).closest('tr').children('td.prix_ht').replaceWith($compile(
                "<td class='prix_ht'><input class='smallInput' type='text' name='prix_ht'  value="+prix_ht+"></td>")(scope));
            $(oneBtn).closest('tr').children('th.control-buttons').replaceWith($compile(
                "<th class='control-buttons' edit-line='test'><button type='submit' class='btn' ><i class='icon-ok'></i></button>" +
                    "<button id='cancel' class='btn cancel' ><i class='icon-remove'></i> </button></th>")(scope));


            //alert('edit: '+$(oneBtn).closest('tr').children('td.prix_ht').text());
        }

        deleteln = function(){
            alert(twoBtn.attr('class'));
        }

        cancel = function(){
            alert('cancel');
            location.reload();
        }

        update = function (){
            alert('update');
        }

        if (oneBtn.attr('class')=='btn editline'&& twoBtn.attr('class')=='btn deleteline'){
            $(oneBtn).on('click', editln);
            $(twoBtn).on('click', deleteln);
        }
        else {
            //$(oneBtn).on('click', update);
            $(twoBtn).on('click', cancel);
        }

    }

    return {
        restrict: 'A',
        link: linkFn
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

