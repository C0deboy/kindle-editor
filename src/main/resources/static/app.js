var sock;
var editorEl = document.querySelector(".editor");
var errors = document.querySelector('.errors');
var watch = document.querySelector('.watch-btn');

var host = window.location.hostname;

connect();

function updateData() {
    return function (data) {
        var d = data.data.split('|');
        editorEl.textContent = d[0];

        editorEl.setSelectionRange(d[1], d[1]);

        editorEl.focus();

        autoScroll(d[1]);
    };
}

function connect() {

    sock = new SockJS('http://' + host + ':8080/editor-socket', null, {transports: "xhr-streaming"});

    sock.onerror = function (event) {
        editorEl.value = event;
    };

    sock.onmessage = function (data) {
        updateData()(data);
        sock.onmessage = null;
    };
}


watch.addEventListener('click', function (el) {

    sock.onmessage = updateData();

    watch.parentElement.removeChild(watch);
});

if (host !== 'localhost')
    watch.click();
else
    editorEl.style.fontSize = '35px';

editorEl.addEventListener('keyup', function (el) {
    var text = el.target.value;
    sock.send(text + "|" + el.target.selectionStart);
});

function autoScroll(selectionStart) {

    const fullText = editorEl.value;
    editorEl.value = fullText.substring(0, selectionStart);
    editorEl.scrollTop = editorEl.scrollHeight + 20;
    editorEl.value = fullText;

    editorEl.setSelectionRange(selectionStart, selectionStart);
}

window.addEventListener("beforeunload", function() {
    sock.close();
    errors.innerHTML = 'closing';
});
