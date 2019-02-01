var editorEl = document.querySelector(".editor");
var errors = document.querySelector('.errors');
var reader = document.querySelector('#reader');
var scrollToggle = document.querySelector('#auto-scroll');
var watch = document.querySelector('.watch-btn');

var autoScroll = true;

var url = '/content';

function connect() {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);

    xhr.setRequestHeader('Content-Type', 'text/plain');
    xhr.onreadystatechange = function () {
        if (xhr.readyState > 3 && xhr.status == 200) {
            editorEl.textContent = xhr.responseText;
        }
    };

    xhr.send();

    if (autoScroll) {
        editorEl.scrollTop = editorEl.scrollHeight
    }
}

scrollToggle.addEventListener('click', function (el) {

    autoScroll = !autoScroll;
});


watch.addEventListener('click', function (el) {

    setInterval(connect, 100);

    watch.parentElement.removeChild(watch);


    // setInterval(function () {
    //     reader.contentWindow.location.reload(true);
    // }, 200);
});


editorEl.addEventListener('keyup', function (el) {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", url, false);
    xhr.setRequestHeader('Content-Type', 'text/plain');
    var text = el.target.innerText;
    xhr.send(text);
});

function toggleFullScreen() {
    if (!document.fullscreenElement) {
        document.documentElement.requestFullscreen();
    } else {
        if (document.exitFullscreen) {
            document.exitFullscreen();
        }
    }
}
