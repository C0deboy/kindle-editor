var sock;
var editor = document.querySelector(".editor");
var errors = document.querySelector('.errors');
var progress = document.querySelector('.progress');

var host = window.location.hostname;

connect();

function updateData() {
    return function (e) {
        var data = JSON.parse(e.data);
        autoScroll(data.cursor, data.text);

        var percantage = data.cursor.start * 100 / data.text.length;
        console.log(percantage);
        progress.style.width = percantage + '%';
    };
}

function autoScroll(cursor, fullText) {

    editor.focus();

    editor.value = fullText.substring(0, cursor.start);
    editor.scrollTop = editor.scrollHeight;
    editor.value = fullText;
    if (editor.scrollTop > 100)
        editor.scrollTop += 600;

    editor.setSelectionRange(cursor.start, cursor.end);
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
        cursor: {start: editor.selectionStart, end: editor.selectionEnd}
    }));
});

window.addEventListener("beforeunload", function() {
    sock.close();
});
