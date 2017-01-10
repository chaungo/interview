$(function () {
    <!-- home page -->
    $('.mypanel').lobiPanel({
        reload: false,
        close: false,
        editTitle: false
    });
    <!-- show dashboard -->
    $('.mypanel1').lobiPanel({
        reload: false,
        close: false,
        editTitle: false,
        unpin: false,
        expand: false,
        minimize: false
    });
    <!-- two panels -->
    $('#lobipanel-multiple').find('.panel').lobiPanel({
        reload: false,
        // close: false,
        editTitle: false,
        sortable: true
    });
});

$(document).ready(function () {
    $('ul.nav li.dropdown').hover(function () {
        $(this).find('.dropdown-menu').stop(true, true).delay(100).fadeIn(500);
    }, function () {
        $(this).find('.dropdown-menu').stop(true, true).delay(100).fadeOut(500);
    })
});
	