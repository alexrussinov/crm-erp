/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 07/08/13
 * Time: 21:59
 * To change this template use File | Settings | File Templates.
 */
!function ($) {

    $(function(){

        var $window = $(window) ;
        var $body   = $(document.body)  ;

        var navHeight = $('.navbar').outerHeight(true) + 10;

        $body.scrollspy({
            target: '.bs-sidebar',
            offset: navHeight
        });

        $('.container [href=#]').click(function (e) {
            e.preventDefault()
        });

        // back to top
        setTimeout(function () {
            var $sideBar = $('.bs-sidebar');

            $sideBar.affix({
                offset: {
                    top: function () {
                        var offsetTop      = $sideBar.offset().top
                        var sideBarMargin  = parseInt($sideBar.children(0).css('margin-top'), 10)
                        var navOuterHeight = $('.bs-docs-nav').height()

                        return (this.top = offsetTop - navOuterHeight - sideBarMargin)
                    }
                    , bottom: function () {
                        return (this.bottom = $('.bs-footer').outerHeight(true))
                    }
                }
            })
        }, 100)

        setTimeout(function () {
            $('.bs-top').affix()
        }, 100);
    });
}