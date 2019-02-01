var sock;
var editor = document.querySelector(".editor");
var errors = document.querySelector('.errors');
var watch = document.querySelector('.watch-btn');

var host = window.location.hostname;

connect();

function updateData() {
    return function (e) {
        var data = JSON.parse(e.data);

        autoScroll(data.cursor, data.text);
    };
}

function autoScroll(selectionStart, fullText) {

    editor.focus();

    editor.value = fullText.substring(0, selectionStart);
    editor.scrollTop = editor.scrollHeight;
    editor.value = fullText;
    if (editor.scrollTop > 100)
        editor.scrollTop += 600;

    editor.setSelectionRange(selectionStart, selectionStart);
}

function connect() {

    sock = new SockJS('http://' + host + ':8080/editor-socket', null, {transports: "xhr-streaming"});

    sock.onerror = function (event) {
        editor.value = event;
    };

    sock.onmessage = function (data) {
        updateData()(data);
        sock.onmessage = null;
    };
}

if (host !== 'localhost') {
    sock.onmessage = updateData();
}

editor.addEventListener('keyup', function (el) {
    sock.send(JSON.stringify({
        text: editor.value,
        cursor: editor.selectionStart
    }));
});

window.addEventListener("beforeunload", function() {
    sock.close();
});
