var sock;
var editor = document.querySelector(".editor");
var errors = document.querySelector('.errors');
var progress = document.querySelector('.progress');

var host = window.location.hostname;

connect();

function updateData(e) {
    var data = JSON.parse(e.data);
    autoScroll(data.cursor, data.text);

    var percentage = data.cursor.start * 100 / data.text.length;
    progress.style.width = percentage + '%';
}

function autoScroll(cursor, fullText) {

    editor.focus();

    editor.value = fullText.substring(0, cursor.end);
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
        updateData(data);
        sock.onmessage = null;
    };
}

if (host !== 'localhost') {
    sock.onmessage = updateData;
} else {
    editor.addEventListener('keyup', function (e) {
        sock.send(JSON.stringify({
            text: editor.value,
            cursor: {start: editor.selectionStart, end: editor.selectionEnd}
        }));
    });

    editor.addEventListener('keydown', function (e) {
        if (e.ctrlKey && e.keyCode === 83) {// ctrl + s
            e.preventDefault()
            var xhr = new XMLHttpRequest()
            xhr.open('POST', 'http://' + host + ':8080/backup', true);
            xhr.send(editor.value)
        }
    });
}

window.addEventListener("beforeunload", function() {
    sock.close();
});
