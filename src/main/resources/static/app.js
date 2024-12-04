const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/stomp',
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/sub/chat/hello', (greeting) => {
        showGreeting(JSON.parse(greeting.body));
    },{
        Authorization: 'Basic aGVsbG86cG93ZXJhc3M=',
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greeting").html("");
}

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.publish({
        destination: "/pub/chat/hello",
        body: JSON.stringify({'content': $("#name").val()}),
        headers: {
            Authorization: 'Basic aGVsbG86cG93ZXJhc3M=',
        },
    });
}

function enterRoom() {
    stompClient.publish({
        destination: "/pub/chat/hello/enter",
        headers: {
            Authorization: 'Basic aGVsbG86cG93ZXJhc3M=',
        },
    });
}

function leaveRoom() {
    stompClient.publish({
        destination: "/pub/chat/hello/leave",
        headers: {
            Authorization: 'Basic aGVsbG86cG93ZXJhc3M=',
        },
    });
}

function showGreeting(message) {
    console.log('Received: ' + JSON.stringify(message));
    $("#greeting").append("<tr><td>" + JSON.stringify(message) + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
    $("#send").click(() => sendName());
    $("#enter").click(() => enterRoom());
    $("#leave").click(() => leaveRoom());
});
